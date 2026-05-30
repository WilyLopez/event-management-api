package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.query.DashboardFinancieroQuery;
import com.playzone.pems.application.finanzas.port.in.ConsultarDashboardFinancieroUseCase;
import com.playzone.pems.domain.finanzas.repository.RegistroEgresoRepository;
import com.playzone.pems.domain.finanzas.repository.RegistroIngresoRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ReservaPublicaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardFinancieroService implements ConsultarDashboardFinancieroUseCase {

    private final RegistroIngresoRepository   registroIngresoRepository;
    private final RegistroEgresoRepository    registroEgresoRepository;
    private final ReservaPublicaJpaRepository reservaJpaRepository;
    private final EventoPrivadoJpaRepository  eventoJpaRepository;

    @Override
    public DashboardFinancieroQuery consultar(Long idSede, int anio, int mes) {
        BigDecimal ingresoReservas  = registroIngresoRepository
                .sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes, "RESERVA_PUBLICA");
        BigDecimal ingresoAdelantos = registroIngresoRepository
                .sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes, "ADELANTO_EVENTO");
        BigDecimal ingresoManual    = registroIngresoRepository
                .sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes, "INGRESO_MANUAL")
                .add(registroIngresoRepository
                        .sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes, "OTRO"));
        BigDecimal totalIngresos    = ingresoReservas.add(ingresoAdelantos).add(ingresoManual);

        BigDecimal egresoFijo       = registroEgresoRepository
                .sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes, "RECURRENTE_FIJO");
        BigDecimal egresoVariable   = registroEgresoRepository
                .sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes, "RECURRENTE_VARIABLE");
        BigDecimal egresoEventual   = registroEgresoRepository
                .sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes, "EVENTUAL");
        BigDecimal totalEgresos     = egresoFijo.add(egresoVariable).add(egresoEventual);

        BigDecimal utilidadNeta     = totalIngresos.subtract(totalEgresos);

        long reservasConfirmadas    = reservaJpaRepository.countConfirmadasBySedeAndPeriodo(idSede, anio, mes);
        long reservasCanceladas     = reservaJpaRepository.countCanceladasBySedeAndPeriodo(idSede, anio, mes);
        BigDecimal ticketPromedio   = reservaJpaRepository.avgTicketBySedeAndPeriodo(idSede, anio, mes);
        BigDecimal saldoPendiente   = eventoJpaRepository.sumSaldoPendienteBySedeAndMes(idSede, anio, mes);

        return DashboardFinancieroQuery.builder()
                .anio(anio)
                .mes(mes)
                .totalIngresos(totalIngresos)
                .totalEgresos(totalEgresos)
                .utilidadNeta(utilidadNeta)
                .ingresoReservas(ingresoReservas)
                .ingresoAdelantos(ingresoAdelantos)
                .ingresoManual(ingresoManual)
                .egresoFijo(egresoFijo)
                .egresoVariable(egresoVariable)
                .egresoEventual(egresoEventual)
                .reservasConfirmadas(reservasConfirmadas)
                .reservasCanceladas(reservasCanceladas)
                .ticketPromedio(ticketPromedio)
                .saldoPendienteEventos(saldoPendiente)
                .build();
    }
}
