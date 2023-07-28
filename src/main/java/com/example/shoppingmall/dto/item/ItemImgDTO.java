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
    private String uploadImgName;
    private String oriImgName;
    private String uploadImgUrl;
    private String uploadImgPath;

    @Builder
    public ItemImgDTO(Long id,
                      String uploadImgName,
                      String oriImgName,
                      String uploadImgUrl,
                      String uploadImgPath) {
        this.id = id;
        this.uploadImgName = uploadImgName;
        this.oriImgName = oriImgName;
        this.uploadImgUrl = uploadImgUrl;
        this.uploadImgPath = uploadImgPath;
    }
}
