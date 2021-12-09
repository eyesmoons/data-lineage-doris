package com.eyesmoons.lineage.listener;

import com.eyesmoons.lineage.contants.Constants;
import com.eyesmoons.lineage.exception.CustomException;
import com.eyesmoons.lineage.model.response.DorisSqlAudit;
import com.eyesmoons.lineage.utils.JSONUtil;
import com.eyesmoons.lineage.contants.NeoConstant;
import com.eyesmoons.lineage.handler.BaseMessageHandler;
import com.eyesmoons.lineage.handler.BaseStorageHandler;
import com.eyesmoons.lineage.model.response.LineageContext;
import com.eyesmoons.lineage.utils.StringUtil;
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
public class FeLogListener {

    @Autowired
    private Map<String, BaseMessageHandler> messageHandlerMap;

    @Autowired
    private BaseStorageHandler mergeStorageHandler;

    //@KafkaListener(topics = "lineage", containerFactory = "ackContainerFactory")
    public void handleMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            // 判断是否为空，并且数字开头
            if (record != null) {
                String records = record.value();
                if (Character.isDigit(records.charAt(0))) {
                    // 转换kafka消息
                    DorisSqlAudit audit = convert2DorisAudit(records);
                    // 获取消息处理器
                    BaseMessageHandler messageHandler = messageHandlerMap.get(NeoConstant.SourceType.SQL);
                    Objects.requireNonNull(messageHandler, "messageHandler required");
                    // 获取消息上下文
                    LineageContext lineageContext = messageHandler.handle(audit);
                    log.info("关系节点：{}", JSONUtil.toJson(lineageContext.getRelationNodeList()));
                    log.info("数据库节点：{}", JSONUtil.toJson(lineageContext.getDbNodeList()));
                    log.info("表血缘：{}", JSONUtil.toJson(lineageContext.getTableNodeList()));
                    log.info("字段血缘：{}", JSONUtil.toJson(lineageContext.getFieldNodeList()));
                    Objects.requireNonNull(lineageContext, "lineageContext required");
                    // 消息存储
                    mergeStorageHandler.handle(lineageContext);
                }
            }
        } catch (Exception e) {
            log.error("kafkaListener错误：{},偏移量是：{}", e.getMessage(), record.offset());
        } finally {
            // 手动提交 offset
            acknowledgment.acknowledge();
        }
    }

    /**
     * 验证消息合法性
     */
    private String[] validMessage(String records) {
        String[] line = records.split("\\|");
        // 验证日志长度
        if (line.length < Constants.LOG_LENGTH) {
            throw new CustomException("日志长度验证失败：", records);
        }

        String status = line[4].substring(6);
        if ("ERR".equalsIgnoreCase(status)) {
            throw new CustomException("不支持ERR类型的SQL：", records);
        }

        // 验证时间字符串
        String timeStr = line[0];
        if (timeStr.length() < 19) {
            throw new CustomException("[time]格式验证失败：", records);
        }

        String queryTimeStr = line[5].substring(5);
        if (!StringUtil.isNumeric(queryTimeStr)) {
            throw new CustomException("[queryTime]格式验证失败：", records);
        }

        String scanBytesStr = line[6].substring(10);
        if (!StringUtil.isNumeric(scanBytesStr)) {
            throw new CustomException("[scanBytes]格式验证失败：", records);
        }

        String scanRowsStr = line[7].substring(9);
        if (!StringUtil.isNumeric(scanRowsStr)) {
            throw new CustomException("[scanRows]格式验证失败：", records);
        }

        String returnRowsStr = line[8].substring(11);
        if (!StringUtil.isNumeric(returnRowsStr)) {
            throw new CustomException("[returnRows]格式验证失败：", records);
        }

        String stmtIdStr = line[9].substring(7);
        if (!StringUtil.isNumeric(stmtIdStr)) {
            throw new CustomException("[stmtId]格式验证失败：", records);
        }

        return line;
    }

    /**
     * 转换kafka消息
     */
    private DorisSqlAudit convert2DorisAudit(String records) {
        String[] line = validMessage(records);
        // 封装fe日志
        DorisSqlAudit audit = new DorisSqlAudit();
        audit.setTime(line[0].substring(0, 19));
        audit.setClientIp(line[1].substring(7));
        audit.setUser(line[2].substring(5).contains(":") ? line[2].substring(5).split(":")[1] : line[2].substring(5));
        audit.setDb((line[3].substring(3).contains(":") ? line[3].substring(3).split(":")[1] : line[3].substring(3)).replace("`", ""));
        audit.setState(line[4].substring(6));
        audit.setQueryTime(Long.parseLong(line[5].substring(5)));
        audit.setScanBytes(Long.parseLong(line[6].substring(10)));
        audit.setScanRows(Long.parseLong(line[7].substring(9)));
        audit.setReturnRows(Long.parseLong(line[8].substring(11)));
        audit.setStmtId(Long.parseLong(line[9].substring(7)));
        audit.setQueryId(line[10].substring(8));
        audit.setIsQuery(Boolean.parseBoolean(line[11].substring(8)));
        audit.setFrontendIp(line[12].substring(5));
        audit.setStmt(line[13].substring(5));
        return audit;
    }
}
