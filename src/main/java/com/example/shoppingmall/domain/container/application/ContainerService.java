package com.example.shoppingmall.domain.container.application;

import com.example.shoppingmall.domain.container.dto.ContainerItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContainerService {
    // 상품 창고 검색
    Page<ContainerItemDTO> getSellPlaceList(Pageable pageable);
}
