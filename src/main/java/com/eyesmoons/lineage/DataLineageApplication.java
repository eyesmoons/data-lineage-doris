package com.eyesmoons.lineage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = {DataLineageApplication.BASE_PACKAGE}, exclude = {DataSourceAutoConfiguration.class})
@EntityScan(basePackages = "com.eyesmoons.lineage.event.domain.model")
public class DataLineageApplication {

    public final static String BASE_PACKAGE = "com.eyesmoons.lineage.*";

    public static void main(String[] args) {
        SpringApplication.run(DataLineageApplication.class, args);
    }

}
