package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.eyesmoons.lineage.parser.anotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

/**
 * SQLCharExpr
 * eg: select 'str1' + 'st2r' as c
 */
@SQLObjectType(clazz = SQLCharExpr.class)
public class SQLCharExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLCharExpr sqlCharExpr = (SQLCharExpr) expr;
        // TODO 常量解析待开发
    }
}
