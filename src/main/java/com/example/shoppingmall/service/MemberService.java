package com.example.shoppingmall.service;

import com.example.shoppingmall.entity.MemberEntity;
import com.example.shoppingmall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// 비즈니스 로직을 담당하는 서비스 계층 클래스에
// @Transactional 어노테이션을 선언합니다.
// 로직을 처리하다가 에러가 발생하면
// 변경된 데이터 로직을 처리하기 전으로 콜백해줍니다.
@Transactional
// 빈 주입 방법중 한 개인데
// @NonNull 이나 final 붙은 필드에 생성자를 생성
@RequiredArgsConstructor
// MemberService가 UserDetailsService를 구현합니다.
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberEntity saveMember(MemberEntity member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    // saveMember 메소드가 실행될 때 member 매개변수가 넘어오면
    // validateDuplicateMember(Member member)가 실행되고
    // 매개변수를 받아와서 레포지토리에서 만든 findByEmail을 실행해서
    // 이메일이 있는지 검사하고 findMember에 넣어줍니다.
    // 그리고 빈값이 아니면 IllegalStateException 예외를 발생시켜줍니다.
    private void validateDuplicateMember(MemberEntity member) {
        MemberEntity findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    // UserDetailsService 인터페이스의 loadUserByUsernmae() 메소드를 오버라이딩합니다.
    // 로그인할 유저의 email을 파라미터로 전달 받습니다.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberEntity member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new UsernameNotFoundException(email);
        }

        // UserDetail을 구현하고 있는 User 객체를 반환해줍니다.
        // User 객체를 생성하기 위해서 생성자로 회원의 메일, 비밀번호, role을 파라미터로 넘겨줌
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
