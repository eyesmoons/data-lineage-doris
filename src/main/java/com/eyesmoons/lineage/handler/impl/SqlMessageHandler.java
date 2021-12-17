package com.eyesmoons.lineage.handler.impl;


import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.eyesmoons.lineage.contants.Constants;
import com.eyesmoons.lineage.exception.CustomException;
import com.eyesmoons.lineage.handler.BaseMessageHandler;
import com.eyesmoons.lineage.model.response.DorisSqlAudit;
import com.eyesmoons.lineage.parser.visitor.RewriteAllColumnsVisitor;
import com.eyesmoons.lineage.utils.JSONUtil;
import com.eyesmoons.lineage.annotation.SourceHandler;
import com.eyesmoons.lineage.contants.HandlerConstant;
import com.eyesmoons.lineage.contants.NeoConstant;
import com.eyesmoons.lineage.model.request.SqlMessage;
import com.eyesmoons.lineage.model.response.LineageContext;
import com.eyesmoons.lineage.neo4j.domain.*;
import com.eyesmoons.lineage.utils.LineageUtil;
import com.eyesmoons.lineage.utils.SqlKafkaUtil;
import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;
import com.eyesmoons.lineage.parser.analyse.impl.DefaultHandlerChain;
import com.eyesmoons.lineage.model.parser.ParseColumnNode;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import com.eyesmoons.lineage.utils.TreeNodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SQL 解析
 */
@SourceHandler(NeoConstant.SourceType.SQL)
@Slf4j
public class SqlMessageHandler implements BaseMessageHandler {

    private static final String DATA_SOURCE_NAME = "test_doris";

    @Autowired
    private DefaultHandlerChain defaultHandlerChain;

    @Override
    public LineageContext handle(DorisSqlAudit audit) {
        String sql = rewriteSql(audit);
        SqlMessage sqlMessage = new SqlMessage();
        sqlMessage.setSql(sql);
        sqlMessage.setDataSourceName(DATA_SOURCE_NAME);
        sqlMessage.setDbType(Constants.DEFAULT_DB_TYPE);
        LineageContext context = new LineageContext();
        context.setSqlMessage(sqlMessage);
        // 处理
        List<SqlRequestContext> contextList = SqlKafkaUtil.convertSqlRequest(sqlMessage);
        // 解析SQL
        contextList.forEach(requestContext -> this.handleSql(requestContext, context));
        // 建立上层节点 DataSource、Db
        createUpperLayerNode(context);
        return context;
    }

