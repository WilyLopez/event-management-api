package com.playzone.pems.infrastructure.security;

import com.playzone.pems.application.usuario.port.out.GenerarTokenPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final GenerarTokenPort       jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolverToken(request);

        if (token != null && jwtTokenProvider.esTokenValido(token)) {
            try {
                Long idUsuario = jwtTokenProvider.extraerIdUsuario(token);
                String rol     = jwtTokenProvider.extraerRol(token);

                String correo = resolverCorreo(request, token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(correo);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                log.warn("No se pudo establecer la autenticación del token: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolverCorreo(HttpServletRequest request, String token) {
        String correoHeader = request.getHeader("X-User-Email");
        if (correoHeader != null && !correoHeader.isBlank()) return correoHeader;
        return jwtTokenProvider.extraerCorreo(token);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth")
                || path.startsWith("/api/v1/clientes/registro")
                || path.startsWith("/api/v1/clientes/verificar")
                || path.startsWith("/api/v1/public")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
}