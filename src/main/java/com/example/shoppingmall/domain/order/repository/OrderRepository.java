package com.example.shoppingmall.domain.order.repository;

import com.example.shoppingmall.domain.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    OrderEntity findByOrderMember_MemberId(Long memberId);
}
