package com.eyesmoons.lineage.event.domain.model;

import com.eyesmoons.lineage.event.contants.NeoConstant;
import com.eyesmoons.lineage.event.util.LineageUtil;
import lombok.*;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Transient;

import java.util.List;
import java.util.Optional;

/**
 * 主要是处理多个节点之间的关系
 * Process节点主键及pk字段生成规则如下：
 * 示例：
 * sourceNodePkList：x,y
 * targetNodePk: z
 * md5(targetNodePk + sourceNodePkList排序后使用下划线'_'连接)
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@NodeEntity(NeoConstant.Type.NODE_PROCESS)
public class ProcessNode extends BaseNodeEntity {

    /**
     * 关系类型
     * default: 表关系  TABLE_PROCESS - TABLE
     * 字段关系 FIELD_PROCESS - FIELD
     */
    @Builder.Default
    private String processType = NeoConstant.ProcessType.TABLE_PROCESS;
    /**
     * 来源类型 sqoop|sql
     */
    private String type;

    /**
     * 存储Node.pk
     * 示例：
     * create table A as select B.col1, C.col2 from B,C where xxx
     * sourceNode: B, C
     * targetNode: A
     */
    @Transient
    private List<String> sourceNodePkList;

    @Transient
    private String targetNodePk;

    public ProcessNode(String processType, List<String> sourceNodePkList, String targetNodePk) {
        Optional.ofNullable(processType).ifPresent(this::setProcessType);
        this.setSourceNodePkList(sourceNodePkList);
        this.setTargetNodePk(targetNodePk);
        this.setPk(LineageUtil.genProcessNodePk(this));
    }
}
