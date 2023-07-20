package com.example.shoppingmall.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 헤더에서 jwt 추출
        String jwt = resovleToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        if(StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
            Authentication authentication = jwtProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다., uri : {}",
                    authentication.getName(), requestURI);
        } else {
            log.debug("유효한 JWT 토큰이 없습니다. uri : {}", requestURI);
        }
        chain.doFilter(request, response);
    }

    private String resovleToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(HEADER_AUTHORIZATION);

        if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        } else {
            return null;
        }
    }
}
