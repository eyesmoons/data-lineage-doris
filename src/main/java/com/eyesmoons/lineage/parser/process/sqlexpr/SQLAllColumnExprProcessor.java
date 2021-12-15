package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

/**
 * select * 的处理
 */
@SQLObjectType(clazz = SQLAllColumnExpr.class)
@Slf4j
public class SQLAllColumnExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLAllColumnExpr sqlAllColumnExpr = (SQLAllColumnExpr) expr;
        content.addItem(SqlExprContent.builder().name("*").build());
    }
}
