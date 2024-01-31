package com.example.shoppingmall.domain.container.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ContainerDTO {
    private String containerName;
    private String containerAddr;

    @Builder
    public ContainerDTO(String containerName, String containerAddr) {
        this.containerName = containerName;
        this.containerAddr = containerAddr;
    }
}
