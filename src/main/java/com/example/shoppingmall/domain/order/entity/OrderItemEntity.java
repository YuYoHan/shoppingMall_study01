package com.example.shoppingmall.domain.order.entity;

import com.example.shoppingmall.domain.cart.entity.CartItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.model.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity(name = "orderItem")
@Getter
@ToString(exclude = {"item", "order"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItemEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    private int price;
    private int count;

    public static OrderItemEntity saveOrderItem(CartItemEntity cartItem) {
        return OrderItemEntity.builder()
                .item(cartItem.getItem())
                .price(cartItem.getPrice())
                .count(cartItem.getCount())
                .build();
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}
