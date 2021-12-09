package com.eyesmoons.lineage.event.domain.repository;

import com.eyesmoons.lineage.event.domain.model.FieldNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 字段关系
 */
@Repository
public interface FieldRepository extends Neo4jRepository<FieldNode, String> {

    @Query("MATCH (f:FIELD),(p:RELATION) " +
            "WHERE f.pk = $sourceFieldNodePk " +
            "and p.pk= $relationNodePk " +
            "MERGE (f)-[r:relation_input]->(p)")
    void mergeRelWithRelation(@Param("sourceFieldNodePk") String sourceFieldNodePk, @Param("relationNodePk") String relationNodePk);

}
