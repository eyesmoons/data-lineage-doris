package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

@SQLObjectType(clazz = SQLNullExpr.class)
public class SQLNullExprProcessor implements SQLExprProcessor{
    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {

    }
}
