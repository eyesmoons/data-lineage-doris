package com.eyesmoons.lineage.handler;

import com.eyesmoons.lineage.model.response.LineageContext;

/**
 * 存储处理器
 */
public interface BaseStorageHandler {

    void handle(LineageContext context);
}
