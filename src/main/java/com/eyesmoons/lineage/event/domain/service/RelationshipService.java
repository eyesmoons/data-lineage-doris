package com.eyesmoons.lineage.event.domain.service;

import java.util.List;

/**
 * 关联创建
 */
public interface RelationshipService {

    /**
     * 批量合并process 以多对一的方式合并去建立关系 TABLE|FIELD -(PROCESS_IN)> Process
     * @param starts 开始节点的列表
     * @param end    结束节点
     */
    void mergeRelProcessInputs(List<String> starts, String end);
}
