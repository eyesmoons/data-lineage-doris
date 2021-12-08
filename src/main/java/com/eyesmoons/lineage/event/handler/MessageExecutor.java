package com.eyesmoons.lineage.event.handler;

import com.eyesmoons.lineage.event.contants.NeoConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * kafka消息处理
 */
@Component
@Slf4j
public class MessageExecutor {

    @Autowired
    private Map<String, BaseMessageHandler> messageHandlerMap;

    @Autowired
    private BaseStorageHandler mergeStorageHandler;

    @KafkaListener(topics = "lineage", containerFactory = "ackContainerFactory", groupId = "lineage_v004")
    public void handleMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            // 获取消息处理器
            BaseMessageHandler messageHandler = messageHandlerMap.get(NeoConstant.SourceType.SQL);
            Objects.requireNonNull(messageHandler, "messageHandler required");
            // 获取消息上下文
            LineageContext lineageContext = messageHandler.handle(record);
            log.info("关系节点：{}", lineageContext.getProcessNodeList());
            log.info("数据库节点：{}", lineageContext.getDbNodeList());
            log.info("表血缘：{}", lineageContext.getTableNodeList());
            log.info("字段血缘：{}", lineageContext.getFieldNodeList());
            Objects.requireNonNull(lineageContext, "lineageContext required");
            // 消息存储
            mergeStorageHandler.handle(lineageContext);
        } catch (Exception e) {
            log.error("SQL解析异常：{}", e.getMessage());
        } finally {
            // 手动提交 offset
            acknowledgment.acknowledge();
        }
    }
}
