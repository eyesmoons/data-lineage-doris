package com.eyesmoons.lineage.event.domain.service.impl;

import com.eyesmoons.lineage.common.exception.CommonException;
import com.eyesmoons.lineage.common.util.StringPool;
import com.eyesmoons.lineage.event.domain.service.RelationshipService;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PROCESS_IN  TABLE|FIELD -> (PROCESS_IN) -> Process
 * 以多对一的方式合并去建立关系
 */
@Service
@Slf4j
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    private SessionFactory sessionFactory;

    private static final String DELIMITER = "','";

    /**
     * s -> (n:1) e
     */
    private static final String MERGE_PROCESS_INPUTS = "MATCH (s),(e:PROCESS) WHERE " +
            "s.pk in [%s] and e.pk = '%s'  "
            + "MERGE (s)-[r:process_in]->(e) set r.timestamp=timestamp() "
            + "RETURN id(r) as relId";

    @Override
    public void mergeRelProcessInputs(List<String> starts, String end) {
        if (CollectionUtils.isEmpty(starts)) {
            return;
        }

        // （'pk1','pk2','pk3'）
        String cql = String.format( MERGE_PROCESS_INPUTS, starts.stream().collect(Collectors.joining(DELIMITER, StringPool.SINGLE_QUOTE, StringPool.SINGLE_QUOTE)), end);
        log.debug("execute cql is [{}]", cql);
        Session session = sessionFactory.openSession();
        Result result = session.query(cql, Collections.emptyMap());
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        result.forEach(resultMapList::add);
        if (resultMapList.size() != starts.size()) {
            log.error("execute recode num [{}] != success num [{}]. maybe neo4j question. current cql is [{}]", starts.size(), resultMapList.size(), cql);
            // record
            throw new CommonException("execute recode num [%s] != success num [%s]. maybe neo4j question", starts.size(), resultMapList.size());
        }
    }
}
