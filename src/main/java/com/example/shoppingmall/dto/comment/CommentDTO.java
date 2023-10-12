package com.example.shoppingmall.dto.comment;

import com.example.shoppingmall.entity.comment.CommentEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class CommentDTO {
    private Long commentId;
    private String comment;
    private LocalDateTime writeTime;
    private String nickName;

    @Builder
    public CommentDTO(Long commentId, String comment, LocalDateTime writeTime, String nickName) {
        this.commentId = commentId;
        this.comment = comment;
        this.writeTime = writeTime;
        this.nickName = nickName;
    }

    public static CommentDTO toCommentDTO(CommentEntity commentEntity) {
        return  CommentDTO.builder()
                .commentId(commentEntity.getCommentId())
                .comment(commentEntity.getComment())
                .writeTime(LocalDateTime.now())
                .nickName(commentEntity.getMember().getNickName())
                .build();
    }
}