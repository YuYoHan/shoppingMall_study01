package com.example.shoppingmall.domain.board.entity;

import com.example.shoppingmall.domain.board.dto.CreateBoardDTO;
import com.example.shoppingmall.domain.board.dto.ResponseBoardDTO;
import com.example.shoppingmall.domain.comment.dto.CommentDTO;
import com.example.shoppingmall.domain.comment.entity.CommentEntity;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.model.BaseEntity;
import com.example.shoppingmall.domain.model.Secret;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(length = 300, nullable = false)
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    private Secret boardSecret;

    @Enumerated(EnumType.STRING)
    private ReplyStatus replyStatus;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("commentId desc ")
    @Builder.Default
    private  List<CommentEntity> commentEntityList = new ArrayList<>();


    // 게시글 DTO를 엔티티로 변환
    public static BoardEntity toBoardEntity(ResponseBoardDTO board,
                                            MemberEntity member) {
        BoardEntity boardEntity = BoardEntity.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .member(member)
                .boardSecret(Secret.UN_LOCK)
                .build();

        // 댓글 처리
        List<CommentDTO> commentDTOList = board.getCommentDTOList();

        for (CommentDTO commentDTO : commentDTOList) {
            CommentEntity commentEntity = CommentEntity.changeEntity(commentDTO, member, boardEntity);
            boardEntity.commentEntityList.add(commentEntity);
        }
        return boardEntity;
    }

    /* 비즈니스 로직 */
    // 게시글 작성
    public static BoardEntity createBoard(CreateBoardDTO boardDTO,
                                          MemberEntity member) {
        return BoardEntity.builder()
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                // 본인이 작성한 글은 읽을 수 있어야하기 때문에 UN_ROCK
                .boardSecret(Secret.UN_LOCK)
                .member(member)
                .replyStatus(ReplyStatus.REPLY_X)
                .build();
    }

    public void updateBoard(CreateBoardDTO boardDTO) {
         BoardEntity.builder()
                .boardId(this.boardId)
                .title(boardDTO.getTitle() != null ?
                        boardDTO.getTitle() : this.title)
                .content(boardDTO.getContent() != null ?
                        boardDTO.getContent() : this.content)
                .member(this.member)
                .commentEntityList(this.commentEntityList)
                .boardSecret(this.boardSecret)
                .build();
    }

    // 답장 상태 변화
    public void changeReply(ReplyStatus replyStatus) {
        this.replyStatus = replyStatus;
    }
    // 잠금 상태 변화
    public void changeSecret(Secret boardSecret) {
        this.boardSecret = boardSecret;
    }

}
