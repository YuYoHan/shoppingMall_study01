package com.example.shoppingmall.domain.container.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ContainerEntity {
    private String containerName;
    private String containerAddr;
}
