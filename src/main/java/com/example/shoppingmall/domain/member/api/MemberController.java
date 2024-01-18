package com.example.shoppingmall.domain.member.api;

import com.example.shoppingmall.domain.jwt.application.TokenService;
import com.example.shoppingmall.domain.jwt.dto.TokenDTO;
import com.example.shoppingmall.domain.member.application.MemberService;
import com.example.shoppingmall.domain.member.dto.LoginDTO;
import com.example.shoppingmall.domain.member.dto.ModifyMemberDTO;
import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import com.example.shoppingmall.domain.member.dto.ResponseMemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "member", description = "유저 API")
public class MemberController {
    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("")
    @Tag(name = "member")
    @Operation(summary = "회원가입", description = "회원가입하는 API입니다")
    public ResponseEntity<?> join(@Validated @RequestBody RequestMemberDTO member,
                                  BindingResult result) {
        try {
            // 입력값 검증 예외가 발생하면 예외 메시지를 응답한다.
            if (result.hasErrors()) {
                log.info("BindingResult error : " + result.hasErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getClass().getSimpleName());
            }
            return memberService.signUp(member);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{memberId}")
    @Tag(name = "member")
    @Operation(summary = "회원 조회", description = "회원을 검색하는 API입니다.")
    public ResponseEntity<?> search(@PathVariable Long memberId) {
        try {
            ResponseMemberDTO search = memberService.search(memberId);
            return ResponseEntity.ok().body(search);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    @Tag(name = "member")
    @Operation(summary = "로그인 API", description = "로그인을 하면 JWT를 반환해줍니다.")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            String email = loginDTO.getMemberEmail();
            String password = loginDTO.getMemberPw();
            ResponseEntity<?> login = memberService.login(email, password);
            return ResponseEntity.ok().body(login);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인을 실패했습니다.");
        }
    }

    // 회원 수정
    @PutMapping("/{memberId}")
    @Tag(name = "member")
    @Operation(summary = "수정 API", description = "유저 정보를 수정하는 API입니다.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long memberId,
                                    @Validated @RequestBody ModifyMemberDTO modifyMemberDTO,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("email : " + email);
            log.info("비밀번호 체크 : " + modifyMemberDTO.getMemberPw());
            ResponseEntity<?> responseEntity = memberService.updateUser(memberId, modifyMemberDTO, email);
            return ResponseEntity.ok().body(responseEntity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 중복체크
    @GetMapping("/email/{memberEmail}")
    @Tag(name = "member")
    @Operation(summary = "중복체크 API", description = "userEmail이 중복인지 체크하는 API입니다.")
    public boolean emailCheck(@PathVariable String memberEmail) {
        log.info("email : " + memberEmail);
        return memberService.emailCheck(memberEmail);
    }

    // 닉네임 조회
    @GetMapping("/nickName/{nickName}")
    @Tag(name = "member")
    @Operation(summary = "닉네임 조회", description = "중복된 닉네임이 있는지 확인하는 API입니다.")
    public boolean nickNameCheck(@PathVariable String nickName) {
        log.info("nickName : " + nickName);
        return memberService.nickNameCheck(nickName);
    }

    // 회원 탈퇴
    @DeleteMapping("/{memberId}")
    @Tag(name = "member")
    @Operation(summary = "삭제 API", description = "유저를 삭제하는 API입니다.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String remove(@PathVariable Long memberId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("email : " + email);
            String remove = memberService.removeUser(memberId, email);
            return remove;
        } catch (Exception e) {
            return "회원탈퇴 실패했습니다. :" + e.getMessage();
        }
    }

    // accessToken 만료시 refreshToken으로 accessToken 발급
    @GetMapping("/refresh")
    @Tag(name = "member")
    @Operation(summary = "access token 발급", description = "refresh token을 받으면 access token을 반환해줍니다.")
    public ResponseEntity<?> refreshToken(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            String email = userDetails.getUsername();
            log.info("이메일 : " + email);

            if (email != null) {
                ResponseEntity<TokenDTO> accessToken = tokenService.createAccessToken(email);
                return ResponseEntity.ok().body(accessToken);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
