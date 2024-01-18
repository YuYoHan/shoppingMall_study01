package com.example.shoppingmall.domain.member.application;

import com.example.shoppingmall.domain.jwt.dto.TokenDTO;
import com.example.shoppingmall.domain.jwt.entity.TokenEntity;
import com.example.shoppingmall.domain.jwt.repository.TokenRepository;
import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import com.example.shoppingmall.domain.member.dto.ResponseMemberDTO;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.entity.Role;
import com.example.shoppingmall.domain.member.exception.UserException;
import com.example.shoppingmall.domain.member.repository.MemberRepository;
import com.example.shoppingmall.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;


    @Override
    public ResponseEntity<?> signUp(RequestMemberDTO member) {
        String encodePw = passwordEncoder.encode(member.getMemberPw());
        try {
            log.info("email : " + member.getEmail());
            log.info("nickName : " + member.getNickName());

            if(!emailCheck(member.getEmail())) {
                throw new UserException("이미 존재하는 이미엘이 있습니다.");
            }

            if(!nickNameCheck(member.getNickName())) {
                throw new UserException("이미 존재하는 닉네임이 있습니다.");
            }

            MemberEntity saveMember = MemberEntity.saveMember(member, encodePw);
            log.info("유저 : " + saveMember);
            MemberEntity save = memberRepository.save(saveMember);
            return ResponseEntity.ok().body(ResponseMemberDTO.changeDTO(save));
        } catch (Exception e) {
            log.error("에러 발생 : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 중복체크
    @Override
    public boolean emailCheck(String email) {
        MemberEntity findEmail = memberRepository.findByEmail(email);
        if (findEmail == null) {
            return true;
        } else {
            return false;
        }
    }

    // 닉네임 체크
    @Override
    public boolean nickNameCheck(String nickName) {
        MemberEntity findNickName = memberRepository.findByNickName(nickName);
        if (findNickName == null) {
            return true;
        } else {
            return false;
        }
    }

    // 회원 조회
    @Override
    public ResponseMemberDTO search(Long memberId) {
        try {
            MemberEntity findUser = memberRepository.findById(memberId)
                    .orElseThrow(EntityNotFoundException::new);
            return ResponseMemberDTO.changeDTO(findUser);
        } catch (EntityNotFoundException e) {
            log.error("에러 : " + e.getMessage());
            throw new EntityNotFoundException("회원이 존재 하지 않습니다.");
        }
    }

    // 로그인

    @Override
    public ResponseEntity<?> login(String memberEmail, String memberPw) {
        try {
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);

            if(findUser != null) {
                if(passwordEncoder.matches(memberPw, findUser.getMemberPw())) {
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(memberEmail, memberPw);
                    List<GrantedAuthority> authoritiesForUser = getAuthoritiesForUser(findUser);

                    TokenDTO token = jwtProvider.createToken(authentication, authoritiesForUser, findUser.getMemberId());
                    TokenEntity findToken = tokenRepository.findByMemberEmail(token.getMemberEmail());
                    TokenEntity tokenEntity;
                    if(findToken == null) {
                        tokenEntity = TokenEntity.changeEntity(token);
                    } else {
                        tokenEntity = TokenEntity.updateToken(findToken.getId(), token);
                    }
                    tokenRepository.save(tokenEntity);
                    return ResponseEntity.ok().body(token);
                }
            }
            throw new EntityNotFoundException("회원이 존재하지 않습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // 회원의 권한을 GrantedAuthority타입으로 반환하는 메소드
    private List<GrantedAuthority> getAuthoritiesForUser(MemberEntity member) {
        Role memberRole = member.getMemberRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + memberRole.name()));
        log.info("role : " + authorities);
        return authorities;
    }
}