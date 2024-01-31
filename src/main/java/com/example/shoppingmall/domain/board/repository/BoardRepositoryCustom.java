package com.example.shoppingmall.domain.board.repository;

import com.example.shoppingmall.domain.board.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BoardRepositoryCustom {
    Optional<BoardEntity> findByBoardId(Long boardId);

    Page<BoardEntity> findByTitle(Pageable pageable, String searchKeyword);

    Page<BoardEntity> findAll(Pageable pageable);

    Page<BoardEntity> findBoardByEmail(String email,
                                       Pageable pageable,
                                       String searchKeyword);

    Page<BoardEntity> findBoardByNickName(String nickName,
                                          Pageable pageable,
                                          String searchKeyword);

}