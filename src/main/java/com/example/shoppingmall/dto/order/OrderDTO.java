package com.example.shoppingmall.dto.order;

import com.example.shoppingmall.dto.member.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Schema(description = "주문한 상품")
    private List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

    @Builder
    public OrderDTO(Long orderId,
                    MemberDTO member,
                    LocalDateTime orderDate,
                    OrderStatus orderStatus,
                    List<OrderItemDTO> orderItemDTOList) {
        this.orderId = orderId;
        this.member = member;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderItemDTOList = orderItemDTOList;
    }
}
