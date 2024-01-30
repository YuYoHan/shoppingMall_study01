package com.example.shoppingmall.domain.order.dto;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestOrderDTO {
    private Long itemId;
    private Long cartItemId;
}
