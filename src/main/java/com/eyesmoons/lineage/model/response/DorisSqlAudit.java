package com.eyesmoons.lineage.model.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class DorisSqlAudit implements Serializable {

    private static final long serialVersionUID = 3403468816121863235L;

    /** Unique query id */
    private String queryId;

    /** Query start time */
    private String time;

    /** Client IP */
    private String clientIp;

    /** User name */
    private String user;

    /** Database of this query */
    private String db;

    /** Query result state. EOF, ERR, OK */
    private String state;

    /** Query execution time in millisecond */
    private Long queryTime;

    /** Total scan bytes of this query */
    private Long scanBytes;

    /** Total scan rows of this query */
    private Long scanRows;

    /** Returned rows of this query */
    private Long returnRows;

    /** An incremental id of statement */
    private Long stmtId;

    /** Is this statemt a query. 1 or 0 */
    private Boolean isQuery;

    /** Frontend ip of executing this statement */
    private String frontendIp;

    /** The original statement, trimed if longer than 2048 bytes */
    private String stmt;
}
