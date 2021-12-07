package com.eyesmoons.lineage.parser.analyse.handler;

import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 按照Ordered默认顺序处理
 */
@Component
public class DefaultHandlerChain implements IHandlerChain {

    private final List<IHandler> handlerList;

    public DefaultHandlerChain(List<IHandler> handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public void handle(SqlRequestContext request, SqlResponseContext response) {
        handlerList.forEach(handler -> handler.handleRequest(request, response));
    }
}
