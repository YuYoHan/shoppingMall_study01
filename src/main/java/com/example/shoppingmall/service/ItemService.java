package com.example.shoppingmall.service;

import com.example.shoppingmall.dto.ItemFormDTO;
import com.example.shoppingmall.dto.ItemImgDTO;
import com.example.shoppingmall.entity.Item;
import com.example.shoppingmall.entity.ItemImg;
import com.example.shoppingmall.repository.ItemImgRepository;
import com.example.shoppingmall.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDTO itemFormDTO, List<MultipartFile> itemImgList) throws Exception{
        // 상품 등록
        // 상품 등록 폼으로부터 입력 받은 데이터를 이용하여 item 객체를 만듭니다.
        Item item = itemFormDTO.createItem();
        // 상품 데이터를 저장합니다.
        itemRepository.save(item);

        // 이미지 등록
        for (int i = 0; i < itemImgList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            // 첫 번째 이미지의 경우 대표 상품 이미지 여부 값을 "Y"로 세팅합니다.
            // 나머지 상품 이미지는 "N"으로 설정합니다.
            if(i == 0) {
                itemImg.setRepimgYn("Y");
            } else {
                itemImg.setRepimgYn("N");
                // 상품의 이미지 정보를 저장합니다.
            }
            itemImgService.saveItemImg(itemImg, itemImgList.get(i));
        }
        return item.getId();
    }

    // 상품 데이터를 읽어오는 트랜잭션을 읽기 전용으로 설정합니다.
    // 이럴경우 JPA가 더티체킹(변경감지)을 수행하지 않아서 성능을 향상 시킬 수 있다.
    @Transactional(readOnly = true)
    public ItemFormDTO getItemDtl(Long itemId) {
        // 해당 상품의 이미지를 조회합니다. 등록순으로 가지고 오기 위해서
        // 상품 이미지 아이디 오름차순으로 가지고 오겠습니다.
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDTO> itemImgDTOList = new ArrayList<>();
        for (ItemImg itemImg: itemImgList
             ) {
            ItemImgDTO itemImgDTO = ItemImgDTO.of(itemImg);
            itemImgDTOList.add(itemImgDTO);
        }

        // 상품의 아이디를 통해 상품 엔티티를 조회합니다.
        // 존재하지 않을 때는 EntityNotFoundException을 발생시킵니다.
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        ItemFormDTO itemFormDTO = ItemFormDTO.of(item);
        itemFormDTO.setItemImgDTOList(itemImgDTOList);
        return itemFormDTO;
    }

    public Long updateItem(ItemFormDTO itemFormDTO, List<MultipartFile> itemFileList) throws Exception{
        // 상품 수정
        // 상품 등록 화면으로부터 전달 받은 상품 아이디를 이용하여 상품 엔티티를 조회합니다.
        Item item = itemRepository.findById(itemFormDTO.getId())
                .orElseThrow(EntityNotFoundException::new);
        // 상품 등록 화면으로 부터 전달 받은 ItemFormDTO를 통해 상품 엔티티를 업데이트합니다.
        item.updateItem(itemFormDTO);
        // 상품 이미지 아이디 리스트를 조회합니다.
        List<Long> itemImgIds = itemFormDTO.getItemImgIds();

        // 이미지 등록
        for (int i = 0; i < itemFileList.size(); i++) {
            // 상품 이미지를 업데이트하기 위해서 updateItemImg() 메소드에 상품 이미지 아이디와
            // 상품 이미지 파일 정보를 파라미터로 전달합니다.
            itemImgService.updateItemImg(itemImgIds.get(i), itemFileList.get(i));
        }
        return item.getId();
    }
}
