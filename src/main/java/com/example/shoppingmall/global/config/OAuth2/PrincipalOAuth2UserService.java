package com.example.shoppingmall.global.config.OAuth2;

import com.example.shoppingmall.domain.jwt.dto.TokenDTO;
import com.example.shoppingmall.domain.jwt.entity.TokenEntity;
import com.example.shoppingmall.domain.jwt.repository.TokenRepository;
import com.example.shoppingmall.domain.member.entity.Role;
import com.example.shoppingmall.domain.member.entity.SocialMemberEntity;
import com.example.shoppingmall.domain.member.repository.SocialMemberRepository;
import com.example.shoppingmall.global.config.OAuth2.provider.GoogleUser;
import com.example.shoppingmall.global.config.OAuth2.provider.NaverUser;
import com.example.shoppingmall.global.config.OAuth2.provider.OAuth2UserInfo;
import com.example.shoppingmall.global.config.PrincipalDetails;
import com.example.shoppingmall.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class PrincipalOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
        private final SocialMemberRepository socialMemberRepository;
        private final JwtProvider jwtProvider;
        private final TokenRepository tokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String tokenValue = userRequest.getAccessToken().getTokenValue();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService =
                new DefaultOAuth2UserService();

        log.info("clientRegistration : " + clientRegistration);
        log.info("소셜 로그인 accessToken : " + tokenValue);
        log.info("oAuth2UserService : " + oAuth2UserService);

        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;
        String registrationId = clientRegistration.getRegistrationId();

        // 구글로 로그인할경우
        if(registrationId.equals("google")) {
            log.info("구글 로그인");
            oAuth2UserInfo = new GoogleUser(oAuth2User, clientRegistration);
            // 네이버로 로그인할 경우
        } else  if(registrationId.equals("naver")) {
            log.info("네이버 로그인");
            oAuth2UserInfo = new NaverUser(oAuth2User, clientRegistration);
        } else {
            log.error("지원하지 않는 소셜 로그인입니다.");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();
        Role role = Role.USER;

        SocialMemberEntity findUser = socialMemberRepository.findByEmail(email);

        if(findUser == null) {
            findUser = SocialMemberEntity.builder()
                    .email(email)
                    .memberName(name)
                    .email(email)
                    .memberName(name)
                    .provider(provider)
                    .providerId(providerId)
                    .memberRole(role)
                    .nickName(name)
                    .build();

            log.info("member : " + findUser);
            findUser = socialMemberRepository.save(findUser);
        } else {
            log.info("로그인을 이미 한적이 있습니다.");
        }

        List<GrantedAuthority> authoritiesForUser = getAuthoritiesForUser(findUser);
        TokenDTO token = jwtProvider.createTokenForOAuth2(email, authoritiesForUser, findUser.getMemberId());
        TokenEntity findToken = tokenRepository.findByMemberEmail(token.getMemberEmail());

        TokenEntity saveToken;
        if(findToken == null) {
            TokenEntity tokenEntity = TokenEntity.changeEntity(token);
            saveToken = tokenRepository.save(tokenEntity);
        } else {
            TokenEntity tokenEntity = TokenEntity.updateToken(findToken.getId(), token);
            saveToken = tokenRepository.save(tokenEntity);
        }

        if(StringUtils.hasText(saveToken.getRefreshToken())
        && jwtProvider.validateToken(saveToken.getAccessToken())) {
            // 토큰에 인증절차를 해줍니다.
            // 그러면 토큰에 인증과 권한이 주어지게 됩니다.
            Authentication authenticationToken = jwtProvider.getAuthentication(saveToken.getAccessToken());
            log.info("authentication : " + authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 유저에 대한 권한을 주기위해서 구현한 로직입니다.
            UserDetails userDetails = new User(email, "", authoritiesForUser);
            log.info("userDetails : " + userDetails);
            // UserDetails 객체는 사용자의 주요 정보를 캡슐화하고
            // Authentication 객체는 사용자의 인증 상태와 권한을 나타냅니다
            Authentication authenticationUser =
                    new UsernamePasswordAuthenticationToken(userDetails, authoritiesForUser);
            log.info("authentication1 : " + authenticationUser);
            SecurityContextHolder.getContext().setAuthentication(authenticationUser);
        }else {
            log.error("검증 실패");
        }
        // attributes가 있는 생성자를 사용하여 PrincipalDetails 객체 생성
        // 소셜 로그인인 경우에는 attributes도 함께 가지고 있는 PrincipalDetails 객체를 생성하게 됩니다.
        PrincipalDetails principalDetails = new PrincipalDetails(findUser, oAuth2User.getAttributes());
        log.info("principalDetails : " + principalDetails);
        return principalDetails;
    }

    private List<GrantedAuthority> getAuthoritiesForUser(SocialMemberEntity findUser) {
        Role memberRole = findUser.getMemberRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + memberRole.name()));
        return authorities;
    }
}
