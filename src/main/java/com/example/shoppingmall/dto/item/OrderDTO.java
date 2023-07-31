package com.example.shoppingmall.dto.item;

import com.example.shoppingmall.dto.member.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@Getter
public class OrderDTO {
    @Schema(description = "주문 번호")
    private Long orderId;

    @Schema(description = "회원 정보")
    private MemberDTO member;

    @Schema(description = "주문 날짜")
    private LocalDateTime orderDate;

    @Schema(description = "주문 상태")
    private OrderStatus orderStatus;

    @Schema(description = "주문 등록 시간")
    private LocalDateTime regTime;

    @Schema(description = "주문 업데이트 시간")
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
