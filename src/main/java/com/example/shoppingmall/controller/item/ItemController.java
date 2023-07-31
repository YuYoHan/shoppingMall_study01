package com.example.shoppingmall.controller.item;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.shoppingmall.dto.item.ItemDTO;
import com.example.shoppingmall.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    // 상품 등록
    @PostMapping("/")
    public ResponseEntity<?> insertItem(@Validated @RequestBody ItemDTO itemDTO,
                                        List<MultipartFile> itemImages,
                                        BindingResult result) throws Exception {
        if(result.hasErrors()) {
            log.info("BindingResult error : " + result.hasErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getClass().getSimpleName());
        }

        if(itemImages.get(0).isEmpty()) {
            log.info("첫 번째 상품 이미지는 필 수 입력입니다.");
            return ResponseEntity.notFound().build();
        }

        try {
            ResponseEntity<?> image = itemService.saveItem(itemDTO, itemImages);
            return ResponseEntity.ok().body(image);

        } catch (Exception e) {
            log.info("에러 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 상품 조회
    @GetMapping("/{itemId}")
    public ResponseEntity<?> itemDetail(@PathVariable Long itemId) {
        try {
            ResponseEntity<ItemDTO> item = itemService.getItem(itemId);
            return ResponseEntity.ok().body(item);
        } catch (EntityNotFoundException e) {
            log.info("존재하지 않는 상품입니다.");
            return ResponseEntity.notFound().build();
        }
    }



}
