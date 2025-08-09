package com.shoppinglive.api.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainer {

    @Container
    protected static final MySQLContainer<?> mysqlContainer;

    static {
        mysqlContainer = new MySQLContainer<>("mysql:8.4.0");
        mysqlContainer.start();
    }

    @DynamicPropertySource
    protected static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }
}
