package com.example.shoppingmall.entity.order;

import com.example.shoppingmall.dto.order.OrderStatus;
import com.example.shoppingmall.entity.base.BaseTimeEntity;
import com.example.shoppingmall.entity.item.ItemEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@Table
@ToString
@Getter
@NoArgsConstructor
public class OrderEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    // 한명의 회원은 여러 번 주문을 할 수 있기 때문에
    // 주문 엔티티 기준에서 다대일 단방향 매핑을 합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private LocalDateTime orderDate;    // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    // 주문 상태

    // 외래키(order_id)가 order_item 테이블에 있으므로 연관 관계의 주인은
    // OrderItemEntity 입니다. order 엔티티가 주인이 아니므로 "mappedBy"
    // 속성으로 연관 관계의 주인을 설정합니다.
    // 속성의 값으로 order 를 적어준 이유는 OrderItemEntity 에 있는 order에 의해
    // 관리된다는 의미로 해석하면 됩니다.
    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    // 하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해서 매핑합니다.
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;


    @Builder
    public OrderEntity(Long orderId,
                       MemberEntity member,
                       LocalDateTime orderDate,
                       OrderStatus orderStatus,
                       List<OrderItemEntity> orderItems,
                       ItemEntity item) {
        this.orderId = orderId;
        this.member = member;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
        this.item = item;
    }
}