    /**
     * 重写SQL，处理SQL中字段为[*]的情况
     */
    private String rewriteSql(DorisSqlAudit audit) {
        String sql = audit.getStmt();
        RewriteAllColumnsVisitor rewriteAllColumnsVisitor = new RewriteAllColumnsVisitor();
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, DbType.mysql);
        List<String> newSql = new ArrayList<>();
        for (SQLStatement statement : statements) {
            statement.accept(rewriteAllColumnsVisitor);
            StringBuilder sb = new StringBuilder();
            MySqlOutputVisitor mySqlOutputVisitor = new MySqlOutputVisitor(sb);
            statement.accept(mySqlOutputVisitor);
            newSql.add(sb.toString());
        }
        log.info("重写后的SQL：{}", newSql);
        return String.join(";", newSql);
    }

    private void createUpperLayerNode(LineageContext context) {
        SqlMessage sqlMessage = context.getSqlMessage();

        // 一次SQL任务只涉及一种数据源
        context.getDataSourceNodeList().add(new DataSourceNode(sqlMessage.getDataSourceName()));

        // 根据 table 生成 db 节点信息
        context.getTableNodeList().forEach(tableNode -> context.getDbNodeList().add(new DbNode(tableNode.getDataSourceName(), tableNode.getDbName())));
    }

    private void handleSql(SqlRequestContext request, LineageContext context) {
        SqlResponseContext response = new SqlResponseContext();
        defaultHandlerChain.handle(request, response);
        if (Objects.isNull(response.getLineageTableTree())) {
            return;
        }
        // 处理表关系
        handleTableNode(request, response, context);
        handleFieldNode(request, response, context);
    }

    private void handleFieldNode(SqlRequestContext request, SqlResponseContext response, LineageContext context) {
        // 获取字段关系树
        List<TreeNode<ParseColumnNode>> lineageColumnTreeList = response.getLineageColumnTreeList();
        if (CollectionUtils.isEmpty(lineageColumnTreeList)) {
            return;
        }
        lineageColumnTreeList.forEach(columnNodeTreeNode -> {
            ParseColumnNode target = columnNodeTreeNode.getValue();
            List<ParseColumnNode> leafParseColumnNodeList = TreeNodeUtil.searchTreeLeafNodeList(columnNodeTreeNode);
            // convert
            FieldNode targetFieldNode = buildFieldNodeNeo4j(request, target);
            List<FieldNode> sourceFieldNodeList = leafParseColumnNodeList.stream()
                    .filter(parseColumnNode -> Objects.nonNull(parseColumnNode.getOwner()))
                    .map(fieldNode -> this.buildFieldNodeNeo4j(request, fieldNode))
                    .collect(Collectors.toList());
            // save
            context.getFieldNodeList().add(targetFieldNode);
            context.getFieldNodeList().addAll(sourceFieldNodeList);
            // 处理字段关系
            List<String> filedSourceNodePkList = sourceFieldNodeList.stream().map(BaseNodeEntity::getPk).distinct().collect(Collectors.toList());
            // 字段关系节点
            RelationNode fieldRelationNode = new RelationNode(NeoConstant.RelationType.FIELD_RELATION, filedSourceNodePkList, targetFieldNode.getPk());
            fieldRelationNode.setType(HandlerConstant.SOURCE_TYPE_SQL_PARSER);
            // 填充 dataSourceName
            LineageUtil.fillingRelationNode(targetFieldNode, fieldRelationNode);
            // 执行的SQL
            fieldRelationNode.getExtra().put("sql", request.getSql());
            // 添加字段关系节点
            context.getRelationNodeList().add(fieldRelationNode);
        });
    }

    private void handleTableNode(SqlRequestContext request, SqlResponseContext response, LineageContext context) {
        // 获取表关系树
        TreeNode<ParseTableNode> lineageTableTree = response.getLineageTableTree();
        List<ParseTableNode> leafParseTableNodeList = TreeNodeUtil.searchTreeLeafNodeList(lineageTableTree);
        ParseTableNode rootParseTableNode = lineageTableTree.getRoot().getValue();
        // convert
        TableNode targetTableNode = buildTableNodeNeo4j(request, rootParseTableNode);
        List<TableNode> sourceTableNodeList = leafParseTableNodeList.stream().map(tableNode -> this.buildTableNodeNeo4j(request, tableNode)).collect(Collectors.toList());
        context.getTableNodeList().add(targetTableNode);
        context.getTableNodeList().addAll(sourceTableNodeList);
        List<String> sourceNodePkList = sourceTableNodeList.stream().map(BaseNodeEntity::getPk).distinct().collect(Collectors.toList());
        // 表关系节点
        RelationNode relationNode = new RelationNode(NeoConstant.RelationType.TABLE_RELATION, sourceNodePkList, targetTableNode.getPk());
        relationNode.setType(HandlerConstant.SOURCE_TYPE_SQL_PARSER);
        // 填充 dataSourceName
        LineageUtil.fillingRelationNode(targetTableNode, relationNode);
        // 执行的SQL
        relationNode.getExtra().put("sql", request.getSql());
        // 添加表关系节点
        context.getRelationNodeList().add(relationNode);
    }

    private FieldNode buildFieldNodeNeo4j(SqlRequestContext context, ParseColumnNode parserParseColumnNode) {
        ParseTableNode parseTableNode = Optional.ofNullable(parserParseColumnNode.getOwner()).orElseThrow(() -> new CustomException("column table node is null"));
        String dbName = parseTableNode.getDbName();
        return new FieldNode(context.getDataSourceName(), dbName, parseTableNode.getName(), parserParseColumnNode.getName());
    }

    private TableNode buildTableNodeNeo4j(SqlRequestContext context, ParseTableNode parserParseTableNode) {
        String dbName = parserParseTableNode.getDbName();
        return new TableNode(context.getDataSourceName(), dbName, parserParseTableNode.getName());
    }
}
