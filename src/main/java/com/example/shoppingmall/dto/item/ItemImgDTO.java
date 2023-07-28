package com.example.shoppingmall.dto.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ItemImgDTO {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repingYn;

    @Builder
    public ItemImgDTO(Long id,
                      String imgName,
                      String oriImgName,
                      String imgUrl,
                      String repingYn) {
        this.id = id;
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl;
        this.repingYn = repingYn;
    }
}
