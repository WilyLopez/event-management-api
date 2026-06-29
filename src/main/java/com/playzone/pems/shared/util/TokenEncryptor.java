package com.playzone.pems.shared.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class TokenEncryptor {

    private static final String ALGORITMO = "AES";

    private final SecretKeySpec secretKey;

    public TokenEncryptor(@Value("${playzone.encryption.key:playzoneSecretKey1234567890123456}") String keyString) {
        try {
            byte[] keyBytes = keyString.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            keyBytes = sha.digest(keyBytes);
            keyBytes = Arrays.copyOf(keyBytes, 16);
            this.secretKey = new SecretKeySpec(keyBytes, ALGORITMO);
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar la clave de encriptacion", e);
        }
    }

    public String encrypt(String text) {
        if (text == null || text.isBlank()) return text;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar datos", e);
        }
    }

    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isBlank()) return encryptedText;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar datos", e);
        }
    }
}
