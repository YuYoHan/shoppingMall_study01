package com.example.shoppingmall.domain.cart.entity;

import com.example.shoppingmall.domain.cart.dto.CreateCartDTO;
import com.example.shoppingmall.domain.cart.dto.UpdateCartDTO;
import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemSellStatus;
import com.example.shoppingmall.domain.model.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity(name = "cartItem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartItemEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;
    private int count;
    private int price;
    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    public void addCartItemPrice(CreateCartDTO cartItem) {
        // 기존의 갯수에 추가
        this.count += cartItem.getCount();
        this.price = cartItem.getCount() * this.item.getPrice();
    }

    public static CartItemEntity saveCartItem(CreateCartDTO cartDTO,
                                              ItemEntity item) {
        return CartItemEntity.builder()
                .count(cartDTO.getCount())
                .price(item.getPrice() * cartDTO.getCount())
                .status(CartStatus.WAIT)
                .item(item)
                .build();
    }

    public void setCart(CartEntity cart) {
        this.cart = cart;
    }

    public void updateCart(UpdateCartDTO cartDTO) {
        CartItemEntity.builder()
                .cartItemId(this.cartItemId)
                .count(cartDTO.getCount())
                .price(this.item.getPrice() * cartDTO.getCount())
                .status(this.status)
                .cart(this.cart)
                .item(this.item)
                .build();
    }

    public void changeStatus(CartStatus cartStatus) {
        this.status = cartStatus;
    }

    public void orderItem(String email) {
        this.getItem().changeStatus(ItemSellStatus.RESERVED);
        this.getItem().reserveItem(email, this.count);
    }

    public void cancelOrderItem() {
        // 상품 엔티티의 상태 변경 메소드 호출
        this.getItem().changeStatus(ItemSellStatus.SELL);
        this.getItem().cancelReserveItem(this.count);
    }
}
