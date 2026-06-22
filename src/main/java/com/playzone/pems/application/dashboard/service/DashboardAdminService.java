package com.playzone.pems.application.dashboard.service;

import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;
import com.playzone.pems.application.calendario.port.in.ConsultarDisponibilidadUseCase;
import com.playzone.pems.application.dashboard.dto.query.*;
import com.playzone.pems.application.dashboard.port.in.ConsultarDashboardAdminUseCase;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import com.playzone.pems.domain.finanzas.repository.AperturaCajaRepository;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardAdminService implements ConsultarDashboardAdminUseCase {

    private final ReservaPublicaRepository       reservaPublicaRepository;
    private final EventoPrivadoRepository        eventoPrivadoRepository;
    private final AperturaCajaRepository         aperturaCajaRepository;
    private final ConsultarDisponibilidadUseCase calendarioService;
    private final ClientePerfilRepository        clientePerfilRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardAdminQuery obtener(Long idSede) {
        LocalDate hoy       = LocalDate.now();
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        int reservasHoy    = reservaPublicaRepository.countActivasBySedeAndFecha(idSede, hoy);
        int reservasConf   = reservaPublicaRepository.countConfirmadasBySedeAndFecha(idSede, hoy);
        int pendientesPago = reservaPublicaRepository.countBySedeAndFechaAndEstado(
                                 idSede, hoy, EstadoReservaPublica.PENDIENTE);

        DisponibilidadQuery hoyDisp = calendarioService.consultarPorFecha(idSede, hoy);
        int aforoMaximo = hoyDisp.getAforoMaximo();
        int plazas      = hoyDisp.getPlazasDisponibles();

        int eventosSemana  = eventoPrivadoRepository.countBySedeAndRangoAndEstado(
                                 idSede, hoy, finSemana, EstadoEventoPrivado.CONFIRMADA);
        int solicitudes    = eventoPrivadoRepository.countBySedeAndEstado(
                                 idSede, EstadoEventoPrivado.SOLICITADA);
        int saldoPendiente = eventoPrivadoRepository.countConfirmadosConSaldo(idSede);
        boolean cajaAbierta = aperturaCajaRepository.findBySedeAndFecha(idSede, hoy)
                .filter(c -> c.getEstado() == EstadoCaja.ABIERTA)
                .isPresent();

        List<ReservaPublica> reservasDetalle = reservaPublicaRepository.findBySedeAndFecha(idSede, hoy);
        List<AgendaReservaQuery> agendaReservas = reservasDetalle.stream()
                .filter(r -> r.getEstado() != EstadoReservaPublica.CANCELADA)
                .map(r -> AgendaReservaQuery.builder()
                        .numeroTicket(r.getNumeroTicket())
                        .nombreNino(r.getNombreNino())
                        .edadNino(r.getEdadNino())
                        .estado(r.getEstado().name())
                        .build())
                .toList();

        List<EventoPrivado> eventosHoy = eventoPrivadoRepository
                .findBySedeAndFecha(idSede, hoy).stream()
                .filter(e -> e.getEstado() == EstadoEventoPrivado.CONFIRMADA)
                .toList();
        List<AgendaEventoQuery> agendaEventos = eventosHoy.stream()
                .map(e -> AgendaEventoQuery.builder()
                        .id(e.getId())
                        .tipoEvento(e.getTipoEvento())
                        .nombreCliente(clientePerfilRepository.buscarPorId(e.getIdCliente())
                                .map(ClientePerfil::nombreCompleto)
                                .orElse(null))
                        .turno(e.getCodigoTurno())
                        .estado(e.getEstado().getCodigo())
                        .build())
                .toList();

        List<ReservasDiaQuery> tendencia = reservaPublicaRepository
                .countAgrupadoPorDia(idSede, hoy.minusDays(29), hoy)
                .stream()
                .map(r -> new ReservasDiaQuery(r.fecha(), r.cantidad()))
                .toList();

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
