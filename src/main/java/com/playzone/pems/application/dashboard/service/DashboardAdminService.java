package com.playzone.pems.application.dashboard.service;

import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;
import com.playzone.pems.application.calendario.port.in.ConsultarDisponibilidadUseCase;
import com.playzone.pems.application.dashboard.dto.query.*;
import com.playzone.pems.application.dashboard.port.in.ConsultarDashboardAdminUseCase;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.AperturaCajaJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ReservaPublicaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardAdminService implements ConsultarDashboardAdminUseCase {

    private final ReservaPublicaJpaRepository  reservaJpaRepo;
    private final EventoPrivadoJpaRepository   eventoJpaRepo;
    private final AperturaCajaJpaRepository    cajaJpaRepo;
    private final ConsultarDisponibilidadUseCase calendarioService;

    @Override
    @Transactional(readOnly = true)
    public DashboardAdminQuery obtener(Long idSede) {
        LocalDate hoy       = LocalDate.now();
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        int reservasHoy       = reservaJpaRepo.countActivasBySedeAndFecha(idSede, hoy);
        int reservasConf      = reservaJpaRepo.countConfirmadasBySedeAndFecha(idSede, hoy);
        int pendientesPago    = reservaJpaRepo.countBySedeAndFechaAndEstado(
                                    idSede, hoy, EstadoReservaPublica.PENDIENTE);

        DisponibilidadQuery hoyDisp = calendarioService.consultarPorFecha(idSede, hoy);
        int aforoMaximo       = hoyDisp.getAforoMaximo();
        int plazas            = hoyDisp.getPlazasDisponibles();

        int eventosSemana     = eventoJpaRepo.countBySedeAndRangoAndEstado(
                                    idSede, hoy, finSemana, EstadoEventoPrivado.CONFIRMADA);
        int solicitudes       = eventoJpaRepo.countBySedeAndEstado(
                                    idSede, EstadoEventoPrivado.SOLICITADA);
        int saldoPendiente    = eventoJpaRepo.countConfirmadosConSaldo(idSede);
        boolean cajaAbierta   = cajaJpaRepo.findBySede_IdAndFecha(idSede, hoy)
                .filter(c -> "ABIERTA".equals(c.getEstado().name()))
                .isPresent();

        List<ReservaPublicaEntity> reservasDetalle = reservaJpaRepo.findBySede_IdAndFechaEvento(idSede, hoy);
        List<AgendaReservaQuery> agendaReservas = reservasDetalle.stream()
                .filter(r -> r.getEstado() != EstadoReservaPublica.CANCELADA)
                .map(r -> AgendaReservaQuery.builder()
                        .numeroTicket(r.getNumeroTicket())
                        .nombreNino(r.getNombreNino())
                        .edadNino(r.getEdadNino())
                        .estado(r.getEstado().name())
                        .build())
                .toList();

        List<EventoPrivadoEntity> eventosHoy = eventoJpaRepo
                .findBySede_IdAndFechaEvento(idSede, hoy).stream()
                .filter(e -> e.getEstado() == EstadoEventoPrivado.CONFIRMADA)
                .toList();
        List<AgendaEventoQuery> agendaEventos = eventosHoy.stream()
                .map(e -> AgendaEventoQuery.builder()
                        .id(e.getId())
                        .tipoEvento(e.getTipoEvento())
                        .nombreCliente(e.getCliente().getNombre())
                        .turno(e.getTurno().getCodigo())
                        .estado(e.getEstado().getCodigo())
                        .build())
                .toList();

        List<ReservasDiaQuery> tendencia = reservaJpaRepo
                .countAgrupadoPorDia(idSede, hoy.minusDays(29), hoy);

        List<DisponibilidadQuery> semanaDisp = calendarioService
                .consultarRango(idSede, hoy, hoy.plusDays(6));
        List<DisponibilidadDiaQuery> semana = semanaDisp.stream()
                .map(d -> DisponibilidadDiaQuery.builder()
                        .fecha(d.getFecha())
                        .turnoT1Disponible(d.isTurnoT1Disponible())
                        .turnoT2Disponible(d.isTurnoT2Disponible())
                        .totalEventos(d.getTotalEventos())
                        .build())
                .toList();

        return DashboardAdminQuery.builder()
                .fecha(hoy)
                .reservasHoy(reservasHoy)
                .reservasConfirmadas(reservasConf)
                .pendientesPago(pendientesPago)
                .aforoMaximo(aforoMaximo)
                .plazasDisponibles(plazas)
                .eventosEstaSemana(eventosSemana)
                .solicitudesEventoSinResponder(solicitudes)
                .eventosSaldoPendiente(saldoPendiente)
                .cajaAbierta(cajaAbierta)
                .reservasHoyDetalle(agendaReservas)
                .eventosHoyDetalle(agendaEventos)
                .reservasUltimos30Dias(tendencia)
                .disponibilidadSemana(semana)
                .build();
    }
}
