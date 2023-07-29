package com.example.shoppingmall.entity.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemImgEntity is a Querydsl query type for ItemImgEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemImgEntity extends EntityPathBase<ItemImgEntity> {

    private static final long serialVersionUID = -2108737278L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemImgEntity itemImgEntity = new QItemImgEntity("itemImgEntity");

    public final com.example.shoppingmall.entity.base.QBaseEntity _super = new com.example.shoppingmall.entity.base.QBaseEntity(this);

    //inherited
    public final StringPath createBy = _super.createBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QItemEntity item;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath oriImgName = createString("oriImgName");

    public final StringPath repImgYn = createString("repImgYn");

    public final StringPath uploadImgName = createString("uploadImgName");

    public final StringPath uploadImgPath = createString("uploadImgPath");

    public final StringPath uploadImgUrl = createString("uploadImgUrl");

    public QItemImgEntity(String variable) {
        this(ItemImgEntity.class, forVariable(variable), INITS);
    }

    public QItemImgEntity(Path<? extends ItemImgEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemImgEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemImgEntity(PathMetadata metadata, PathInits inits) {
        this(ItemImgEntity.class, metadata, inits);
    }

    public QItemImgEntity(Class<? extends ItemImgEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new QItemEntity(forProperty("item")) : null;
    }

}

