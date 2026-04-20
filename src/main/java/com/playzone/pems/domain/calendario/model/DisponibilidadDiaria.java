package com.playzone.pems.domain.calendario.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadDiaria {

    private Long          id;
    private Long          idSede;
    private LocalDate     fecha;
    private boolean       accesoPublicoActivo;
    private boolean       turnoT1Disponible;
    private boolean       turnoT2Disponible;
    private int           aforoPublicoActual;
    private LocalDateTime fechaActualizacion;

    public boolean admiteReservaPublica(int aforoMaximo) {
        return accesoPublicoActivo && aforoPublicoActual < aforoMaximo;
    }

    public int plazasDisponibles(int aforoMaximo) {
        if (!accesoPublicoActivo) return 0;
        return Math.max(0, aforoMaximo - aforoPublicoActual);
    }

    public boolean esTurnoDisponibleParaEvento(String codigoTurno) {
        return switch (codigoTurno.toUpperCase()) {
            case "T1" -> turnoT1Disponible;
            case "T2" -> turnoT2Disponible;
            default   -> false;
        };
    }
}