package com.eyesmoons.lineage.event.handler;

import com.eyesmoons.lineage.event.domain.model.ProcessNode;
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
    private ProcessRepository processRepository;

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
        this.createOrUpdateProcessRelationship(lineageMapping);
    }

    private void createOrUpdateProcessRelationship(LineageContext lineageMapping) {
        List<ProcessNode> processNodeList = lineageMapping.getProcessNodeList();
        if (CollectionUtils.isEmpty(processNodeList)) {
            return;
        }
        processNodeList.forEach(processNode -> {
            // table | fields -> (process_in) -> process
            relationshipService.mergeRelProcessInputs(processNode.getSourceNodePkList(), processNode.getPk());
            processRepository.mergeRelProcessOutput(processNode.getPk(), processNode.getTargetNodePk());
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
        // process
        processRepository.saveAll(lineageMapping.getProcessNodeList());
    }
}
