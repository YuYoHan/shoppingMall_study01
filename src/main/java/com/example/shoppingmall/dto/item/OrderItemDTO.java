package com.example.shoppingmall.dto.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private ItemDTO item;
    private OrderDTO order;
    private int price;
    private int count;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    @Builder
    public OrderItemDTO(Long id,
                        ItemDTO item,
                        OrderDTO order,
                        int price,
                        int count,
                        LocalDateTime regTime,
                        LocalDateTime updateTime) {
        this.id = id;
        this.item = item;
        this.order = order;
        this.price = price;
        this.count = count;
        this.regTime = regTime;
        this.updateTime = updateTime;
    }
}
