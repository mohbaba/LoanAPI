package org.simbrella.loanservice.services;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanServiceImplTest {

    @Autowired
    private LoanService loanService;
    @Autowired
    private DiscoveryClient discoveryClient;

//    public User getUser(String email) {
//        // Get the actual service name from Eureka
//        String serviceName = "USER-SERVICE"; // You can hardcode this if you know it
//        String url = getServiceUrl(serviceName) + "/api/v1/users/{email}";
//
//        return webClient.get()
//                .uri(url, email) // Dynamically created URL with the service's address
//                .retrieve()
//                .bodyToMono(User.class)
//                .block(); // Blocking for simplicity; you can adjust for async handling
//    }

    private String getServiceUrl(String serviceName) {
        // Retrieve the URI for the service registered in Eureka
        return discoveryClient.getInstances(serviceName)
                .stream()
                .findFirst()
                .map(instance -> instance.getUri().toString())
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void applyForLoan() {
    }

    @Test
    void getUser() {
//        System.out.println(getServiceUrl("USER-SERVICE"));
        System.out.println(loanService.getUser("ajaybaba@email.com"));
    }
}