package com.playzone.pems.infrastructure.persistence.calendario.mapper;

import com.playzone.pems.domain.calendario.model.*;
import com.playzone.pems.infrastructure.persistence.calendario.entity.*;
import org.springframework.stereotype.Component;

@Component
public class CalendarioEntityMapper {

    public Turno toDomain(TurnoEntity e) {
        if (e == null) return null;
        return Turno.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .descripcion(e.getDescripcion())
                .horaInicio(e.getHoraInicio())
                .horaFin(e.getHoraFin())
                .build();
    }

    public Feriado toDomain(FeriadoEntity e) {
        if (e == null) return null;
        return Feriado.builder()
                .id(e.getId())
                .tipoFeriado(e.getTipoFeriado())
                .fecha(e.getFecha())
                .descripcion(e.getDescripcion())
                .anio(e.getAnio())
                .fechaCreacion(e.getFechaCreacion())
                .idUsuarioCreador(e.getCreadoPor() != null ? e.getCreadoPor().getId() : null)
                .build();
    }

    public BloqueCalendario toDomain(BloqueCalendarioEntity e) {
        if (e == null) return null;
        return BloqueCalendario.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .motivo(e.getMotivo())
                .activo(e.isActivo())
                .idUsuarioCreador(e.getUsuarioCreador().getId())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public DisponibilidadDiaria toDomain(DisponibilidadDiariaEntity e) {
        if (e == null) return null;
        return DisponibilidadDiaria.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .fecha(e.getFecha())
                .accesoPublicoActivo(e.isAccesoPublicoActivo())
                .turnoT1Disponible(e.isTurnoT1Disponible())
                .turnoT2Disponible(e.isTurnoT2Disponible())
                .aforoPublicoActual(e.getAforoPublicoActual())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public Tarifa toDomain(TarifaEntity e) {
        if (e == null) return null;
        return Tarifa.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .tipoDia(e.getTipoDia())
                .precio(e.getPrecio())
                .vigenciaDesde(e.getVigenciaDesde())
                .vigenciaHasta(e.getVigenciaHasta())
                .activo(e.isActivo())
                .idUsuarioCreador(e.getUsuarioCreador().getId())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}