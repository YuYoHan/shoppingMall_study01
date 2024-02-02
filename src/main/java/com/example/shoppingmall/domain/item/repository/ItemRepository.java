package com.example.shoppingmall.domain.item.repository;

import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemSellStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    ItemEntity findByItemId(Long itemId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update item i set i.itemSellStatus = :newStatus where i.itemId in :itemIds")
    void updateItemsStatus(@Param("newStatus") ItemSellStatus newStatus,
                                               @Param("itemIds") List<Long> itemIds);
}
