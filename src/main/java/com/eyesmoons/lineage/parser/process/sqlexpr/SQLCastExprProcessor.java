package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

/**
 * 类型转换
 * eg: select col::text as c
 */
@SQLObjectType(clazz = SQLCastExpr.class)
public class SQLCastExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLCastExpr sqlCastExpr = (SQLCastExpr) expr;
        SQLExpr castExprExpr = sqlCastExpr.getExpr();
        ProcessorRegister.getSQLExprProcessor(castExprExpr.getClass()).process(dbType, castExprExpr, content);
    }
}
