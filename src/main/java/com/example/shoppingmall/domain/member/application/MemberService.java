package com.example.shoppingmall.domain.member.application;

import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import com.example.shoppingmall.domain.member.dto.ResponseMemberDTO;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<?> signUp(RequestMemberDTO member);
    // 이메일 체크
    boolean emailCheck(String email);
    // 닉네임 체크
    boolean nickNameCheck(String nickName);
    // 회원조회
    ResponseMemberDTO search(Long memberId);
    // 회원삭제
    String removeUser(Long memberId, String email);
    // 로그인
    ResponseEntity<?> login(String memberEmail, String memberPw);
    // 회원수정
    ResponseEntity<?> updateUser(Long memberId, ModifyMemberDTO modifyMemberDTO, String memberEmail);
}
