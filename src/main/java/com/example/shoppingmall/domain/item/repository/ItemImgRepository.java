package com.example.shoppingmall.domain.item.repository;

import com.example.shoppingmall.domain.item.entity.ItemImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemImgRepository extends JpaRepository<ItemImgEntity, Long> {
    Optional<ItemImgEntity> findByName(String fileName);

    List<ItemImgEntity> findByItemItemId(Long itemId);
}
