package com.example.shoppingmall.domain.item.application;

import com.example.shoppingmall.domain.container.dto.ContainerDTO;
import com.example.shoppingmall.domain.container.entity.ContainerItemEntity;
import com.example.shoppingmall.domain.container.repository.ContainerItemRepository;
import com.example.shoppingmall.domain.item.dto.CreateItemDTO;
import com.example.shoppingmall.domain.item.dto.ItemSearchCondition;
import com.example.shoppingmall.domain.item.dto.ResponseItemDTO;
import com.example.shoppingmall.domain.item.dto.UpdateItemDTO;
import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemImgEntity;
import com.example.shoppingmall.domain.item.entity.ItemSellStatus;
import com.example.shoppingmall.domain.item.exception.ItemException;
import com.example.shoppingmall.domain.item.repository.ItemImgRepository;
import com.example.shoppingmall.domain.item.repository.ItemQuerydslRepository;
import com.example.shoppingmall.domain.item.repository.ItemRepository;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.exception.UserException;
import com.example.shoppingmall.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ItemServiceImpl implements ItemService {
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ContainerItemRepository containerItemRepository;
    private final ItemQuerydslRepository itemQuerydslRepository;

    @Value("${file.path}")
    private String file_path;

    @Override
    public ResponseItemDTO saveItem(CreateItemDTO itemDTO,
                                    List<MultipartFile> itemFiles,
                                    String memberEmail) throws Exception {
        try {
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);

            if (findUser != null) {
                ResponseItemDTO itemInfo = ResponseItemDTO.builder()
                        .itemName(itemDTO.getItemName())
                        .price(itemDTO.getPrice())
                        .itemDetail(itemDTO.getItemDetail())
                        .stockNumber(itemDTO.getStockNumber())
                        .sellPlace(ContainerDTO.builder()
                                .containerName(itemDTO.getSellPlace().getContainerName())
                                .containerAddr(itemDTO.getSellPlace().getContainerAddr())
                                .build())
                        .itemSellStatus(ItemSellStatus.SELL)
                        .itemReserver(null)
                        .itemRamount(0)
                        .build();
                // DTO를 엔티티로 변환
                // 이미지 엔티티에 넣어주기 위해서
                ItemEntity itemEntity = ItemEntity.saveEntity(itemInfo, findUser);
                // 이미지 엔티티 만들기
                // 상품 정보와 경로, 그리고 이미지들을 매개변수로 넘겨줍니다.
                ItemImgEntity itemImgEntity =
                        ItemImgEntity.saveImg(file_path ,itemFiles, itemEntity);
                // 기존의 만든 상품 엔티티의 이미지 리스트에 추가
                itemEntity.addItemImgList(itemImgEntity);
                // 썸네일 작업
                for (int i = 0; i < itemEntity.getItemImgList().size(); i++) {
                    ItemImgEntity itemImg = itemEntity.getItemImgList().get(i);
                    if (i == 0) {
                        itemImg.changeRepImgY();
                    } else {
                        itemImg.changeRepImgN();
                    }
                }
                ContainerItemEntity containerItemEntity = ContainerItemEntity.saveContainer(itemEntity);
                containerItemRepository.save(containerItemEntity);
                ItemEntity save = itemRepository.save(itemEntity);
                return ResponseItemDTO.changeDTO(save);
            }
            return null;
        } catch (Exception e) {
            return null;
    }
}

    @Override
    public ResponseItemDTO getItem(Long itemId) {
        try {
            // 상품 조회
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("상품이 없습니다."));

            ResponseItemDTO responseItemDTO = ResponseItemDTO.changeDTO(findItem);
            ContainerItemEntity container =
                    containerItemRepository.findByContainerName(findItem.getItemPlace().getContainerName());

            if(container == null) {
                responseItemDTO.setSellPlace("폐점된 지점", null);
            } else {
                responseItemDTO.setSellPlace(container.getContainerName(), container.getContainerAddr());
            }
            return responseItemDTO;
        } catch (EntityNotFoundException e) {
            throw new ItemException(e.getMessage());
        }
    }

    // 상품 수정
    @Override
    public ResponseItemDTO updateItem(Long itemId,
                                      UpdateItemDTO itemDTO,
                                      List<MultipartFile> itemFiles,
                                      String memberEmail,
                                      String role) throws Exception {
        try {
            // 상품 조회
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(EntityNotFoundException::new);
            log.info("item : " + findItem);
            // 유저 조회
            MemberEntity findMember = memberRepository.findByEmail(memberEmail);
            log.info("member : " + findMember);
            // 이미지 조회
            List<ItemImgEntity> itemImgs = itemImgRepository.findByItemItemId(itemId);

            if(findItem.getMember().getEmail().equals(findMember.getEmail())) {
                // 상품 정보 수정
                findItem.updateItem(itemDTO);

                if(!itemImgs.isEmpty()) {
                    if(!itemDTO.getRemoveImgId().isEmpty()) {
                        findItem.getItemImgList().stream()
                                .filter(img -> itemDTO.getRemoveImgId().contains(img.getId()))
                                .forEach(img -> findItem.getItemImgList().remove(img));
                    }
                }

                if(itemFiles != null) {
                    ItemImgEntity itemImgEntity = ItemImgEntity.saveImg(file_path, itemFiles, findItem);
                    findItem.addItemImgList(itemImgEntity);
                }
                // 썸네일 작업
                boolean isFirstImage = true;
                for (ItemImgEntity itemImg : findItem.getItemImgList()) {
                    if(isFirstImage) {
                        itemImg.changeRepImgY();
                        isFirstImage = false;
                    } else {
                        itemImg.changeRepImgN();
                    }
                }
                ItemEntity update = itemRepository.save(findItem);
                return ResponseItemDTO.changeDTO(update);
            } else {
                throw new UserException("본인이 올린 상품이 아니므로 수정할 수 없습니다.");
            }
        } catch (Exception e) {
            throw new ItemException("상품 수정하는 작업을 실패했습니다.\n" + e.getMessage());
        }
    }

    // 상품 삭제
    @Override
    public String removeItem(Long itemId, String memberEmail, String role) {
        try {
            // 상품 조회
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemException("해당 상품 정보가 존재하지 않습니다."));

            if (findItem.getItemSellStatus() == ItemSellStatus.RESERVED) {
                throw new ItemException("해당 상품은 예약되었으니 관리자 혹은 예약자와 논의 후 삭제를 진행하여 주시기 바랍니다.");
            }
            // 회원 조회
            MemberEntity sellUser = memberRepository.findById(findItem.getMember().getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));

            MemberEntity findUser = memberRepository.findByEmail(memberEmail);

            if(role.equals("ROLE_ADMIN") || findUser.getMemberId().equals(sellUser.getMemberId())) {
//                // item을 참조하고 있는 자식Entity값 null셋팅
//                List<CartItemDTO> items = cartItemRepository.findByItemId(itemId);
//                for (CartItemDTO item : items) {
//                    item.setItem(null);
//                    cartItemRepository.save(item);
//                }

                // 상품 삭제
                itemRepository.deleteById(itemId);
                return "상품과 이미지를 삭제했습니다.";
            }
            throw new UserException("삭제 조건에 맞지 않습니다.");
        } catch (Exception e) {
            throw new ItemException("상품 삭제에 실패하였습니다.\n" + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponseItemDTO> searchItemsConditions(Pageable pageable, ItemSearchCondition condition) {
        try {
            Page<ItemEntity> findItemConditions = itemQuerydslRepository.itemSearch(condition, pageable);
            if (findItemConditions.isEmpty()) {
                throw new EntityNotFoundException("조건에 만족하는 상품이 없습니다.");
            }

            Page<ResponseItemDTO> pageItem = findItemConditions.map(ResponseItemDTO::changeDTO);
            pageItem.forEach(status -> {
                ContainerItemEntity containerName = containerItemRepository.findByContainerName(status.getSellPlace().getContainerName());
                if(containerName == null) {
                    status.setSellPlace("폐점된 지점", null);
                } else {
                    status.setSellPlace(containerName.getContainerName(), containerName.getContainerAddr());
                }
            });
            return pageItem;
        }catch (Exception e) {
            log.error("error : " + e.getMessage());
            throw new EntityNotFoundException("상품 조회에 실패하였습니다.\n" + e.getMessage());
        }
    }

}
