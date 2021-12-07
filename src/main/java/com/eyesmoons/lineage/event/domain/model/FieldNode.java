package com.eyesmoons.lineage.event.domain.model;

import com.eyesmoons.lineage.event.contants.NeoConstant;
import com.eyesmoons.lineage.event.domain.NodeQualifiedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Optional;

/**
 * Node Field
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@NodeEntity(NeoConstant.Type.NODE_FIELD)
public class FieldNode extends BaseNodeEntity {

    private String tableName;
    private String fieldName;

    public FieldNode(String dataSourceName, String dbName, String tableName, String fieldName) {
        Optional.ofNullable(dataSourceName).ifPresent(this::setDataSourceName);
        this.setDbName(dbName);
        this.setTableName(tableName);
        this.setFieldName(fieldName);
        String pk = NodeQualifiedName.ofField(this.getDataSourceName(), this.getDbName(), this.getTableName(), this.getFieldName()).toString();
        this.setPk(pk);
    }
}
