package com.example.shoppingmall.domain.jwt.repository;

import com.example.shoppingmall.domain.jwt.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    TokenEntity findByMemberEmail(String memberEmail);
}
