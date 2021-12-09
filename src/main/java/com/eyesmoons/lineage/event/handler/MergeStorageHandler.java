package com.eyesmoons.lineage.event.handler;

import com.eyesmoons.lineage.event.domain.model.RelationNode;
import com.eyesmoons.lineage.event.domain.repository.*;
import com.eyesmoons.lineage.event.domain.service.RelationshipService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 从处理上下文中，取出元数据进行存储
 * merge的方式： 存在则更新，不存在则新增
 */
@Component
public class MergeStorageHandler implements BaseStorageHandler {

    @Autowired
    private DbRepository dbRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private RelationshipService relationshipService;

    @Override
    public void handle(LineageContext lineageMapping) {
        // 创建或更新节点信息
        createOrUpdateNode(lineageMapping);
        // 创建或更新节点关系
        createOrUpdateRelationShip(lineageMapping);
    }

    private void createOrUpdateRelationShip(LineageContext lineageMapping) {
        // dataSource -> dbs
        dataSourceRepository.mergeRelWithDb();
        // db -> tables
        dbRepository.mergeRelWithTable();
        // table -> fields
        tableRepository.mergeRelWithField();
        // 创建输入输出关系
        this.createOrUpdateRelationship(lineageMapping);
    }

    private void createOrUpdateRelationship(LineageContext lineageMapping) {
        List<RelationNode> relationNodeList = lineageMapping.getRelationNodeList();
        if (CollectionUtils.isEmpty(relationNodeList)) {
            return;
        }
        relationNodeList.forEach(relationNode -> {
            // table | fields -> (relation_input) -> relation
            relationshipService.mergeRelRelationInputs(relationNode.getSourceNodePkList(), relationNode.getPk());
            relationRepository.mergeRelRelationOutput(relationNode.getPk(), relationNode.getTargetNodePk());
        });
    }

    private void createOrUpdateNode(LineageContext lineageMapping) {
        // dataSource
        dataSourceRepository.saveAll(lineageMapping.getDataSourceNodeList());
        // db
        dbRepository.saveAll(lineageMapping.getDbNodeList());
        // table
        tableRepository.saveAll(lineageMapping.getTableNodeList());
        // field
        fieldRepository.saveAll(lineageMapping.getFieldNodeList());
        // relation
        relationRepository.saveAll(lineageMapping.getRelationNodeList());
    }
}
