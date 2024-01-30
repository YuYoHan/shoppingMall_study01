package com.example.shoppingmall.domain.jwt.application;

import org.springframework.http.ResponseEntity;

public interface TokenService {
    // accessToken 발급
    ResponseEntity<?> createAccessToken(String refreshToken);
}
