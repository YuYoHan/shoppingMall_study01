package com.example.shoppingmall.domain.comment.application;

import com.example.shoppingmall.domain.comment.dto.ModifyCommentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CommentService {
    // 댓글 작성
    ResponseEntity<?> save(Long boardId,
                           ModifyCommentDTO commentDTO,
                           String memberEmail);

    // 댓글 삭제
    String remove(Long boardId,
                  Long commentId,
                  UserDetails userDetails);

    // 댓글 수정
    ResponseEntity<?> update(Long boardId,
                             Long commentId,
                             ModifyCommentDTO commentDTO,
                             String memberEmail);
}
