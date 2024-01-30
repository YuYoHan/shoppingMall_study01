package com.example.shoppingmall.domain.cart.dto;

import com.example.shoppingmall.domain.cart.entity.CartItemEntity;
import lombok.*;

import javax.validation.constraints.Min;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCartDTO {
    private Long itemId;
    @Min(value = 1, message = "최소 1개 이상 담아야 합니다.")
    private int count;

    public static CreateCartDTO changeDTO(CartItemEntity cartItem) {
        return CreateCartDTO.builder()
                .itemId(cartItem.getItem().getItemId())
                .count(cartItem.getCount())
                .build();
    }
}
