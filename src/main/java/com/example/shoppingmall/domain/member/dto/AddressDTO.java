package com.example.shoppingmall.domain.member.dto;

import com.example.shoppingmall.domain.member.entity.AddressEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class AddressDTO {
    @Schema(description = "우편번호")
    private String memberAddr;
    @Schema(description = "주소")
    private String memberAddrDetail;
    @Schema(description = "상세 주소")
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
