package com.example.shoppingmall.domain.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;

@Embeddable
@ToString
@Getter
@NoArgsConstructor
public class AddressEntity {
    private String memberAddr;
    private String memberAddrDetail;
    private String memberZipCode;

    @Builder
    public AddressEntity(String memberAddr, String memberAddrDetail, String memberZipCode) {
        this.memberAddr = memberAddr;
        this.memberAddrDetail = memberAddrDetail;
        this.memberZipCode = memberZipCode;
    }
}
