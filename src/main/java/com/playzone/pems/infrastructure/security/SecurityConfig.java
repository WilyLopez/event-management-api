package com.playzone.pems.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SupabaseJwtFilter supabaseJwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(supabaseJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/sedes/**",
                                "/api/v1/calendario/**",
                                "/api/v1/feriados",
                                "/api/v1/tarifas",
                                "/api/v1/tarifas/sedes/*/precios",
                                "/api/v1/banners/**",
                                "/api/v1/galeria/**",
                                "/api/v1/cms/**",
                                "/api/v1/contenido/**",
                                "/api/v1/resenas/**",
                                "/api/v1/promociones/**",
                                "/api/v1/actividades/**",
                                "/api/v1/novedades/**",
                                "/api/v1/paquetes/**",
                                "/api/v1/zonas/**",
                                "/api/v1/servicios-cotizacion/**",
                                "/api/v1/health/ping"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, 
                                "/api/v1/resenas",
                                "/api/v1/clientes/registro",
                                "/api/v1/auth/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.setCharacterEncoding("UTF-8");
                            res.getWriter().write(jsonError("unauthorized", e.getMessage()));
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType("application/json");
                            res.setCharacterEncoding("UTF-8");
                            res.getWriter().write(jsonError("forbidden", e.getMessage()));
                        })
                );

        return http.build();
    }

    private static String jsonError(String error, String message) {
        String safeMsg = message == null ? "" : message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
        return "{\"error\":\"" + error + "\",\"message\":\"" + safeMsg + "\"}";
    }
}
