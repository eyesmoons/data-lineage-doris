package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

/**
 * SQLCharExpr
 * eg: select 'str1' + 'st2r' as c
 */
@SQLObjectType(clazz = SQLCharExpr.class)
@Slf4j
public class SQLCharExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLCharExpr sqlCharExpr = (SQLCharExpr) expr;
        log.info("处理常量表达式:{}", sqlCharExpr);
    }
}
