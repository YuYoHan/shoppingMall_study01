package com.example.shoppingmall.global.config.security;

import com.example.shoppingmall.domain.jwt.exception.JwtAccessDeniedHandler;
import com.example.shoppingmall.domain.jwt.exception.JwtAuthenticationEntryPoint;
import com.example.shoppingmall.global.config.OAuth2.OAuth2FailHandler;
import com.example.shoppingmall.global.config.OAuth2.OAuth2SuccessHandler;
import com.example.shoppingmall.global.config.OAuth2.PrincipalOAuth2UserService;
import com.example.shoppingmall.global.config.jwt.JwtProvider;
import com.example.shoppingmall.global.config.jwt.JwtSecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailHandler oAuth2FailHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .httpBasic()
                .disable()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                // 인증절차에 대한 설정
                .authorizeRequests()
                .antMatchers(HttpMethod.PUT, "/api/v1/users")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/v1/users/{memberId}")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/users/**").permitAll()
                .antMatchers("/api/v1/{itemId}/boards/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/v1/boards/{boardId}")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/v1/boards")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/v1/boards/{boardId}")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/{itemId}/boards/{boardId}/comments/**")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/items/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/items")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/v1/items/{itemId}")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/v1/items/{itemId}")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/v1/admins").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/admins/mails").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/admins/verifications").permitAll()
                .antMatchers("/api/v1/cart")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/cart/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/v1/cart")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/v1/cart/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/v1/cart/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admins/**")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/v1/admins/**")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/v1/admins/**")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll();

        http
                .apply(new JwtSecurityConfig(jwtProvider));

        http
                .oauth2Login()
                .userInfoEndpoint()
                .userService(principalOAuth2UserService)
                .and()
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailHandler);

        http
                .exceptionHandling()
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new JwtAccessDeniedHandler());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoder = new HashMap<>();
        encoder.put(idForEncode, new BCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(idForEncode, encoder);
    }
}
