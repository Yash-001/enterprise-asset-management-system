package com.yashconsulting.eams.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI enterpriseAssetManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Asset Management API")
                        .version("v1.0")
                        .description("REST APIs for Enterprise Asset Management System.")
                        .contact(new Contact()
                                .name("Yash Ranjan"))
                        .license(new License()
                                .name("MIT")));
    }
}
