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
import com.playzone.pems.domain.calendario.exception.FechaNoDisponibleException;
import com.playzone.pems.domain.venta.model.Venta;
import com.playzone.pems.domain.venta.model.VentaPago;
import com.playzone.pems.domain.venta.repository.VentaPagoRepository;
import com.playzone.pems.domain.venta.repository.VentaRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.calendario.repository.TurnoRepository;
import com.playzone.pems.domain.comercial.repository.ExtraPaqueteRepository;
import com.playzone.pems.domain.comercial.repository.ServicioCotizacionRepository;
import com.playzone.pems.domain.evento.model.EventoExtra;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.domain.evento.repository.EventoExtraRepository;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.domain.calendario.model.Turno;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventoPrivadoService
        implements SolicitarEventoPrivadoUseCase,
        ConfirmarEventoPrivadoUseCase,
        CancelarEventoPrivadoUseCase,
        ConsultarEventosPrivadosUseCase,
        BuscarEventosAdminUseCase {

    private final EventoPrivadoRepository         eventoRepository;
    private final ReservaPublicaRepository        reservaRepository;
    private final ClientePerfilRepository          clientePerfilRepository;
    private final BloqueCalendarioRepository      bloqueRepository;
    private final FeriadoRepository               feriadoRepository;
    private final TurnoRepository                 turnoRepository;
    private final ConfiguracionCalendarioRepository configRepository;
    private final EnviarNotificacionEventoPort    notificacionPort;
    private final EventoExtraRepository           eventoExtraRepository;
    private final VentaRepository                 ventaRepository;
    private final VentaPagoRepository             ventaPagoRepository;
    private final SupabaseAuthFacade              supabaseAuthFacade;
    private final ExtraPaqueteRepository          extraPaqueteRepository;
    private final ServicioCotizacionRepository    servicioCotizacionRepository;

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
        validarTurnoEvento(command.getIdSede(), command.getFechaEvento(), command.getIdTurno());

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
                .notasInternas(command.getObservaciones())
                .idPaquete(command.getIdPaquete())
                .descripcionPersonalizada(command.getDescripcionPersonalizada())
                .presupuestoEstimado(command.getPresupuestoEstimado())
                .esCotizacionPersonalizada(command.isEsCotizacionPersonalizada())
                .build();

        EventoPrivado guardado = eventoRepository.save(evento);
        persistirExtras(guardado.getId(), command.getIdsExtras(), command.getExtrasLibres());
        persistirServiciosCotizacion(guardado.getId(), command.getIdsServiciosCotizacion());

        ClientePerfil cliente = obtenerCliente(guardado.getIdCliente());
        Turno         turno   = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno, true);

        if (cliente.getCorreo() != null) {
            notificacionPort.notificarSolicitudRecibida(cliente.getCorreo(), query);
        } else {
            log.warn("Evento {} sin correo de cliente {}, no se envia notificacion solicitud", guardado.getId(), guardado.getIdCliente());
        }
        notificacionPort.notificarAdminNuevaSolicitud(query);
        return query;
    }

    @Override
    @Transactional
    public EventoPrivadoQuery ejecutar(Long idEvento, BigDecimal precioTotal,
                                       BigDecimal montoAdelanto,
                                       UUID idUsuarioGestor,
                                       String medioPago) {
        EventoPrivado evento = obtenerEvento(idEvento);

        if (evento.getEstado() != EstadoEventoPrivado.SOLICITADA) {
            throw new ValidationException("Solo se pueden confirmar eventos en estado SOLICITADA.");
        }

        EventoPrivado confirmado = evento.toBuilder()
                .estado(EstadoEventoPrivado.CONFIRMADA)
                .precioTotalContrato(precioTotal)
                .montoAdelanto(montoAdelanto != null ? montoAdelanto : BigDecimal.ZERO)
                .idUsuarioGestor(idUsuarioGestor)
                .build();

        EventoPrivado guardado = eventoRepository.save(confirmado);

        if (montoAdelanto != null && montoAdelanto.compareTo(BigDecimal.ZERO) > 0) {
            Venta ventaAdelanto = ventaRepository.save(Venta.builder()
                    .idSede(guardado.getIdSede())
                    .clienteId(guardado.getIdCliente())
                    .eventoId(guardado.getId())
                    .tipo("ADELANTO_EVENTO")
                    .canalCodigo("MOSTRADOR")
                    .subtotal(montoAdelanto)
                    .descuento(BigDecimal.ZERO)
                    .total(montoAdelanto)
                    .efectivoRecibido(BigDecimal.ZERO)
                    .vuelto(BigDecimal.ZERO)
                    .actaFirmada(false)
                    .esAnticipada(false)
                    .createdBy(idUsuarioGestor)
                    .build());
            ventaPagoRepository.save(VentaPago.builder()
                    .ventaId(ventaAdelanto.getId())
                    .medioPagoCodigo(medioPago)
                    .monto(montoAdelanto)
                    .esValidado(true)
                    .validadoPor(idUsuarioGestor)
                    .validadoAt(java.time.OffsetDateTime.now())
                    .build());
        }

        ClientePerfil cliente = obtenerCliente(guardado.getIdCliente());
        Turno         turno   = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno, true);
        if (cliente.getCorreo() != null) {
            notificacionPort.notificarEventoConfirmado(cliente.getCorreo(), query);
        } else {
            log.warn("Evento {} sin correo de cliente {}, no se envia notificacion confirmacion", guardado.getId(), guardado.getIdCliente());
        }
        return query;
    }

    @Transactional
    public EventoPrivadoQuery completar(Long idEvento, UUID idUsuarioGestor) {
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
    public EventoPrivadoQuery registrarSaldo(Long idEvento, BigDecimal monto, String medioPago, UUID idUsuarioGestor) {
        EventoPrivado evento = obtenerEvento(idEvento);

        Venta ventaSaldo = ventaRepository.save(Venta.builder()
                .idSede(evento.getIdSede())
                .clienteId(evento.getIdCliente())
                .eventoId(evento.getId())
                .tipo("SALDO_EVENTO")
                .canalCodigo("MOSTRADOR")
                .subtotal(monto)
                .descuento(BigDecimal.ZERO)
                .total(monto)
                .efectivoRecibido(BigDecimal.ZERO)
                .vuelto(BigDecimal.ZERO)
                .actaFirmada(false)
                .esAnticipada(false)
                .createdBy(idUsuarioGestor)
                .build());
        ventaPagoRepository.save(VentaPago.builder()
                .ventaId(ventaSaldo.getId())
                .medioPagoCodigo(medioPago)
                .monto(monto)
                .esValidado(true)
                .validadoPor(idUsuarioGestor)
                .validadoAt(java.time.OffsetDateTime.now())
                .build());

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
        ClientePerfil cliente = obtenerCliente(guardado.getIdCliente());
        Turno         turno   = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno, false);
        if (cliente.getCorreo() != null) {
            notificacionPort.notificarEventoCancelado(cliente.getCorreo(), query, motivoCancelacion);
        } else {
            log.warn("Evento {} sin correo de cliente {}, no se envia notificacion cancelacion", guardado.getId(), guardado.getIdCliente());
        }
        return query;
    }

    private void validarFechaEvento(Long idSede, LocalDate fecha) {
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);
        long dias = FechaUtil.diasEntre(FechaUtil.hoy(), fecha);

        if (dias < cfg.getDiasMinEventoPrivado()) {
            throw new FechaNoDisponibleException(fecha,
                    "Los eventos privados deben reservarse con un minimo de "
                    + cfg.getDiasMinEventoPrivado()
                    + " dias de anticipacion. Por favor selecciona una fecha posterior.");
        }
        if (dias > cfg.getDiasMaxEventoPrivado()) {
            throw new ValidationException(
                    "Los eventos solo pueden agendarse hasta " + cfg.getDiasMaxEventoPrivado() + " dias adelante.");
        }
        if (feriadoRepository.existsByFecha(fecha)) {
            throw new FechaNoDisponibleException(fecha, "Esta fecha es feriado.");
        }
        if (bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha)) {
            throw new FechaNoDisponibleException(fecha, "La fecha esta bloqueada.");
        }
        if (reservaRepository.existsActivaBySedeAndFecha(idSede, fecha)) {
            throw new ValidationException(
                    "Esta fecha ya tiene reservas publicas y no admite eventos privados.");
        }
    }

    private void validarTurnoEvento(Long idSede, LocalDate fecha, Long idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new ValidationException("Turno no encontrado."));
        if (eventoRepository.existsActivoBySedeAndFechaAndCodigoTurno(idSede, fecha, turno.getCodigo())) {
            throw new ValidationException(
                    "Este turno ya tiene un evento privado. Elige otro turno u otra fecha.");
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

    private ClientePerfil obtenerCliente(Long idCliente) {
        return clientePerfilRepository.buscarPorId(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", idCliente));
    }

    private Turno obtenerTurno(Long idTurno) {
        return turnoRepository.findById(idTurno)
                .orElseThrow(() -> new ResourceNotFoundException("Turno", idTurno));
    }

    private EventoPrivadoQuery toQuery(EventoPrivado e, ClientePerfil c, Turno t, boolean cargarExtras) {
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
                .nombreCliente(c.nombreCompleto())
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
                .observaciones(e.getNotasInternas())
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
                .medioPago(fetchMedioPagoEvento(e.getId()))
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    private String fetchMedioPagoEvento(Long idEvento) {
        List<Venta> ventas = ventaRepository.findByEventoId(idEvento);
        if (ventas.isEmpty()) return null;
        List<VentaPago> pagos = ventas.stream()
                .flatMap(v -> ventaPagoRepository.findByVentaId(v.getId()).stream())
                .toList();
        if (pagos.isEmpty()) return null;
        long distinct = pagos.stream().map(VentaPago::getMedioPagoCodigo).distinct().count();
        return distinct == 1 ? pagos.get(0).getMedioPagoCodigo() : "MULTIPLE";
    }
}
