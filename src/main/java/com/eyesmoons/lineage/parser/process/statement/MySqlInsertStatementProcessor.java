package com.eyesmoons.lineage.parser.process.statement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * MySqlInsertStatement 处理
 */
@SQLObjectType(clazz = MySqlInsertStatement.class)
@Slf4j
public class MySqlInsertStatementProcessor extends AbstractStatementProcessor {

    @Override
    public void doProcess(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> root, SQLStatement statement) {
        MySqlInsertStatement mysqlInsertStatement = (MySqlInsertStatement) statement;
        SQLExprTableSource sqlExprTableSource = mysqlInsertStatement.getTableSource();
        // 构建根表
        this.constructRootNode(dbType, root, statement, sqlExprTableSource);
        // 获取SQLSelectQuery
        SQLSelectQuery sqlSelectQuery = mysqlInsertStatement.getQuery().getQuery();
        // 执行SQLSelectQuery 查询
        ProcessorRegister.getSQLSelectQueryProcessor(sqlSelectQuery.getClass()).process(dbType, sequence, root, sqlSelectQuery);
    }
}
