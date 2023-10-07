package com.example.shoppingmall.controller.comment;

import com.example.shoppingmall.dto.comment.CommentDTO;
import com.example.shoppingmall.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{itemId}/comments")
    public ResponseEntity<?> save(@PathVariable Long itemId,
                                  @RequestBody CommentDTO commentDTO,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ResponseEntity<?> save = commentService.save(itemId, commentDTO, email);
        return ResponseEntity.ok().body(save);
    }

    // 댓글 삭제
    @DeleteMapping("/{itemId}/{commentId}")
    public String remove(@PathVariable Long itemId,
                         @PathVariable Long commentId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        String remove = commentService.remove(itemId, commentId, email);
        return remove;
    }

    // 댓글 수정
    @PutMapping("/{itemId}/{commentId}")
    public ResponseEntity<?> update(@PathVariable Long itemId,
                                    @PathVariable Long commentId,
                                    @RequestBody CommentDTO commentDTO,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ResponseEntity<?> update = commentService.update(itemId, commentId, commentDTO, email);
        return ResponseEntity.ok().body(update);
    }


}
