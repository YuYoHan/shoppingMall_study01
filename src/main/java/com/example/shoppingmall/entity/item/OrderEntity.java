package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.dto.item.OrderStatus;
import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.base.BaseTimeEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "orders")
@Table
@ToString
@Getter
@NoArgsConstructor
public class OrderEntity extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 한명의 회원은 여러 번 주문을 할 수 있기 때문에
    // 주문 엔티티 기준에서 다대일 단방향 매핑을 합니다.
    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private LocalDateTime orderDate;    // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    // 주문 상태

    @Builder
    public OrderEntity(Long id,
                       MemberEntity member,
                       LocalDateTime orderDate,
                       OrderStatus orderStatus) {
        this.id = id;
        this.member = member;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }
}
