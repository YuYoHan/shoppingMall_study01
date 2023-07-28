package com.example.shoppingmall.repository.item;

import com.example.shoppingmall.entity.item.ItemImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImgRepository extends JpaRepository<ItemImgEntity, Long> {
}
