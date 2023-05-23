package com.example.shoppingmall.controller;

import com.example.shoppingmall.dto.MemberDTO;
import com.example.shoppingmall.dto.UserDTO;
import com.example.shoppingmall.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/member/login")
    public String login() {
        String client_id = "adc3fa9b7be49b66d283fb643dbdbaf3";

        return "member/memberLoginForm";
    }

    @GetMapping("/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberDTO());
        return "member/memberForm";
    }


}
