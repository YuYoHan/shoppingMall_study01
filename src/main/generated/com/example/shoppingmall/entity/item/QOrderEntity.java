package com.example.shoppingmall.entity.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderEntity is a Querydsl query type for OrderEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderEntity extends EntityPathBase<OrderEntity> {

    private static final long serialVersionUID = -88952352L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderEntity orderEntity = new QOrderEntity("orderEntity");

    public final com.example.shoppingmall.entity.base.QBaseTimeEntity _super = new com.example.shoppingmall.entity.base.QBaseTimeEntity(this);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.shoppingmall.entity.member.QMemberEntity member;

    public final DateTimePath<java.time.LocalDateTime> orderDate = createDateTime("orderDate", java.time.LocalDateTime.class);

    public final ListPath<OrderItemEntity, QOrderItemEntity> orderItems = this.<OrderItemEntity, QOrderItemEntity>createList("orderItems", OrderItemEntity.class, QOrderItemEntity.class, PathInits.DIRECT2);

    public final EnumPath<com.example.shoppingmall.dto.item.OrderStatus> orderStatus = createEnum("orderStatus", com.example.shoppingmall.dto.item.OrderStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regTime = _super.regTime;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public QOrderEntity(String variable) {
        this(OrderEntity.class, forVariable(variable), INITS);
    }

    public QOrderEntity(Path<? extends OrderEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderEntity(PathMetadata metadata, PathInits inits) {
        this(OrderEntity.class, metadata, inits);
    }

    public QOrderEntity(Class<? extends OrderEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.example.shoppingmall.entity.member.QMemberEntity(forProperty("member"), inits.get("member")) : null;
    }

}

