package com.eyesmoons.lineage.event.domain.model;

import com.eyesmoons.lineage.event.contants.NodeStatus;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;

import java.time.LocalDateTime;

import static com.eyesmoons.lineage.event.contants.NeoConstant.Node.DEFAULT_DATASOURCE;

/**
 * Node attribute abstraction
 */
@Setter
@Getter
public abstract class BaseNodeEntity extends BaseEntity {

    @Id
    @Index(unique = true)
    private String pk;

    private String status = NodeStatus.ACTIVE.name();
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime = LocalDateTime.now();

    // 图的展示名称
    private String name;

    private String dataSourceName = DEFAULT_DATASOURCE;

    private String dbName;
}
