package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

/**
 * select * 的处理
 */
@SQLObjectType(clazz = SQLAllColumnExpr.class)
public class SQLAllColumnExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        // 需后置处理，节点处理时由下至上
        content.addItem(SqlExprContent.builder().name("*").build());
    }
}
