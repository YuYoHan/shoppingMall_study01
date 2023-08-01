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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        List<ItemImgEntity> itemImgEntities = new ArrayList<>();
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
            itemImgEntities.add(itemImg);
            itemImgRepository.save(itemImg);

            item = ItemEntity.builder()
                    .itemImgList(itemImgEntities)
                    .build();

            ItemEntity itemSave = itemRepository.save(item);
            savedItem.add(ItemDTO.toItemDTO(itemSave));
        }

        return ResponseEntity.ok().body(savedItem);
    }

    // 상품 상세정보
    // 상품 데이터를 읽어오는 트랜잭션을 읽기 전용으로 설정합니다.
    // 이럴 경우 JPA가 더티체킹(변경감지)를 수행하지 않아서 성능을 향상 시킬 수 있다.
    @Transactional(readOnly = true)
    public ResponseEntity<ItemDTO> getItem(Long itemId) {

        // 해당 상품의 이미지를 조회합니다.
        // 등록 순으로 가지고 오기 위해서 상품 이미지 아이디 오름차순으로 가지고 옵니다.
        // 근데 의문이 생길 수 있습니다.
        // itemImg 를 찾아오는데 왜 받는 것은 itemId 인가?
        // ItemImgEntity 안에는 ItemEntity 가 있어서
        // itemId에 따라 해당 상품과 연관된 이미지들을 조회합니다.
        List<ItemImgEntity> itemImgList =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        List<ItemImgDTO> itemImgDTOList = new ArrayList<>();

        for (ItemImgEntity itemImg : itemImgList) {
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

    // 상품 수정
    public ResponseEntity<?> updateItem(Long itemId,
                                        ItemDTO itemDTO,
                                        List<MultipartFile> itemFiles) throws Exception {

        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 상품 정보 수정
        item = ItemEntity.builder()
                .itemId(item.getItemId())
                .itemNum(itemDTO.getItemNum())
                .itemDetail(itemDTO.getItemDetail())
                .stockNumber(itemDTO.getStockNumber())
                .price(itemDTO.getPrice())
                .build();

        // 기존의 이미지 불러오기
        List<ItemImgEntity> existingItemImgs =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 새로운 이미지들 업로드
        List<ItemImgDTO> products = s3UploaderService.upload("product", itemFiles);

        List<ItemDTO> savedItem = new ArrayList<>();

        if (existingItemImgs.isEmpty()) {
            for (int i = 0; i < products.size(); i++) {
                ItemImgDTO itemImgDTO = products.get(i);

                ItemImgEntity itemImg = ItemImgEntity.builder()
                        .item(item)
                        .oriImgName(itemImgDTO.getOriImgName())
                        .uploadImgName(itemImgDTO.getUploadImgName())
                        .uploadImgUrl(itemImgDTO.getUploadImgUrl())
                        .uploadImgPath(itemImgDTO.getUploadImgPath())
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
                existingItemImgs.add(itemImg);
            }
        } else {
            for (ItemImgDTO product : products) {
                // DB에 등록하기 위헤서 엔티티에 넣어준다.
                ItemImgEntity itemImg = ItemImgEntity.builder()
                        .item(item)
                        .oriImgName(product.getOriImgName())
                        .uploadImgName(product.getUploadImgName())
                        .uploadImgUrl(product.getUploadImgUrl())
                        .uploadImgPath(product.getUploadImgPath())
                        // 대표 이미지 설정
                        // 대표 이미지는 한 개만 있어야 하기 때문에
                        // 기존의 대표 이미지가 있으면 그것을 불러온다.
                        .repImgYn(product.getRepImgYn())
                        .build();

                itemImgRepository.save(itemImg);
                existingItemImgs.add(itemImg);
            }
        }

        // 위에서 작성하지 못한 ItemImgEntity 를 item 엔티티에 넣어준다.
        item = ItemEntity.builder()
                .itemImgList(existingItemImgs)
                .build();

        ItemEntity save = itemRepository.save(item);

        savedItem.add(ItemDTO.toItemDTO(save));
        return ResponseEntity.ok().body(savedItem);
    }

    // 상품 삭제
    public String removeItem(Long itemId, ItemImgDTO itemImg) {
        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 상품 정보 삭제
        itemRepository.deleteByItemId(itemEntity.getItemId());
        // DB에서 이미지 삭제
        itemImgRepository.deleteByItemId(itemEntity.getItemId());

        String uploadFilePath = itemImg.getUploadImgPath();
        String uuidFileName = itemImg.getUploadImgName();
        // S3에서 이미지 삭제
        String result = s3UploaderService.deleteFile(uploadFilePath, uuidFileName);
        return result;
    }

}
