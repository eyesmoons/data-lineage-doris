package com.eyesmoons.lineage.listener;

import com.eyesmoons.lineage.contants.NeoConstant;
import com.eyesmoons.lineage.handler.BaseMessageHandler;
import com.eyesmoons.lineage.handler.BaseStorageHandler;
import com.eyesmoons.lineage.model.response.DorisSqlAudit;
import com.eyesmoons.lineage.model.response.LineageContext;
import com.eyesmoons.lineage.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * kafka消息处理，如果使用自定义的topic，并且topic中只有SQL，用这个类
 */
@Component
@Slf4j
public class CustomFeLogListener {

    @Autowired
    private Map<String, BaseMessageHandler> messageHandlerMap;

    @Autowired
    private BaseStorageHandler mergeStorageHandler;

    @KafkaListener(topics = "lineage", containerFactory = "ackContainerFactory")
    public void handleMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            // 判断是否为空，并且数字开头
            if (record != null) {
                String records = record.value();
                // 转换kafka消息
                DorisSqlAudit audit = new DorisSqlAudit();
                audit.setStmt(records);
                // 获取消息处理器
                BaseMessageHandler messageHandler = messageHandlerMap.get(NeoConstant.SourceType.SQL);
                Objects.requireNonNull(messageHandler, "messageHandler required");
                // 获取消息上下文
                LineageContext lineageContext = messageHandler.handle(audit);
                log.info("relation：{}", JSONUtil.toJson(lineageContext.getRelationNodeList()));
                log.info("db：{}", JSONUtil.toJson(lineageContext.getDbNodeList()));
                log.info("table：{}", JSONUtil.toJson(lineageContext.getTableNodeList()));
                log.info("field：{}", JSONUtil.toJson(lineageContext.getFieldNodeList()));
                Objects.requireNonNull(lineageContext, "lineageContext required");
                // 消息存储
                mergeStorageHandler.handle(lineageContext);
            }
        } catch (Exception e) {
            log.error("kafkaListener错误：{},偏移量是：{}", e.getMessage(), record.offset());
            e.printStackTrace();
        } finally {
            // 手动提交 offset
            acknowledgment.acknowledge();
        }
    }
}
