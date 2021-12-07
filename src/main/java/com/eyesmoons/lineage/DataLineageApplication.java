package com.eyesmoons.lineage;

import com.eyesmoons.lineage.event.domain.repository.SimpleJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * 启动类
 */
@SpringBootApplication(
        scanBasePackages = {DataLineageApplication.BASE_PACKAGE}, exclude = {DataSourceAutoConfiguration.class}
)
@EnableNeo4jRepositories(repositoryBaseClass = SimpleJpaRepositoryImpl.class, basePackages = "com.eyesmoons.lineage.event.domain.repository")
@EntityScan(basePackages = "com.eyesmoons.lineage.event.domain.model")
@EnableKafka
public class DataLineageApplication {

    public final static String BASE_PACKAGE = "com.eyesmoons.lineage.*";

    public static void main(String[] args) {
        SpringApplication.run(DataLineageApplication.class, args);
    }

}
