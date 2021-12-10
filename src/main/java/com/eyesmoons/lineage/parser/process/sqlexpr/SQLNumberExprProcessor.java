package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

@SQLObjectType(clazz = SQLNumberExpr.class)
@Slf4j
public class SQLNumberExprProcessor implements SQLExprProcessor{
    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLNumberExpr sqlNumberExpr = (SQLNumberExpr) expr;
        log.info("处理数字表达式:{}", sqlNumberExpr);
    }
}
