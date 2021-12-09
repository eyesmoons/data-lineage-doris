package com.eyesmoons.lineage.neo4j.dao;

import com.eyesmoons.lineage.neo4j.domain.DataSourceNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;


/**
 * 数据源关系
 */
@Repository
public interface DataSourceRepository extends Neo4jRepository<DataSourceNode, String> {

    /**
     * db_from_datasource Merge: if not exists create,otherwise,update it
     */
    @Query("MATCH (c:Datasource),(s:Db) " +
            "WHERE c.dataSourceName = s.dataSourceName  " +
            "MERGE (s)-[r:db_from_datasource]->(c)")
    void mergeRelWithDb();

}
