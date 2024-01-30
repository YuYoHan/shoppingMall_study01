package com.example.shoppingmall.domain.container.repository;

import com.example.shoppingmall.domain.container.entity.ContainerItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContainerItemRepositoryCustom {
    Page<ContainerItemEntity> findAllPage(Pageable pageable);
}
