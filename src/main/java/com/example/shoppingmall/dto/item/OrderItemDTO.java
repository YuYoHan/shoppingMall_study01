package com.example.shoppingmall.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class OrderItemDTO {
    @Schema(description = "주문 상품 번호")
    private Long orderItemId;

    @Schema(description = "상품 정보")
    private ItemDTO item;

    @Schema(description = "주문 정보")
    private OrderDTO order;

    @Schema(description = "주문 상품 가격")
    private int price;

    @Schema(description = "주문 상품 갯수")
    private int count;

    @Schema(description = "주문 상품 등록 시간")
    private LocalDateTime regTime;

    @Schema(description = "주문 상품 업데이트 시간")
    private LocalDateTime updateTime;

    @Builder
    public OrderItemDTO(Long orderItemId,
                        ItemDTO item,
                        OrderDTO order,
                        int price,
                        int count,
                        LocalDateTime regTime,
                        LocalDateTime updateTime) {
        this.orderItemId = orderItemId;
        this.item = item;
        this.order = order;
        this.price = price;
        this.count = count;
        this.regTime = regTime;
        this.updateTime = updateTime;
    }
}
