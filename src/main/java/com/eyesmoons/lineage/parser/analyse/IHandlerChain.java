package com.eyesmoons.lineage.parser.analyse;

import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;

/**
 * 血缘流程处理
 */
public interface IHandlerChain {

    /**
     * 处理
     *
     * @param request  request
     * @param response response
     */
    void handle(SqlRequestContext request, SqlResponseContext response);

}
