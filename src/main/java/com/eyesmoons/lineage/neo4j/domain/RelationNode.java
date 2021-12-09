package com.eyesmoons.lineage.neo4j.domain;

import com.eyesmoons.lineage.contants.NeoConstant;
import com.eyesmoons.lineage.utils.LineageUtil;
import lombok.*;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Transient;

import java.util.List;
import java.util.Optional;

/**
 * 主要是处理多个节点之间的关系
 * Relation节点主键及pk字段生成规则如下：
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
@NodeEntity(NeoConstant.Type.NODE_RELATION)
public class RelationNode extends BaseNodeEntity {

    /**
     * 关系类型
     * 表关系  table_relation - table
     * 字段关系 field_relation - field
     */
    @Builder.Default
    private String relationType = NeoConstant.RelationType.TABLE_RELATION;
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

    public RelationNode(String relationType, List<String> sourceNodePkList, String targetNodePk) {
        Optional.ofNullable(relationType).ifPresent(this::setRelationType);
        this.setSourceNodePkList(sourceNodePkList);
        this.setTargetNodePk(targetNodePk);
        this.setPk(LineageUtil.genRelationNodePk(this));
    }
}
