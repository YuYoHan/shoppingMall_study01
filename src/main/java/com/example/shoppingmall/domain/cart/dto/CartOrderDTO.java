package com.example.shoppingmall.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주문을 예약 넣는 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrderDTO {
    private Long cartItemId;
}
