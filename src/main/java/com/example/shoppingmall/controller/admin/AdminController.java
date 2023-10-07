package com.example.shoppingmall.controller.admin;

import com.example.shoppingmall.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {
    private final AdminService adminService;

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    // @PreAuthorize 는 권한을 체크하고 함수를 호출할지 말지 결정한다.
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBoard(@PathVariable Long boardId,
                         @AuthenticationPrincipal UserDetails userDetails) throws Exception{
        try {
            String remove = adminService.removeBoard(boardId, userDetails);
            return remove;
        } catch (AccessDeniedException e) {
            return "삭제할 권한이 없습니다.";
        }
    }

    // 상품 삭제
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteItem(@PathVariable Long itemId,
                             @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            String remove = adminService.removeItem(itemId, userDetails);
            return remove;
        } catch (AccessDeniedException e) {
            return "삭제할 권한이 없습니다.";
        }
    }
}
