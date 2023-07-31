package com.example.shoppingmall.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class CartItemDTO {
    @Schema(description = "카트 상품 번호")
    private Long cartItemId;
    @Schema(description = "카트 정보")
    private CartDTO cart;
    @Schema(description = "상품 정보")
    private ItemDTO item;
    @Schema(description = "갯수")
    private int count;

    @Builder
    public CartItemDTO(Long cartItemId,
                       CartDTO cart,
                       ItemDTO item,
                       int count) {
        this.cartItemId = cartItemId;
        this.cart = cart;
        this.item = item;
        this.count = count;
    }
}
