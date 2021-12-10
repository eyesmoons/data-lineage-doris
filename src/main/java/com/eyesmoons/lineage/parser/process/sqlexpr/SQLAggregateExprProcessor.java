package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

/**
 * SQLAggregateExpr
 * use case:
 * max()
 * min()
 */
@SQLObjectType(clazz = SQLAggregateExpr.class)
@Slf4j
public class SQLAggregateExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLAggregateExpr sqlAggregateExpr = (SQLAggregateExpr) expr;
        log.info("处理聚合函数表达式:{}", sqlAggregateExpr);
        sqlAggregateExpr.getArguments().forEach(sqlExpr -> ProcessorRegister.getSQLExprProcessor(sqlExpr.getClass()).process(dbType, sqlExpr, content));
    }
}
