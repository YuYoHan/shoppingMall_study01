package com.example.shoppingmall.entity.member.embedded;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAddressEntity is a Querydsl query type for AddressEntity
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QAddressEntity extends BeanPath<AddressEntity> {

    private static final long serialVersionUID = -551617795L;

    public static final QAddressEntity addressEntity = new QAddressEntity("addressEntity");

    public final StringPath userAddr = createString("userAddr");

    public final StringPath userAddrDetail = createString("userAddrDetail");

    public final StringPath userAddrEtc = createString("userAddrEtc");

    public QAddressEntity(String variable) {
        super(AddressEntity.class, forVariable(variable));
    }

    public QAddressEntity(Path<? extends AddressEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAddressEntity(PathMetadata metadata) {
        super(AddressEntity.class, metadata);
    }

}

