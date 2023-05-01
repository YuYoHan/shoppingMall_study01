package com.example.shoppingmall.controller;

import com.example.shoppingmall.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
    @GetMapping("/test")
    public UserDTO test() {
        UserDTO user = new UserDTO();
        user.setName("채원");
        user.setAge(22);
        return user;
    }
}
