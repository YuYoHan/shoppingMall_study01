package com.example.shoppingmall.domain.container.repository;

import com.example.shoppingmall.domain.container.entity.ContainerItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.global.repository.support.QuerydslRepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.example.shoppingmall.domain.container.entity.QContainerItemEntity.containerItemEntity;
import static com.example.shoppingmall.domain.item.entity.QItemEntity.itemEntity;

public class ContainerItemRepositoryImpl extends QuerydslRepositorySupport implements ContainerItemRepositoryCustom {

    public ContainerItemRepositoryImpl() {
        super(ItemEntity.class);
    }

    @Override
    public Page<ContainerItemEntity> findAllPage(Pageable pageable) {
        return applyPagination(pageable, content -> content
                .selectFrom(containerItemEntity)
                .join(containerItemEntity.item, itemEntity).fetchJoin()
                , count -> count
                        .select(containerItemEntity.count())
                        .from(containerItemEntity));
    }
}
