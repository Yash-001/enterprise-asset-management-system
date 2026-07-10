package com.yashconsulting.eams.auth.service;

import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.auth.dto.RegisterRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);
}
