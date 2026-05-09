package com.playzone.pems.domain.contrato.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EstadoContrato {

    BORRADOR("BORRADOR",          true,  false),
    ENVIADO("ENVIADO",            true,  false),
    PENDIENTE_FIRMA("PENDIENTE_FIRMA", true, false),
    FIRMADO("FIRMADO",            false, true),
    VENCIDO("VENCIDO",            false, false),
    CANCELADO("CANCELADO",        false, false),
    ARCHIVADO("ARCHIVADO",        false, false);

    private final String  codigo;
    private final boolean editable;
    private final boolean firmado;

    public boolean esEditable() { return editable; }
    public boolean esFirmado()  { return firmado;  }
    public boolean esTerminal() {
        return this == FIRMADO || this == CANCELADO || this == ARCHIVADO;
    }
}