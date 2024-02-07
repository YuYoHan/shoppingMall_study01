package com.example.shoppingmall.domain.board.repository;

import com.example.shoppingmall.domain.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>, BoardRepositoryCustom {
    void deleteByBoardId(Long boardId);
    void deleteAllByMemberMemberId(Long memberId);

}
