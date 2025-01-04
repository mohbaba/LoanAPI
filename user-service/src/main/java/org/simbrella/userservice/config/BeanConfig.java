package org.simbrella.userservice.config;

import org.keycloak.admin.client.Keycloak;
import org.simbrella.userservice.services.AuthService;
import org.simbrella.userservice.services.AuthServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public AuthService authService(Keycloak keycloak){
        return new AuthServiceImpl(keycloak);
    }
}
