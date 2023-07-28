package com.example.shoppingmall.repository.item;

import com.example.shoppingmall.entity.item.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
