package com.example.shoppingmall.service.item;

import com.example.shoppingmall.dto.item.ItemDTO;
import com.example.shoppingmall.dto.item.ItemImgDTO;
import com.example.shoppingmall.entity.item.ItemEntity;
import com.example.shoppingmall.entity.item.ItemImgEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import com.example.shoppingmall.repository.comment.CommentRepository;
import com.example.shoppingmall.repository.item.ItemImgRepository;
import com.example.shoppingmall.repository.item.ItemRepository;
import com.example.shoppingmall.repository.member.MemberRepository;
import com.example.shoppingmall.service.s3Upload.S3ItemImgUploaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    private final MemberRepository memberRepository;

    private final ItemRepository itemRepository;
    private final S3ItemImgUploaderService s3ItemImgUploaderService;
    private final ItemImgRepository itemImgRepository;
    private final CommentRepository commentRepository;

    // 상품을 등록하는 메소드
    public ResponseEntity<?> saveItem(ItemDTO itemDTO,
                                      List<MultipartFile> itemFiles,
                                      String userEmail) throws Exception {

        MemberEntity findUser = memberRepository.findByUserEmail(userEmail);

        if (findUser != null) {
            // 상품 등록
            ItemEntity item = ItemEntity.builder()
                    .itemName(itemDTO.getItemName())
                    .itemDetail(itemDTO.getItemDetail())
                    .price(itemDTO.getPrice())
                    .itemSellStatus(itemDTO.getItemSellStatus())
                    .stockNumber(itemDTO.getStockNumber())
                    .build();

            // S3에 업로드하는 로직
            List<ItemImgDTO> productImg = s3ItemImgUploaderService.upload("product", itemFiles);

            // List형식으로 이미지를 담는 이유는 Item에 List형식으로
            // 이미지와 양방향을 맺고 있기 때문에 List로 넣어줘야 한다.
            List<ItemImgEntity> itemImgEntities = new ArrayList<>();
            List<ItemDTO> savedItem = new ArrayList<>();

            for (int i = 0; i < productImg.size(); i++) {
                ItemImgDTO uploadedImage = productImg.get(i);

                // 첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 Y로 세팅합니다.
                // 나머지 상품 이미지는 N으로 설정합니다.
                if (i == 0) {
                    ItemImgEntity itemImg = ItemImgEntity.builder()
                            .item(item)
                            .oriImgName(uploadedImage.getOriImgName())
                            .uploadImgName(uploadedImage.getUploadImgName())
                            .uploadImgUrl(uploadedImage.getUploadImgUrl())
                            .uploadImgPath(uploadedImage.getUploadImgPath())
                            .repImgYn("Y")
                            .build();
                    itemImgEntities.add(itemImg);
                    itemImgRepository.save(itemImg);
                } else {
                    ItemImgEntity itemImg = ItemImgEntity.builder()
                            .item(item)
                            .oriImgName(uploadedImage.getOriImgName())
                            .uploadImgName(uploadedImage.getUploadImgName())
                            .uploadImgUrl(uploadedImage.getUploadImgUrl())
                            .uploadImgPath(uploadedImage.getUploadImgPath())
                            .repImgYn("N")
                            .build();
                    itemImgEntities.add(itemImg);
                    itemImgRepository.save(itemImg);
                }
                item = ItemEntity.builder()
                        .itemName(itemDTO.getItemName())
                        .itemDetail(itemDTO.getItemDetail())
                        .price(itemDTO.getPrice())
                        .itemSellStatus(itemDTO.getItemSellStatus())
                        .stockNumber(itemDTO.getStockNumber())
                        .itemImgList(itemImgEntities)
                        .member(findUser)
                        .build();

                ItemEntity itemSave = itemRepository.save(item);
                savedItem.add(ItemDTO.toItemDTO(itemSave));
            }
            return ResponseEntity.ok().body(savedItem);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 없습니다.");
        }
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
        ItemEntity findItem = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        ItemDTO itemDTO = ItemDTO.toItemDTO(findItem);
        return ResponseEntity.ok().body(itemDTO);
    }

    // 상품 수정
    public ResponseEntity<?> updateItem(Long itemId,
                                        ItemDTO itemDTO,
                                        List<MultipartFile> itemFiles,
                                        String userEmail) throws Exception {

        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        MemberEntity findUser = memberRepository.findByUserEmail(userEmail);

        if (findUser.getNickName().equals(item.getMember().getNickName())) {
            // 상품 정보 수정
            item = ItemEntity.builder()
                    .itemId(item.getItemId())
                    .itemName(itemDTO.getItemName())
                    .itemDetail(itemDTO.getItemDetail())
                    .stockNumber(item.getStockNumber())
                    .price(itemDTO.getPrice())
                    .build();

            // 기존의 이미지 불러오기
            List<ItemImgEntity> itemImgList1 = item.getItemImgList();
            // 새로운 이미지들 업로드
            List<ItemImgDTO> products = s3ItemImgUploaderService.upload("product", itemFiles);

            if (itemImgList1.isEmpty()) {
                for (int i = 0; i < products.size(); i++) {
                    ItemImgDTO imgDTO = products.get(i);
                    ItemImgEntity itemImg = ItemImgEntity.builder()
                            .item(item)
                            .oriImgName(imgDTO.getOriImgName())
                            .uploadImgName(imgDTO.getUploadImgName())
                            .uploadImgUrl(imgDTO.getUploadImgUrl())
                            .uploadImgPath(imgDTO.getUploadImgPath())
                            // 첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 Y로 세팅합니다.
                            // 나머지 상품 이미지는 N으로 설정합니다.
                            .repImgYn(i == 0 ? "Y" : "N") // 대표 이미지 여부 지정
                            .build();

                    itemImgRepository.save(itemImg);
                    itemImgList1.add(itemImg);
                }
            } else {
                // 비어있지 않는 경우
                for (ItemImgEntity imgEntity : itemImgList1) {
                    for (int i = 0; i < products.size(); i++) {
                        ItemImgDTO imgDTO = products.get(i);
                        // DB에 등록하기 위헤서 엔티티에 넣어준다.
                        ItemImgEntity itemImg = ItemImgEntity.builder()
                                .itemImgId(imgEntity.getItemImgId())
                                .item(item)
                                .oriImgName(imgDTO.getOriImgName())
                                .uploadImgName(imgDTO.getUploadImgName())
                                .uploadImgUrl(imgDTO.getUploadImgUrl())
                                .uploadImgPath(imgDTO.getUploadImgPath())
                                // 대표 이미지 설정
                                // 대표 이미지는 한 개만 있어야 하기 때문에
                                // 기존의 대표 이미지가 있으면 그것을 불러온다.
                                .repImgYn(i == 0 ? "Y" : "N")
                                .build();

                        itemImgRepository.save(itemImg);
                        itemImgList1.add(itemImg);
                    }
                }
            }


            // 위에서 작성하지 못한 ItemImgEntity 를 item 엔티티에 넣어준다.
            item = ItemEntity.builder()
                    .itemId(item.getItemId())
                    .itemName(itemDTO.getItemName())
                    .itemDetail(itemDTO.getItemDetail())
                    .stockNumber(itemDTO.getStockNumber())
                    .price(itemDTO.getPrice())
                    .itemImgList(itemImgList1)
                    .member(findUser)
                    .itemSellStatus(item.getItemSellStatus())
                    .commentEntityList(item.getCommentEntityList())
                    .build();

            ItemEntity save = itemRepository.save(item);
            ItemDTO itemDTO1 = ItemDTO.toItemDTO(save);
            return ResponseEntity.ok().body(itemDTO1);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // 상품 삭제
    public String removeItem(Long itemId, String memberEmail) {
        // 상품 조회
        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 이미지 조회
        List<ItemImgEntity> findImgList = itemImgRepository.findByItemItemId(itemId);

        // 회원 조회
        MemberEntity findUser = memberRepository.findByUserEmail(memberEmail);

        if (findUser.getUserEmail().equals(itemEntity.getMember().getUserEmail())) {
            for (ItemImgEntity img : findImgList) {
                String uploadFilePath = img.getUploadImgPath();
                String uuidFileName = img.getUploadImgName();

                // 상품 정보 삭제
                itemRepository.deleteByItemId(itemEntity.getItemId());
                // DB에서 이미지 삭제
                itemImgRepository.deleteById(img.getItemImgId());
                // 댓글 삭제
                commentRepository.deleteByItemItemId(itemEntity.getItemId());

                // S3에서 이미지 삭제
                String result = s3ItemImgUploaderService.deleteFile(uploadFilePath, uuidFileName);
                log.info(result);
            }
        } else {
            return "해당 유저의 게시글이 아닙니다.";
        }
        return "상품과 이미지를 삭제했습니다.";
    }

    // 전체 상품 보여주기
    @Transactional(readOnly = true)
    public Page<ItemDTO> getItems(Pageable pageable) {
        Page<ItemEntity> all = itemRepository.findAll(pageable);
        // 각 아이템 엔티티를 ItemDTO로 변환합니다.
        // 이 변환은 ItemDTO::toItemDTO 메서드를 사용하여 수행됩니다.
        return all.map(ItemDTO::toItemDTO);
    }

    // 검색
    @Transactional(readOnly = true)
    public Page<ItemDTO> getSearchItems(Pageable pageable,
                                        String searchKeyword) {
        Page<ItemEntity> searchItems = itemRepository.findByItemNameContaining(pageable, searchKeyword);
        return searchItems.map(ItemDTO::toItemDTO);
    }

}
