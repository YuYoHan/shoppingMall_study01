package com.example.shoppingmall.dto.board;

import com.example.shoppingmall.dto.item.ItemDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class BoardImgDTO {
    private Long boardImgId;
    private String uploadImgName;
    private String oriImgName;
    private String uploadImgUrl;
    private String uploadImgPath;
    private String repImgYn;
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
}
