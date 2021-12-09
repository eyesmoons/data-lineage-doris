package com.eyesmoons.lineage.event.handler;

import com.eyesmoons.lineage.event.domain.model.*;
import com.eyesmoons.lineage.event.handler.sql.SqlMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 血缘上下文
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LineageContext {
    private final List<DataSourceNode> dataSourceNodeList = new ArrayList<>();
    private final List<DbNode> dbNodeList = new ArrayList<>();
    private final List<TableNode> tableNodeList = new ArrayList<>();
    private final List<FieldNode> fieldNodeList = new ArrayList<>();
    private final List<RelationNode> relationNodeList = new ArrayList<>();

    private SqlMessage sqlMessage;
}
