package com.example.shoppingmall.domain.member.application;

import com.example.shoppingmall.domain.jwt.repository.TokenRepository;
import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import com.example.shoppingmall.domain.member.dto.ResponseMemberDTO;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.exception.UserException;
import com.example.shoppingmall.domain.member.repository.MemberRepository;
import com.example.shoppingmall.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}