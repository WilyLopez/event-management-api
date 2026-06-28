package com.playzone.pems.domain.notificacion.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class NotificacionNotFoundException extends ResourceNotFoundException {

    public NotificacionNotFoundException(Long id) {
        super("Notificacion", id);
    }
}
