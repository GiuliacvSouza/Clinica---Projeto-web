package app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/cadastro", "/dashboard",
                                "/consultas", "/consultas/**",
                                "/marcar-consulta", "/reagendar-consulta",
                                "/faturas", "/perfil", "/css/**", "/images/**",
                                "/recuperar-senha", "/redefinir-senha").permitAll()
                        .anyRequest().permitAll()
                )
                .build();
    }
}
