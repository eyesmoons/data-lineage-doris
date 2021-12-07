package com.eyesmoons.lineage.event.domain.repository;


import com.eyesmoons.lineage.event.domain.model.ProcessNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 关系节点
 */
@Repository
public interface ProcessRepository extends Neo4jRepository<ProcessNode, String> {
    /**
     * PROCESS_OUTPUT Merge: if not exists create,otherwise,update it
     * process -> field|table
     *
     * @param processPk processPk
     * @param tablePk   tablePk
     */
    @Query("MATCH (t),(p:PROCESS) " +
            "WHERE t.dataSourceName = p.dataSourceName  " +
            "AND t.pk = $tablePk  " +
            "AND p.pk = $processPk  " +
            "MERGE (p)-[r:process_output]->(t)")
    void mergeRelProcessOutput(@Param("processPk") String processPk, @Param("tablePk") String tablePk);

}
