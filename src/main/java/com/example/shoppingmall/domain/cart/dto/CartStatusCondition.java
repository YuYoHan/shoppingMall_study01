package com.example.shoppingmall.domain.cart.dto;

import com.example.shoppingmall.domain.cart.entity.CartStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CartStatusCondition {
    private CartStatus cartStatus;

    @Builder

    public CartStatusCondition(CartStatus cartStatus) {
        this.cartStatus = cartStatus;
    }
}
