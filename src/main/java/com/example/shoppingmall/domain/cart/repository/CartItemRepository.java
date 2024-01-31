package com.example.shoppingmall.domain.cart.repository;

import com.example.shoppingmall.domain.cart.entity.CartItemEntity;
import com.example.shoppingmall.domain.cart.entity.CartStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    CartItemEntity findByItem_ItemId(Long itemId);

    @Query("select c from cartItem c" +
            " where c.cart.cartId = :cartId and c.item.itemId = :itemId")
    CartItemEntity findCartItem(@Param("cartId") Long cartId, @Param("itemId") Long itemId);

    // 벌크 연산
    // 데이터베이스의 부하를 줄이기 위해서 사용
    @Modifying(clearAutomatically = true)
    @Transactional
    // fetch join과 같습니다.
    // 상품 엔티티를 한번에 가져오기 위해서 사용하고 있습니다.
    @EntityGraph(attributePaths = "item")
    @Query("update cartItem c set c.status = :newStatus where c.cartItemId in :cartItemIds")
    List<CartItemEntity> updateCartItemsStatus(@Param("newStatus") CartStatus newStatus,
                               @Param("cartItemIds") List<Long> cartItemIds);

}
