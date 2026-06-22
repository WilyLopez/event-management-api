package com.playzone.pems.infrastructure.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SupabaseJwksProvider {

    private final String jwksUrl;
    private final ConcurrentHashMap<String, PublicKey> cache = new ConcurrentHashMap<>();
    private final AtomicLong lastFetchAt = new AtomicLong(0);
    private static final long CACHE_TTL_MS = 60 * 60 * 1000L;

    public SupabaseJwksProvider(@Value("${supabase.jwt.jwks-url}") String jwksUrl) {
        this.jwksUrl = jwksUrl;
    }

    public PublicKey resolverClavePorKid(String kid) {
        long now = System.currentTimeMillis();
        if (now - lastFetchAt.get() > CACHE_TTL_MS || !cache.containsKey(kid)) {
            refrescarCache();
        }
        PublicKey key = cache.get(kid);
        if (key == null) {
            throw new IllegalStateException("Clave publica no encontrada para kid: " + kid);
        }
        return key;
    }

    private synchronized void refrescarCache() {
        try {
            URL url = URI.create(jwksUrl).toURL();
            try (InputStream is = url.openStream()) {
                JsonNode root = new ObjectMapper().readTree(is);
                JsonNode keys = root.get("keys");
                if (keys == null || !keys.isArray()) return;
                for (JsonNode keyNode : keys) {
                    String kid = keyNode.get("kid").asText();
                    PublicKey publicKey = construirClavePublicaES256(keyNode);
                    cache.put(kid, publicKey);
                }
                lastFetchAt.set(System.currentTimeMillis());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error al descargar JWKS de Supabase", e);
        }
    }

    private PublicKey construirClavePublicaES256(JsonNode keyNode) throws Exception {
        String x = keyNode.get("x").asText();
        String y = keyNode.get("y").asText();
        byte[] xBytes = Base64.getUrlDecoder().decode(x);
        byte[] yBytes = Base64.getUrlDecoder().decode(y);
        BigInteger xInt = new BigInteger(1, xBytes);
        BigInteger yInt = new BigInteger(1, yBytes);
        AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
        params.init(new ECGenParameterSpec("secp256r1"));
        ECParameterSpec ecSpec = params.getParameterSpec(ECParameterSpec.class);
        ECPoint point = new ECPoint(xInt, yInt);
        ECPublicKeySpec keySpec = new ECPublicKeySpec(point, ecSpec);
        return KeyFactory.getInstance("EC").generatePublic(keySpec);
    }
}
