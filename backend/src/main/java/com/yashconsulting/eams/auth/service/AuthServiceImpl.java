package com.yashconsulting.eams.auth.service;

import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.security.CustomUserDetailsService;
import com.yashconsulting.eams.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Duration jwtExpiration;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            @Value("${jwt.expiration}") Duration jwtExpiration) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtExpiration = jwtExpiration;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration.toMillis())
                .build();
    }
}
