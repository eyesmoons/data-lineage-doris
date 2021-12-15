package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

/**
 * SQLBinaryOpExpr
 * use case
 * select ((a1+a2)-a3)*a4/a5 as a
 */
@SQLObjectType(clazz = SQLBinaryOpExpr.class)
@Slf4j
public class SQLBinaryOpExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) expr;
        ProcessorRegister.getSQLExprProcessor(sqlBinaryOpExpr.getLeft().getClass()).process(dbType, sqlBinaryOpExpr.getLeft(), content);
        ProcessorRegister.getSQLExprProcessor(sqlBinaryOpExpr.getRight().getClass()).process(dbType, sqlBinaryOpExpr.getRight(), content);
    }
}
