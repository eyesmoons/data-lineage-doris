package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SQLMethodInvokeExpr
 * eg select substring(a1,a2) as c
 */
@SQLObjectType(clazz = SQLMethodInvokeExpr.class)
@Slf4j
public class SQLMethodInvokeExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLMethodInvokeExpr sqlMethodInvokeExpr = (SQLMethodInvokeExpr) expr;
        log.info("处理函数表达式:{}", sqlMethodInvokeExpr);
        this.getAllCaseExprChild(sqlMethodInvokeExpr).forEach(ep -> ProcessorRegister.getSQLExprProcessor(ep.getClass()).process(dbType, ep, content));
    }

    private List<SQLExpr> getAllCaseExprChild(SQLMethodInvokeExpr expr) {
        List<SQLExpr> list = new ArrayList<>();
        Optional.ofNullable(expr.getOwner()).ifPresent(list::add);
        Optional.ofNullable(expr.getFrom()).ifPresent(list::add);
        Optional.ofNullable(expr.getUsing()).ifPresent(list::add);
        Optional.ofNullable(expr.getFor()).ifPresent(list::add);
        if (CollectionUtils.isNotEmpty(expr.getArguments())) {
            list.addAll(expr.getArguments());
        }
        return list;
    }
}
