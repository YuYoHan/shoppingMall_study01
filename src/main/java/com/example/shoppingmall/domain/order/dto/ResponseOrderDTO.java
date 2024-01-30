package com.example.shoppingmall.domain.order.dto;

import com.example.shoppingmall.domain.order.entity.OrderEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseOrderDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private String seller;
    private Long orderMember;
    @Builder.Default
    private List<ResponseOrderItemDTO> orderItems = new ArrayList<>();


    public static ResponseOrderDTO createOrder(OrderEntity order) {
        List<ResponseOrderItemDTO> collect = order.getOrderItems().stream()
                .map(ResponseOrderItemDTO::changeDTO)
                .collect(Collectors.toList());

        return ResponseOrderDTO.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getRegTime())
                .seller(order.getSeller())
                .orderItems(collect)
                .build();
    }
}
