package com.example.shoppingmall.domain.comment.repository;

import com.example.shoppingmall.domain.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    CommentEntity findByMemberEmail(String email);
}
