package ac.su.inclassspringsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity  // URL 요청에 대한 Spring Security 동작 활성화
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests( // 요청 인가 여부 결정을 위한 조건 판단
//                        (authorizeHttpRequests) ->
//                                authorizeHttpRequests.requestMatchers(
//                                        // Apache Ant 스타일 패턴을 사용해 URL 매칭 정의
//                                        new AntPathRequestMatcher(
//                                                "/**"   // 모든 URL 패턴에 대해 허용
//                                        )
//                                        // ).denyAll() // 403 NOT_A
//                                ).permitAll()
//                )
//        ;
        http
                .authorizeHttpRequests(
                        (authorizeHttpRequests) ->
                                authorizeHttpRequests.requestMatchers(
                                        // Apache Ant 스타일 패턴을 사용해 URL 매칭 정의
                                        new AntPathRequestMatcher(
                                                "/**"   // 모든 URL 패턴에 대해 허용
                                        )
                                        // ).denyAll()
                                ).permitAll()
                )
                .csrf(
                        (csrf) ->
                                csrf.ignoringRequestMatchers(
                                        // 필요 시 특정 페이지 CSRF 토큰 무시 설정
                                        new AntPathRequestMatcher("/h2-console/**")
                                        // , new AntPathRequestMatcher("/login")
                                        // , new AntPathRequestMatcher("/logout")
                                        // , new AntPathRequestMatcher("/signup")
                                )
                )
                .headers(
                        (headers) ->
                                headers.addHeaderWriter(
                                        new XFrameOptionsHeaderWriter(
                                                // X-Frame-Options 는 웹 페이지 내에서 다른 웹 페이지 표시 허용 여부 제어
                                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN  // 동일 도메인 내에서 표시 허용
                                        )
                                )
                )
                .formLogin(
                        (formLogin) ->
                                formLogin
                                        .loginPage("/users/login")
                                        .defaultSuccessUrl("/")
                )
                .logout(
                        (logout) ->
                                logout
                                        .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout"))
                                        .logoutSuccessUrl("/")
                                        .invalidateHttpSession(true)
                )
        ;

        return http.build();
    }

//     passwordEncoder 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
