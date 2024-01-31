package com.example.shoppingmall.domain.cart.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCartItemDTO {
    @Builder.Default
    private List<Long> itemId = new ArrayList<>();
}
