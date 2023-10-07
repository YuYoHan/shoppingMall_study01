package com.example.shoppingmall.config.oauth2;

import com.example.shoppingmall.config.auth.PrincipalDetails;
import com.example.shoppingmall.config.jwt.JwtProvider;
import com.example.shoppingmall.config.oauth2.provicer.GoogleUserInfo;
import com.example.shoppingmall.config.oauth2.provicer.NaverUserInfo;
import com.example.shoppingmall.config.oauth2.provicer.OAuth2UserInfo;
import com.example.shoppingmall.dto.jwt.TokenDTO;
import com.example.shoppingmall.dto.member.Role;
import com.example.shoppingmall.entity.jwt.TokenEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import com.example.shoppingmall.repository.jwt.TokenRepository;
import com.example.shoppingmall.repository.member.MemberRepository;
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

// 소셜 로그인하면 사용자 정보를 가지고 온다.
// 가져온 정보와 PrincipalDetails 객체를 생성합니다.
@Service
@Log4j2
@RequiredArgsConstructor
public class PrincipalOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // ClientRegistration은 Spring Security에서 OAuth 2.0 또는 OpenID Connect (OIDC) 클라이언트
        // 애플리케이션의 등록 정보를 나타내는 클래스입니다. 클라이언트 애플리케이션의 설정 및 속성을 포함합니다.

        // userRequest.getClientRegistration()은 인증 및 인가된 사용자 정보를 가져오는
        // Spring Security에서 제공하는 메서드입니다.
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        log.info("clientRegistration : " + clientRegistration);
        String socialAccessToken = userRequest.getAccessToken().getTokenValue();
        log.info("소셜 로그인 accessToken : " + socialAccessToken);

        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService =
                new DefaultOAuth2UserService();
        log.info("oAuth2UserService : " + oAuth2UserService);

        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        log.info("oAuth2User : " + oAuth2User);
        log.info("getAttribute : " + oAuth2User.getAttributes());

        // 회원가입 강제 진행
        OAuth2UserInfo oAuth2UserInfo = null;
        String registrationId = clientRegistration.getRegistrationId();

        if(registrationId.equals("google")) {
            log.info("구글 로그인");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User, clientRegistration);
        } else if(registrationId.equals("naver")) {
            log.info("네이버 로그인");
            oAuth2UserInfo = new NaverUserInfo(oAuth2User, clientRegistration);
        } else {
            log.error("지언하지 않는 소셜 로그인입니다.");
        }

        // 사용자가 로그인한 소셜 서비스를 가지고 옵니다.
        // 예시) google or naver 같은 값을 가질 수 있다.
        String provider = oAuth2UserInfo.getProvider();
        // 사용자의 소셜 서비스(provider)에서 발급된 고유한 식별자를 가져옵니다.
        // 이 값은 해당 소셜 서비스에서 유니크한 사용자를 식별하는 용도로 사용됩니다.
        String providerID = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();
        // 사용자의 이메일 주소를 가지고 옵니다.
        // 소셜 서비스에서 제공하는 이메일 정보를 사용합니다.
        String email = oAuth2UserInfo.getEmail();
        // 소셜 로그인의 경우 무조건 USER 등급으로 고정이다.
        Role role = Role.USER;

        MemberEntity findUser = memberRepository.findByUserEmail(email);

        if(findUser == null) {
            log.info("OAuth 로그인이 최초입니다.");
            log.info("OAuth 자동 회원가입을 진행합니다.");

            findUser = MemberEntity.builder()
                    .userEmail(email)
                    .userName(name)
                    .role(role)
                    .provider(provider)
                    .providerId(providerID)
                    .build();

            log.info("member : " + findUser);
            memberRepository.save(findUser);
        } else {
            log.info("로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어 있습니다.");
        }
        List<GrantedAuthority> authoritiesForUser = getAuthoritiesForUser(findUser);
        TokenDTO tokenForOAuth2 = jwtProvider.createTokenForOAuth2(findUser.getUserEmail(), authoritiesForUser);
        TokenEntity findToken = tokenRepository.findByUserEmail(tokenForOAuth2.getUserEmail());

        TokenEntity saveToken = null;
        if(findToken == null) {
            TokenEntity tokenEntity = TokenEntity.toTokenEntity(tokenForOAuth2);
            saveToken = tokenRepository.save(tokenEntity);
            log.info("token : " + saveToken);
        } else {
            tokenForOAuth2 = TokenDTO.builder()
                    .id(findToken.getId())
                    .grantType(tokenForOAuth2.getGrantType())
                    .accessToken(tokenForOAuth2.getAccessToken())
                    .refreshToken(tokenForOAuth2.getRefreshToken())
                    .userEmail(tokenForOAuth2.getUserEmail())
                    .build();
            TokenEntity tokenEntity = TokenEntity.toTokenEntity(tokenForOAuth2);
            saveToken = tokenRepository.save(tokenEntity);
            log.info("token : " + saveToken);
        }

        // 토큰이 제대로 되어 있나 검증
        if(StringUtils.hasText(saveToken.getAccessToken())
                && jwtProvider.validateToken(saveToken.getAccessToken())) {
            Authentication authentication = jwtProvider.getAuthentication(saveToken.getAccessToken());
            log.info("authentication : " + authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = new User(email, "", authoritiesForUser);
            log.info("userDetails : " + userDetails);
            Authentication authentication1 =
                    new UsernamePasswordAuthenticationToken(userDetails, authoritiesForUser);
            log.info("authentication1 : " + authentication1);
            SecurityContextHolder.getContext().setAuthentication(authentication1);
        } else {
            log.info("검증 실패");
        }

        // attributes가 있는 생성자를 사용하여 PrincipalDetails 객체 생성
        // 소셜 로그인인 경우에는 attributes도 함께 가지고 있는 PrincipalDetails 객체를 생성하게 됩니다.
        PrincipalDetails principalDetails = new PrincipalDetails(findUser, oAuth2User.getAttributes());
        log.info("principalDetails : " + principalDetails);
        return principalDetails;
    }

    private List<GrantedAuthority> getAuthoritiesForUser(MemberEntity findUser) {
        Role role = findUser.getRole();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        log.info("권한 : " + role.name());
        return authorities;
    }
}

