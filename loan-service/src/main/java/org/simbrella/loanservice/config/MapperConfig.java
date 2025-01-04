package org.simbrella.loanservice.config;

import org.modelmapper.ModelMapper;
import org.simbrella.loanservice.repositories.LoanRepository;
import org.simbrella.loanservice.services.LoanService;
import org.simbrella.loanservice.services.LoanServiceImpl;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public LoanService loanService(LoanRepository loanRepository, ModelMapper modelMapper, WebClient webClient, DiscoveryClient discoveryClient){
        return new LoanServiceImpl(loanRepository, modelMapper, webClient, discoveryClient);
    }
}
