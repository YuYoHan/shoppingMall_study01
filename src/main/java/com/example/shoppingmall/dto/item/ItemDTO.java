package com.example.shoppingmall.dto.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class ItemDTO {
    private Long id;            // 상품 코드
    private String itemNum;     // 상품 명
    private int price;          // 가격
    private int stockNumber;    // 재고수량
    private String itemDetail;  // 상품 상세 설명
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    @Builder
    public ItemDTO(Long id,
                   String itemNum,
                   int price,
                   int stockNumber,
                   String itemDetail,
                   ItemSellStatus itemSellStatus,
                   LocalDateTime regTime,
                   LocalDateTime updateTime) {
        this.id = id;
        this.itemNum = itemNum;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
        this.regTime = regTime;
        this.updateTime = updateTime;
    }
}