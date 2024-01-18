package com.example.shoppingmall.domain.member.api;

import com.example.shoppingmall.domain.member.application.MemberService;
import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import com.example.shoppingmall.domain.member.dto.ResponseMemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
