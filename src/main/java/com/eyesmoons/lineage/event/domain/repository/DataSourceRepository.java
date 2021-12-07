package com.eyesmoons.lineage.event.domain.repository;

import com.eyesmoons.lineage.event.domain.model.DataSourceNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;


/**
 * 数据源关系
 */
@Repository
public interface DataSourceRepository extends Neo4jRepository<DataSourceNode, String> {

    /**
     * DB_FROM_DATASOURCE Merge: if not exists create,otherwise,update it
     */
    @Query("MATCH (c:DATASOURCE),(s:DB) " +
            "WHERE c.dataSourceName = s.dataSourceName  " +
            "MERGE (s)-[r:db_from_datasource]->(c)")
    void mergeRelWithDb();

}
