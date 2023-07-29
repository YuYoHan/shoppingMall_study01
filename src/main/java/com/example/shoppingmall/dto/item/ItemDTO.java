package com.example.shoppingmall.dto.item;

import com.example.shoppingmall.entity.item.ItemEntity;
import com.example.shoppingmall.entity.item.ItemImgEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
public class ItemDTO {
    private Long itemId;            // 상품 코드
    @NotBlank(message = "상품명은 필수 입력입니다.")
    private String itemNum;     // 상품 명
    @NotNull(message = "가격은 필수 입력입니다.")
    private int price;          // 가격
    @NotNull(message = "재고 수량은 필수 입력입니다.")
    private int stockNumber;    // 재고수량
    @NotNull(message = "설명은 필수 입력입니다.")
    private String itemDetail;  // 상품 상세 설명
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    // 상품 저장 후 수정할 때 상품 이미지 정보를 저장하는 리스트
    private List<ItemImgDTO> itemImgList = new ArrayList<>();

    @Builder
    public ItemDTO(Long itemId,
                   String itemNum,
                   int price,
                   int stockNumber,
                   String itemDetail,
                   ItemSellStatus itemSellStatus,
                   LocalDateTime regTime,
                   LocalDateTime updateTime,
                   List<ItemImgDTO> itemImgList
                   ) {
        this.itemId = itemId;
        this.itemNum = itemNum;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
        this.regTime = regTime;
        this.updateTime = updateTime;
        this.itemImgList = itemImgList;
    }

    public static ItemDTO toItemDTO(ItemEntity item) {
        List<ItemImgEntity> itemImgEntities = item.getItemImgList();
        List<ItemImgDTO> itemDTOList = new ArrayList<>();

        for(ItemImgEntity itemImgEntity : itemImgEntities) {
            ItemImgDTO itemImgDTO = ItemImgDTO.builder()
                    .oriImgName(itemImgEntity.getOriImgName())
                    .uploadImgName(itemImgEntity.getUploadImgName())
                    .uploadImgUrl(itemImgEntity.getUploadImgUrl())
                    .uploadImgPath(itemImgEntity.getUploadImgPath())
                    .repImgYn(itemImgEntity.getRepImgYn())
                    .build();

            itemDTOList.add(itemImgDTO);
        }

        return ItemDTO.builder()
                .itemId(item.getItemId())
                .itemNum(item.getItemNum())
                .price(item.getPrice())
                .stockNumber(item.getStockNumber())
                .itemDetail(item.getItemDetail())
                .itemSellStatus(item.getItemSellStatus())
                .regTime(item.getRegTime())
                .updateTime(item.getUpdateTime())
                .itemImgList(itemDTOList) // 이미지 정보를 추가합니다.
                .build();
    }
}
