package com.example.shoppingmall.domain.cart.dto;

import com.example.shoppingmall.domain.cart.entity.CartItemEntity;
import com.example.shoppingmall.domain.cart.entity.CartStatus;
import com.example.shoppingmall.domain.item.dto.ResponseItemDTO;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseCartItemDTO {
    private Long cartItemId;
    private String name;
    private int price;
    private int count;
    private CartStatus cartStatus;
    private ResponseItemDTO item;

    public static ResponseCartItemDTO changeDTO(CartItemEntity cartItem) {
        return ResponseCartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .name(cartItem.getItem().getItemName())
                .price(cartItem.getPrice())
                .count(cartItem.getCount())
                .cartStatus(cartItem.getStatus())
                .item(ResponseItemDTO.changeDTO(cartItem.getItem()))
                .build();
    }

}
