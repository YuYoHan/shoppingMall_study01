package com.example.shoppingmall.entity;

import com.example.shoppingmall.dto.MemberDTO;
import com.example.shoppingmall.repository.CartRepository;
import com.example.shoppingmall.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartTest {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    // 회원 엔티티를 생성하는 메소드
    public MemberEntity createMember() {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail("test@gamil.com");
        memberDTO.setName("테스터");
        memberDTO.setAddr("서울시 마포구 합정동");
        memberDTO.setPassword("1234");
        return MemberEntity.createMember(memberDTO, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest() {
        MemberEntity member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        // JPA는 영속성 컨텍스트에 데이터를 저장 후 트랜잭션이 끝날 때
        // flush()를 호출하여 데이터베이스에 반영한다.
        // 회원 엔티티와 장바구니 엔티티를 영속성 컨텍스트에 저장 후
        // 엔티티 매니저로부터 강제로 flush()를 호출하여 데이터베이스 반영합니다.
        em.flush();;
        // JPA는 영속성 컨텍스트로부터 엔티티를 조회 후 영속성 컨텍스에 엔티티가 없을 경우
        // 데이터베이스를 조회합니다. 실제 데이터베이스에서 장바구니 엔티티를 가지고 올 때 회원 엔티티도
        // 같이 가지고 오는지 보기 위해서 영속성 컨텍스트를 비워주겠습니다.
        em.clear();

        // 저장된 장바구니 엔티티를 조회합니다.
        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new);
        // 처음에 저장한 member 엔티티의 id와 savedCart에 매핑된 member 엔티티의 id 비교
        assertEquals(savedCart.getMember().getId(), member.getId());
    }
}