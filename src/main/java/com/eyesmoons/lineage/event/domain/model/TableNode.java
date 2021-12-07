package com.eyesmoons.lineage.event.domain.model;

import com.eyesmoons.lineage.event.contants.NeoConstant;
import com.eyesmoons.lineage.event.domain.NodeQualifiedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Optional;

/**
 * Table Node
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@NodeEntity(NeoConstant.Type.NODE_TABLE)
public class TableNode extends BaseNodeEntity {

    private String tableName;

    public TableNode(String dataSourceName, String dbName, String tableName) {
        Optional.ofNullable(dataSourceName).ifPresent(this::setDataSourceName);
        Optional.ofNullable(tableName).ifPresent(this::setTableName);
        Optional.ofNullable(this.getTableName()).ifPresent(this::setName);
        this.setDbName(dbName);
        // dataSource/db/table
        String pk = NodeQualifiedName.ofTable(this.getDataSourceName(), this.getDbName(), this.getTableName()).toString();
        this.setPk(pk);
    }
}
