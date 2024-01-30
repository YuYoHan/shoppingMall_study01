package com.example.shoppingmall.domain.comment.dto;

import lombok.*;

/*
 *   writer : 유요한
 *   work :
 *          댓글에 대한 정보를 업데이트할 때 사용하는 RequsetDTO
 *   date : 2023/10/11
 * */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyCommentDTO {
    private String comment;
    private Long parentId;
}
