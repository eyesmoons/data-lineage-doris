package com.eyesmoons.lineage.parser.process.sqlselectquery;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.contants.ParserConstant;
import com.eyesmoons.lineage.model.parser.ParseColumnNode;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * MySqlSelectQueryBlock
 * use case:
 * create view view_test as
 * select temp.a1,temp.a2 (
 * select a1,a2 from table1
 * ) temp
 */
@SQLObjectType(clazz = MySqlSelectQueryBlock.class)
@Slf4j
public class MySqlSelectQueryBlockProcessor extends AbstractSQLSelectQueryProcessor {

    @Override
    public void process(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> parent, SQLSelectQuery sqlSelectQuery) {
        MySqlSelectQueryBlock mysqlSelectQueryBlock = (MySqlSelectQueryBlock) sqlSelectQuery;
        // 建立表节点，并关系父级关系
        ParseTableNode proxyTable = ParseTableNode.builder()
                .isVirtualTemp(true)
                .expression(SQLUtils.toSQLString(mysqlSelectQueryBlock))
                .name(ParserConstant.TEMP_TABLE_PREFIX + sequence.incrementAndGet())
                .alias(this.getSubQueryTableSourceAlias(mysqlSelectQueryBlock))
                .build();
        TreeNode<ParseTableNode> proxyNode = TreeNode.of(proxyTable);
        parent.addChild(proxyNode);
        // 生成字段
        List<ParseColumnNode> columnList = mysqlSelectQueryBlock.getSelectList()
                .stream().map(sqlSelectItem -> this.convertSelectItem2Column(dbType, sqlSelectItem))
                .collect(Collectors.toList());
        // 表字段填充到表
        proxyTable.getColumns().addAll(columnList);
        // 继续向下处理
        ProcessorRegister.getTableSourceProcessor(mysqlSelectQueryBlock.getFrom().getClass()).process(dbType, sequence, proxyNode, mysqlSelectQueryBlock.getFrom());
    }

    /**
     * 构建字段，带来源字段
     *
     * @param dbType        数据库类型
     * @param sqlSelectItem SQLSelectItem
     * @return ColumnNode
     */
    private ParseColumnNode convertSelectItem2Column(String dbType, SQLSelectItem sqlSelectItem) {
        //      1. 如果字段由多字段构成
        //        a. 别名不为空
        //  	设置别名为第一层字段，来源字段为列表
        //        b. 别名为空
        //   	    // 现在考虑为多字段必须写上别名
        //        暂时考虑不为空
        //      2. 如果字段由单字段构成
        //        a. 别名为空。
        //   	取出字段名，取出表名。
        //        b. 别名不为空。
        //      3. 考虑来源字段为文本｜int 值
        //         // 现在考虑为字段为文本｜ int值时过滤掉
        //   	设置别名为第一层字段，来源字段为列表
        SQLExpr sqlExpr = sqlSelectItem.getExpr();
        SqlExprContent sqlExprContent = SqlExprContent.of();
        ProcessorRegister.getSQLExprProcessor(sqlExpr.getClass()).process(dbType, sqlExpr, sqlExprContent);
        String alias = sqlSelectItem.getAlias();
        if (sqlExprContent.isEmptyChildren()) {
            String name = sqlExprContent.getName();
            String ownerTable = sqlExprContent.getOwner();
            ParseColumnNode parseColumnNode = ParseColumnNode.builder().name(name).tableName(ownerTable).build();
            if (!StringUtils.isEmpty(alias)) {
                parseColumnNode.setAlias(alias);
            }
            return parseColumnNode;
        }
        ParseColumnNode firstParseColumnNode = ParseColumnNode.builder().alias(alias).build();

        List<SqlExprContent> allItems = sqlExprContent.getAllItems();
        List<ParseColumnNode> sourceParseColumnNodeList = new ArrayList<>();
        allItems.forEach(item -> sourceParseColumnNodeList.add(ParseColumnNode.builder().name(item.getName()).tableName(item.getOwner()).build()));
        firstParseColumnNode.getSourceColumns().addAll(sourceParseColumnNodeList);

        return firstParseColumnNode;
    }

}
