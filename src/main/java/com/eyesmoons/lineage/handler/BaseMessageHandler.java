package com.eyesmoons.lineage.handler;

import com.eyesmoons.lineage.model.response.DorisSqlAudit;
import com.eyesmoons.lineage.model.response.LineageContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 事件处理器
 */
public interface BaseMessageHandler {

    LineageContext handle(DorisSqlAudit audit);
}
