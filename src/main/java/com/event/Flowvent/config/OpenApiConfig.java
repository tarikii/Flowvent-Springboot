package com.event.Flowvent.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flowventOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flowvent API")
                        .version("1.0.0")
                        .description("REST API for managing events, clients and tickets."));
    }
}
