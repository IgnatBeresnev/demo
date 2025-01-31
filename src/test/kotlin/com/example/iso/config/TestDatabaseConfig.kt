package com.example.iso.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

@TestConfiguration
class TestDatabaseConfig {

    companion object {
        private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:14-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .apply { start() }
    }

    @Bean
    fun dataSource(): DataSource {
        return DriverManagerDataSource().apply {
            setDriverClassName("org.postgresql.Driver")
            url = postgres.jdbcUrl
            username = postgres.username
            password = postgres.password
        }
    }
}
