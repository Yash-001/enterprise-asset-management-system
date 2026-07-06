package com.yashconsulting.eams.auth.service;

import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
