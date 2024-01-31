package com.example.shoppingmall.domain.item.repository;

import com.example.shoppingmall.domain.item.dto.ItemSearchCondition;
import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemSellStatus;
import com.example.shoppingmall.global.repository.support.QuerydslRepositorySupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.example.shoppingmall.domain.item.entity.QItemEntity.itemEntity;
import static com.example.shoppingmall.domain.member.entity.QMemberEntity.memberEntity;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class ItemQuerydslRepository extends QuerydslRepositorySupport {
    public ItemQuerydslRepository() {
        super(ItemEntity.class);
    }

    public Page<ItemEntity> itemSearch(ItemSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable, contentQuery -> contentQuery
                        .selectFrom(itemEntity)
                        .join(itemEntity.member, memberEntity).fetchJoin()
                        .where(nameEq(condition.getName()),
                                detailEq(condition.getDetail()),
                                priceEq(condition.getStartP(), condition.getEndP()),
                                placeEq(condition.getPlace()),
                                reserverEq(condition.getReserver()),
                                itemStatusEq(condition.getStatus())),
                countQuery -> countQuery
                        .select(itemEntity.count())
                        .from(itemEntity)
                        .where(nameEq(condition.getName()),
                                detailEq(condition.getDetail()),
                                priceEq(condition.getStartP(), condition.getEndP()),
                                placeEq(condition.getPlace()),
                                reserverEq(condition.getReserver()),
                                itemStatusEq(condition.getStatus())));

    }

    /* 조건을 동정으로 처리하기 위해서 동적 쿼리 적용 */
    private BooleanExpression nameEq(String name) {
        // likeIgnoreCase는 QueryDSL에서 문자열에 대한 대소문자를 무시하고 부분 일치 검색을 수행하는 메서드입니다.
        // 이 메서드는 SQL에서의 LIKE 연산과 유사하지만, 대소문자를 구분하지 않고 비교합니다.
        return hasText(name) ? itemEntity.itemName.likeIgnoreCase("%"+ name + "%") : null;
    }

    private BooleanExpression detailEq(String detail) {
        return hasText(detail) ? itemEntity.itemDetail.eq(detail) : null;
    }

    private BooleanBuilder priceEq(Long startP, Long endP) {
        BooleanBuilder builder = new BooleanBuilder();
        if (startP != null) {
            builder.and(priceGoe(startP));
        }
        if (endP != null) {
            builder.and(priceLoe(endP));
        }

        return builder;
    }
    // 이상
    private BooleanExpression priceGoe(Long startP) {
        return startP != null ? itemEntity.price.goe(startP) : null;
    }

    // 이하
    private BooleanExpression priceLoe(Long endP) {
        return endP != null ? itemEntity.price.loe(endP) : null;
    }

    private BooleanBuilder placeEq(String place) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(placeNameEq(place));
        builder.or(placeAddrEq(place));
        return builder;
    }

    private BooleanExpression placeNameEq(String place) {
        return hasText(place) ? itemEntity.itemPlace.containerName.likeIgnoreCase("%" + place +"%") : null;
    }
    private BooleanExpression placeAddrEq(String place) {
        return hasText(place) ? itemEntity.itemPlace.containerAddr.likeIgnoreCase("%" + place +"%") : null;
    }
    private BooleanExpression reserverEq(String reserver) {
        return hasText(reserver) ? itemEntity.itemReserver.eq(reserver) : null;
    }

    private Predicate itemStatusEq(ItemSellStatus status) {
        return status != null ? itemEntity.itemSellStatus.eq(status) : null;
    }

}
