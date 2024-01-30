package com.example.shoppingmall.domain.cart.repository;

import com.example.shoppingmall.domain.cart.dto.CartStatusCondition;
import com.example.shoppingmall.domain.cart.entity.CartItemEntity;
import com.example.shoppingmall.domain.cart.entity.CartStatus;
import com.example.shoppingmall.domain.member.entity.Role;
import com.example.shoppingmall.global.repository.support.QuerydslRepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.example.shoppingmall.domain.cart.entity.QCartItemEntity.cartItemEntity;
import static com.example.shoppingmall.domain.item.entity.QItemEntity.itemEntity;
import static com.example.shoppingmall.domain.member.entity.QMemberEntity.memberEntity;

@Repository
public class CartItemQuerydslRepository extends QuerydslRepositorySupport {
    public CartItemQuerydslRepository() {
        super(CartItemEntity.class);
    }

    public Page<CartItemEntity> findAllCart(Pageable pageable, CartStatusCondition condition) {
        return applyPagination(pageable, content -> content
                .selectFrom(cartItemEntity)
                .join(cartItemEntity.item, itemEntity).fetchJoin()
                .leftJoin(memberEntity).on(memberEntity.memberRole.eq(Role.USER))
                .where(statusEq(condition.getCartStatus())),
                count -> count
                        .select(cartItemEntity.count())
                        .from(cartItemEntity)
                        .where(statusEq(condition.getCartStatus())));
    }

    private BooleanExpression statusEq(CartStatus cartStatus) {
        return cartStatus != null ? cartItemEntity.status.eq(cartStatus) : null;
    }
}
