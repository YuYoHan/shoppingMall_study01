package com.example.shoppingmall.domain.order.repository;

import com.example.shoppingmall.domain.order.entity.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    @Query("select oi from orderItem oi" +
    " join fetch oi.item" +
    " where (:orderId is null or oi.order.orderId = :orderId)")
    Page<OrderItemEntity> findByOrder_OrderId(Pageable pageable,
                                              @Param("orderId") Long orderId);
}
