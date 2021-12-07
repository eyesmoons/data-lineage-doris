package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.eyesmoons.lineage.parser.process.SqlExprContent;

/**
 * SQLExpr 处理器
 */
public interface SQLExprProcessor {

    /**
     * SQLExpr 内容提取
     * @param dbType  数据库类型
     * @param expr    SQLExpr
     * @param content SqlExprContent
     */
    void process(String dbType, SQLExpr expr, SqlExprContent content);
}
