package com.example.shoppingmall.dto.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private CartDTO cart;
    private ItemDTO item;
    private int count;

    @Builder
    public CartItemDTO(Long id, CartDTO cart, ItemDTO item, int count) {
        this.id = id;
        this.cart = cart;
        this.item = item;
        this.count = count;
    }
}
