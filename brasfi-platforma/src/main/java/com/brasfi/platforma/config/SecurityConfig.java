package com.brasfi.platforma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/registrar",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/imagens/**",
                                "/favicon.ico",
                                "/exemplo.jpg",
                                "/favicon-brasfi.png"
                        ).permitAll() // rotas públicas
                        .requestMatchers("/").authenticated() // raiz exige login
                        .anyRequest().authenticated() // tudo o que não foi listado acima também exige login
                )
                .formLogin(login -> login
                        .loginPage("/login") // especifica a página de login
                        .permitAll() // permite que todos acessem a página de login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")                     // URL chamada para logout
                        .logoutSuccessUrl("/login?logout")        // para onde redirecionar após logout
                        .invalidateHttpSession(true)              // invalida a sessão
                        .deleteCookies("JSESSIONID")              // limpa o cookie de sessão
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**") // CSRF desabilitado para h2-console
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // permite iframe para h2-console
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //  encriptar senhas
    }
}
