package com.example.shoppingmall.domain.cart.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateCartDTO {
    private Long itemId;
    private int count;
}
