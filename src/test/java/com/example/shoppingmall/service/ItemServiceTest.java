package com.example.shoppingmall.service;

import com.example.shoppingmall.constant.ItemSellStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class ItemServiceTest {
    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFile() throws Exception {

        List<MultipartFile> multipartFiles = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String path = "C:/upload/file" ;
            String imageName = "image" + i + ".jpg";
            MockMultipartFile mockMultipartFile =
                    new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1,2,3,4});
            multipartFiles.add(mockMultipartFile);
        }
        return multipartFiles;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem()throws  Exception {
        ItemFormDTO itemFormDTO = new ItemFormDTO();
        itemFormDTO.setItemName("테스트 상품");
        itemFormDTO.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDTO.setItemDetail("테스트 상품입니다.");
        itemFormDTO.setPrice(1000);
        itemFormDTO.setStockNumber(100);

        List<MultipartFile> multipartFiles = createMultipartFile();
        Long itemId = itemService.saveItem(itemFormDTO, multipartFiles);

        List<ItemImg> itemImgs = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        Assertions.assertThat(itemFormDTO.getItemName()).isEqualTo(item.getItemNm());
        Assertions.assertThat(itemFormDTO.getItemSellStatus()).isEqualTo(item.getItemSellStatus());
        Assertions.assertThat(itemFormDTO.getItemDetail()).isEqualTo(item.getItemDetail());
        Assertions.assertThat(itemFormDTO.getPrice()).isEqualTo(item.getPrice());
        Assertions.assertThat(itemFormDTO.getStockNumber()).isEqualTo(item.getStockNumber());
        // 상품 이미지는 첫 번째 파일의 원본 이미지 파일 이름만 같은지 확인하겠습니다.
        Assertions.assertThat(multipartFiles.get(0).getOriginalFilename()).isEqualTo(itemImgs.get(0).getOriImgName());
    }


}