package com.eyesmoons.lineage.neo4j.dao;

import com.eyesmoons.lineage.neo4j.domain.TableNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * 字段关系
 */
@Repository
public interface TableRepository extends Neo4jRepository<TableNode, String> {

    /**
     * field_from_table Merge: if not exists create,otherwise,update it
     */
    @Query("MATCH (field:Field),(table:Table) " +
            "WHERE field.dataSourceName = table.dataSourceName " +
            "AND field.dbName = table.dbName " +
            "AND field.tableName = table.tableName " +
            "MERGE (field)-[:field_from_table]->(table)")
    void mergeRelWithField();
}
