package com.example.shoppingmall.dto.board;

import com.example.shoppingmall.entity.board.BoardImgEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class BoardImgDTO {
    @Schema(description = "게시글 이미지 번호")
    private Long boardImgId;

    @Schema(description = "게시글 업로드 이름")
    private String uploadImgName;

    @Schema(description = "원본 상품 이름")
    private String oriImgName;

    @Schema(description = "업로드 이미지 URL")
    private String uploadImgUrl;

    @Schema(description = "업로드 이미지 Path")
    private String uploadImgPath;

    @Schema(description = "대표 이미지 여부")
    private String repImgYn;

    @Schema(description = "상품 정보")
    private BoardDTO board;

    @Builder
    public BoardImgDTO(Long boardImgId,
                      String uploadImgName,
                      String oriImgName,
                      String uploadImgUrl,
                      String uploadImgPath,
                      String repImgYn,
                      BoardDTO board) {
        this.boardImgId = boardImgId;
        this.uploadImgName = uploadImgName;
        this.oriImgName = oriImgName;
        this.uploadImgUrl = uploadImgUrl;
        this.uploadImgPath = uploadImgPath;
        this.repImgYn = repImgYn;
        this.board = board;
    }

    public static BoardImgDTO toBoardImgDTO(BoardImgEntity boardImgEntity) {
        return BoardImgDTO.builder()
                .boardImgId(boardImgEntity.getBoardImgId())
                .oriImgName(boardImgEntity.getOriImgName())
                .uploadImgName(boardImgEntity.getUploadImgName())
                .uploadImgUrl(boardImgEntity.getUploadImgUrl())
                .uploadImgPath(boardImgEntity.getUploadImgPath())
                .repImgYn(boardImgEntity.getRepImgYn())
                .build();
    }
}
