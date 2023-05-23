package com.example.shoppingmall.security;

import com.example.shoppingmall.config.CustomAuthenticationEntryPoint;
import com.example.shoppingmall.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@NoArgsConstructor
public class SecurityConfig {

    private MemberService memberService;

        /*
    *       @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                    auth.userDetailsService(memberService)
                        .passwordEncoder(passwordEncoder());
    }
    *       ↓변경
    * */
    // 인증에 대한 인터페이스
    // 인증에 대한 환경설정을 수행합니다.
    // 스프링 시큐리티의 인증은 AuthenticationManager를 통해 이루어지며
    // AuthenticationManagerBuilder가 AuthenticationManager를 생성합니다.
    // userDetailsService를 구현하고 있는 객체로 memberService를 지정해주며
    // 비밀번호 암호화를 위해 passwordEncoder를 지정해줍니다.
//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder) throws Exception {
//        return builder.userDetailsService(memberService).passwordEncoder(passwordEncoder()).and().build();
//    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin()
//                .loginPage("/member/login")
//                .defaultSuccessUrl("/")
//                .usernameParameter("email")
//                .failureUrl("/member/login/error")
//                .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
//                .logoutSuccessUrl("/")
//        ;
//
//        http.authorizeRequests()
//                .mvcMatchers("/", "/member/**", "/item/**", "/images/**").permitAll()
//                .mvcMatchers("/admin/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//        ;
//
//        http.exceptionHandling()
//                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
//        ;
//    }
//      ↓변경

    // HTTP 인증, 인가
    // HTTP에 대해서 '인증'과 '인가'를 담당하는 메서드이며
    // 필터를 통해 인증 방식과 인증 절차에 대해서 등록하며 설정을 담당하는 메서드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // formLogin() : 인증이 필요한 요청은 스프링 시큐리티에서 사용하는 기본 Form Login Page 사용
        http.formLogin()
                // 사용자가 로그인 요청을 보내는 주소
                .loginPage("/member/login")
                // 로그인 성공하면 보내지는 기본 페이지를 의미한다.
                .defaultSuccessUrl("/")
                // 로그인 시 사용할 파라미터 이름으로 email을 지정합니다.
                .usernameParameter("email")
                // 로그인 실패하면 보내지는 페이지
                .failureUrl("/member/login/error")
                .and()
                .logout()
                // 로그아웃 url을 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                // 로그아웃 성공시 이동할 URL을 설정
                .logoutSuccessUrl("/")
        ;

        http.csrf().disable();

        // 특정한 경로에 특정한 권한을 가진 사용자만 접근할 수 있도록 아래의 메소드를 이용합니다.
        // 시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미
        http.authorizeRequests()
                // mvcMatchers : 특정 경로를 지정해서 권한 설정 가능
                // permitAll() : 모든 사용자가 인증(로그인)없이 해당 경로에 접근할 수 있도록 설정
                // 메인 페이지, 회원 관련 URL, 상품 상세 페이지, 상품 이미지를 불러오는 경로가 이에 해당
                .mvcMatchers("/", "/member/**", "/item/**", "/images/**").permitAll()
                // /admin으로 시작되는 경로는 해당 계정이 ADMIN Role일 경우에만 접근 가능하도록 합니다.
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                // permitAll()과 ADMIN일때만 접근할 수있는 요청에 설정한 URL를 제외한
                // 나머지 경로들은 모두 인증을 요구하도록 설정
                .anyRequest().authenticated()
        ;

        // 인증되지 않은 사용자가 리소스에 접근했을 때 수행되는 핸들러를 등록
        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;
        return http.build();
    }




//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
//        web.httpFirewall(defaultHttpFirewall());
//    }

    // 정적 자원(Resource)에 대해서 인증된 사용자가
    // 정적 자원에 대한 접근에 대해 '인가'에 대한 설정을
    // 담당하는 메서드
    // static 디렉터리의 하위 파일은 인증을 무시하도록 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/css/**", "/js/**", "/img/**")
                .and()
                .httpFirewall(defaultHttpFirewall());

    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }
}
