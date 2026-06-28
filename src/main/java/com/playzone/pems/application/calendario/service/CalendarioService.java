package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.dto.command.BloquearFechasCommand;
import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;
import com.playzone.pems.application.calendario.port.in.BloquearFechasUseCase;
import com.playzone.pems.application.calendario.port.in.ConsultarDisponibilidadUseCase;
import com.playzone.pems.domain.calendario.exception.ConflictoActividadException;
import com.playzone.pems.domain.calendario.model.BloqueCalendario;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.model.Feriado;
import com.playzone.pems.domain.calendario.model.OcupacionDia;
import com.playzone.pems.domain.calendario.model.enums.TipoOcupacionDia;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.calendario.repository.ProgramacionSemanalRepository;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.evento.query.IngresosPorDia;
import com.playzone.pems.domain.evento.query.ReservasPorDia;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarioService
        implements ConsultarDisponibilidadUseCase,
        BloquearFechasUseCase {

    private final BloqueCalendarioRepository           bloqueRepository;
    private final FeriadoRepository                    feriadoRepository;
    private final ReservaPublicaRepository             reservaRepository;
    private final EventoPrivadoRepository              eventoRepository;
    private final ConfiguracionCalendarioRepository    configRepository;
    private final ProgramacionSemanalRepository        programacionRepository;

    public OcupacionDia ocupacionDia(Long idSede, LocalDate fecha) {
        if (feriadoRepository.existsByFecha(fecha))
            return OcupacionDia.feriado();
        if (bloqueRepository.existsBloqueEfectivoEnFecha(idSede, fecha))
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

        if (fecha.isBefore(hoy)) return false;
        if (!esDiaOperacion(cfg, fecha)) return false;

        if (fecha.isEqual(hoy)) {
            if (FechaUtil.ahora().toLocalTime().isAfter(cfg.getHoraCierre())) return false;
        }

        if (!programacionRepository.existeActivaEnFecha(idSede, fecha)) return false;

        // Un feriado NO bloquea la venta de entradas (solo cambia la tarifa).
        // Para cerrar un día se usa un bloqueo (BLOQUEADO), no el feriado.
        OcupacionDia oc = ocupacionDia(idSede, fecha);
        if (oc.getTipo() == TipoOcupacionDia.PRIVADO_PARCIAL
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
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);

        var feriadoOpt = feriadoRepository.findByFecha(fecha);
        boolean esFeriado = feriadoOpt.isPresent();
        String descripcionFeriado = feriadoOpt.map(Feriado::getDescripcion).orElse(null);

        boolean bloqueado = bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha);
        String motivoBloqueo = null;
        Long idBloqueo = null;
        String tBloqueo = null;
        if (bloqueado) {
            var b = bloqueRepository.findActivosBySede(idSede).stream()
                    .filter(x -> x.comprendeFecha(fecha))
                    .findFirst()
                    .orElse(null);
            if (b != null) {
                motivoBloqueo = b.getMotivo();
                idBloqueo = b.getId();
                tBloqueo = b.getTipoBloqueo();
            }
        }

        OcupacionDia oc = ocupacionDia(idSede, fecha);
        boolean diaOperacion = esDiaOperacion(cfg, fecha);

        int aforoActual = reservaRepository.countActivasBySedeAndFecha(idSede, fecha);
        int aforoMax = cfg.getAforoMaximo();
        int plazas = Math.max(0, aforoMax - aforoActual);

        List<EventoPrivado> eventos = eventoRepository.findActivosBySedeAndFecha(idSede, fecha);

        BigDecimal ingresoEstimado = reservaRepository.sumIngresosBySedeAndFecha(idSede, fecha);

        int pct = aforoMax > 0 ? Math.min(100, aforoActual * 100 / aforoMax) : 0;

        String tipoDia;
        if (esFeriado) tipoDia = "FERIADO";
        else if (bloqueado && (oc.getTipo() == TipoOcupacionDia.BLOQUEADO)) tipoDia = "BLOQUEADO";
        else if (!diaOperacion) tipoDia = "NO_LABORABLE";
        else tipoDia = "LABORABLE";

        boolean bloqueadoEfectivo = bloqueRepository.existsBloqueEfectivoEnFecha(idSede, fecha);
        boolean tieneProgramacionSemanal = programacionRepository.existeActivaEnFecha(idSede, fecha);

        LocalDate hoy = FechaUtil.hoy();
        long diasDesdeHoy = ChronoUnit.DAYS.between(hoy, fecha);
        boolean pasoCierreHoy = fecha.isEqual(hoy)
                && FechaUtil.ahora().toLocalTime().isAfter(cfg.getHoraCierre());

        // Un feriado NO bloquea la venta de entradas (solo cambia la tarifa).
        // Solo un bloqueo real (bloqueadoEfectivo) cierra el día al público.
        boolean disponiblePublico = diaOperacion && !bloqueadoEfectivo && tieneProgramacionSemanal
                && oc.getTipo() != TipoOcupacionDia.PRIVADO_PARCIAL
                && oc.getTipo() != TipoOcupacionDia.PRIVADO_LLENO
                && aforoActual < aforoMax
                && !pasoCierreHoy
                && diasDesdeHoy >= cfg.getDiasMinReservaPublica()
                && diasDesdeHoy <= cfg.getDiasMaxReservaPublica();

        boolean disponiblePrivado = diaOperacion && !esFeriado && !bloqueadoEfectivo
                && oc.getTipo() != TipoOcupacionDia.PUBLICO;

        return DisponibilidadQuery.builder()
                .idSede(idSede)
                .fecha(fecha)
                .tipoDia(tipoDia)
                .esFeriado(esFeriado)
                .descripcionFeriado(descripcionFeriado)
                .accesoPublicoActivo(diaOperacion && !bloqueado)
                .turnoT1Disponible(!oc.isTurnoT1Ocupado() && diaOperacion && !esFeriado && !bloqueado)
                .turnoT2Disponible(!oc.isTurnoT2Ocupado() && diaOperacion && !esFeriado && !bloqueado)
                .aforoPublicoActual(aforoActual)
                .aforoMaximo(aforoMax)
                .plazasDisponibles(plazas)
                .aforoCompleto(aforoActual >= aforoMax)
                .bloqueadoManualmente(bloqueado)
                .idBloqueo(idBloqueo)
                .tipoBloqueo(tBloqueo)
                .motivoBloqueo(motivoBloqueo)
                .totalReservas(aforoActual)
                .totalEventos(eventos.size())
                .ingresoEstimado(ingresoEstimado)
                .tieneNotas(false)
                .ocupacionPorcentaje(pct)
                .tipoOcupacion(oc.getTipo().name())
                .disponiblePublico(disponiblePublico)
                .disponiblePrivado(disponiblePrivado)
                .turnoT1Ocupado(oc.isTurnoT1Ocupado())
                .turnoT2Ocupado(oc.isTurnoT2Ocupado())
                .tituloEventoT1(oc.getTituloEventoT1())
                .idEventoT1(oc.getIdEventoT1())
                .tituloEventoT2(oc.getTituloEventoT2())
                .idEventoT2(oc.getIdEventoT2())
                .tituloEvento(eventos.size() == 1 ? eventos.get(0).getTipoEvento() : null)
                .idEvento(eventos.size() == 1 ? eventos.get(0).getId() : null)
                .tieneProgramacionSemanal(tieneProgramacionSemanal)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadQuery> consultarRango(Long idSede, LocalDate inicio, LocalDate fin) {
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);

        Map<LocalDate, Feriado> feriados = feriadoRepository.findByFechaBetween(inicio, fin).stream()
                .collect(Collectors.toMap(Feriado::getFecha, f -> f));

        List<BloqueCalendario> bloques = bloqueRepository.findActivosBySedeAndRango(idSede, inicio, fin);

        Map<LocalDate, List<EventoPrivado>> eventosMap = eventoRepository.findActivosBySedeAndFechaBetween(idSede, inicio, fin).stream()
                .collect(Collectors.groupingBy(EventoPrivado::getFechaEvento));

        Map<LocalDate, Long> reservasMap = reservaRepository.countAgrupadoPorDia(idSede, inicio, fin).stream()
                .collect(Collectors.toMap(ReservasPorDia::fecha, ReservasPorDia::cantidad));

        Map<LocalDate, BigDecimal> ingresosMap = reservaRepository.sumIngresosAgrupadoPorDia(idSede, inicio, fin).stream()
                .collect(Collectors.toMap(IngresosPorDia::fecha, IngresosPorDia::monto));

        var programaciones = programacionRepository.findActivasBySedeAndRango(idSede, inicio, fin);

        LocalDate hoy = FechaUtil.hoy();
        boolean yaCerroHoy = FechaUtil.ahora().toLocalTime().isAfter(cfg.getHoraCierre());

        List<DisponibilidadQuery> resultado = new ArrayList<>();
        LocalDate current = inicio;
        while (!current.isAfter(fin)) {
            final LocalDate fechaRef = current;
            Feriado feriado = feriados.get(fechaRef);
            boolean esFeriado = feriado != null;
            String descFeriado = esFeriado ? feriado.getDescripcion() : null;

            BloqueCalendario bloque = bloques.stream()
                    .filter(b -> b.comprendeFecha(fechaRef))
                    .findFirst().orElse(null);
            boolean bloqueado = bloque != null;
            String motivoBloqueo = bloqueado ? bloque.getMotivo() : null;

            List<EventoPrivado> eventosDia = eventosMap.getOrDefault(fechaRef, List.of());
            boolean t1Ocupado = eventosDia.stream().anyMatch(e -> "T1".equals(obtenerCodigoTurno(e)));
            boolean t2Ocupado = eventosDia.stream().anyMatch(e -> "T2".equals(obtenerCodigoTurno(e)));

            OcupacionDia oc;
            if (esFeriado) oc = OcupacionDia.feriado();
            else if (bloqueado) oc = OcupacionDia.bloqueado();
            else if (t1Ocupado || t2Ocupado) {
                TipoOcupacionDia tipo = (t1Ocupado && t2Ocupado) ? TipoOcupacionDia.PRIVADO_LLENO : TipoOcupacionDia.PRIVADO_PARCIAL;
                EventoPrivado evT1 = eventosDia.stream().filter(e -> "T1".equals(obtenerCodigoTurno(e))).findFirst().orElse(null);
                EventoPrivado evT2 = eventosDia.stream().filter(e -> "T2".equals(obtenerCodigoTurno(e))).findFirst().orElse(null);
                oc = OcupacionDia.privado(tipo, t1Ocupado, t2Ocupado,
                        evT1 != null ? evT1.getId() : null, evT1 != null ? evT1.getTipoEvento() : null,
                        evT2 != null ? evT2.getId() : null, evT2 != null ? evT2.getTipoEvento() : null);
            } else {
                long resCount = reservasMap.getOrDefault(fechaRef, 0L);
                oc = resCount > 0 ? OcupacionDia.publico((int) resCount) : OcupacionDia.libre();
            }

            boolean diaOperacion = esDiaOperacion(cfg, fechaRef);
            int aforoActual = (int) (long) reservasMap.getOrDefault(fechaRef, 0L);
            int aforoMax = cfg.getAforoMaximo();
            int plazas = Math.max(0, aforoMax - aforoActual);

            BigDecimal ingresosReservas = ingresosMap.getOrDefault(fechaRef, BigDecimal.ZERO);
            BigDecimal ingresosEventos  = eventosDia.stream()
                    .filter(e -> e.getMontoAdelanto() != null)
                    .map(EventoPrivado::getMontoAdelanto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ingresoEstimado  = ingresosReservas.add(ingresosEventos);

            int pct = aforoMax > 0 ? Math.min(100, aforoActual * 100 / aforoMax) : 0;

            String tipoDia;
            if (esFeriado) tipoDia = "FERIADO";
            else if (bloqueado && (oc.getTipo() == TipoOcupacionDia.BLOQUEADO)) tipoDia = "BLOQUEADO";
            else if (!diaOperacion) tipoDia = "NO_LABORABLE";
            else tipoDia = "LABORABLE";

            boolean bloqueadoEfectivo = bloqueado;
            boolean tieneProgramacion = programaciones.stream().anyMatch(p -> p.comprendeFecha(fechaRef));

            long diasDesdeHoy = ChronoUnit.DAYS.between(hoy, fechaRef);
            boolean pasoCierreHoy = fechaRef.isEqual(hoy) && yaCerroHoy;

            // Un feriado NO bloquea la venta de entradas (solo cambia la tarifa).
            boolean dispPublico = diaOperacion && !bloqueadoEfectivo && tieneProgramacion
                    && oc.getTipo() != TipoOcupacionDia.PRIVADO_PARCIAL
                    && oc.getTipo() != TipoOcupacionDia.PRIVADO_LLENO
                    && aforoActual < aforoMax
                    && !pasoCierreHoy
                    && diasDesdeHoy >= cfg.getDiasMinReservaPublica()
                    && diasDesdeHoy <= cfg.getDiasMaxReservaPublica();

            boolean dispPrivado = diaOperacion && !esFeriado && !bloqueadoEfectivo
                    && oc.getTipo() != TipoOcupacionDia.PUBLICO;

            resultado.add(DisponibilidadQuery.builder()
                    .idSede(idSede).fecha(fechaRef).tipoDia(tipoDia).esFeriado(esFeriado).descripcionFeriado(descFeriado)
                    .accesoPublicoActivo(diaOperacion && !bloqueadoEfectivo)
                    .turnoT1Disponible(!oc.isTurnoT1Ocupado() && diaOperacion && !esFeriado && !bloqueadoEfectivo)
                    .turnoT2Disponible(!oc.isTurnoT2Ocupado() && diaOperacion && !esFeriado && !bloqueadoEfectivo)
                    .aforoPublicoActual(aforoActual).aforoMaximo(aforoMax).plazasDisponibles(plazas).aforoCompleto(aforoActual >= aforoMax)
                    .bloqueadoManualmente(bloqueado).idBloqueo(bloqueado ? bloque.getId() : null)
                    .tipoBloqueo(bloqueado ? bloque.getTipoBloqueo() : null).motivoBloqueo(motivoBloqueo)
                    .totalReservas(aforoActual).totalEventos(eventosDia.size()).ingresoEstimado(ingresoEstimado)
                    .tieneNotas(false).ocupacionPorcentaje(pct).tipoOcupacion(oc.getTipo().name())
                    .disponiblePublico(dispPublico).disponiblePrivado(dispPrivado)
                    .turnoT1Ocupado(oc.isTurnoT1Ocupado()).turnoT2Ocupado(oc.isTurnoT2Ocupado())
                    .tituloEventoT1(oc.getTituloEventoT1()).idEventoT1(oc.getIdEventoT1())
                    .tituloEventoT2(oc.getTituloEventoT2()).idEventoT2(oc.getIdEventoT2())
                    .tituloEvento(eventosDia.size() == 1 ? eventosDia.get(0).getTipoEvento() : null)
                    .idEvento(eventosDia.size() == 1 ? eventosDia.get(0).getId() : null)
                    .tieneProgramacionSemanal(tieneProgramacion)
                    .build());

            current = current.plusDays(1);
        }
        return resultado;
    }

    @Override
    @Transactional
    public BloqueCalendario ejecutar(BloquearFechasCommand command) {
        LocalDate hoy = FechaUtil.hoy();
        ConfiguracionCalendario cfg = configRepository.obtener(command.getIdSede());

        if ("PLANIFICACION_SEMANAL".equals(command.getTipoBloqueo())) {
            throw new ValidationException("tipoBloqueo",
                    "Use el endpoint de programación semanal para habilitar reservas públicas.");
        }
        if (command.getFechaInicio().isBefore(hoy)) {
            throw new ValidationException("No se pueden bloquear fechas pasadas.");
        }
        if (command.getFechaFin().isBefore(command.getFechaInicio())) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        long diasRango = ChronoUnit.DAYS.between(command.getFechaInicio(), command.getFechaFin()) + 1;
        if (diasRango > cfg.getRangoMaxBloqueo()) {
            throw new ValidationException("fechaFin",
                    "El rango de bloqueo no puede exceder " + cfg.getRangoMaxBloqueo() + " dias.");
        }
        if (command.getMotivo() == null || command.getMotivo().isBlank()) {
            throw new ValidationException("El motivo del bloqueo es obligatorio.");
        }
        if (bloqueRepository.existsSolapamientoEnRango(
                command.getIdSede(), command.getFechaInicio(), command.getFechaFin())) {
            throw new ValidationException("El rango de fechas se solapa con un bloqueo existente.");
        }
        if (existeActividadEnRango(command.getIdSede(), command.getFechaInicio(), command.getFechaFin())) {
            throw new ConflictoActividadException(
                    "No se puede bloquear un rango con reservas o eventos activos.");
        }

        BloqueCalendario bloque = BloqueCalendario.builder()
                .idSede(command.getIdSede())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .tipoBloqueo(command.getTipoBloqueo())
                .motivo(command.getMotivo())
                .idUsuarioCreador(command.getIdUsuarioAdmin())
                .activo(true)
                .build();

        return bloqueRepository.save(bloque);
    }

    private boolean existeActividadEnRango(Long idSede, LocalDate inicio, LocalDate fin) {
        if (reservaRepository.countConfirmadasBySedeAndRango(idSede, inicio, fin) > 0) return true;
        return !eventoRepository.findBySedeAndFechaBetween(idSede, inicio, fin).isEmpty();
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
}
