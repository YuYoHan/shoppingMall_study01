package com.example.shoppingmall.domain.item.api;

import com.example.shoppingmall.domain.item.application.ItemService;
import com.example.shoppingmall.domain.item.dto.CreateItemDTO;
import com.example.shoppingmall.domain.item.dto.ItemSearchCondition;
import com.example.shoppingmall.domain.item.dto.ResponseItemDTO;
import com.example.shoppingmall.domain.item.dto.UpdateItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Log4j2
public class ItemController {
    private final ItemService itemService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> createItem(@Validated @RequestPart("item")CreateItemDTO createItem,
                                        @RequestPart(value = "files", required = false)List<MultipartFile> itemFiles,
                                        BindingResult result,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if(result.hasErrors()) {
                log.error("error : " + result.hasErrors());
                throw new Exception(result.getClass().getSimpleName());
            }
            String email = userDetails.getUsername();
            ResponseItemDTO responseItemDTO = itemService.saveItem(createItem, itemFiles, email);
            return ResponseEntity.ok().body(responseItemDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 상품 상세 정보
    @GetMapping("/{itemId}")
    public ResponseEntity<?> itemDetail(@PathVariable Long itemId) {
        try {
            ResponseItemDTO item = itemService.getItem(itemId);
            log.info("상품 : " + item);
            return ResponseEntity.ok().body(item);
        } catch (EntityNotFoundException e) {
            log.error("존재하지 않는 상품입니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 상품입니다.");
        }
    }

    // 상품 수정
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Long itemId,
                                        @RequestPart("item")UpdateItemDTO itemDTO,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> itemFiles,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            ResponseItemDTO updateItem = itemService.updateItem(itemId, itemDTO, itemFiles, email, role);
            return ResponseEntity.ok().body(updateItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 상품 삭제
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            String result = itemService.removeItem(itemId, email, role);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchItemsConditions(Pageable pageable,
                                                   ItemSearchCondition condition) {
        try {
            log.info("condition : " + condition);
            Page<ResponseItemDTO> items = itemService.searchItemsConditions(pageable, condition);
            log.info("상품 조회 {}", items);

            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("items", items.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", items.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", items.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", items.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", items.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", items.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", items.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", items.isLast());
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
