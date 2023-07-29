package com.example.shoppingmall.dto.item;

import com.example.shoppingmall.dto.member.MemberDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@Getter
public class OrderDTO {
    private Long orderId;
    private MemberDTO member;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    @Builder
    public OrderDTO(Long orderId,
                    MemberDTO member,
                    LocalDateTime orderDate,
                    OrderStatus orderStatus,
                    LocalDateTime regTime,
                    LocalDateTime updateTime) {
        this.orderId = orderId;
        this.member = member;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.regTime = regTime;
        this.updateTime = updateTime;
    }
}
