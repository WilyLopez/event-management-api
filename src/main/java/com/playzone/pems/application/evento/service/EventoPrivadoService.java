package com.playzone.pems.application.evento.service;

import com.playzone.pems.application.evento.dto.command.SolicitarEventoPrivadoCommand;
import com.playzone.pems.application.evento.dto.query.EventoExtraQuery;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import com.playzone.pems.application.evento.port.in.BuscarEventosAdminUseCase;
import com.playzone.pems.application.evento.port.in.CancelarEventoPrivadoUseCase;
import com.playzone.pems.application.evento.port.in.ConfirmarEventoPrivadoUseCase;
import com.playzone.pems.application.evento.port.in.ConsultarEventosPrivadosUseCase;
import com.playzone.pems.application.evento.port.in.SolicitarEventoPrivadoUseCase;
import com.playzone.pems.application.evento.port.out.EnviarNotificacionEventoPort;
import com.playzone.pems.application.finanzas.port.in.RegistrarIngresoUseCase;
import com.playzone.pems.domain.calendario.exception.FechaNoDisponibleException;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.TurnoRepository;
import com.playzone.pems.domain.comercial.repository.ExtraPaqueteRepository;
import com.playzone.pems.domain.comercial.repository.ServicioCotizacionRepository;
import com.playzone.pems.domain.evento.model.EventoExtra;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.domain.evento.repository.EventoExtraRepository;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.domain.calendario.model.Turno;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoPrivadoService
        implements SolicitarEventoPrivadoUseCase,
        ConfirmarEventoPrivadoUseCase,
        CancelarEventoPrivadoUseCase,
        ConsultarEventosPrivadosUseCase,
        BuscarEventosAdminUseCase {

    private final EventoPrivadoRepository    eventoRepository;
    private final ReservaPublicaRepository   reservaRepository;
    private final ClienteRepository          clienteRepository;
    private final BloqueCalendarioRepository bloqueRepository;
    private final TurnoRepository            turnoRepository;
    private final EnviarNotificacionEventoPort notificacionPort;
    private final RegistrarIngresoUseCase    registrarIngresoUseCase;
    private final EventoExtraRepository          eventoExtraRepository;
    private final ExtraPaqueteRepository         extraPaqueteRepository;
    private final ServicioCotizacionRepository   servicioCotizacionRepository;

    @Value("${playzone.negocio.anticipacion-min-evento-dias:15}")
    private int anticipacionMinDias;

    @Override
    @Transactional(readOnly = true)
    public Page<EventoPrivadoQuery> buscar(
            Long idSede, String estado, LocalDate fecha, String search, Pageable pageable) {

        EstadoEventoPrivado estadoEnum = null;
        if (estado != null && !estado.isBlank()) {
            try { estadoEnum = EstadoEventoPrivado.valueOf(estado); }
            catch (IllegalArgumentException ignored) {}
        }
        String searchPattern = (search != null && !search.isBlank())
                ? "%" + search.toLowerCase() + "%" : null;

        return eventoRepository.buscarAdmin(idSede, estadoEnum, fecha, searchPattern, pageable)
                .map(e -> toQuery(e, obtenerCliente(e.getIdCliente()), obtenerTurno(e.getIdTurno()), false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoPrivadoQuery> consultarPorCliente(Long idCliente, Pageable pageable) {
        return eventoRepository.findByCliente(idCliente, pageable)
                .map(e -> toQuery(e, obtenerCliente(e.getIdCliente()), obtenerTurno(e.getIdTurno()), false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoPrivadoQuery> consultarPorSedeYEstado(Long idSede, String estado, Pageable pageable) {
        return eventoRepository.findBySedeAndEstado(idSede, EstadoEventoPrivado.valueOf(estado), pageable)
                .map(e -> toQuery(e, obtenerCliente(e.getIdCliente()), obtenerTurno(e.getIdTurno()), false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoPrivadoQuery> consultarPorSedeYRangoFechas(Long idSede, LocalDate inicio, LocalDate fin, Pageable pageable) {
        return eventoRepository.findBySedeAndFechasBetween(idSede, inicio, fin, pageable)
                .map(e -> toQuery(e, obtenerCliente(e.getIdCliente()), obtenerTurno(e.getIdTurno()), false));
    }

    @Override
    @Transactional(readOnly = true)
    public EventoPrivadoQuery consultarPorId(Long idEvento) {
        EventoPrivado e = obtenerEvento(idEvento);
        return toQuery(e, obtenerCliente(e.getIdCliente()), obtenerTurno(e.getIdTurno()), true);
    }

    @Override
    @Transactional
    public EventoPrivadoQuery ejecutar(SolicitarEventoPrivadoCommand command) {
        validarFechaEvento(command.getIdSede(), command.getFechaEvento());

        if (eventoRepository.existsActivoBySedeAndFechaAndTurno(
                command.getIdSede(), command.getFechaEvento(), command.getIdTurno())) {
            throw new ValidationException("Ya existe un evento activo para esa fecha y turno.");
        }

        EventoPrivado evento = EventoPrivado.builder()
                .idCliente(command.getIdCliente())
                .idSede(command.getIdSede())
                .estado(EstadoEventoPrivado.SOLICITADA)
                .idTurno(command.getIdTurno())
                .fechaEvento(command.getFechaEvento())
                .tipoEvento(command.getTipoEvento())
                .contactoAdicional(command.getContactoAdicional())
                .aforoDeclarado(command.getAforoDeclarado())
                .montoAdelanto(BigDecimal.ZERO)
                .nombreNino(command.getNombreNino())
                .edadCumple(command.getEdadCumple())
                .observaciones(command.getObservaciones())
                .idPaquete(command.getIdPaquete())
                .descripcionPersonalizada(command.getDescripcionPersonalizada())
                .presupuestoEstimado(command.getPresupuestoEstimado())
                .esCotizacionPersonalizada(command.isEsCotizacionPersonalizada())
                .build();

        EventoPrivado guardado = eventoRepository.save(evento);
        persistirExtras(guardado.getId(), command.getIdsExtras(), command.getExtrasLibres());
        persistirServiciosCotizacion(guardado.getId(), command.getIdsServiciosCotizacion());

        Cliente cliente = obtenerCliente(guardado.getIdCliente());
        Turno   turno   = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno, true);

        notificacionPort.notificarSolicitudRecibida(cliente.getCorreo(), query);
        notificacionPort.notificarAdminNuevaSolicitud(query);
        return query;
    }

    @Override
    @Transactional
    public EventoPrivadoQuery ejecutar(Long idEvento, BigDecimal precioTotal,
                                       BigDecimal montoAdelanto, String medioPagoAdelanto,
                                       Long idUsuarioGestor) {
        EventoPrivado evento = obtenerEvento(idEvento);

        if (evento.getEstado() != EstadoEventoPrivado.SOLICITADA) {
            throw new ValidationException("Solo se pueden confirmar eventos en estado SOLICITADA.");
        }

        EventoPrivado confirmado = evento.toBuilder()
                .estado(EstadoEventoPrivado.CONFIRMADA)
                .precioTotalContrato(precioTotal)
                .montoAdelanto(montoAdelanto != null ? montoAdelanto : BigDecimal.ZERO)
                .medioPagoAdelanto(medioPagoAdelanto)
                .idUsuarioGestor(idUsuarioGestor)
                .build();

        EventoPrivado guardado = eventoRepository.save(confirmado);

        if (montoAdelanto != null && montoAdelanto.compareTo(BigDecimal.ZERO) > 0) {
            registrarIngresoUseCase.registrarAutomatico(
                    CategoriaIngreso.ADELANTO_EVENTO,
                    guardado.getIdSede(),
                    null,
                    guardado.getId(),
                    montoAdelanto,
                    guardado.getFechaEvento(),
                    medioPagoAdelanto);
        }

        Cliente cliente = obtenerCliente(guardado.getIdCliente());
        Turno   turno   = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno, true);
        notificacionPort.notificarEventoConfirmado(cliente.getCorreo(), query);
        return query;
    }

    @Transactional
    public EventoPrivadoQuery completar(Long idEvento, Long idUsuarioGestor) {
        EventoPrivado evento = obtenerEvento(idEvento);
        if (evento.getEstado() != EstadoEventoPrivado.CONFIRMADA) {
            throw new ValidationException("Solo se pueden completar eventos en estado CONFIRMADA.");
        }
        EventoPrivado completado = evento.toBuilder()
                .estado(EstadoEventoPrivado.COMPLETADA)
                .idUsuarioGestor(idUsuarioGestor)
                .build();
        EventoPrivado guardado = eventoRepository.save(completado);
        return toQuery(guardado, obtenerCliente(guardado.getIdCliente()), obtenerTurno(guardado.getIdTurno()), false);
    }

    @Transactional
    public EventoPrivadoQuery registrarSaldo(Long idEvento, BigDecimal monto, String medioPago, Long idUsuarioGestor) {
        EventoPrivado evento = obtenerEvento(idEvento);

        registrarIngresoUseCase.registrarAutomatico(
                CategoriaIngreso.SALDO_EVENTO,
                evento.getIdSede(),
                null,
                evento.getId(),
                monto,
                LocalDate.now(),
                medioPago);

        BigDecimal nuevoAdelanto = (evento.getMontoAdelanto() != null ? evento.getMontoAdelanto() : BigDecimal.ZERO)
                .add(monto);
        EventoPrivado actualizado = evento.toBuilder().montoAdelanto(nuevoAdelanto).build();
        EventoPrivado guardado = eventoRepository.save(actualizado);
        return toQuery(guardado, obtenerCliente(guardado.getIdCliente()), obtenerTurno(guardado.getIdTurno()), false);
    }

    @Override
    @Transactional
    public EventoPrivadoQuery ejecutar(Long idEvento, String motivoCancelacion) {
        EventoPrivado evento = obtenerEvento(idEvento);

        if (!evento.puedeCancelarse()) {
            throw new ValidationException("El evento no puede cancelarse en su estado actual.");
        }
        if (motivoCancelacion == null || motivoCancelacion.isBlank()) {
            throw new ValidationException("motivoCancelacion", "El motivo de cancelacion es obligatorio.");
        }

        EventoPrivado cancelado = evento.toBuilder()
                .estado(EstadoEventoPrivado.CANCELADA)
                .motivoCancelacion(motivoCancelacion)
                .build();

        EventoPrivado guardado = eventoRepository.save(cancelado);
        Cliente cliente = obtenerCliente(guardado.getIdCliente());
        Turno   turno   = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno, false);
        notificacionPort.notificarEventoCancelado(cliente.getCorreo(), query, motivoCancelacion);
        return query;
    }

    private void validarFechaEvento(Long idSede, LocalDate fecha) {
        long diasRestantes = FechaUtil.diferenciaEnDias(FechaUtil.hoyPeru(), fecha);
        if (diasRestantes < anticipacionMinDias) {
            throw new FechaNoDisponibleException(fecha,
                    "Los eventos privados deben reservarse con un minimo de " + anticipacionMinDias
                    + " dias de anticipacion. Por favor selecciona una fecha posterior.");
        }
        if (bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha)) {
            throw new FechaNoDisponibleException(fecha, "La fecha esta bloqueada por el administrador.");
        }
        if (reservaRepository.existsActivaBySedeAndFecha(idSede, fecha)) {
            throw new ValidationException(
                    "Esta fecha ya tiene reservas publicas y no puede usarse para un evento privado.");
        }
        if (eventoRepository.existsActivoBySedeAndFecha(idSede, fecha)) {
            throw new ValidationException(
                    "Esta fecha ya tiene un evento privado registrado. Elige otra fecha.");
        }
    }

    private void persistirServiciosCotizacion(Long idEvento, List<Long> idsServicios) {
        if (idsServicios == null || idsServicios.isEmpty()) return;
        List<EventoExtra> extras = servicioCotizacionRepository.findAllActivos().stream()
                .filter(s -> idsServicios.contains(s.getId()))
                .map(s -> EventoExtra.builder()
                        .idEventoPrivado(idEvento)
                        .nombreLibre("Servicio: " + s.getNombre())
                        .build())
                .toList();
        if (!extras.isEmpty()) {
            eventoExtraRepository.saveAll(extras);
        }
    }

    private void persistirExtras(Long idEvento, List<Long> idsExtras, List<String> extrasLibres) {
        List<EventoExtra> extras = new ArrayList<>();
        if (idsExtras != null) {
            idsExtras.forEach(idExtra -> extras.add(
                    EventoExtra.builder().idEventoPrivado(idEvento).idExtra(idExtra).build()));
        }
        if (extrasLibres != null) {
            extrasLibres.stream().filter(t -> t != null && !t.isBlank()).forEach(texto -> extras.add(
                    EventoExtra.builder().idEventoPrivado(idEvento).nombreLibre(texto).build()));
        }
        if (!extras.isEmpty()) {
            eventoExtraRepository.saveAll(extras);
        }
    }

    private EventoPrivado obtenerEvento(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventoPrivado", id));
    }

    private Cliente obtenerCliente(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", idCliente));
    }

    private Turno obtenerTurno(Long idTurno) {
        return turnoRepository.findById(idTurno)
                .orElseThrow(() -> new ResourceNotFoundException("Turno", idTurno));
    }

    private EventoPrivadoQuery toQuery(EventoPrivado e, Cliente c, Turno t, boolean cargarExtras) {
        List<EventoExtraQuery> extras = cargarExtras
                ? eventoExtraRepository.findByEvento(e.getId()).stream()
                        .map(ex -> {
                            String nombre = null;
                            if (ex.getIdExtra() != null) {
                                nombre = extraPaqueteRepository.findById(ex.getIdExtra())
                                        .map(ep -> ep.getNombre()).orElse(null);
                            }
                            return EventoExtraQuery.builder()
                                    .id(ex.getId())
                                    .idExtra(ex.getIdExtra())
                                    .nombreExtra(nombre)
                                    .nombreLibre(ex.getNombreLibre())
                                    .build();
                        }).toList()
                : null;

        return EventoPrivadoQuery.builder()
                .id(e.getId())
                .idCliente(e.getIdCliente())
                .nombreCliente(c.getNombre())
                .correoCliente(c.getCorreo())
                .telefonoCliente(c.getTelefono())
                .idSede(e.getIdSede())
                .estado(e.getEstado().getCodigo())
                .idTurno(e.getIdTurno())
                .turno(t.getCodigo())
                .horaInicio(t.getHoraInicio().toString())
                .horaFin(t.getHoraFin().toString())
                .fechaEvento(e.getFechaEvento())
                .tipoEvento(e.getTipoEvento())
                .contactoAdicional(e.getContactoAdicional())
                .aforoDeclarado(e.getAforoDeclarado())
                .precioTotalContrato(e.getPrecioTotalContrato())
                .montoAdelanto(e.getMontoAdelanto())
                .montoSaldo(e.calcularMontoSaldo())
                .medioPagoAdelanto(e.getMedioPagoAdelanto())
                .notasInternas(e.getNotasInternas())
                .observaciones(e.getObservaciones())
                .nombreNino(e.getNombreNino())
                .edadCumple(e.getEdadCumple())
                .idPaquete(e.getIdPaquete())
                .descripcionPersonalizada(e.getDescripcionPersonalizada())
                .presupuestoEstimado(e.getPresupuestoEstimado())
                .esCotizacionPersonalizada(e.isEsCotizacionPersonalizada())
                .usuarioGestor(e.getIdUsuarioGestor() != null ? "USR-" + e.getIdUsuarioGestor() : null)
                .estadoOperativo(e.getEstadoOperativo())
                .checklistCompleto(e.isChecklistCompleto())
                .horaInicioReal(e.getHoraInicioReal())
                .horaFinReal(e.getHoraFinReal())
                .extras(extras)
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
