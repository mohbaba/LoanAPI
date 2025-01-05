package org.simbrella.loanservice;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.profiles.active=test")
@Testcontainers
public abstract class AbstractTestContainer {

    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb");

    static {
        POSTGRESQL_CONTAINER.start();
    }

    @BeforeAll
    static void setupMocks() {
        // Mock behavior for the DiscoveryClient can be added here if needed globally
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

//         registry.add("eureka.client.enabled", () -> "false");
//        registry.add("eureka.client.register-with-eureka", () -> "false");
//        registry.add("eureka.client.fetch-registry", () -> "false");
    }

    @Configuration
    static class MockEurekaConfig {
        @Bean
        public DiscoveryClient mockDiscoveryClient() {
            DiscoveryClient mockClient = mock(DiscoveryClient.class);

            when(mockClient.getServices()).thenReturn(Collections.emptyList());

            when(mockClient.getInstances("MY-SERVICE")).thenReturn(Collections.emptyList());

            return mockClient;
        }
    }
}
