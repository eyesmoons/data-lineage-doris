package com.eyesmoons.lineage;

import com.eyesmoons.lineage.neo4j.dao.SimpleJpaRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = {DataLineageApplication.BASE_PACKAGE}, exclude = {DataSourceAutoConfiguration.class})
@EnableNeo4jRepositories(repositoryBaseClass = SimpleJpaRepositoryImpl.class, basePackages = "com.eyesmoons.lineage.neo4j.dao")
@EntityScan(basePackages = "com.eyesmoons.lineage.neo4j.domain")
@Slf4j
public class DataLineageApplication {

    public final static String BASE_PACKAGE = "com.eyesmoons.lineage.*";

    public static void main(String[] args) {
        SpringApplication.run(DataLineageApplication.class, args);
        log.info("DataLineageApplication启动完成，当前时间：{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

}
