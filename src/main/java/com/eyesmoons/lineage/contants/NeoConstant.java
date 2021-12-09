package com.eyesmoons.lineage.contants;

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

        public static final String NODE_DATASOURCE = "Datasource";
        public static final String NODE_DB = "Db";
        public static final String NODE_TABLE = "Table";
        public static final String NODE_FIELD = "Field";
        public static final String NODE_RELATION = "Relation";
    }

    public static class RelationType {
        private RelationType() {}

        public static final String TABLE_RELATION = "table";
        public static final String FIELD_RELATION = "field";
    }

    public static class SourceType {
        public static final String SQL = "SQL";
    }
}
