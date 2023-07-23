package com.example.shoppingmall.controller.member;

import com.example.shoppingmall.dto.jwt.TokenDTO;
import com.example.shoppingmall.dto.member.MemberDTO;
import com.example.shoppingmall.entity.member.MemberEntity;
import com.example.shoppingmall.service.jwt.RefreshTokenService;
import com.example.shoppingmall.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    // 회원 가입
    @PostMapping("/api/v1/users/")
    // BindingResult 타입의 매개변수를 지정하면 BindingResult 매개 변수가 입력값 검증 예외를 처리한다.
    public ResponseEntity<?> join(@Validated @RequestBody MemberDTO memberDTO,
                                  BindingResult result) throws Exception{

        // 입력값 검증 예외가 발생하면 예외 메시지를 응답한다.
        if(result.hasErrors()) {
            log.info("BindingResult error : " + result.hasErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getClass().getSimpleName());
        }

        try {
            String join = memberService.signUp(memberDTO);
            return ResponseEntity.ok().body(join);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 회원 조회
    @GetMapping("/api/v1/users/{userId}")
    public ResponseEntity<MemberDTO> search(@PathVariable Long userId) throws Exception {
        try {
            MemberDTO search = memberService.search(userId);
            return ResponseEntity.ok().body(search);
        } catch (NullPointerException e) {
            log.info("회원이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 로그인
    @PostMapping("/api/v1/users/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO memberDTO) throws Exception {
        log.info("member : " + memberDTO);
        try {
            log.info("-----------------");

            ResponseEntity<TokenDTO> login =
                    memberService.login(memberDTO.getUserEmail(), memberDTO.getUserPw());
            log.info("login : " + login);

            return ResponseEntity.ok().body(login);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("문제가 있습니다");
        }
    }

    // refresh로 access 토큰 재발급
    // @RequsetHeader"Authorization")은 Authorization 헤더에서 값을 추출합니다.
    // 일반적으로 리프레시 토큰은 Authorization 헤더의 값으로 전달되며,
    // Bearer <token> 형식을 따르는 경우가 일반적입니다. 여기서 <token> 부분이 실제 리프레시 토큰입니다
    // 로 추출하면 다음과 같이 문자열로 나온다.
    // Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) throws Exception {
        try {
            if(token != null) {
                ResponseEntity<TokenDTO> accessToken = refreshTokenService.createAccessToken(token);
                return ResponseEntity.ok().body(accessToken);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // 로그아웃
    @GetMapping("/logOut")
    public String logOut(HttpServletRequest request,
                         HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request,
                response,
                SecurityContextHolder.getContext().getAuthentication());
        return "로그아웃하셨습니다.";
    }

    // 회원정보 수정
    @PutMapping("/api/v1/users/")
    public ResponseEntity<?> update(@RequestBody MemberDTO memberDTO) throws Exception{
        try {
            MemberDTO update = memberService.update(memberDTO);
            return ResponseEntity.ok().body(update);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("잘못된 요청");
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/api/v1/users/{userId}")
    public String remove(@PathVariable Long userId) {
        String delete = memberService.remove(userId);
        return delete;
    }
}
