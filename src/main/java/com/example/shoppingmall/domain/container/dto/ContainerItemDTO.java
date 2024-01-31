package com.example.shoppingmall.domain.container.dto;

import com.example.shoppingmall.domain.container.entity.ContainerItemEntity;
import com.example.shoppingmall.domain.item.dto.ResponseItemDTO;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ContainerItemDTO {
    private String containerName;
    private String containerAddr;
    private ResponseItemDTO itemDTO;

    public static ContainerItemDTO changeDTO(ContainerItemEntity container) {
        return ContainerItemDTO.builder()
                .containerName(container.getContainerName())
                .containerAddr(container.getContainerAddr())
                .itemDTO(ResponseItemDTO.changeDTO(container.getItem()))
                .build();
    }
}
