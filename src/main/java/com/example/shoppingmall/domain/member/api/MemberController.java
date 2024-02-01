package com.example.shoppingmall.domain.member.api;

import com.example.shoppingmall.domain.jwt.application.TokenService;
import com.example.shoppingmall.domain.member.application.MemberService;
import com.example.shoppingmall.domain.member.dto.LoginDTO;
import com.example.shoppingmall.domain.member.dto.ModifyMemberDTO;
import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import com.example.shoppingmall.domain.member.dto.ResponseMemberDTO;
import com.example.shoppingmall.domain.order.application.OrderService;
import com.example.shoppingmall.domain.order.dto.ResponseOrderItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final OrderService orderService;

    @PostMapping("")
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
    public boolean emailCheck(@PathVariable String memberEmail) {
        log.info("email : " + memberEmail);
        return memberService.emailCheck(memberEmail);
    }

    // 닉네임 조회
    @GetMapping("/nickName/{nickName}")
    public boolean nickNameCheck(@PathVariable String nickName) {
        log.info("nickName : " + nickName);
        return memberService.nickNameCheck(nickName);
    }

    // 회원 탈퇴
    @DeleteMapping("/{memberId}")
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
    public ResponseEntity<?> refreshToken(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            String email = userDetails.getUsername();
            log.info("이메일 : " + email);
            ResponseEntity<?> accessToken = tokenService.createAccessToken(email);
            return ResponseEntity.ok().body(accessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 주문 조회
    @GetMapping(value = "/orders")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getOrders(Pageable pageable,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Page<ResponseOrderItemDTO> ordersPage = orderService.getOrders(pageable, userDetails);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("items", ordersPage.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", ordersPage.getNumber()+1);
            // 전체 페이지 수
            response.put("totalPage", ordersPage.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", ordersPage.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", ordersPage.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", ordersPage.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", ordersPage.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", ordersPage.isLast());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
