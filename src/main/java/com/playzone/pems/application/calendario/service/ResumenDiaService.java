package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.dto.query.ResumenDiaQuery;
import com.playzone.pems.application.calendario.port.in.ConsultarResumenDiaUseCase;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumenDiaService implements ConsultarResumenDiaUseCase {

    private final ReservaPublicaRepository         reservaRepository;
    private final EventoPrivadoRepository          eventoRepository;
    private final ConfiguracionCalendarioRepository configRepository;
    private final ClientePerfilRepository          clientePerfilRepository;

    @Override
    @Transactional(readOnly = true)
    public ResumenDiaQuery ejecutar(Long idSede, LocalDate fecha) {
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);
        List<ReservaPublica> reservas = reservaRepository.findBySedeAndFecha(idSede, fecha);
        List<EventoPrivado> eventos = eventoRepository.findActivosBySedeAndFecha(idSede, fecha);

        BigDecimal ingresoEstimado = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReservaPublica.CONFIRMADA
                          || r.getEstado() == EstadoReservaPublica.COMPLETADA)
                .map(ReservaPublica::getTotalPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pagosPendientes = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReservaPublica.PENDIENTE)
                .map(r -> r.getPrecioHistorico().subtract(
                        r.getDescuentoAplicado() != null ? r.getDescuentoAplicado() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int aforoActual = (int) reservas.stream()
                .filter(r -> r.getEstado().ocupaAforo())
                .count();
        int aforoMax = cfg.getAforoMaximo();

        EventoPrivado evT1 = eventos.stream()
                .filter(e -> "T1".equals(e.getCodigoTurno())).findFirst().orElse(null);
        EventoPrivado evT2 = eventos.stream()
                .filter(e -> "T2".equals(e.getCodigoTurno())).findFirst().orElse(null);

        ResumenDiaQuery.ResumenTurno turnoT1 = ResumenDiaQuery.ResumenTurno.builder()
                .disponible(evT1 == null)
                .totalReservas(0)
                .eventoPrivado(evT1 != null ? toResumenEvento(evT1, cfg) : null)
                .build();

        ResumenDiaQuery.ResumenTurno turnoT2 = ResumenDiaQuery.ResumenTurno.builder()
                .disponible(evT2 == null)
                .totalReservas(0)
                .eventoPrivado(evT2 != null ? toResumenEvento(evT2, cfg) : null)
                .build();

        List<ResumenDiaQuery.AlertaDiaQuery> alertas = new ArrayList<>();
        if (aforoMax > 0 && aforoActual * 100 / aforoMax >= 80) {
            alertas.add(ResumenDiaQuery.AlertaDiaQuery.builder()
                    .tipo("AFORO_ALTO")
                    .mensaje("El aforo supera el 80%")
                    .nivel("ADVERTENCIA")
                    .build());
        }
        long pendientes = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReservaPublica.PENDIENTE)
                .count();
        if (pendientes > 0) {
            alertas.add(ResumenDiaQuery.AlertaDiaQuery.builder()
                    .tipo("PAGOS_PENDIENTES")
                    .mensaje(pendientes + " reserva(s) con pago pendiente")
                    .nivel("INFO")
                    .build());
        }

        return ResumenDiaQuery.builder()
                .fecha(fecha)
                .totalReservas(reservas.size())
                .totalEventos(eventos.size())
                .ingresoEstimado(ingresoEstimado)
                .pagosPendientes(pagosPendientes)
                .aforoPublicoActual(aforoActual)
                .aforoMaximo(aforoMax)
                .turnoT1(turnoT1)
                .turnoT2(turnoT2)
                .reservas(reservas.stream().map(this::toResumenReserva).toList())
                .eventos(eventos.stream().map(e -> toResumenEvento(e, cfg)).toList())
                .alertas(alertas)
                .build();
    }

    private ResumenDiaQuery.ResumenReservaQuery toResumenReserva(ReservaPublica r) {
        String nombreCliente = clientePerfilRepository.buscarPorId(r.getIdCliente())
                .map(ClientePerfil::nombreCompleto)
                .orElse(null);
        return ResumenDiaQuery.ResumenReservaQuery.builder()
                .id(r.getId())
                .numeroTicket(r.getNumeroTicket())
                .nombreNino(r.getNombreNino())
                .nombreCliente(nombreCliente)
                .estado(r.getEstado().name())
                .totalPagado(r.getTotalPagado())
                .build();
    }

    private ResumenDiaQuery.ResumenEventoQuery toResumenEvento(EventoPrivado e, ConfiguracionCalendario cfg) {
        String nombreCliente = clientePerfilRepository.buscarPorId(e.getIdCliente())
                .map(ClientePerfil::nombreCompleto)
                .orElse(null);
        String horaInicio = null;
        String horaFin = null;
        if ("T1".equals(e.getCodigoTurno()) && cfg.getTurnoT1Inicio() != null) {
            horaInicio = cfg.getTurnoT1Inicio().toString();
            horaFin = cfg.getTurnoT1Fin() != null ? cfg.getTurnoT1Fin().toString() : null;
        } else if ("T2".equals(e.getCodigoTurno()) && cfg.getTurnoT2Inicio() != null) {
            horaInicio = cfg.getTurnoT2Inicio().toString();
            horaFin = cfg.getTurnoT2Fin() != null ? cfg.getTurnoT2Fin().toString() : null;
        }
        return ResumenDiaQuery.ResumenEventoQuery.builder()
                .id(e.getId())
                .tipoEvento(e.getTipoEvento())
                .turno(e.getCodigoTurno())
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .nombreCliente(nombreCliente)
                .estado(e.getEstado().name())
                .aforoDeclarado(e.getAforoDeclarado())
                .build();
    }
}
