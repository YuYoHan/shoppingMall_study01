package com.example.shoppingmall.service.item;

import com.example.shoppingmall.dto.item.ItemDTO;
import com.example.shoppingmall.dto.item.ItemImgDTO;
import com.example.shoppingmall.entity.item.ItemEntity;
import com.example.shoppingmall.entity.item.ItemImgEntity;
import com.example.shoppingmall.repository.item.ItemImgRepository;
import com.example.shoppingmall.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final S3UploaderService s3UploaderService;
    private final ItemImgRepository itemImgRepository;

    // 상품을 등록하는 메소드
    public ResponseEntity<?> saveItem(ItemDTO itemDTO, List<MultipartFile> itemFiles) throws Exception {
        // 상품 등록
        ItemEntity item = ItemEntity.builder()
                .itemNum(itemDTO.getItemNum())
                .itemDetail(itemDTO.getItemDetail())
                .price(itemDTO.getPrice())
                .itemSellStatus(itemDTO.getItemSellStatus())
                .stockNumber(itemDTO.getStockNumber())
                .build();

        itemRepository.save(item);

        // S3에 업로드하는 로직
        List<ItemImgDTO> productImg = s3UploaderService.upload("product", itemFiles);

        // 이미지 등록
        List<ItemImgDTO> savedImages = new ArrayList<>();
        for (int i = 0; i < productImg.size(); i++) {
            ItemImgDTO uploadedImage = productImg.get(i);

            ItemImgEntity itemImg = ItemImgEntity.builder()
                    .item(item)
                    .oriImgName(uploadedImage.getOriImgName())
                    .uploadImgName(uploadedImage.getUploadImgName())
                    .uploadImgUrl(uploadedImage.getUploadImgUrl())
                    .uploadImgPath(uploadedImage.getUploadImgPath())
                    .build();

            // 첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 Y로 세팅합니다.
            // 나머지 상품 이미지는 N으로 설정합니다.
            if (i == 0) {
                itemImg = ItemImgEntity.builder()
                        .repImgYn("Y")
                        .build();
            } else {
                itemImg = ItemImgEntity.builder()
                        .repImgYn("N")
                        .build();
            }
            ItemImgEntity save = itemImgRepository.save(itemImg);
            log.info("save : " + save);
            savedImages.add(ItemImgDTO.toItemDTO(save));
        }
        return ResponseEntity.ok().body(savedImages);
    }
}
