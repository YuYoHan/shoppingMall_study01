package com.example.shoppingmall.repository.board;

import com.example.shoppingmall.entity.board.BoardImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardImgRepository extends JpaRepository<BoardImgEntity, Long> {
    List<BoardImgEntity> findByBoardBoardId(Long BoardId);
}
