package com.example.shoppingmall.repository.item;

import com.example.shoppingmall.entity.item.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
}
