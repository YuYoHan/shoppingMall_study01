package com.example.shoppingmall.repository.comment;

import com.example.shoppingmall.entity.comment.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    void deleteByItemItemId(Long itemId);
}
