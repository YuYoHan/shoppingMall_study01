package com.example.shoppingmall.domain.order.api;

import com.example.shoppingmall.domain.order.application.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

}
