package com.example.demo;

import com.yugabyte.data.jdbc.datasource.YugabyteTransactionManager;
import com.yugabyte.data.jdbc.repository.config.AbstractYugabyteJdbcConfiguration;
import com.yugabyte.data.jdbc.repository.config.EnableYsqlRepositories;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

        @Configuration
    @EnableYsqlRepositories
    static class DatabaseConfiguration extends AbstractYugabyteJdbcConfiguration {

        @Bean
        DataSource dataSource() {
            String hostName = "localhost";
            String port = "5433";

            Properties poolProperties = new Properties();
            poolProperties.setProperty("dataSourceClassName",
                    "com.yugabyte.ysql.YBClusterAwareDataSource");
            poolProperties.setProperty("dataSource.serverName", hostName);
            poolProperties.setProperty("dataSource.portNumber", port);
            poolProperties.setProperty("dataSource.user", "yugabyte");
            poolProperties.setProperty("dataSource.password", "yugabyte");
//            poolProperties.setProperty("dataSource.loadBalance", "true");

            HikariConfig hikariConfig = new HikariConfig(poolProperties);
            DataSource ybClusterAwareDataSource = new HikariDataSource(hikariConfig);
            return ybClusterAwareDataSource;
        }

        @Bean
        JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {

            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            return jdbcTemplate;
        }

        @Bean
        NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        TransactionManager transactionManager(DataSource dataSource) {
            return new YugabyteTransactionManager(dataSource);
        }
    }
}
