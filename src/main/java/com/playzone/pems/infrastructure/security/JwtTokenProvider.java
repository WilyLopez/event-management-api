package com.playzone.pems.infrastructure.security;

import com.playzone.pems.application.usuario.port.out.GenerarTokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider implements GenerarTokenPort {

    private static final String CLAIM_ROL    = "rol";
    private static final String CLAIM_CORREO = "correo";

    @Value("${playzone.seguridad.jwt-secret}")
    private String secret;

    @Value("${playzone.seguridad.jwt-expiracion-ms:1800000}")
    private long expiracionMs;

    @Value("${playzone.seguridad.jwt-verificacion-expiracion-ms:86400000}")
    private long expiracionVerificacionMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String generarTokenAcceso(Long idUsuario, String correo, String rol) {
        Date ahora       = new Date();
        Date vencimiento = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(String.valueOf(idUsuario))
                .claim(CLAIM_CORREO, correo)
                .claim(CLAIM_ROL, rol)
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(key())
                .compact();
    }

    @Override
    public String generarTokenVerificacionCorreo(Long idCliente) {
        Date ahora       = new Date();
        Date vencimiento = new Date(ahora.getTime() + expiracionVerificacionMs);

        return Jwts.builder()
                .subject(String.valueOf(idCliente))
                .id(UUID.randomUUID().toString())
                .claim("tipo", "VERIFICACION_CORREO")
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(key())
                .compact();
    }

    @Override
    public boolean esTokenValido(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Long extraerIdUsuario(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    @Override
    public String extraerCorreo(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(CLAIM_CORREO, String.class);
    }

    @Override
    public String extraerRol(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(CLAIM_ROL, String.class);
    }

    @Override
    public String resolverToken(jakarta.servlet.http.HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}