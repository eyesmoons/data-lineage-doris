package com.eyesmoons.lineage.event.domain.model;

import com.eyesmoons.lineage.event.contants.NeoConstant;
import com.eyesmoons.lineage.event.domain.NodeQualifiedName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Optional;

/**
 * Db Node
 * 存储字段：dbNode
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@Data
@NodeEntity(NeoConstant.Type.NODE_DB)
public class DbNode extends BaseNodeEntity {

    private String sql;

    public DbNode(String dataSourceName, String dbName) {
        Optional.ofNullable(dataSourceName).ifPresent(this::setDataSourceName);
        this.setDbName(dbName);
        String pk = NodeQualifiedName.ofDb(this.getDataSourceName(), this.getDbName()).toString();
        this.setPk(pk);
        // displayName
        this.setName(this.getDbName());
    }


    public DbNode(String dbName) {
        this(null, dbName);
    }

}
