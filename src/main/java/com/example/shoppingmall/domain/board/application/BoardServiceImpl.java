package com.example.shoppingmall.domain.board.application;

import com.example.shoppingmall.domain.board.dto.CreateBoardDTO;
import com.example.shoppingmall.domain.board.dto.ResponseBoardDTO;
import com.example.shoppingmall.domain.board.entity.BoardEntity;
import com.example.shoppingmall.domain.board.entity.ReplyStatus;
import com.example.shoppingmall.domain.board.repository.BoardRepository;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.repository.MemberRepository;
import com.example.shoppingmall.domain.model.Secret;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BoardServiceImpl implements BoardService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    // 게시글 등록
    @Override
    public ResponseEntity<?> saveBoard(Long itemId,
                                       CreateBoardDTO boardDTO,
                                       String memberEmail) throws Exception {
        try {
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            log.info("유저 : " + findUser);

            if(findUser != null) {
                BoardEntity board = BoardEntity.createBoard(boardDTO, findUser);
                BoardEntity saveBoard = boardRepository.save(board);
                ResponseBoardDTO responseBoardDTO = ResponseBoardDTO.changeDTO(saveBoard);
                return ResponseEntity.ok().body(responseBoardDTO);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 게시글 수정
    @Override
    public ResponseEntity<?> updateBoard(Long boardId,
                                         CreateBoardDTO boardDTO,
                                         String memberEmail) {
        try {
            BoardEntity findBoard = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
            log.info("게시글 : " + findBoard);
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            log.info("유저 : " + findUser);

            if(findBoard.getMember().getEmail().equals(memberEmail)) {
                findBoard.updateBoard(boardDTO);
                BoardEntity updateBoard = boardRepository.save(findBoard);
                ResponseBoardDTO responseBoardDTO = ResponseBoardDTO.changeDTO(updateBoard);
                log.info("게시글 수정 : " + responseBoardDTO);
                return ResponseEntity.ok().body(responseBoardDTO);
            }
            return ResponseEntity.badRequest().body("일치하지 않습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("수정하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 게시글 삭제
    @Override
    public String removeBoard(Long boardId, UserDetails userDetails) {
        try {
            String memberEmail = userDetails.getUsername();
            log.info("이메일 : " + memberEmail);
            // 권한 가져오기
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            // 게시글 조회
            BoardEntity findBoard = boardRepository.findByBoardId(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
            log.info("게시글 : " + findBoard);
            // 유저 조회
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            log.info("유저 : " + findUser);

            boolean equalsEmail = findUser.getEmail().equals(findBoard.getMember().getEmail());
            String authority = authorities.iterator().next().getAuthority();

            // 일치하다면 내 글이 맞으므로 삭제할 수 있다.
            if (equalsEmail) {
                // 게시글 삭제
                boardRepository.deleteById(findBoard.getBoardId());
                return "게시글을 삭제했습니다.";
                // 관리자 등급이 맞다면 삭제할 수 있다.
            } else if(authority.equals("ADMIN") || authority.equals("ROLE_ADMIN")){
                boardRepository.deleteById(findBoard.getBoardId());
                return "게시글을 삭제 했습니다.";
            } else {
                return "삭제할 수 없습니다.";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> getBoard(Long boardId, String memberEmail) {
        try {
            // 회원 조회
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            // 문의글 조회
            BoardEntity findBoard = boardRepository.findByBoardId(boardId)
                    .orElseThrow(EntityNotFoundException::new);

            // 문의글을 작성할 때 등록된 이메일이 받아온 이메일이 맞아야 true
            if (findUser.getEmail().equals(findBoard.getMember().getEmail())) {
                ResponseBoardDTO boardDTO = ResponseBoardDTO.changeDTO(findBoard);
                return ResponseEntity.ok().body(boardDTO);
            } else {
                return ResponseEntity.badRequest().body("해당 유저의 문의글이 아닙니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponseBoardDTO> getMyBoards(String memberEmail,
                                              Pageable pageable,
                                              String searchKeyword) {
        try {
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            Page<BoardEntity> findBoards = boardRepository.findBoardByEmail(memberEmail, pageable, searchKeyword);

            findBoards.forEach(board -> board.changeReply(
                            board.getCommentEntityList().isEmpty() ?
                                    ReplyStatus.REPLY_X : ReplyStatus.REPLY_O
                    ));

            // 해당 게시글을 만들때 id와 조회한 id를 체크
            // 그리고 맞다면 읽을 권한주고 없으면 잠가주기
            findBoards.forEach(board -> {
                if(board.getMember().getMemberId().equals(findUser.getMemberId())) {
                    board.changeSecret(Secret.UN_LOCK);
                } else {
                    board.changeSecret(Secret.LOCK);
                }
            });
            return findBoards.map(ResponseBoardDTO::changeDTO);
        } catch (Exception e) {
            return null;
        }
    }

}
