package com.playzone.pems.domain.usuario.exception;

import com.playzone.pems.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UsuarioBlockedException extends BusinessException {

    private static final String CODIGO = "USUARIO_BLOQUEADO";

    public UsuarioBlockedException(LocalDateTime bloqueadoHasta) {
        super(
                String.format(
                        "Cuenta bloqueada temporalmente por intentos fallidos. " +
                                "Intente nuevamente después de las %s.",
                        bloqueadoHasta.format(DateTimeFormatter.ofPattern("HH:mm 'del' dd/MM/yyyy"))
                ),
                HttpStatus.FORBIDDEN,
                CODIGO
        );
    }

    public UsuarioBlockedException() {
        super(
                "Cuenta bloqueada temporalmente por múltiples intentos fallidos.",
                HttpStatus.FORBIDDEN,
                CODIGO
        );
    }
}