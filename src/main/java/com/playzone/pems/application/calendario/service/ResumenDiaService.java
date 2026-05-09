package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.dto.query.ResumenDiaQuery;
import com.playzone.pems.application.calendario.port.in.ConsultarResumenDiaUseCase;
import com.playzone.pems.domain.calendario.model.DisponibilidadDiaria;
import com.playzone.pems.domain.calendario.model.Turno;
import com.playzone.pems.domain.calendario.repository.DisponibilidadDiariaRepository;
import com.playzone.pems.domain.calendario.repository.TurnoRepository;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumenDiaService implements ConsultarResumenDiaUseCase {

    private final DisponibilidadDiariaRepository disponibilidadRepository;
    private final ReservaPublicaRepository        reservaRepository;
    private final EventoPrivadoRepository         eventoRepository;
    private final ClienteRepository               clienteRepository;
    private final TurnoRepository                 turnoRepository;

    @Value("${playzone.negocio.aforo-maximo:60}")
    private int aforoMaximo;

    @Override
    @Transactional(readOnly = true)
    public ResumenDiaQuery ejecutar(Long idSede, LocalDate fecha) {
        DisponibilidadDiaria disp = disponibilidadRepository
                .findBySedeAndFecha(idSede, fecha)
                .orElse(DisponibilidadDiaria.builder()
                        .idSede(idSede)
                        .fecha(fecha)
                        .accesoPublicoActivo(true)
                        .turnoT1Disponible(true)
                        .turnoT2Disponible(true)
                        .aforoPublicoActual(0)
                        .build());

        List<ReservaPublica> reservas = reservaRepository.findBySedeAndFecha(idSede, fecha);
        List<EventoPrivado>  eventos  = eventoRepository.findBySedeAndFecha(idSede, fecha);
        List<Turno>          turnos   = turnoRepository.findAll();
        Map<Long, Turno>     turnoMap = turnos.stream().collect(Collectors.toMap(Turno::getId, t -> t));

        BigDecimal ingresoTotal = reservas.stream()
                .map(r -> r.getTotalPagado() != null ? r.getTotalPagado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal adelantosEventos = eventos.stream()
                .map(e -> e.getMontoAdelanto() != null ? e.getMontoAdelanto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoPendiente = eventos.stream()
                .map(e -> e.calcularMontoSaldo() != null ? e.calcularMontoSaldo() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ResumenDiaQuery.ResumenReservaQuery> resReservas = reservas.stream()
                .map(r -> {
                    Cliente c = clienteRepository.findById(r.getIdCliente()).orElse(null);
                    return ResumenDiaQuery.ResumenReservaQuery.builder()
                            .id(r.getId())
                            .numeroTicket(r.getNumeroTicket())
                            .nombreNino(r.getNombreNino())
                            .nombreCliente(c != null ? c.nombreParaMostrar() : "Anonimo")
                            .estado(r.getEstado().name())
                            .totalPagado(r.getTotalPagado())
                            .build();
                }).toList();

        List<ResumenDiaQuery.ResumenEventoQuery> resEventos = eventos.stream()
                .map(e -> toEventoQuery(e, turnoMap.get(e.getIdTurno())))
                .toList();

        Turno t1 = turnos.stream().filter(t -> "T1".equals(t.getCodigo())).findFirst().orElse(null);
        Turno t2 = turnos.stream().filter(t -> "T2".equals(t.getCodigo())).findFirst().orElse(null);

        return ResumenDiaQuery.builder()
                .fecha(fecha)
                .totalReservas(reservas.size())
                .totalEventos(eventos.size())
                .ingresoEstimado(ingresoTotal.add(adelantosEventos))
                .pagosPendientes(saldoPendiente)
                .aforoPublicoActual(disp.getAforoPublicoActual())
                .aforoMaximo(aforoMaximo)
                .turnoT1(buildResumenTurno(disp.isTurnoT1Disponible(), t1, eventos))
                .turnoT2(buildResumenTurno(disp.isTurnoT2Disponible(), t2, eventos))
                .reservas(resReservas)
                .eventos(resEventos)
                .alertas(new ArrayList<>()) // Se puede implementar logica de alertas
                .build();
    }

    private ResumenDiaQuery.ResumenTurno buildResumenTurno(
            boolean disponible, Turno turno, List<EventoPrivado> eventos) {
        
        EventoPrivado ev = (turno == null) ? null : eventos.stream()
                .filter(e -> e.getIdTurno().equals(turno.getId()))
                .filter(e -> !e.getEstado().name().equals("CANCELADA"))
                .findFirst().orElse(null);

        return ResumenDiaQuery.ResumenTurno.builder()
                .disponible(disponible)
                .totalReservas(0) // No aplica a turnos especificos segun modelo actual
                .eventoPrivado(ev != null ? toEventoQuery(ev, turno) : null)
                .build();
    }

    private ResumenDiaQuery.ResumenEventoQuery toEventoQuery(EventoPrivado e, Turno t) {
        Cliente c = clienteRepository.findById(e.getIdCliente()).orElse(null);
        return ResumenDiaQuery.ResumenEventoQuery.builder()
                .id(e.getId())
                .tipoEvento(e.getTipoEvento())
                .turno(t != null ? t.getCodigo() : "N/A")
                .horaInicio(t != null ? t.getHoraInicio().toString() : "")
                .horaFin(t != null ? t.getHoraFin().toString() : "")
                .nombreCliente(c != null ? c.nombreParaMostrar() : "Anonimo")
                .estado(e.getEstado().name())
                .aforoDeclarado(e.getAforoDeclarado())
                .build();
    }
}