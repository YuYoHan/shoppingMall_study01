package com.example.shoppingmall.dto.item;

import com.example.shoppingmall.entity.item.ItemImgEntity;
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
    private String repImgYn;
    private ItemDTO item;

    @Builder
    public ItemImgDTO(Long id,
                      String uploadImgName,
                      String oriImgName,
                      String uploadImgUrl,
                      String uploadImgPath,
                      String repImgYn,
                      ItemDTO item) {
        this.id = id;
        this.uploadImgName = uploadImgName;
        this.oriImgName = oriImgName;
        this.uploadImgUrl = uploadImgUrl;
        this.uploadImgPath = uploadImgPath;
        this.repImgYn = repImgYn;
        this.item = item;
    }

    public static ItemImgDTO toItemDTO(ItemImgEntity itemImgEntity) {
        ItemImgDTO itemImgDTO = ItemImgDTO.builder()
                .oriImgName(itemImgEntity.getOriImgName())
                .uploadImgName(itemImgEntity.getUploadImgName())
                .uploadImgUrl(itemImgEntity.getUploadImgUrl())
                .uploadImgPath(itemImgEntity.getUploadImgPath())
                .repImgYn(itemImgEntity.getRepImgYn())
                .item(ItemDTO.builder()
                        .id(itemImgEntity.getItem().getId())
                        .itemNum(itemImgEntity.getItem().getItemNum())
                        .itemDetail(itemImgEntity.getItem().getItemDetail())
                        .itemSellStatus(itemImgEntity.getItem().getItemSellStatus())
                        .price((itemImgEntity.getItem().getPrice()))
                        .stockNumber(itemImgEntity.getItem().getStockNumber())
                        .build())
                .build();

        return itemImgDTO;
    }
}
