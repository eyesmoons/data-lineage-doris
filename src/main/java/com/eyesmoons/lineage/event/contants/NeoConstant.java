package com.eyesmoons.lineage.event.contants;

/**
 * Neo常量类
 */
public class NeoConstant {

    private NeoConstant() {}

    public static final String ENTITY_EXTRA_PREFIX = "ext";

    public static class Node {
        public static final String DEFAULT_DATASOURCE = "default";
    }

    public static class Type {
        private Type() {}

        public static final String NODE_DATASOURCE = "DATASOURCE";
        public static final String NODE_DB = "DB";
        public static final String NODE_TABLE = "TABLE";
        public static final String NODE_FIELD = "FIELD";
        public static final String NODE_PROCESS = "PROCESS";
    }

    public static class ProcessType {
        private ProcessType() {}

        public static final String TABLE_PROCESS = "TABLE";
        public static final String FIELD_PROCESS = "FIELD";
    }

    public static class SourceType {
        public static final String SQL = "SQL";
        public static final String SQOOP = "SQOOP";
    }
}
