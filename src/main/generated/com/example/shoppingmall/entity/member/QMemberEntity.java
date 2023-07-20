package com.example.shoppingmall.entity.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberEntity is a Querydsl query type for MemberEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberEntity extends EntityPathBase<MemberEntity> {

    private static final long serialVersionUID = 1654301255L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberEntity memberEntity = new QMemberEntity("memberEntity");

    public final com.example.shoppingmall.entity.base.QBaseEntity _super = new com.example.shoppingmall.entity.base.QBaseEntity(this);

    public final com.example.shoppingmall.entity.member.embedded.QAddressEntity address;

    //inherited
    public final StringPath createBy = _super.createBy;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath nickName = createString("nickName");

    public final EnumPath<com.example.shoppingmall.dto.member.Role> role = createEnum("role", com.example.shoppingmall.dto.member.Role.class);

    public final StringPath userEmail = createString("userEmail");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userName = createString("userName");

    public final StringPath userPw = createString("userPw");

    public QMemberEntity(String variable) {
        this(MemberEntity.class, forVariable(variable), INITS);
    }

    public QMemberEntity(Path<? extends MemberEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberEntity(PathMetadata metadata, PathInits inits) {
        this(MemberEntity.class, metadata, inits);
    }

    public QMemberEntity(Class<? extends MemberEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.example.shoppingmall.entity.member.embedded.QAddressEntity(forProperty("address")) : null;
    }

}

