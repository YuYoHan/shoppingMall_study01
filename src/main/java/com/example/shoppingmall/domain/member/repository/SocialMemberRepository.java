package com.example.shoppingmall.domain.member.repository;

import com.example.shoppingmall.domain.member.entity.SocialMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialMemberRepository extends JpaRepository<SocialMemberEntity, Long> {
    SocialMemberEntity findByEmail(String email);
}
