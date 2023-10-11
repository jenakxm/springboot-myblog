package jenakxm.myblog.config;

import jenakxm.myblog.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final UserDetailService userService;

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    // 특정 HTTP 요쳥에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request -> request
                    .requestMatchers(
                            new AntPathRequestMatcher("/login"),
                            new AntPathRequestMatcher("/signup"),
                            new AntPathRequestMatcher("/user")).permitAll()
                    .anyRequest().authenticated()	// 어떠한 요청이라도 인증필요
            )
            .formLogin(login -> login	// form 방식 로그인 사용
                    .loginPage("/login")
                    .defaultSuccessUrl("/articles", true)	// 성공 시 articles로 이동
                    .permitAll()	// 이동이 막히면 안 되므로 허용
            )
            .logout(logout -> logout
                    .logoutSuccessUrl("/login") // 로그아웃하면 로그인 페이지로 이동
                    .invalidateHttpSession(true)    // 로그아웃은 기본설정으로 (/logout으로 인증해제)
            );

        return http.build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, AuthenticationManagerBuilder auth, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(bCryptPasswordEncoder);

        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
