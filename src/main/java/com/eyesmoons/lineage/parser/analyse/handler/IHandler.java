package com.eyesmoons.lineage.parser.analyse.handler;


import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;

/**
 * 血缘处理
 */
public interface IHandler {

    /**
     * 表血缘处理
     * @param request  sql 请求
     * @param response 响应
     */
    void handleRequest(SqlRequestContext request, SqlResponseContext response);
}
