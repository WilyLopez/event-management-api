package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.dto.command.BloquearFechasCommand;
import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;
import com.playzone.pems.application.calendario.port.in.BloquearFechasUseCase;
import com.playzone.pems.application.calendario.port.in.ConsultarDisponibilidadUseCase;
import com.playzone.pems.domain.calendario.exception.DisponibilidadNotFoundException;
import com.playzone.pems.domain.calendario.model.BloqueCalendario;
import com.playzone.pems.domain.calendario.model.DisponibilidadDiaria;
import com.playzone.pems.domain.calendario.model.Feriado;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.calendario.model.enums.TipoOcupacionDia;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.DisponibilidadDiariaRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarioService
        implements ConsultarDisponibilidadUseCase,
        BloquearFechasUseCase {

    private final DisponibilidadDiariaRepository disponibilidadRepository;
    private final BloqueCalendarioRepository     bloqueRepository;
    private final FeriadoRepository              feriadoRepository;
    private final ReservaPublicaRepository       reservaRepository;
    private final EventoPrivadoRepository        eventoRepository;

    @Value("${playzone.negocio.aforo-maximo:60}")
    private int aforoMaximo;

    @Value("${playzone.negocio.dias-max-reserva-publica:14}")
    private int diasMaxReservaPublica;

    @Value("${playzone.negocio.anticipacion-min-evento-dias:15}")
    private int anticipacionMinEventoDias;

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadQuery consultarPorFecha(Long idSede, LocalDate fecha) {
        DisponibilidadDiaria disp = disponibilidadRepository
                .findBySedeAndFecha(idSede, fecha)
                .orElseGet(() -> buildDefaultDisponibilidad(idSede, fecha));

        boolean bloqueado = bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha);

        Feriado feriado        = feriadoRepository.findByFecha(fecha).orElse(null);
        boolean esFeriado      = feriado != null;
        String  descFeriado    = esFeriado ? feriado.getDescripcion() : null;

        List<EventoPrivado> todosEventos = eventoRepository.findBySedeAndFecha(idSede, fecha);
        EventoPrivado eventoActivo = todosEventos.stream()
                .filter(e -> e.getEstado() == EstadoEventoPrivado.SOLICITADA
                          || e.getEstado() == EstadoEventoPrivado.CONFIRMADA)
                .findFirst().orElse(null);
        int totalEventos = (int) todosEventos.stream()
                .filter(e -> e.getEstado() != EstadoEventoPrivado.CANCELADA).count();

        int totalReservas = (int) reservaRepository.findBySedeAndFecha(idSede, fecha).stream()
                .filter(r -> !r.getEstado().name().equals("CANCELADA"))
                .count();

        return buildQuery(disp, resolverTipoDia(fecha, esFeriado).getCodigo(),
                bloqueado, esFeriado, descFeriado,
                totalReservas, totalEventos, eventoActivo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadQuery> consultarRango(Long idSede, LocalDate inicio, LocalDate fin) {
        List<DisponibilidadDiaria> existentes = disponibilidadRepository
                .findBySedeAndFechasBetween(idSede, inicio, fin);

        Map<LocalDate, DisponibilidadDiaria> dispMap = existentes.stream()
                .collect(Collectors.toMap(DisponibilidadDiaria::getFecha, d -> d));

        Map<LocalDate, String> feriadosMap = feriadoRepository.findByFechaBetween(inicio, fin).stream()
                .collect(Collectors.toMap(
                        Feriado::getFecha,
                        f -> f.getDescripcion() != null ? f.getDescripcion() : "Feriado",
                        (a, b) -> a));

        List<EventoPrivado> todosEventosRango = eventoRepository.findBySedeAndFechaBetween(idSede, inicio, fin);

        Map<LocalDate, Long> eventosMap = todosEventosRango.stream()
                .filter(e -> e.getEstado() != EstadoEventoPrivado.CANCELADA)
                .collect(Collectors.groupingBy(EventoPrivado::getFechaEvento, Collectors.counting()));

        Map<LocalDate, EventoPrivado> eventoActivoMap = todosEventosRango.stream()
                .filter(e -> e.getEstado() == EstadoEventoPrivado.SOLICITADA
                          || e.getEstado() == EstadoEventoPrivado.CONFIRMADA)
                .collect(Collectors.toMap(EventoPrivado::getFechaEvento, e -> e, (a, b) -> a));

        Map<LocalDate, Long> reservasMap = reservaRepository.findBySedeAndFechaBetween(idSede, inicio, fin).stream()
                .filter(r -> !r.getEstado().name().equals("CANCELADA"))
                .collect(Collectors.groupingBy(ReservaPublica::getFechaEvento, Collectors.counting()));

        List<DisponibilidadQuery> result = new ArrayList<>();
        LocalDate current = inicio;
        while (!current.isAfter(fin)) {
            DisponibilidadDiaria d = dispMap.getOrDefault(current, buildDefaultDisponibilidad(idSede, current));
            boolean bloqueado     = bloqueRepository.existsBloqueActivoEnFecha(idSede, current);
            boolean esFeriado     = feriadosMap.containsKey(current);
            String  descFeriado   = feriadosMap.get(current);
            int totalReservas     = reservasMap.getOrDefault(current, 0L).intValue();
            int totalEventos      = eventosMap.getOrDefault(current, 0L).intValue();
            EventoPrivado evActivo = eventoActivoMap.get(current);

            result.add(buildQuery(d, resolverTipoDia(current, esFeriado).getCodigo(),
                    bloqueado, esFeriado, descFeriado, totalReservas, totalEventos, evActivo));
            current = current.plusDays(1);
        }
        return result;
    }

    private DisponibilidadDiaria buildDefaultDisponibilidad(Long idSede, LocalDate fecha) {
        return DisponibilidadDiaria.builder()
                .idSede(idSede)
                .fecha(fecha)
                .accesoPublicoActivo(true)
                .turnoT1Disponible(true)
                .turnoT2Disponible(true)
                .aforoPublicoActual(0)
                .build();
    }

    @Override
    @Transactional
    public BloqueCalendario ejecutar(BloquearFechasCommand command) {
        if (command.getFechaFin().isBefore(command.getFechaInicio())) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (bloqueRepository.existsSolapamientoEnRango(
                command.getIdSede(), command.getFechaInicio(), command.getFechaFin())) {
            throw new ValidationException("El rango de fechas se solapa con un bloqueo existente.");
        }

        BloqueCalendario bloque = BloqueCalendario.builder()
                .idSede(command.getIdSede())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .motivo(command.getMotivo())
                .idUsuarioCreador(command.getIdUsuarioAdmin())
                .activo(true)
                .build();

        return bloqueRepository.save(bloque);
    }

    @Override
    @Transactional
    public void desactivar(Long idBloque) {
        bloqueRepository.desactivar(idBloque);
    }

    private TipoDia resolverTipoDia(LocalDate fecha, boolean esFeriado) {
        return (FechaUtil.esFindeSemana(fecha) || esFeriado)
                ? TipoDia.FIN_SEMANA_FERIADO
                : TipoDia.SEMANA;
    }

    private DisponibilidadQuery buildQuery(
            DisponibilidadDiaria d,
            String tipoDiaCodigo,
            boolean bloqueado,
            boolean esFeriado,
            String descripcionFeriado,
            int totalReservas,
            int totalEventos,
            EventoPrivado eventoActivo) {

        LocalDate fecha = d.getFecha();
        LocalDate hoy   = LocalDate.now();

        TipoOcupacionDia tipo;
        if (esFeriado)           tipo = TipoOcupacionDia.FERIADO;
        else if (bloqueado)      tipo = TipoOcupacionDia.BLOQUEADO;
        else if (eventoActivo != null) tipo = TipoOcupacionDia.PRIVADO;
        else if (totalReservas > 0)   tipo = TipoOcupacionDia.PUBLICO;
        else                          tipo = TipoOcupacionDia.LIBRE;

        LocalDate limite = hoy.plusDays(diasMaxReservaPublica);
        boolean dentroVentanaPublica = !fecha.isBefore(hoy) && !fecha.isAfter(limite);

        boolean disponiblePublico = (tipo == TipoOcupacionDia.LIBRE || tipo == TipoOcupacionDia.PUBLICO)
                && dentroVentanaPublica
                && d.admiteReservaPublica(aforoMaximo);

        long diasHastaFecha = ChronoUnit.DAYS.between(hoy, fecha);
        boolean disponiblePrivado = tipo == TipoOcupacionDia.LIBRE
                && diasHastaFecha >= anticipacionMinEventoDias;

        boolean turnoT1 = tipo != TipoOcupacionDia.PRIVADO && d.isTurnoT1Disponible();
        boolean turnoT2 = tipo != TipoOcupacionDia.PRIVADO && d.isTurnoT2Disponible();

        return DisponibilidadQuery.builder()
                .idSede(d.getIdSede())
                .fecha(fecha)
                .tipoDia(tipoDiaCodigo)
                .esFeriado(esFeriado)
                .descripcionFeriado(descripcionFeriado)
                .accesoPublicoActivo(d.isAccesoPublicoActivo())
                .turnoT1Disponible(turnoT1)
                .turnoT2Disponible(turnoT2)
                .aforoPublicoActual(d.getAforoPublicoActual())
                .aforoMaximo(aforoMaximo)
                .plazasDisponibles(d.plazasDisponibles(aforoMaximo))
                .aforoCompleto(!d.admiteReservaPublica(aforoMaximo))
                .bloqueadoManualmente(bloqueado)
                .totalReservas(totalReservas)
                .totalEventos(totalEventos)
                .tipoOcupacion(tipo.name())
                .disponiblePublico(disponiblePublico)
                .disponiblePrivado(disponiblePrivado)
                .tituloEvento(eventoActivo != null ? eventoActivo.getTipoEvento() : null)
                .idEvento(eventoActivo != null ? eventoActivo.getId() : null)
                .build();
    }
}
