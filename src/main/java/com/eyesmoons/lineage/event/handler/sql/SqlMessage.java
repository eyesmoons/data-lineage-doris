package com.eyesmoons.lineage.event.handler.sql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SQL对象封装
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SqlMessage {

    private String dbType;

    private String dataSourceName;

    private String tableName;

    private String sql;

    private Date createTime;
}
