package com.example.shoppingmall.controller.board;

import com.example.shoppingmall.dto.board.BoardDTO;
import com.example.shoppingmall.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(name = "/api/v1/boards")
public class BoardController {

    private final BoardService boardService;

    // 게시글 작성
    @PostMapping("")
    public ResponseEntity<?> createBoard(@RequestBody BoardDTO boardDTO,
                                         @RequestPart("files") List<MultipartFile> boardImages,
                                         BindingResult result,
                                         @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        if (result.hasErrors()) {
            log.info("BindingResult error : " + result.hasErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getClass().getSimpleName());
        }
        try {
            String userEmail = userDetails.getUsername();
            ResponseEntity<?> board = boardService.createBoard(boardDTO, boardImages, userEmail);
            return ResponseEntity.ok().body(board);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 게시글 상세 정보
    @GetMapping("/{boardId}")
    public ResponseEntity<?> getBoard(@PathVariable Long boardId) throws Exception {
        try {
            ResponseEntity<?> board = boardService.getBoard(boardId);
            return ResponseEntity.ok().body(board);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 없습니다.");
        }
    }

    // 전체 게시글 보여주기
    @GetMapping("")
    public ResponseEntity<?> getBoards(
            @PageableDefault(sort = "boardId", direction = Sort.Direction.DESC)
            Pageable pageable,
            String seachKeyword) throws Exception {
        try {
            if (seachKeyword == null) {
                Page<BoardDTO> boards = boardService.getBoards(pageable);

                Map<String, Object> response = new HashMap<>();
                // 현재 페이지의 아이템 목록
                response.put("items", boards.getContent());
                // 현재 페이지 번호
                response.put("nowPageNumber", boards.getNumber());
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
            } else {
                Page<BoardDTO> searchBoards = boardService.getSearchBoards(pageable, seachKeyword);
                Map<String, Object> response = new HashMap<>();
                // 현재 페이지의 아이템 목록
                response.put("items", searchBoards.getContent());
                // 현재 페이지 번호
                response.put("nowPageNumber", searchBoards.getNumber());
                // 전체 페이지 수
                response.put("totalPage", searchBoards.getTotalPages());
                // 한 페이지에 출력되는 데이터 개수
                response.put("pageSize", searchBoards.getSize());
                // 다음 페이지 존재 여부
                response.put("hasNextPage", searchBoards.hasNext());
                // 이전 페이지 존재 여부
                response.put("hasPreviousPage", searchBoards.hasPrevious());
                // 첫 번째 페이지 여부
                response.put("isFirstPage", searchBoards.isFirst());
                // 마지막 페이지 여부
                response.put("isLastPage", searchBoards.isLast());
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<?> update(@PathVariable Long boardId,
                                    @RequestBody BoardDTO boardDTO,
                                    @RequestPart(value = "files") List<MultipartFile> boardImages,
                                    @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            String email = userDetails.getUsername();
            ResponseEntity<?> update = boardService.update(boardId, boardDTO, boardImages, email);
            return ResponseEntity.ok().body(update);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 상품삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> delete(@PathVariable Long boardId,
                                    @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            String email = userDetails.getUsername();
            String result = boardService.removeBoard(boardId, email);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
