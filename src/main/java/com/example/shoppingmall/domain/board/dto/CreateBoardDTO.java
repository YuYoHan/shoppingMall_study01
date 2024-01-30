package com.example.shoppingmall.domain.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@ToString
public class CreateBoardDTO {
    @NotNull(message = "문의글 제목은 필 수 입력입니다.")
    private String title;

    private String content;

    @Builder
    public CreateBoardDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
