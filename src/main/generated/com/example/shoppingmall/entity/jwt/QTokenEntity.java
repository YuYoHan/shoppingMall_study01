package com.example.shoppingmall.entity.jwt;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTokenEntity is a Querydsl query type for TokenEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTokenEntity extends EntityPathBase<TokenEntity> {

    private static final long serialVersionUID = -1976720469L;

    public static final QTokenEntity tokenEntity = new QTokenEntity("tokenEntity");

    public final StringPath accessToken = createString("accessToken");

    public final DateTimePath<java.util.Date> accessTokenTime = createDateTime("accessTokenTime", java.util.Date.class);

    public final StringPath grantType = createString("grantType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickName = createString("nickName");

    public final StringPath refreshToken = createString("refreshToken");

    public final DateTimePath<java.util.Date> refreshTokenTime = createDateTime("refreshTokenTime", java.util.Date.class);

    public final EnumPath<com.example.shoppingmall.dto.member.Role> role = createEnum("role", com.example.shoppingmall.dto.member.Role.class);

    public final StringPath userEmail = createString("userEmail");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTokenEntity(String variable) {
        super(TokenEntity.class, forVariable(variable));
    }

    public QTokenEntity(Path<? extends TokenEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTokenEntity(PathMetadata metadata) {
        super(TokenEntity.class, metadata);
    }

}

