package com.example.shoppingmall.controller.thymeleaf;

import com.example.shoppingmall.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ThymeleafController {
    @GetMapping("/ex01")
    public String ex01(Model model) {
        model.addAttribute("data", "타임리프예제입니다.");
        return "/thymeleaf/ex01";
    }

    @GetMapping("/ex02")
    public String ex02(Model model) {
        ItemDto item = new ItemDto();
        item.setItemDetail("상품 상세 설명");
        item.setItemNum("상품1");
        item.setPrice(10000);
        item.setRegTime(LocalDateTime.now());

        model.addAttribute("item", item);
        return "/thymeleaf/ex02";
    }

    @GetMapping("/ex03")
    public String ex03(Model model) {
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ItemDto item = new ItemDto();
            item.setItemDetail("상품상세 설명" + i);
            item.setItemNum("상품"+i);
            item.setPrice(10000);
            item.setRegTime(LocalDateTime.now());
            itemDtoList.add(item);
        }
        model.addAttribute("itemList", itemDtoList);
        return "/thymeleaf/ex03";
    }

    @GetMapping("/ex04")
    public String ex04(Model model) {
        List<ItemDto> itemDTOList2 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ItemDto item = new ItemDto();
            item.setItemDetail("상품 상세 설명" + i);
            item.setItemNum("상품 " + i);
            item.setPrice(10000 * i);
            item.setRegTime(LocalDateTime.now());

            itemDTOList2.add(item);
        }
        model.addAttribute("itemList", itemDTOList2);
        return "/ex04";
    }
    @GetMapping("ex05")
    public String ex05(Model model) {
        List<ItemDto> itemDTOList3 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ItemDto item = new ItemDto();
            item.setItemDetail("상품 상세 설명" + i);
            item.setItemNum("상품 " + i);
            item.setPrice(10000 * i);
            item.setRegTime(LocalDateTime.now());

            itemDTOList3.add(item);
        }
        model.addAttribute("itemList", itemDTOList3);
        return "/ex05";
    }

    @GetMapping("/ex06")
    public String ex06() {
        return "/ex06";
    }

    @GetMapping("/ex07")
    public String ex07(String param1, String param2, Model model) {
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return "/ex07";
    }

    @GetMapping("/ex08")
    public String ex08() {
        return "ex08";
    }
}
