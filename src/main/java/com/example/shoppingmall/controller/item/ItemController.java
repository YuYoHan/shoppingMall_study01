package com.example.shoppingmall.controller.item;

import com.example.shoppingmall.dto.item.ItemDTO;
import com.example.shoppingmall.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    // 상품 등록
    @PostMapping("")
    // @RequestPart(value = "files")는 주로 Spring 기반의 웹 애플리케이션에서 사용되는 Java 어노테이션입니다.
    // HTTP 요청을 처리하는 컨트롤러 메서드에서 다중 파트 파일 업로드를 처리하는 데 사용됩니다.
    // 이 어노테이션은 HTTP 요청에서 "files"라는 이름의 다중 파트를 추출하고 해당 파일을 처리하기 위해 사용됩니다.
    public ResponseEntity<?> insertItem(@Validated @RequestBody ItemDTO itemDTO,
                                        @RequestPart(value = "files") List<MultipartFile> itemImages,
                                        BindingResult result,
                                        @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            if (result.hasErrors()) {
                log.info("BindingResult error : " + result.hasErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getClass().getSimpleName());
            }

            if (itemImages.get(0).isEmpty()) {
                log.info("첫 번째 상품 이미지는 필 수 입력입니다.");
                return ResponseEntity.notFound().build();
            }

            String userEmail = userDetails.getUsername();
            ResponseEntity<?> image = itemService.saveItem(itemDTO, itemImages, userEmail);
            return ResponseEntity.ok().body(image);

        } catch (Exception e) {
            log.info("에러 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 상품 상세 정보
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

    // 전체 상품 보여주기
    @GetMapping("")
    public ResponseEntity<?> getItems(
            // SecuritConfig에 Page 설정을 한 페이지에 10개 보여주도록
            // 설정을 해서 여기서는 할 필요가 없다.
            @PageableDefault(sort = "itemId", direction = Sort.Direction.DESC)
            Pageable pageable, String searchKeyword) {
        // 검색하지 않을 때는 모든 글을 가져오고 검색하면 검색한거를 보여줌
        if(searchKeyword == null) {
            Page<ItemDTO> items = itemService.getItems(pageable);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("items", items.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", items.getNumber());
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
        } else {
            Page<ItemDTO> searchItems = itemService.getSearchItems(pageable, searchKeyword);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("items", searchItems.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", searchItems.getNumber());
            // 전체 페이지 수
            response.put("totalPage", searchItems.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", searchItems.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", searchItems.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", searchItems.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", searchItems.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", searchItems.isLast());

            return ResponseEntity.ok().body(response);

        }
    }


    // 상품 수정
    @PutMapping("/{itemId}")
    // multipart/form-data의 Content-Type을 요청을 처리하기 위해 @RequestPart 어노테이션을 사용
    public ResponseEntity<?> updateItem(@PathVariable Long itemId,
                                        @RequestBody ItemDTO itemDTO,
                                        @RequestPart(value = "files") List<MultipartFile> itemImages,
                                        @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            String userEmail = userDetails.getUsername();
            ResponseEntity<?> responseEntity =
                    itemService.updateItem(itemId, itemDTO, itemImages, userEmail);
            return ResponseEntity.ok().body(responseEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 상품 삭제
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String email = userDetails.getUsername();
            String result = itemService.removeItem(itemId, email);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
    }
}
