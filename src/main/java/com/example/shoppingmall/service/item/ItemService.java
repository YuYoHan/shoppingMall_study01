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

import javax.persistence.EntityNotFoundException;
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

        // S3에 업로드하는 로직
        List<ItemImgDTO> productImg = s3UploaderService.upload("product", itemFiles);

        // 이미지 등록
        List<ItemDTO> savedItem = new ArrayList<>();
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
            itemImgRepository.save(itemImg);

            item = ItemEntity.builder()
                    .itemImgList((List<ItemImgEntity>) itemImg)
                    .build();

            ItemEntity itemSave = itemRepository.save(item);
            savedItem.add(ItemDTO.toItemDTO(itemSave));
        }

        return ResponseEntity.ok().body(savedItem);
    }

    // 상품 검색
    // 상품 데이터를 읽어오는 트랜잭션을 읽기 전용으로 설정합니다.
    // 이럴 경우 JPA가 더티체킹(변경감지)를 수행하지 않아서 성능을 향상 시킬 수 있다.
    @Transactional(readOnly = true)
    public ResponseEntity<ItemDTO> getItem(Long itemId) {

        // 해당 상품의 이미지를 조회합니다.
        // 등록 순으로 가지고 오기 위해서 상품 이미지 아이디 오름차순으로
        // 가지고 옵니다.
        List<ItemImgEntity> itemImgList =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        List<ItemImgDTO> itemImgDTOList = new ArrayList<>();

        for(ItemImgEntity itemImg : itemImgList) {
            ItemImgDTO itemImgDTO = ItemImgDTO.toItemDTO(itemImg);
            itemImgDTOList.add(itemImgDTO);
        }
        log.info("itemImgDTOList : " + itemImgDTOList);

        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);


        ItemDTO itemDTO = ItemDTO.builder()
                .itemId(item.getItemId())
                .itemNum(item.getItemNum())
                .itemDetail(item.getItemDetail())
                .stockNumber(item.getStockNumber())
                .price(item.getPrice())
                .itemImgList(itemImgDTOList)
                .build();
        log.info("itemDTO : " + itemDTO);

        return ResponseEntity.ok().body(itemDTO);
    }
}
