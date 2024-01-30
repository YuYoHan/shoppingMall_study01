package com.example.shoppingmall.domain.board.api;

import com.example.shoppingmall.domain.board.application.BoardService;
import com.example.shoppingmall.domain.board.dto.CreateBoardDTO;
import com.example.shoppingmall.domain.board.dto.ResponseBoardDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final BoardService boardService;

    // 문의 등록
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createBoard(
            @PathVariable Long itemId,
            @RequestBody @Validated CreateBoardDTO board,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("bindingResult error : " + bindingResult.hasErrors());
                return ResponseEntity.badRequest().body(bindingResult.getClass().getSimpleName());
            }
            String email = userDetails.getUsername();

            ResponseEntity<?> returnBoard = boardService.saveBoard(itemId, board, email);
            return ResponseEntity.ok().body(returnBoard);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 문의 삭제
    @DeleteMapping("/{boardId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String removeBoard(@PathVariable Long boardId,
                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String result = boardService.removeBoard(boardId, userDetails);
            return result;
        } catch (Exception e) {
            return "문의를 삭제하는데 실패했습니다.";
        }
    }

    // 문의 수정
    @PutMapping("/{boardId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBoard(@PathVariable Long boardId,
                                         @RequestBody CreateBoardDTO modifyDTO,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("이메일 : " + email);
        return boardService.updateBoard(boardId, modifyDTO, email);
    }

    // 문의 상세 보기
    // 상품 안에 있는 문의글은 게시글 형태로 되어 있기 때문에  상세보기로 들어가야 한다.
    // 해당 상세보기 기능은 유저를 가리지 않고 그 상품에 관한 문의글이다.
    @GetMapping("/{boardId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBoard(@PathVariable Long boardId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("email : " + email);
            ResponseEntity<?> board = boardService.getBoard(boardId, email);
            return ResponseEntity.ok().body(board);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 나의 문의글 확인
    @GetMapping("/myboards")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getBoards(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "boardId", direction = Sort.Direction.DESC)
            Pageable pageable,
            String searchKeyword) {
        try {
            String email = userDetails.getUsername();
            log.info("유저 : " + email);

            Page<ResponseBoardDTO> boards = boardService.getMyBoards(email, pageable, searchKeyword);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("items", boards.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", boards.getNumber()+1);
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
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
