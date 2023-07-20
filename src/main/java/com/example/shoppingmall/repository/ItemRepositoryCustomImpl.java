package com.example.shoppingmall.repository;

import com.example.shoppingmall.constant.ItemSellStatus;
import com.example.shoppingmall.entity.QItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    //
    private JPAQueryFactory queryFactory;

    //
    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        //
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    //
    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.eq)
    }
}
