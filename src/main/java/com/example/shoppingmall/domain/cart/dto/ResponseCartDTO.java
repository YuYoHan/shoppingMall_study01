package com.example.shoppingmall.domain.cart.dto;

import com.example.shoppingmall.domain.cart.entity.CartEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class ResponseCartDTO {
    private Long cartId;
    private Long memberId;
    private int totalPrice;
    @Builder.Default
    private List<ResponseCartItemDTO> cartItems = new ArrayList<>();


    public static ResponseCartDTO changeDTO(CartEntity cart) {
        List<ResponseCartItemDTO> responseCartItemDTOS = cart.getCartItems().stream()
                .map(ResponseCartItemDTO::changeDTO)
                .collect(Collectors.toList());

        return ResponseCartDTO.builder()
                .cartId(cart.getCartId())
                .memberId(cart.getMember().getMemberId())
                .totalPrice(cart.getTotalPrice())
                .cartItems(responseCartItemDTOS)
                .build();
    }

    public void addList(List<ResponseCartItemDTO> cartItems) {
        this.cartItems = cartItems;
    }
}
