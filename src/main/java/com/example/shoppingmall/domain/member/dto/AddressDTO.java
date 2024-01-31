package com.example.shoppingmall.domain.member.dto;

import com.example.shoppingmall.domain.member.entity.AddressEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class AddressDTO {
    private String memberAddr;
    private String memberAddrDetail;
    private String memberZipCode;

    @Builder
    public AddressDTO(String memberAddr, String memberAddrDetail, String memberZipCode) {
        this.memberAddr = memberAddr;
        this.memberAddrDetail = memberAddrDetail;
        this.memberZipCode = memberZipCode;
    }

    public AddressEntity toEntity(){
        return AddressEntity.builder()
                .memberAddr(this.memberAddr)
                .memberAddrDetail(this.memberAddrDetail)
                .memberZipCode(this.memberZipCode)
                .build();
    }

}
