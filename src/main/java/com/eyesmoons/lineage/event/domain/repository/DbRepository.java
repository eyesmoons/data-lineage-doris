package com.eyesmoons.lineage.event.domain.repository;

import com.eyesmoons.lineage.event.domain.model.DbNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * 表关系建立
 */
@Repository
public interface DbRepository extends Neo4jRepository<DbNode, String> {

    /**
     * table_from_db Merge: if not exists create,otherwise,update it
     */
    @Query("MATCH (s:Db),(table:Table) " +
            "WHERE table.dataSourceName = s.dataSourceName " +
            "AND table.dbName = s.dbName " +
            "MERGE (table)-[r:table_from_db]->(s)")
    void mergeRelWithTable();
}
