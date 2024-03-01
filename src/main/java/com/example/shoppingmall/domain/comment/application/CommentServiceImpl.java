package com.example.shoppingmall.domain.comment.application;

import com.example.shoppingmall.domain.board.entity.BoardEntity;
import com.example.shoppingmall.domain.board.repository.BoardRepository;
import com.example.shoppingmall.domain.comment.dto.CommentDTO;
import com.example.shoppingmall.domain.comment.dto.ModifyCommentDTO;
import com.example.shoppingmall.domain.comment.entity.CommentEntity;
import com.example.shoppingmall.domain.comment.repository.CommentRepository;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.exception.UserException;
import com.example.shoppingmall.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    // 댓글 작성
    @Override
    public ResponseEntity<?> save(Long boardId,
                                  ModifyCommentDTO commentDTO,
                                  String memberEmail) {
        try {
            BoardEntity findBoard = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            CommentEntity findComment = commentRepository.findById(commentDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

            CommentEntity comment;
            CommentEntity saveComment;
            CommentDTO returnComment;
            if(findUser != null) {
               if(commentDTO.getParentId() == null) {
                   // 댓글 생성
                   comment = CommentEntity.createComment(commentDTO, findUser, findBoard);
                   saveComment = commentRepository.save(comment);
                   returnComment = CommentDTO.changeDTO(saveComment);
               } else {
                   // 대댓글
                   comment = CommentEntity.createComment(commentDTO, findUser, findBoard);
                   findComment.createReply(comment);
                   findComment.changeReply(true);
                   saveComment = commentRepository.save(findComment);
                   returnComment = CommentDTO.changeDTO(saveComment);
               }
                return ResponseEntity.ok().body(returnComment);
            }
            throw  new UserException("유저가 존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 댓글 삭제
    @Override
    public String remove(Long boardId, Long commentId, UserDetails userDetails) {
        try {
            String memberEmail = userDetails.getUsername();
            // userDetails에서 권한을 가져오기
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            // 게시물 조회
            BoardEntity findBoard = boardRepository.findById(boardId)
                    .orElseThrow(EntityNotFoundException::new);
            // 댓글 조회
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(EntityNotFoundException::new);
            // 회원 조회
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);

            // 댓글을 작성한 이메일과 유저 이메일이 맞는지 비교
            boolean equalsEmail = findUser.getEmail().equals(findComment.getMember().getEmail());
            // 댓글을 작성한 곳(게시글)의 id와 받아온 게시글 id를 비교
            boolean equalsId = findComment.getBoard().getBoardId().equals(findBoard.getBoardId());

            // 해당 유저인지 체크하고 댓글을 작성한 게시글 id인지 체크
            if(equalsEmail && equalsId) {
                commentRepository.deleteById(findComment.getCommentId());
                return "댓글을 삭제했습니다.";
            } else if(!authorities.isEmpty() && equalsId) {
                String role = authorities.iterator().next().getAuthority();
                log.info("권한 : " + role);
                if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                    commentRepository.deleteById(findComment.getCommentId());
                    return "관리자가 댓글을 삭제하였습니다.";
                }
            }
            return "조건에 일치한 댓글이 아닙니다.";

        } catch (Exception e) {
            return "댓글을 삭제하는데 실패했습니다.";
        }
    }

    // 댓글 수정
    @Override
    public ResponseEntity<?> update(Long boardId,
                                    Long commentId,
                                    ModifyCommentDTO commentDTO,
                                    String memberEmail) {
        try {
            // 게시물 조회
            BoardEntity findBoard = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
            // 댓글 조회
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));
            // 회원 조회
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);

            boolean equalsEmail = findUser.getEmail().equals(findComment.getMember().getEmail());
            boolean equalsId = findComment.getBoard().getBoardId().equals(findBoard.getBoardId());

            if(equalsEmail && equalsId) {
                // 댓글 수정
                findComment.updateComment(commentDTO);
                CommentEntity save = commentRepository.save(findComment);
                CommentDTO returnComment = CommentDTO.changeDTO(save);
                return ResponseEntity.ok().body(returnComment);
            }
            return ResponseEntity.badRequest().body("일치하지 않습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 수정하는데 실패했습니다.");
        }
    }
}
