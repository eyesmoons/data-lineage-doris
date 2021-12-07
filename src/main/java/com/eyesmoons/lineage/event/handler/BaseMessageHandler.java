package com.eyesmoons.lineage.event.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 基础事件处理
 */
public interface BaseMessageHandler {

    /**
     * 处理事件
     * @param record kafka消息
     */
    LineageContext handle(ConsumerRecord<String, String> record);

}
