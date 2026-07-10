package com.yashconsulting.eams.auth.controller;

import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.auth.dto.RegisterRequest;
import com.yashconsulting.eams.auth.service.AuthService;
import com.yashconsulting.eams.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Public endpoints for user registration and login. No JWT token required.")
@SecurityRequirements // Explicitly marks as NO security required
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user and get JWT",
            description = """
                    Verifies user credentials (email + password) and returns a JSON Web Token (JWT).
                    The token must be included in the `Authorization` header as `Bearer <token>` 
                    for all subsequent API calls.
                    
                    Token expiration is configurable (default: 24h for dev, 12h for prod).
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated — JWT token returned",
                    content = @Content(
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBlYW1zLmNvbSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTcyMDAwMDAwMCwiZXhwIjoxNzIwMDg2NDAwfQ.signature",
                                      "tokenType": "Bearer"
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request — missing or blank email/password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — invalid email or password",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 401,
                                      "message": "Invalid email or password",
                                      "timestamp": "2026-07-10T12:00:00"
                                    }
                                    """)
                    ))
    })
    public ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "email": "admin@eams.com",
                              "password": "Admin@123"
                            }
                            """))
            )
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user account",
            description = """
                    Creates a new user account with USER role by default.
                    The email must be unique across the system.
                    After registration, use `/api/v1/auth/login` to obtain a JWT token.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request — validation errors",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict — email already registered",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 409,
                                      "message": "Email already exists: user@example.com",
                                      "timestamp": "2026-07-10T12:00:00"
                                    }
                                    """)
                    ))
    })
    public ResponseEntity<Void> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New user registration details",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "firstName": "John",
                              "lastName": "Doe",
                              "email": "john.doe@example.com",
                              "password": "SecurePass@123"
                            }
                            """))
            )
            @Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
