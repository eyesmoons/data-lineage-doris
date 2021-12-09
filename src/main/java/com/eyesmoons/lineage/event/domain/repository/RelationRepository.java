package com.eyesmoons.lineage.event.domain.repository;


import com.eyesmoons.lineage.event.domain.model.RelationNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 关系节点
 */
@Repository
public interface RelationRepository extends Neo4jRepository<RelationNode, String> {
    /**
     * relation_output Merge: if not exists create,otherwise,update it
     * relation -> field|table
     *
     * @param relationPk relationPk
     * @param tablePk   tablePk
     */
    @Query("MATCH (t),(p:Relation) " +
            "WHERE t.dataSourceName = p.dataSourceName  " +
            "AND t.pk = $tablePk  " +
            "AND p.pk = $relationPk  " +
            "MERGE (p)-[r:relation_output]->(t)")
    void mergeRelRelationOutput(@Param("relationPk") String relationPk, @Param("tablePk") String tablePk);

}
