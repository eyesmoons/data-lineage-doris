package com.eyesmoons.lineage.parser.process.statement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLCreateViewStatement 处理
 * eg：
 * create view view_test as
 * select temp.a1,temp.a2 (
 * select a1,a2 from table1
 * ) temp
 */
@SQLObjectType(clazz = SQLCreateViewStatement.class)
public class SQLCreateViewStatementProcessor extends AbstractStatementProcessor {

    @Override
    public void doProcess(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> root, SQLStatement statement) {
        SQLCreateViewStatement createViewStatement = (SQLCreateViewStatement) statement;
        SQLExprTableSource sqlExprTableSource = createViewStatement.getTableSource();
        // 构建根表
        this.constructRootNode(dbType, root, statement, sqlExprTableSource);
        // 获取SQLSelectQuery
        SQLSelectQuery sqlSelectQuery = createViewStatement.getSubQuery().getQuery();
        ProcessorRegister.getSQLSelectQueryProcessor(sqlSelectQuery.getClass()).process(dbType, sequence, root, sqlSelectQuery);
    }
}
