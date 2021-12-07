package com.eyesmoons.lineage.parser.process.tablesource;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.eyesmoons.lineage.parser.anotation.SQLObjectType;
import com.eyesmoons.lineage.parser.model.TableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLExprTableSource
 */
@SQLObjectType(clazz = SQLExprTableSource.class)
public class SQLExprTableSourceProcessor implements TableSourceProcessor {

    @Override
    public void process(String dbType, AtomicInteger sequence, TreeNode<TableNode> parent, SQLTableSource sqlTableSource) {
        TableNode proxyTable = TableNode.builder()
                .expression(SQLUtils.toSQLString(sqlTableSource))
                .alias(sqlTableSource.getAlias())
                .build();
        TreeNode<TableNode> proxyNode = TreeNode.of(proxyTable);
        parent.addChild(proxyNode);

        SQLExpr sqlExprTableSourceExpr = ((SQLExprTableSource) sqlTableSource).getExpr();
        SqlExprContent sqlExprContent = new SqlExprContent();
        ProcessorRegister.getSQLExprProcessor(sqlExprTableSourceExpr.getClass()).process(dbType, sqlExprTableSourceExpr, sqlExprContent);
        proxyTable.setName(sqlExprContent.getName());
        proxyTable.setDbName(sqlExprContent.getOwner());
    }
}
