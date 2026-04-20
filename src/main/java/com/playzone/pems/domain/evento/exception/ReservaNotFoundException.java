package com.playzone.pems.domain.evento.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class ReservaNotFoundException extends ResourceNotFoundException {

    public ReservaNotFoundException(Long id) {
        super("ReservaPublica", id);
    }

    public ReservaNotFoundException(String numeroTicket) {
        super("ReservaPublica", "numeroTicket", numeroTicket);
    }
}