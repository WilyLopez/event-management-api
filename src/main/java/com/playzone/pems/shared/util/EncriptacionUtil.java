package com.playzone.pems.shared.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class EncriptacionUtil {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(12);

    private EncriptacionUtil() {}

    public static String hashear(String contraseniaPlana) {
        if (contraseniaPlana == null || contraseniaPlana.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }
        return ENCODER.encode(contraseniaPlana);
    }

    public static boolean verificar(String contraseniaPlana, String hash) {
        if (contraseniaPlana == null || hash == null) {
            return false;
        }
        return ENCODER.matches(contraseniaPlana, hash);
    }

    public static boolean esHashBcrypt(String texto) {
        return texto != null
                && texto.length() == 60
                && (texto.startsWith("$2a$") || texto.startsWith("$2b$") || texto.startsWith("$2y$"));
    }
}