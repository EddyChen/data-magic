package com.example.datamagic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Data Magic API")
                        .version("1.0.0")
                        .description("API for data transformation application")
                        .contact(new Contact()
                                .name("Data Magic Team")
                                .email("support@datamagic.com")));
    }
}