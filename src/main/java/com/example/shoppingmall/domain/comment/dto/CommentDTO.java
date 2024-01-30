package com.example.shoppingmall.domain.comment.dto;

import com.example.shoppingmall.domain.comment.entity.CommentEntity;
import com.example.shoppingmall.domain.model.Secret;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long commentId;

    private String comment;

    private LocalDateTime writeTime;
    private String nickName;

    private List<CommentDTO> commentDTOList;

    private Secret secret;

    private boolean isReply;
    private Long parentId;


    public static List<CommentDTO> reComment(CommentEntity comment) {
        List<CommentEntity> reComment = comment.getChildren();
        List<CommentDTO> commentList = new ArrayList<>();
        for (CommentEntity commentEntity : reComment) {
            CommentDTO commentDTO = CommentDTO.builder()
                    .commentId(commentEntity.getCommentId())
                    .comment(commentEntity.getComment())
                    .writeTime(commentEntity.getRegTime())
                    .nickName(commentEntity.getMember().getNickName())
                    .secret(commentEntity.getSecret())
                    .build();
            commentList.add(commentDTO);
        }
        return commentList;
    }

    public static CommentDTO changeDTO(CommentEntity comment) {
        List<CommentDTO> commentDTOS = CommentDTO.reComment(comment);
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .writeTime(comment.getRegTime())
                .nickName(comment.getMember().getNickName())
                .secret(comment.getSecret())
                .commentDTOList(commentDTOS)
                .isReply(comment.isReply())
                .parentId(comment.getParent().getCommentId() == null ?
                        null : comment.getParent().getCommentId())
                .build();
    }

    public static CommentDTO saveComment(CommentEntity comment) {
        List<CommentDTO> commentDTOS = CommentDTO.reComment(comment);

        return CommentDTO.builder()
                .comment(comment.getComment())
                .writeTime(comment.getRegTime())
                .nickName(comment.getMember().getNickName())
                .secret(comment.getSecret())
                .commentDTOList(commentDTOS)
                .parentId(comment.getParent().getCommentId() == null ?
                        null : comment.getParent().getCommentId())
                .build();
    }
}
