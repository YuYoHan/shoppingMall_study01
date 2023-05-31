package com.example.shoppingmall.entity;

import com.example.shoppingmall.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 한 명의 회원은 여러 번 주문할 수 있으므로 주문 엔티티 기준에서 다대일 단방향 매핑을 합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private LocalDateTime orderDate; // 주문일

    // 주문 상품 엔티티와 일대 매핑을 합니다.
    // 외래키(order_id)가 order_item 테이블에 있으므로
    // 연관 관계의 주인은 OrderItem 엔티티입니다. Order 엔티티가 주인이 아니므로
    // mappedBy 속성으로 연관 관계의 주인을 설정합니다.
    // 속성 값으로 order을 적어준 이유는 OrderItem에 있는 Order에 의해
    // 관리된다고 해석하면 됩니다. 즉, 연관 관계의 주인의 필드인 order를
    // mappedBy의 값으로 세팅하면 됩니다.
    // 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CascadeTypeAll 옵션을 설정
    // orphanRemoval = true : 고아 객체 제거를 사용하기 위해 사용
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true,
    fetch = FetchType.LAZY)
    // 하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해서 매핑합니다.
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
