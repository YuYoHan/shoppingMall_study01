package com.example.shoppingmall.domain.jwt.application;

import com.example.shoppingmall.domain.jwt.dto.TokenDTO;
import org.springframework.http.ResponseEntity;

public interface TokenService {
    // accessToken 발급
    ResponseEntity<TokenDTO> createAccessToken(String refreshToken);
}
