package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.entity.base.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "order_item")
@Table
@ToString
@Getter
@NoArgsConstructor
public class OrderItemEntity extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    // 하나의 상품은 여러 주문 상품으로 들어갈 수 있으므로
    // 주문 상품 기준으로 다대일 단방향 매핑을 한다.
    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    // 한 번의 주문에 여러 개의 상품을 주문할 수 있으므로
    // 주문 상품 엔티티와 주문 엔티티를 다대일 단방향 매핑을 한다.
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    private int orderPrice;
    private int count;

    @Builder
    public OrderItemEntity(Long id,
                           ItemEntity item,
                           OrderEntity order,
                           int orderPrice,
                           int count) {
        this.id = id;
        this.item = item;
        this.order = order;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
