package com.example.shoppingmall.domain.item.application;

import com.example.shoppingmall.domain.item.dto.CreateItemDTO;
import com.example.shoppingmall.domain.item.dto.ItemSearchCondition;
import com.example.shoppingmall.domain.item.dto.ResponseItemDTO;
import com.example.shoppingmall.domain.item.dto.UpdateItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    // 상품 등록
    ResponseItemDTO saveItem(CreateItemDTO itemDTO,
                             List<MultipartFile> itemFiles,
                             String memberEmail) throws Exception;

    // 상품 상세 정보
    ResponseItemDTO getItem(Long itemId);

    // 상품 수정
    ResponseItemDTO updateItem(Long itemId,
                       UpdateItemDTO itemDTO,
                       List<MultipartFile> itemFiles,
                       String memberEmail,
                       String role) throws Exception;

    // 상품 삭제
    String removeItem(Long itemId, String memberEmail, String role);

    // 검색
    Page<ResponseItemDTO> searchItemsConditions(Pageable pageable, ItemSearchCondition condition);

}
