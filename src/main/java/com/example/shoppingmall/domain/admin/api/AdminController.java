package com.example.shoppingmall.domain.admin.api;

import com.example.shoppingmall.domain.admin.application.AdminService;
import com.example.shoppingmall.domain.admin.dto.CodeDTO;
import com.example.shoppingmall.domain.board.application.BoardService;
import com.example.shoppingmall.domain.board.dto.ResponseBoardDTO;
import com.example.shoppingmall.domain.item.application.ItemService;
import com.example.shoppingmall.domain.item.dto.ItemSearchCondition;
import com.example.shoppingmall.domain.item.dto.ResponseItemDTO;
import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import com.example.shoppingmall.domain.order.application.OrderService;
import com.example.shoppingmall.domain.order.dto.RequestOrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/admins")
public class AdminController {
    private final OrderService orderService;
    private final BoardService boardService;
    private final AdminService adminService;
    private final ItemService itemService;


    @PostMapping("/orderItem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> order(@RequestBody List<RequestOrderDTO> orders,
                                   BindingResult result,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getClass().getSimpleName());
            }
            ResponseEntity<?> responseEntity = orderService.orderItem(orders, userDetails);
            return responseEntity;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 게시글 삭제
    @DeleteMapping("/boards/{boardId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> removeBoard(@PathVariable Long boardId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String result = boardService.removeBoard(boardId, userDetails);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 모든 문의글 보기
    @GetMapping("/boards")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBoards(
            // SecuritConfig에 Page 설정을 한 페이지에 10개 보여주도록
            // 설정을 해서 여기서는 할 필요가 없다.
            @PageableDefault(sort = "boardId", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 검색하지 않을 때는 모든 글을 보여준다.
            Page<ResponseBoardDTO> boards = adminService.getAllBoards(pageable, searchKeyword, userDetails);

            Map<String, Object> response = pageInfo(boards);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 문의글 확인합니다.
    @GetMapping("/{boardId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBoard(@PathVariable Long boardId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("email : " + email);
            ResponseEntity<?> board = adminService.getBoard(boardId, userDetails);
            return ResponseEntity.ok().body(board);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 관리자 기준 상품 조건으로 보기
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> searchItemsConditions(Pageable pageable,
                                                   ItemSearchCondition condition) {
        try {
            Page<ResponseItemDTO> items = itemService.searchItemsConditions(pageable, condition);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("items", items.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", items.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", items.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", items.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", items.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", items.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", items.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", items.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 관리자 회원가입
    @PostMapping("")
    public ResponseEntity<?> joinAdmin(@Validated @RequestBody RequestMemberDTO admin,
                                       BindingResult result) {
        try {
            // 입력값 검증 예외가 발생하면 예외 메시지를 응답한다.
            if (result.hasErrors()) {
                log.info("BindingResult error : " + result.hasErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getClass().getSimpleName());
            }

            ResponseEntity<?> adminSignUp = adminService.adminSignUp(admin);
            log.info("결과 : " + adminSignUp);
            return ResponseEntity.ok().body(adminSignUp);
        }catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // 관리자 회원가입시 이메일 인증
    @PostMapping("/mails")
    public String emailAuthentication(@RequestParam("email") String email) {
        try {
            String code = adminService.sendMail(email);
            log.info("사용자에게 발송한 인증코드 ==> " + code);
            return "이메일 인증코드가 발급완료";
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            return e.getMessage();
        }
    }
    // 인증 코드 검증
    @PostMapping("/verifications")
    public ResponseEntity<?> verificationEmail(@RequestBody CodeDTO code) {
        log.info("코드 : " + code);
        String result = adminService.verifyCode(code.getCode());
        log.info("result : " + result);
        return ResponseEntity.ok().body(result);
    }


    private static Map<String, Object> pageInfo(Page<ResponseBoardDTO> boards) {
        Map<String, Object> response = new HashMap<>();
        // 현재 페이지의 아이템 목록
        response.put("items", boards.getContent());
        // 현재 페이지 번호
        response.put("nowPageNumber", boards.getNumber() + 1);
        // 전체 페이지 수
        response.put("totalPage", boards.getTotalPages());
        // 한 페이지에 출력되는 데이터 개수
        response.put("pageSize", boards.getSize());
        // 다음 페이지 존재 여부
        response.put("hasNextPage", boards.hasNext());
        // 이전 페이지 존재 여부
        response.put("hasPreviousPage", boards.hasPrevious());
        // 첫 번째 페이지 여부
        response.put("isFirstPage", boards.isFirst());
        // 마지막 페이지 여부
        response.put("isLastPage", boards.isLast());
        return response;
    }
}
