package com.example.shoppingmall.dto.board;

import com.example.shoppingmall.dto.item.ItemImgDTO;
import com.example.shoppingmall.dto.member.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class BoardDTO {
    @Schema(description = "게시판 번호", example = "1", required = true)
    private Long boardId;

    @Schema(description = "게시판 제목", required = true)
    private String title;

    @Schema(description = "게시판 본문")
    private String content;

    @Schema(description = "게시판을 작성한 유저번호", example = "1", required = true)
    private Long userId;

    @Schema(description = "유저 이메일")
    private String userEmail;

    @Schema(description = "게시글 작성 시간")
    private LocalDateTime regTime;
    @Schema(description = "게시글 수정 시간")
    private LocalDateTime updateTime;

    @Schema(description = "게시글 이미지 정보")
    private List<BoardImgDTO> boardImgDTOList = new ArrayList<>();

    public BoardDTO(Long boardId,
                    String title,
                    String content,
                    Long userId,
                    String userEmail,
                    LocalDateTime regTime,
                    LocalDateTime updateTime,
                    List<BoardImgDTO> boardImgDTOList) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.userEmail = userEmail;
        this.regTime = regTime;
        this.updateTime = updateTime;
        this.boardImgDTOList = boardImgDTOList;
    }
}
