package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.base.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "cart_item")
@ToString
@Getter
@NoArgsConstructor
@Table
public class CartItemEntity extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    // 하나의 장바구니에는 여러 개의 상품을 담을 수 있으므로
    // 다대일 관계를 맺어준다.
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    // 장바구니에 담을 상품의 정보를 알아야 하기 때문에
    // 상품 엔티티에 연결해준다.
    // 하나의 상품은 여러 장바구니의 장바구니 상품으로
    // 담길 수 있으므로 다대일 관계를 맺어준다.
    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    // 같은상품을 장바구니에 몇 개 담을지 저장합니다.
    private int count;
}
