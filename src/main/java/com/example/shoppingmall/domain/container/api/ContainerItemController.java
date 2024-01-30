package com.example.shoppingmall.domain.container.api;

import com.example.shoppingmall.domain.container.application.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/containers")
public class ContainerItemController {
    private final ContainerService containerService;

    // 상품의 판매 지역을 반환해줍니다.
    @GetMapping("/sellplace")
    public ResponseEntity<?> getSellPlaceList(Pageable pageable) {
        return ResponseEntity.ok().body(containerService.getSellPlaceList(pageable));
    }
}
