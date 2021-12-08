package com.eyesmoons.lineage.event.handler.sql;


import com.eyesmoons.lineage.common.exception.CommonException;
import com.eyesmoons.lineage.common.util.JSON;
import com.eyesmoons.lineage.event.annotation.SourceHandler;
import com.eyesmoons.lineage.event.contants.HandlerConstant;
import com.eyesmoons.lineage.event.contants.NeoConstant;
import com.eyesmoons.lineage.event.domain.model.*;
import com.eyesmoons.lineage.event.handler.BaseMessageHandler;
import com.eyesmoons.lineage.event.handler.LineageContext;
import com.eyesmoons.lineage.event.util.LineageUtil;
import com.eyesmoons.lineage.event.util.SqlKafkaUtil;
import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;
import com.eyesmoons.lineage.parser.analyse.handler.DefaultHandlerChain;
import com.eyesmoons.lineage.parser.model.ParseColumnNode;
import com.eyesmoons.lineage.parser.model.ParseTableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import com.eyesmoons.lineage.parser.util.TreeNodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private DefaultHandlerChain defaultHandlerChain;

    @Override
    public LineageContext handle(ConsumerRecord<String, String> record) {
        SqlMessage sqlMessage = JSON.toObj(record.value(), SqlMessage.class);
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
            List<FieldNode> sourceFieldNodeList = leafParseColumnNodeList.stream().map(fieldNode -> this.buildFieldNodeNeo4j(request, fieldNode)).collect(Collectors.toList());
            // save
            context.getFieldNodeList().add(targetFieldNode);
            context.getFieldNodeList().addAll(sourceFieldNodeList);
            // 处理字段关系
            List<String> filedSourceNodePkList = sourceFieldNodeList.stream().map(BaseNodeEntity::getPk).distinct().collect(Collectors.toList());
            // 字段关系节点
            ProcessNode fieldProcessNode = new ProcessNode(NeoConstant.ProcessType.FIELD_PROCESS, filedSourceNodePkList, targetFieldNode.getPk());
            fieldProcessNode.setType(HandlerConstant.SOURCE_TYPE_SQL_PARSER);
            // 填充 dataSourceName
            LineageUtil.fillingProcessNode(targetFieldNode, fieldProcessNode);
            // 执行的SQL
            fieldProcessNode.getExtra().put("sql", request.getSql());
            // 添加字段关系节点
            context.getProcessNodeList().add(fieldProcessNode);
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
        ProcessNode processNode = new ProcessNode(NeoConstant.ProcessType.TABLE_PROCESS, sourceNodePkList, targetTableNode.getPk());
        processNode.setType(HandlerConstant.SOURCE_TYPE_SQL_PARSER);
        // 填充 dataSourceName
        LineageUtil.fillingProcessNode(targetTableNode, processNode);
        // 执行的SQL
        processNode.getExtra().put("sql", request.getSql());
        // 添加表关系节点
        context.getProcessNodeList().add(processNode);
    }

    private FieldNode buildFieldNodeNeo4j(SqlRequestContext context, ParseColumnNode parserParseColumnNode) {
        ParseTableNode parseTableNode = Optional.ofNullable(parserParseColumnNode.getOwner()).orElseThrow(() -> new CommonException("column table node is null"));
        String dbName = parseTableNode.getDbName();
        return new FieldNode(context.getDataSourceName(), dbName, parseTableNode.getName(), parserParseColumnNode.getName());
    }

    private TableNode buildTableNodeNeo4j(SqlRequestContext context, ParseTableNode parserParseTableNode) {
        String dbName = parserParseTableNode.getDbName();
        return new TableNode(context.getDataSourceName(), dbName, parserParseTableNode.getName());
    }
}
