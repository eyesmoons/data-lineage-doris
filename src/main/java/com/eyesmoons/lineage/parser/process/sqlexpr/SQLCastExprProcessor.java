package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

/**
 * 类型转换
 * eg: select col.text as c
 */
@SQLObjectType(clazz = SQLCastExpr.class)
@Slf4j
public class SQLCastExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLCastExpr sqlCastExpr = (SQLCastExpr) expr;
        log.info("处理类型转换表达式:{}", sqlCastExpr);
        SQLExpr castExprExpr = sqlCastExpr.getExpr();
        ProcessorRegister.getSQLExprProcessor(castExprExpr.getClass()).process(dbType, castExprExpr, content);
    }
}
