package com.eyesmoons.lineage.event.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

/**
 * neo4j 节点封装
 */
public class NodeQualifiedName {

    private final String dataSourceName;

    private final String dbName;

    private final String tableName;

    private final String fieldName;

    private String qualifiedName;

    private NodeQualifiedName(String dataSourceName, String dbName, String tableName, String fieldName) {
        this.dataSourceName = standardizeOptional(dataSourceName);
        this.dbName = standardizeOptional(dbName);
        this.tableName = standardizeOptional(tableName);
        this.fieldName = standardizeOptional(fieldName);
    }

    private static String standardizeOptional(final String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase();
    }

    public static NodeQualifiedName ofDataSource(@NonNull final String dataSourceName) {
        return new NodeQualifiedName(dataSourceName, null, null, null);
    }

    public static NodeQualifiedName ofDb(@NonNull final String dataSourceName, @NonNull final String dbName) {
        return new NodeQualifiedName(dataSourceName, dbName, null, null);
    }

    public static NodeQualifiedName ofTable(@NonNull final String dataSourceName, @NonNull final String dbName, @NonNull final String tableName) {
        return new NodeQualifiedName(dataSourceName, dbName, tableName, null);
    }

    public static NodeQualifiedName ofField(@NonNull final String dataSourceName, @NonNull final String dbName, @NonNull final String tableName, @NonNull final String fieldName) {
        return new NodeQualifiedName(dataSourceName, dbName, tableName, fieldName);
    }

    @Override
    public String toString() {
        if (qualifiedName == null) {
            final StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotEmpty(dataSourceName)) {
                sb.append("/").append(dataSourceName);
            }
            if (StringUtils.isNotEmpty(dbName)) {
                sb.append('/').append(dbName);
            }
            if (StringUtils.isNotEmpty(tableName)) {
                sb.append('/').append(tableName);
            }
            if (StringUtils.isNotEmpty(fieldName)) {
                sb.append('/').append(fieldName);
            }
            qualifiedName = sb.toString();
        }
        return qualifiedName;
    }
}
