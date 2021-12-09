# data-lineage-doris
通过Apache Druid解析Doris Sql数据血缘，包括表血缘和字段血缘，最终在图数据库Neo4j中展现

## Idea食用方法
### 0. 使用flume采集Doris日志到kafka的topic中（lineage）
### 1. 启动kafka
### 2. 启动neo4j（3.5.16）数据库，其他版本需要自己适配
### 3. 修改application.properties相关参数
### 4. 启动DataLineageApplication main 方法

