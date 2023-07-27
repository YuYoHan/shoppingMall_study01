package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.repository.item.ItemRepository;
import groovy.util.logging.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemEntityTest {

    // Bean 주입
    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        ItemEntity itemEntity = ItemEntity.builder()
                .itemNum("테스트 상품")
                .price(10000)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(100)
                .build();

        ItemEntity save = itemRepository.save(itemEntity);
        System.out.println(save.toString());

        Assertions.assertThat(save).isEqualTo(itemEntity);
    }

}