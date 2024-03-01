package com.example.shoppingmall.domain.comment.entity;

import com.example.shoppingmall.domain.board.entity.BoardEntity;
import com.example.shoppingmall.domain.comment.dto.CommentDTO;
import com.example.shoppingmall.domain.comment.dto.ModifyCommentDTO;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.model.BaseEntity;
import com.example.shoppingmall.domain.model.Secret;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder
public class CommentEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    @Enumerated(EnumType.STRING)
    private Secret secret;

    @Column(name = "is_reply")
    private boolean isReply; // 대댓글 여부 플래그 추가


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<CommentEntity> children = new ArrayList<>();


    public static CommentEntity changeEntity(CommentDTO comment,
                                             MemberEntity member,
                                             BoardEntity board) {
        List<CommentDTO> reComment = comment.getCommentDTOList();
        List<CommentEntity> commentList = new ArrayList<>();
        for (CommentDTO commentDTO : reComment) {
            CommentEntity commentEntity = CommentEntity.builder()
                    .commentId(commentDTO.getCommentId())
                    .comment(commentDTO.getComment())
                    .secret(commentDTO.getSecret())
                    .build();
            commentList.add(commentEntity);
        }

        return CommentEntity.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .member(member)
                .board(board)
                .secret(comment.getSecret())
                .children(commentList)
                .build();
    }

    // 생성(부모 댓글)
    public static CommentEntity createComment(ModifyCommentDTO commentDTO,
                                              MemberEntity member,
                                              BoardEntity board) {

        return CommentEntity.builder()
                .comment(commentDTO.getComment())
                .member(member)
                .board(board)
                .secret(Secret.UN_LOCK)
                .isReply(false)
                .build();
    }
    // 대댓글 리스트에 추가
    public void createReply(CommentEntity reply) {
        this.children.add(reply);
    }



    // 수정
    public void updateComment(ModifyCommentDTO commentDTO) {
        this.comment = commentDTO.getComment() != null ? commentDTO.getComment() : this.comment;
    }

    // 부모 댓글 수정
    public void updateParent(CommentEntity parent){
        this.parent = parent;
    }

    // 비밀글인지 아닌지 상태를 변환
    public void changeSecret(Secret secret) {
        this.secret = secret;
    }

    public void changeReply(boolean isReply) {
        this.isReply = isReply;
    }
}
