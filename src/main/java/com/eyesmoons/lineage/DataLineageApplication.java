package com.eyesmoons.lineage;

import com.eyesmoons.lineage.neo4j.dao.SimpleJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = {DataLineageApplication.BASE_PACKAGE}, exclude = {DataSourceAutoConfiguration.class})
@EnableNeo4jRepositories(repositoryBaseClass = SimpleJpaRepositoryImpl.class, basePackages = "com.eyesmoons.lineage.neo4j.dao")
@EntityScan(basePackages = "com.eyesmoons.lineage.neo4j.domain")
public class DataLineageApplication {

    public final static String BASE_PACKAGE = "com.eyesmoons.lineage.*";

    public static void main(String[] args) {
        SpringApplication.run(DataLineageApplication.class, args);
    }

}
