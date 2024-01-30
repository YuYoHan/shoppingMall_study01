package com.example.shoppingmall.domain.board.application;

import com.example.shoppingmall.domain.board.dto.CreateBoardDTO;
import com.example.shoppingmall.domain.board.dto.ResponseBoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface BoardService {
    // 문의 등록
    ResponseEntity<?> saveBoard(Long itemId,
                                CreateBoardDTO boardDTO,
                                String memberEmail) throws Exception;

    // 문의 수정
    ResponseEntity<?> updateBoard(Long boardId,
                                  CreateBoardDTO boardDTO,
                                  String memberEmail);

    // 문의 삭제
    String removeBoard(Long boardId, UserDetails userDetails);

    // 문의 자세히 보기
    ResponseEntity<?> getBoard(Long boardId, String memberEmail);

    // 작성자의 문의글 보기
    Page<ResponseBoardDTO> getMyBoards(String memberEmail, Pageable pageable, String searchKeyword);

}
