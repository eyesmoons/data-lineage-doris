package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

@SQLObjectType(clazz = SQLIntegerExpr.class)
public class SQLIntegerExprProcessor implements SQLExprProcessor{
    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLIntegerExpr sqlIntegerExpr = (SQLIntegerExpr) expr;
        content.addItem(SqlExprContent.builder().name(sqlIntegerExpr.getParent().toString()).owner(sqlIntegerExpr.getParent().toString()).build());
    }
}
