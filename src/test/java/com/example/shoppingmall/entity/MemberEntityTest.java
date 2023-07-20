package com.example.shoppingmall.entity;

import com.example.shoppingmall.entity.member.MemberEntity;
import com.example.shoppingmall.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;


@SpringBootTest
@Transactional
class MemberEntityTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Auditing 테스트")
    // 스프링 시큐리티에서 제공하는 어노테이션으로 @WithMockUser에 지정한 사용자가
    // 로그인한 상태라고 가정하고 테스트를 진행할 수 있습니다.
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