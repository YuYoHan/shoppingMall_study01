package com.example.shoppingmall.controller;

import com.example.shoppingmall.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;


}
