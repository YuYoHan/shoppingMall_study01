package com.example.shoppingmall.domain.item.dto;

import com.example.shoppingmall.domain.item.entity.ItemSellStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class ItemSearchCondition {
    private String name;
    private String detail;
    // 시작 가격
    private Long startP;
    // 끝 가격
    private Long endP;
    // 장소
    private String place;
    // 예약자
    private String reserver;
    // 상품 상태
    private ItemSellStatus status;
}
