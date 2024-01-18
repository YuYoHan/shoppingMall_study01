package com.example.shoppingmall.domain.member.application;

import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<?> signUp(RequestMemberDTO member);
    // 이메일 체크
    boolean emailCheck(String email);
    // 닉네임 체크
    boolean nickNameCheck(String nickName);
}
