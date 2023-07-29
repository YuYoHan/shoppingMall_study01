package com.example.shoppingmall.entity.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemEntity is a Querydsl query type for ItemEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemEntity extends EntityPathBase<ItemEntity> {

    private static final long serialVersionUID = 443254247L;

    public static final QItemEntity itemEntity = new QItemEntity("itemEntity");

    public final com.example.shoppingmall.entity.base.QBaseTimeEntity _super = new com.example.shoppingmall.entity.base.QBaseTimeEntity(this);

    public final StringPath itemDetail = createString("itemDetail");

    public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

    public final ListPath<ItemImgEntity, QItemImgEntity> itemImgList = this.<ItemImgEntity, QItemImgEntity>createList("itemImgList", ItemImgEntity.class, QItemImgEntity.class, PathInits.DIRECT2);

    public final StringPath itemNum = createString("itemNum");

    public final EnumPath<com.example.shoppingmall.dto.item.ItemSellStatus> itemSellStatus = createEnum("itemSellStatus", com.example.shoppingmall.dto.item.ItemSellStatus.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regTime = _super.regTime;

    public final NumberPath<Integer> stockNumber = createNumber("stockNumber", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public QItemEntity(String variable) {
        super(ItemEntity.class, forVariable(variable));
    }

    public QItemEntity(Path<? extends ItemEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItemEntity(PathMetadata metadata) {
        super(ItemEntity.class, metadata);
    }

}

