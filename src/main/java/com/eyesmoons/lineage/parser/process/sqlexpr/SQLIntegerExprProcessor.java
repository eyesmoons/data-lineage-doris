package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

@SQLObjectType(clazz = SQLIntegerExpr.class)
@Slf4j
public class SQLIntegerExprProcessor implements SQLExprProcessor{
    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLIntegerExpr sqlIntegerExpr = (SQLIntegerExpr) expr;
        log.info("处理整数表达式:{}", sqlIntegerExpr);
    }
}
