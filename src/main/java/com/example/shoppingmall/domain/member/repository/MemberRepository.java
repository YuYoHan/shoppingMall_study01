package com.example.shoppingmall.domain.member.repository;

import com.example.shoppingmall.domain.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByEmail(String email);
    void deleteByMemberId(Long memberId);
    MemberEntity findByNickName(String nickName);
}
