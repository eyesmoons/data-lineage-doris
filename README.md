# data-lineage-doris
通过Apache Druid解析Doris Sql数据血缘，包括表血缘和字段血缘，最终在图数据库Neo4j中展现

## IntelliJ IDEA使用方法
### 1. 使用flume采集Doris日志到kafka的topic中（lineage）
### 2. 启动kafka
### 3. 启动neo4j（3.5.16）数据库，其他版本需要自己适配
### 4. 修改application.properties相关参数
### 5. 启动DataLineageApplication main 方法

