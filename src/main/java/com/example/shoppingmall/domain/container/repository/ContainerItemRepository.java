package com.example.shoppingmall.domain.container.repository;

import com.example.shoppingmall.domain.container.entity.ContainerItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerItemRepository extends JpaRepository<ContainerItemEntity, Long>, ContainerItemRepositoryCustom {
    ContainerItemEntity findByContainerName(String containerName);
}
