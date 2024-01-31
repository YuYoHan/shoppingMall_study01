package com.example.shoppingmall.domain.order.entity;

import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.model.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@ToString(exclude = {"orderMember", "orderItems"})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity orderMember;

    private String seller;
    private int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    // 주문 금액 총액
    public void totalPrice() {
        this.totalPrice = this.orderItems.stream()
                .mapToInt(OrderItemEntity::getPrice)
                .sum();
    }

    public static OrderEntity createOrder(MemberEntity member,
                                          ItemEntity item,
                                          OrderItemEntity orderItem) {
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        return OrderEntity.builder()
                .orderMember(member)
                .seller(item.getMember().getEmail())
                .orderItems(orderItems)
                .build();
    }
}
