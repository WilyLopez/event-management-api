package com.playzone.pems.domain.evento.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum EstadoReservaPublica {

    PENDIENTE(
            "PENDIENTE",
            "Reserva creada, pago aún no confirmado"
    ),
    CONFIRMADA(
            "CONFIRMADA",
            "Pago confirmado, ticket generado"
    ),
    REPROGRAMADA(
            "REPROGRAMADA",
            "Entrada reprogramada a otra fecha"
    ),
    COMPLETADA(
            "COMPLETADA",
            "Visita realizada"
    ),
    CANCELADA(
            "CANCELADA",
            "Reserva cancelada"
    );

    private final String codigo;
    private final String descripcion;

    private static final Set<EstadoReservaPublica> ESTADOS_TERMINALES =
            Set.of(COMPLETADA, CANCELADA);

    private static final Set<EstadoReservaPublica> CANCELABLES =
            Set.of(PENDIENTE, CONFIRMADA);

    private static final Set<EstadoReservaPublica> REPROGRAMABLES =
            Set.of(CONFIRMADA);

    public boolean esTerminal() {
        return ESTADOS_TERMINALES.contains(this);
    }

    public boolean esCancelable() {
        return CANCELABLES.contains(this);
    }

    public boolean esReprogramable() {
        return REPROGRAMABLES.contains(this);
    }

    public boolean ocupaAforo() {
        return this == CONFIRMADA;
    }

    public static EstadoReservaPublica desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Estado de reserva pública inválido: '" + codigo + "'"));
    }
}