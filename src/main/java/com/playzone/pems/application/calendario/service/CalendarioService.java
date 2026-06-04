package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.dto.command.BloquearFechasCommand;
import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;
import com.playzone.pems.application.calendario.port.in.BloquearFechasUseCase;
import com.playzone.pems.application.calendario.port.in.ConsultarDisponibilidadUseCase;
import com.playzone.pems.domain.calendario.exception.ConflictoActividadException;
import com.playzone.pems.domain.calendario.exception.DisponibilidadNotFoundException;
import com.playzone.pems.domain.calendario.model.BloqueCalendario;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.model.DisponibilidadDiaria;
import com.playzone.pems.domain.calendario.model.Feriado;
import com.playzone.pems.domain.calendario.model.OcupacionDia;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.calendario.model.enums.TipoOcupacionDia;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.DisponibilidadDiariaRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarioService
        implements ConsultarDisponibilidadUseCase,
        BloquearFechasUseCase {

    private final DisponibilidadDiariaRepository       disponibilidadRepository;
    private final BloqueCalendarioRepository           bloqueRepository;
    private final FeriadoRepository                    feriadoRepository;
    private final ReservaPublicaRepository             reservaRepository;
    private final EventoPrivadoRepository              eventoRepository;
    private final ConfiguracionCalendarioRepository    configRepository;

    public OcupacionDia ocupacionDia(Long idSede, LocalDate fecha) {
        if (feriadoRepository.existsByFecha(fecha))
            return OcupacionDia.feriado();
        if (bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha))
            return OcupacionDia.bloqueado();

        List<EventoPrivado> eventosActivos = eventoRepository.findActivosBySedeAndFecha(idSede, fecha);
        boolean t1Ocupado = eventosActivos.stream().anyMatch(e -> "T1".equals(obtenerCodigoTurno(e)));
        boolean t2Ocupado = eventosActivos.stream().anyMatch(e -> "T2".equals(obtenerCodigoTurno(e)));

        if (t1Ocupado || t2Ocupado) {
            TipoOcupacionDia tipo = (t1Ocupado && t2Ocupado)
                    ? TipoOcupacionDia.PRIVADO_LLENO
                    : TipoOcupacionDia.PRIVADO_PARCIAL;

            EventoPrivado evT1 = eventosActivos.stream()
                    .filter(e -> "T1".equals(obtenerCodigoTurno(e))).findFirst().orElse(null);
            EventoPrivado evT2 = eventosActivos.stream()
                    .filter(e -> "T2".equals(obtenerCodigoTurno(e))).findFirst().orElse(null);

            return OcupacionDia.privado(tipo, t1Ocupado, t2Ocupado,
                    evT1 != null ? evT1.getId() : null,
                    evT1 != null ? evT1.getTipoEvento() : null,
                    evT2 != null ? evT2.getId() : null,
                    evT2 != null ? evT2.getTipoEvento() : null);
        }

        int reservas = reservaRepository.countActivasBySedeAndFecha(idSede, fecha);
        if (reservas > 0)
            return OcupacionDia.publico(reservas);

        return OcupacionDia.libre();
    }

    public boolean disponibleParaReservaPublica(Long idSede, LocalDate fecha) {
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);
        LocalDate hoy = FechaUtil.hoy();
        LocalDate min = hoy.plusDays(cfg.getDiasMinReservaPublica());
        LocalDate max = hoy.plusDays(cfg.getDiasMaxReservaPublica());

        if (fecha.isBefore(min) || fecha.isAfter(max)) return false;
        if (!esDiaOperacion(cfg, fecha)) return false;

        if (fecha.isEqual(hoy)) {
            if (FechaUtil.ahora().toLocalTime().isAfter(cfg.getHoraCierre())) return false;
        }

        OcupacionDia oc = ocupacionDia(idSede, fecha);
        if (oc.getTipo() == TipoOcupacionDia.BLOQUEADO
                || oc.getTipo() == TipoOcupacionDia.FERIADO
                || oc.getTipo() == TipoOcupacionDia.PRIVADO_PARCIAL
                || oc.getTipo() == TipoOcupacionDia.PRIVADO_LLENO) return false;

        int reservas = reservaRepository.countActivasBySedeAndFecha(idSede, fecha);
        return reservas < cfg.getAforoMaximo();
    }

    public boolean disponibleParaEventoPrivado(Long idSede, LocalDate fecha, String codigoTurno) {
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);
        long dias = FechaUtil.diasEntre(FechaUtil.hoy(), fecha);

        if (dias < cfg.getDiasMinEventoPrivado()) return false;
        if (dias > cfg.getDiasMaxEventoPrivado()) return false;
        if (!esDiaOperacion(cfg, fecha)) return false;

        OcupacionDia oc = ocupacionDia(idSede, fecha);
        if (oc.getTipo() == TipoOcupacionDia.PUBLICO
                || oc.getTipo() == TipoOcupacionDia.BLOQUEADO
                || oc.getTipo() == TipoOcupacionDia.FERIADO) return false;

        if ("T1".equals(codigoTurno) && oc.isTurnoT1Ocupado()) return false;
        if ("T2".equals(codigoTurno) && oc.isTurnoT2Ocupado()) return false;

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadQuery consultarPorFecha(Long idSede, LocalDate fecha) {
        DisponibilidadDiaria disp = disponibilidadRepository
                .findBySedeAndFecha(idSede, fecha)
                .orElseGet(() -> buildDefaultDisponibilidad(idSede, fecha));

        ConfiguracionCalendario cfg = configRepository.obtener(idSede);
        OcupacionDia oc = ocupacionDia(idSede, fecha);

        Feriado feriado     = feriadoRepository.findByFecha(fecha).orElse(null);
        boolean esFeriado   = feriado != null;
        String  descFeriado = esFeriado ? feriado.getDescripcion() : null;

        return buildQuery(disp, cfg, oc, esFeriado, descFeriado, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadQuery> consultarRango(Long idSede, LocalDate inicio, LocalDate fin) {
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);

        List<DisponibilidadDiaria> existentes = disponibilidadRepository
                .findBySedeAndFechasBetween(idSede, inicio, fin);
        Map<LocalDate, DisponibilidadDiaria> dispMap = existentes.stream()
                .collect(Collectors.toMap(DisponibilidadDiaria::getFecha, d -> d));

        Map<LocalDate, String> feriadosMap = feriadoRepository.findByFechaBetween(inicio, fin).stream()
                .collect(Collectors.toMap(
                        Feriado::getFecha,
                        f -> f.getDescripcion() != null ? f.getDescripcion() : "Feriado",
                        (a, b) -> a));

        List<DisponibilidadQuery> result = new ArrayList<>();
        LocalDate current = inicio;
        while (!current.isAfter(fin)) {
            DisponibilidadDiaria d = dispMap.getOrDefault(current, buildDefaultDisponibilidad(idSede, current));
            OcupacionDia oc       = ocupacionDia(idSede, current);
            boolean esFeriado     = feriadosMap.containsKey(current);
            String  descFeriado   = feriadosMap.get(current);

            result.add(buildQuery(d, cfg, oc, esFeriado, descFeriado, current));
            current = current.plusDays(1);
        }
        return result;
    }

    @Override
    @Transactional
    public BloqueCalendario ejecutar(BloquearFechasCommand command) {
        LocalDate hoy = FechaUtil.hoy();
        if (command.getFechaInicio().isBefore(hoy)) {
            throw new ValidationException("No se pueden bloquear fechas pasadas.");
        }
        if (command.getFechaFin().isBefore(command.getFechaInicio())) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (command.getMotivo() == null || command.getMotivo().isBlank()) {
            throw new ValidationException("El motivo del bloqueo es obligatorio.");
        }
        if (bloqueRepository.existsSolapamientoEnRango(
                command.getIdSede(), command.getFechaInicio(), command.getFechaFin())) {
            throw new ValidationException("El rango de fechas se solapa con un bloqueo existente.");
        }

        boolean hayActividad = existeActividadEnRango(
                command.getIdSede(), command.getFechaInicio(), command.getFechaFin());
        if (hayActividad && !command.isConfirmado()) {
            throw new ConflictoActividadException(
                    "Hay reservas o eventos en este rango. Confirma para bloquear de todos modos.");
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

    private boolean existeActividadEnRango(Long idSede, LocalDate inicio, LocalDate fin) {
        LocalDate current = inicio;
        while (!current.isAfter(fin)) {
            if (reservaRepository.existsActivaBySedeAndFecha(idSede, current)) return true;
            if (eventoRepository.existsActivoBySedeAndFecha(idSede, current)) return true;
            current = current.plusDays(1);
        }
        return false;
    }

    @Override
    @Transactional
    public void desactivar(Long idBloque) {
        bloqueRepository.desactivar(idBloque);
    }

    private boolean esDiaOperacion(ConfiguracionCalendario cfg, LocalDate fecha) {
        DayOfWeek dow = fecha.getDayOfWeek();
        int isoValue  = dow.getValue();
        return Arrays.stream(cfg.getDiasOperacion().split(","))
                .map(String::trim)
                .anyMatch(s -> s.equals(String.valueOf(isoValue)));
    }

    private String obtenerCodigoTurno(EventoPrivado e) {
        if (e.getCodigoTurno() != null) return e.getCodigoTurno();
        return null;
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

    private TipoDia resolverTipoDia(LocalDate fecha, boolean esFeriado) {
        return (FechaUtil.esFindeSemana(fecha) || esFeriado)
                ? TipoDia.FIN_SEMANA_FERIADO
                : TipoDia.SEMANA;
    }

    private DisponibilidadQuery buildQuery(
            DisponibilidadDiaria d,
            ConfiguracionCalendario cfg,
            OcupacionDia oc,
            boolean esFeriado,
            String descripcionFeriado,
            LocalDate fecha) {

        LocalDate hoy = FechaUtil.hoy();

        LocalDate limite = hoy.plusDays(cfg.getDiasMaxReservaPublica());
        boolean disponiblePublico = disponibleParaReservaPublica(d.getIdSede(), fecha);
        boolean turnoT1Disponible = disponibleParaEventoPrivado(d.getIdSede(), fecha, "T1");
        boolean turnoT2Disponible = disponibleParaEventoPrivado(d.getIdSede(), fecha, "T2");

        int totalReservas = oc.getCantidadReservas();
        int totalEventos  = (oc.getTipo() == TipoOcupacionDia.PRIVADO_PARCIAL ? 1 :
                             oc.getTipo() == TipoOcupacionDia.PRIVADO_LLENO   ? 2 : 0);

        int pct = cfg.getAforoMaximo() > 0
                ? Math.min(100, (int) Math.round(totalReservas * 100.0 / cfg.getAforoMaximo()))
                : 0;

        return DisponibilidadQuery.builder()
                .idSede(d.getIdSede())
                .fecha(fecha)
                .tipoDia(resolverTipoDia(fecha, esFeriado).getCodigo())
                .esFeriado(esFeriado)
                .descripcionFeriado(descripcionFeriado)
                .accesoPublicoActivo(d.isAccesoPublicoActivo())
                .turnoT1Disponible(turnoT1Disponible)
                .turnoT2Disponible(turnoT2Disponible)
                .aforoPublicoActual(totalReservas)
                .aforoMaximo(cfg.getAforoMaximo())
                .plazasDisponibles(Math.max(0, cfg.getAforoMaximo() - totalReservas))
                .aforoCompleto(totalReservas >= cfg.getAforoMaximo())
                .bloqueadoManualmente(oc.getTipo() == TipoOcupacionDia.BLOQUEADO)
                .totalReservas(totalReservas)
                .totalEventos(totalEventos)
                .ocupacionPorcentaje(pct)
                .tipoOcupacion(oc.getTipo().name())
                .disponiblePublico(disponiblePublico)
                .disponiblePrivado(turnoT1Disponible || turnoT2Disponible)
                .turnoT1Ocupado(oc.isTurnoT1Ocupado())
                .turnoT2Ocupado(oc.isTurnoT2Ocupado())
                .tituloEventoT1(oc.getTituloEventoT1())
                .idEventoT1(oc.getIdEventoT1())
                .tituloEventoT2(oc.getTituloEventoT2())
                .idEventoT2(oc.getIdEventoT2())
                .build();
    }
}
