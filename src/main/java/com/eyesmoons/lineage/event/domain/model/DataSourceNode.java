package com.eyesmoons.lineage.event.domain.model;

import com.eyesmoons.lineage.event.contants.NeoConstant;
import com.eyesmoons.lineage.event.domain.NodeQualifiedName;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Optional;

/**
 * dataSource Node
 * 基本字段使用存储字段
 * dataSourceName
 */
@EqualsAndHashCode(callSuper = true)
@NodeEntity(NeoConstant.Type.NODE_DATASOURCE)
@NoArgsConstructor
public class DataSourceNode extends BaseNodeEntity {

    public DataSourceNode(String dataSourceName) {
        Optional.ofNullable(dataSourceName).ifPresent(this::setDataSourceName);
        // dataSource
        String pk = NodeQualifiedName.ofDataSource(this.getDataSourceName()).toString();
        this.setPk(pk);
    }
}
