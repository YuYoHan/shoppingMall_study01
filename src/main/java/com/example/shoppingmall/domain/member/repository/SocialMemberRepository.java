package com.example.shoppingmall.domain.member.repository;

import com.example.shoppingmall.domain.member.entity.SocialMemberEntity;

public interface SocialMemberRepository extends JpaRepository<SocialMemberEntity, Long> {
    SocialMemberEntity findByEmail(String email);
}
