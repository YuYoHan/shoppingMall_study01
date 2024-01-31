package com.example.shoppingmall.domain.order.dto;

import com.example.shoppingmall.domain.item.dto.ResponseItemDTO;
import com.example.shoppingmall.domain.order.entity.OrderItemEntity;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseOrderItemDTO {
    private Long orderItemId;
    private int orderPrice;
    private int count;
    private ResponseItemDTO item;

    public static ResponseOrderItemDTO changeDTO(OrderItemEntity orderItem) {
        return ResponseOrderItemDTO.builder()
                .orderItemId(orderItem.getOrderItemId())
                .orderPrice(orderItem.getPrice())
                .count(orderItem.getCount())
                .item(ResponseItemDTO.changeDTO(orderItem.getItem()))
                .build();
    }
}
