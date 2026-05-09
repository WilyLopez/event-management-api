package com.playzone.pems.interfaces.rest.calendario.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DisponibilidadResponse {

    private Long      idSede;
    private LocalDate fecha;
    private String    tipoDia;
    private boolean   esFeriado;
    private String    descripcionFeriado;
    private boolean   accesoPublicoActivo;
    private boolean   turnoT1Disponible;
    private boolean   turnoT2Disponible;
    private int       aforoPublicoActual;
    private int       aforoMaximo;
    private int       plazasDisponibles;
    private boolean   aforoCompleto;
    private boolean   bloqueadoManualmente;
    private String    tipoBloqueo;
    private String    motivoBloqueo;
    private int       totalReservas;
    private int       totalEventos;
    private BigDecimal ingresoEstimado;
    private boolean   tieneNotas;
    private int       ocupacionPorcentaje;
}