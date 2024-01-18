package com.example.shoppingmall.global.config.OAuth2;

import com.example.shoppingmall.domain.jwt.dto.TokenDTO;
import com.example.shoppingmall.domain.jwt.entity.TokenEntity;
import com.example.shoppingmall.domain.jwt.repository.TokenRepository;
import com.example.shoppingmall.domain.member.entity.SocialMemberEntity;
import com.example.shoppingmall.domain.member.repository.SocialMemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final SocialMemberRepository socialMemberRepository;
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            String email = authentication.getName();
            TokenEntity findToken = tokenRepository.findByMemberEmail(email);
            TokenDTO tokenDTO = TokenDTO.changeDTO(findToken);
            SocialMemberEntity findUser = socialMemberRepository.findByEmail(email);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("providerId", findUser.getProviderId());
            responseBody.put("provider", findUser.getProvider());
            responseBody.put("accessToken", tokenDTO.getAccessToken());
            responseBody.put("refreshToken", tokenDTO.getRefreshToken());
            responseBody.put("email", tokenDTO.getMemberEmail());
            responseBody.put("memberId", tokenDTO.getMemberId());
            responseBody.put("grantType", tokenDTO.getGrantType());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (Exception e) {
            // 예외가 발생하면 클라이언트에게 오류 응답을 반환
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("정보를 찾아오지 못했습니다.");
            response.getWriter().write("OAuth 2.0 로그인 성공 후 오류 발생: " + e.getMessage());
            // 이 메서드는 버퍼에 있는 내용을 클라이언트에게 보냅니다.
            // 데이터를 작성하고 나서는 flush()를 호출하여 실제로 데이터를 클라이언트로 전송합니다.
            response.getWriter().flush();
        }
    }
}
