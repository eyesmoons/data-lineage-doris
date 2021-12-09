package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

/**
 * 字段前缀解析
 * eg: select dwd.a1
 */
@SQLObjectType(clazz = SQLPropertyExpr.class)
public class SQLPropertyExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) expr;
        content.addItem(SqlExprContent.builder().name(sqlPropertyExpr.getName()).owner(sqlPropertyExpr.getOwnernName()).build());
    }
}
