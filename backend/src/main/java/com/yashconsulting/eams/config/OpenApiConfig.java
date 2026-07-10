package com.yashconsulting.eams.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Enterprise Asset Management System API",
                version = "1.0.0",
                description = """
                        REST API for managing enterprise assets, maintenance schedules, work orders, 
                        inventory, purchase orders, documents, notifications, and audit trails.
                        
                        ## Authentication
                        All endpoints except `/api/v1/auth/**` require a valid JWT token.
                        Use the `/api/v1/auth/login` endpoint to obtain a token, then include it 
                        in the `Authorization` header as `Bearer <token>`.
                        
                        ## Roles
                        - **ADMIN** — Full access to all operations
                        - **MANAGER** — CRUD on assets, work orders, POs, notifications, documents
                        - **USER** — Read-only access + own notifications + document upload
                        """,
                contact = @Contact(
                        name = "Yash Consulting",
                        email = "contact@yashconsulting.com",
                        url = "https://github.com/yashconsulting"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development"),
                @Server(url = "https://eams-backend.onrender.com", description = "Production")
        },
        security = @SecurityRequirement(name = "Bearer JWT")
)
@SecurityScheme(
        name = "Bearer JWT",
        description = "Enter your JWT token obtained from `/api/v1/auth/login`",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
