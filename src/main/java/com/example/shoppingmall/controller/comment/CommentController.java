package com.example.shoppingmall.controller.comment;

import com.example.shoppingmall.dto.comment.CommentDTO;
import com.example.shoppingmall.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Tag(name = "comment", description = "댓글 API")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{itemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Tag(name = "comment")
    @Operation(summary = "댓글 등록", description = "댓글을 등록하는 API입니다.")
    public ResponseEntity<?> save(@PathVariable Long itemId,
                                  @RequestBody CommentDTO commentDTO,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ResponseEntity<?> save = commentService.save(itemId, commentDTO, email);
        return ResponseEntity.ok().body(save);
    }

    // 댓글 삭제
    @DeleteMapping("/{itemId}/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Tag(name = "comment")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제하는 API입니다.")
    public String remove(@PathVariable Long itemId,
                         @PathVariable Long commentId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        String remove = commentService.remove(itemId, commentId, email);
        return remove;
    }

    // 댓글 수정
    @PutMapping("/{itemId}/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Tag(name = "comment")
    @Operation(summary = "댓글 수정", description = "댓글을 수정하는 API입니다.")
    public ResponseEntity<?> update(@PathVariable Long itemId,
                                    @PathVariable Long commentId,
                                    @RequestBody CommentDTO commentDTO,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ResponseEntity<?> update = commentService.update(itemId, commentId, commentDTO, email);
        return ResponseEntity.ok().body(update);
    }


}
