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
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
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
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);

        var feriadoOpt = feriadoRepository.findByFecha(fecha);
        boolean esFeriado = feriadoOpt.isPresent();
        String descripcionFeriado = feriadoOpt.map(Feriado::getDescripcion).orElse(null);

        boolean bloqueado = bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha);
        String motivoBloqueo = null;
        if (bloqueado) {
            motivoBloqueo = bloqueRepository.findActivosBySede(idSede).stream()
                    .filter(b -> b.comprendeFecha(fecha))
                    .findFirst()
                    .map(BloqueCalendario::getMotivo)
                    .orElse(null);
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
        else if (bloqueado) tipoDia = "BLOQUEADO";
        else if (!diaOperacion) tipoDia = "NO_LABORABLE";
        else tipoDia = "LABORABLE";

        boolean disponiblePublico = diaOperacion && !esFeriado && !bloqueado
                && oc.getTipo() != TipoOcupacionDia.PRIVADO_PARCIAL
                && oc.getTipo() != TipoOcupacionDia.PRIVADO_LLENO
                && aforoActual < aforoMax;

        boolean disponiblePrivado = diaOperacion && !esFeriado && !bloqueado
                && oc.getTipo() != TipoOcupacionDia.PUBLICO;

        return DisponibilidadQuery.builder()
                .idSede(idSede)
                .fecha(fecha)
                .tipoDia(tipoDia)
                .esFeriado(esFeriado)
                .descripcionFeriado(descripcionFeriado)
                .accesoPublicoActivo(diaOperacion && !esFeriado && !bloqueado)
                .turnoT1Disponible(!oc.isTurnoT1Ocupado() && diaOperacion && !esFeriado && !bloqueado)
                .turnoT2Disponible(!oc.isTurnoT2Ocupado() && diaOperacion && !esFeriado && !bloqueado)
                .aforoPublicoActual(aforoActual)
                .aforoMaximo(aforoMax)
                .plazasDisponibles(plazas)
                .aforoCompleto(aforoActual >= aforoMax)
                .bloqueadoManualmente(bloqueado)
                .tipoBloqueo(bloqueado ? "MANUAL" : null)
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
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadQuery> consultarRango(Long idSede, LocalDate inicio, LocalDate fin) {
        List<DisponibilidadQuery> resultado = new ArrayList<>();
        LocalDate current = inicio;
        while (!current.isAfter(fin)) {
            resultado.add(consultarPorFecha(idSede, current));
            current = current.plusDays(1);
        }
        return resultado;
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
}
