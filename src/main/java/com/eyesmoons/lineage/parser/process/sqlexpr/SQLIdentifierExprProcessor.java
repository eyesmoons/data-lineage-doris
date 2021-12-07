package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.eyesmoons.lineage.parser.anotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

/**
 * SQLIdentifierExpr
 * eg: select a1
 */
@SQLObjectType(clazz = SQLIdentifierExpr.class)
public class SQLIdentifierExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) expr;
        // 第一层 除了SQLIdentifierExpr 外，其它可看作是需要查找来源字段的
        content.addItem(SqlExprContent.builder().name(sqlIdentifierExpr.getName()).build());
    }
}
