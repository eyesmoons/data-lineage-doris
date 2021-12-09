package com.eyesmoons.lineage.event.domain.service;

import java.util.List;

/**
 * 关联创建
 */
public interface RelationshipService {

    /**
     * 批量合并relation 以多对一的方式合并去建立关系 table|field -(relation_in)> relation
     * @param starts 开始节点的列表
     * @param end    结束节点
     */
    void mergeRelRelationInputs(List<String> starts, String end);
}
