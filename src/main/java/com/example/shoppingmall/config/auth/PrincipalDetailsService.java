package com.example.shoppingmall.config.auth;

import com.example.shoppingmall.entity.member.MemberEntity;
import com.example.shoppingmall.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8080/login ← 이 때 동작을 함
// 일반 로그인 시 회원 가입한 정보를 가지고 조회해서 찾아와서
// PrincipalDetails 에 넘겨준다. PrincipalDetails 는 이 정보를 가지고
// JwtProvider 에서 토큰을 생성하는데 정보를 제공한다.
// 그래서 JwtProvider 에서 authentication.getName() 이나 UserDetails.getUserName()
// 같은 것을 하면 정보를 가져올 수 있는 것이다.

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 조회
        MemberEntity member = memberRepository.findByUserEmail(username);
        log.info("user in PrincipalDetailsService : " + member);

        if(member == null) {
            throw new UsernameNotFoundException("하당 사용자가 없습니다.");
        }
        return new PrincipalDetails(member);
    }
}
