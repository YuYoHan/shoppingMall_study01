package com.example.shoppingmall.entity;

import com.example.shoppingmall.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberEntityTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Auditing 테스트")
    @WithMockUser(username = "gildong", roles = "USER")
    public void auditingTest() {
        MemberEntity member = new MemberEntity();
        memberRepository.save(member);

        em.flush();
        em.clear();

        MemberEntity member1 = memberRepository.findById(member.getId())
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("register time : " + member1.getRegTime());
        System.out.println("update time : " + member1.getUpdateTime());
        System.out.println("create time : " + member1.getCreatedBy());
        System.out.println("modify time : " + member1.getModifiedBy());
    }



}