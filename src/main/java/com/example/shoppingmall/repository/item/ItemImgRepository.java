package com.example.shoppingmall.repository.item;

import com.example.shoppingmall.entity.item.ItemImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImgRepository extends JpaRepository<ItemImgEntity, Long> {
    List<ItemImgEntity> findByItemIdOrderByIdAsc(Long itemId);
    void deleteByItemId(Long itemId);
    List<ItemImgEntity> findByItemId(Long itemId);
}
