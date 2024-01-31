package com.example.shoppingmall.domain.cart.entity;

import com.example.shoppingmall.domain.cart.dto.ResponseCartDTO;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.model.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;
    private int totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> cartItems = new ArrayList<>();

    public void addCartItems(CartItemEntity cartItem) {
        this.cartItems.add(cartItem);
    }

    public void totalPrice() {
        this.totalPrice = this.cartItems.stream()
                .mapToInt(CartItemEntity::getPrice)
                .sum();
    }

    public static CartEntity changeEntity(ResponseCartDTO cartDTO,
                                          MemberEntity member) {
        return CartEntity.builder()
                .totalPrice(cartDTO.getTotalPrice())
                .member(member)
                .cartItems(new ArrayList<>())
                .build();
    }

    public static CartEntity saveCart(MemberEntity member) {
        return CartEntity.builder()
                .totalPrice(0)
                .member(member)
                .cartItems(new ArrayList<>())
                .build();
    }
}
