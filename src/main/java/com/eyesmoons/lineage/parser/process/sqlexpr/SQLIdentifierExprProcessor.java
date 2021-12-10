package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

/**
 * SQLIdentifierExpr
 * eg: select a1
 */
@SQLObjectType(clazz = SQLIdentifierExpr.class)
@Slf4j
public class SQLIdentifierExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) expr;
        log.info("处理修饰词表达式:{}", sqlIdentifierExpr);
        // 第一层 除了SQLIdentifierExpr 外，其它可看作是需要查找来源字段的
        content.addItem(SqlExprContent.builder().name(sqlIdentifierExpr.getName()).build());
    }
}
