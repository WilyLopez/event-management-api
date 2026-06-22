package com.playzone.pems.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro diseñado para interceptar y corregir URLs mal formadas provenientes del frontend.
 * Específicamente, corrige el problema donde el prefijo base "/api/v1" llega duplicado.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UrlSaneamientoFilter extends OncePerRequestFilter {

    private static final String PREFIJO_DUPLICADO = "/api/v1/api/v1";
    private static final String PREFIJO_CORRECTO = "/api/v1";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI != null && requestURI.startsWith(PREFIJO_DUPLICADO)) {
            String uriCorregida = requestURI.replaceFirst(PREFIJO_DUPLICADO, PREFIJO_CORRECTO);
            
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
                @Override
                public String getRequestURI() {
                    return uriCorregida;
                }

                @Override
                public String getServletPath() {
                    return uriCorregida;
                }
            };
            
            filterChain.doFilter(wrapper, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
