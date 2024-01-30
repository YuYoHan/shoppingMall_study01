package com.example.shoppingmall.domain.comment.api;

import com.example.shoppingmall.domain.comment.application.CommentService;
import com.example.shoppingmall.domain.comment.dto.ModifyCommentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{boardId}/comments")
@Log4j2
public class CommentController {
    private final CommentService commentService;

    // 댓글 등록
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveComment(@PathVariable Long boardId,
                                         @RequestBody ModifyCommentDTO comment,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("유저 : " + email);
            ResponseEntity<?> responseComment = commentService.save(boardId, comment, email);
            return ResponseEntity.ok().body(responseComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> removeComment(@PathVariable Long boardId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String remove = commentService.remove(boardId, commentId, userDetails);
            return ResponseEntity.ok().body(remove);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateComment(@PathVariable Long boardId,
                                           @PathVariable Long commentId,
                                           @RequestBody ModifyCommentDTO commentDTO,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("email : " + email);
            return commentService.update(boardId, commentId, commentDTO, email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
