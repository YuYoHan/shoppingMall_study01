package com.example.shoppingmall.domain.item.dto;

import com.example.shoppingmall.domain.item.entity.ItemImgEntity;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class ItemImgDTO {
    private Long itemImgId;
    private String name;
    private String type;
    private String filePath;
    // 대표 이미지 여부 Y면 대표이미지를 보여줌
    private String repImgYn;


    public static ItemImgDTO changeDTO(ItemImgEntity img) {
        return ItemImgDTO.builder()
                .itemImgId(img.getId())
                .name(img.getName())
                .type(img.getType())
                .filePath(img.getFilePath())
                .repImgYn(img.getRepImgYn())
                .build();
    }
}
