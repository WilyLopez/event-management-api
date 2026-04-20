package com.playzone.pems.application.evento.service;

import com.playzone.pems.application.evento.dto.command.SolicitarEventoPrivadoCommand;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import com.playzone.pems.application.evento.port.in.CancelarEventoPrivadoUseCase;
import com.playzone.pems.application.evento.port.in.ConfirmarEventoPrivadoUseCase;
import com.playzone.pems.application.evento.port.in.SolicitarEventoPrivadoUseCase;
import com.playzone.pems.application.evento.port.out.EnviarNotificacionEventoPort;
import com.playzone.pems.domain.calendario.exception.FechaNoDisponibleException;
import com.playzone.pems.domain.calendario.model.Turno;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.TurnoRepository;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventoPrivadoService
        implements SolicitarEventoPrivadoUseCase,
        ConfirmarEventoPrivadoUseCase,
        CancelarEventoPrivadoUseCase {

    private final EventoPrivadoRepository     eventoRepository;
    private final ClienteRepository           clienteRepository;
    private final BloqueCalendarioRepository  bloqueRepository;
    private final TurnoRepository             turnoRepository;
    private final EnviarNotificacionEventoPort notificacionPort;

    @Value("${playzone.negocio.anticipacion-min-evento-dias:15}")
    private int anticipacionMinDias;

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
                .build();

        EventoPrivado guardado = eventoRepository.save(evento);
        Cliente cliente        = obtenerCliente(guardado.getIdCliente());
        Turno turno            = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno);

        notificacionPort.notificarSolicitudRecibida(cliente.getCorreo(), query);
        return query;
    }

    @Override
    @Transactional
    public EventoPrivadoQuery ejecutar(Long idEvento, BigDecimal precioTotal, Long idUsuarioGestor) {
        EventoPrivado evento = obtenerEvento(idEvento);

        if (evento.getEstado() != EstadoEventoPrivado.SOLICITADA) {
            throw new ValidationException("Solo se pueden confirmar eventos en estado SOLICITADA.");
        }

        EventoPrivado confirmado = evento.toBuilder()
                .estado(EstadoEventoPrivado.CONFIRMADA)
                .precioTotalContrato(precioTotal)
                .idUsuarioGestor(idUsuarioGestor)
                .build();

        EventoPrivado guardado = eventoRepository.save(confirmado);
        Cliente cliente        = obtenerCliente(guardado.getIdCliente());
        Turno turno            = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno);

        notificacionPort.notificarEventoConfirmado(cliente.getCorreo(), query);
        return query;
    }

    @Override
    @Transactional
    public EventoPrivadoQuery ejecutar(Long idEvento, String motivoCancelacion) {
        EventoPrivado evento = obtenerEvento(idEvento);

        if (!evento.puedeCancelarse()) {
            throw new ValidationException("El evento no puede cancelarse en su estado actual.");
        }
        if (motivoCancelacion == null || motivoCancelacion.isBlank()) {
            throw new ValidationException("motivoCancelacion", "El motivo de cancelación es obligatorio.");
        }

        EventoPrivado cancelado = evento.toBuilder()
                .estado(EstadoEventoPrivado.CANCELADA)
                .motivoCancelacion(motivoCancelacion)
                .build();

        EventoPrivado guardado = eventoRepository.save(cancelado);
        Cliente cliente        = obtenerCliente(guardado.getIdCliente());
        Turno turno            = obtenerTurno(guardado.getIdTurno());
        EventoPrivadoQuery query = toQuery(guardado, cliente, turno);

        notificacionPort.notificarEventoCancelado(cliente.getCorreo(), query, motivoCancelacion);
        return query;
    }

    private void validarFechaEvento(Long idSede, LocalDate fecha) {
        long diasRestantes = FechaUtil.diferenciaEnDias(FechaUtil.hoyPeru(), fecha);
        if (diasRestantes < anticipacionMinDias) {
            throw new FechaNoDisponibleException(fecha,
                    "Debe solicitarse con al menos " + anticipacionMinDias + " días de anticipación.");
        }
        if (bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha)) {
            throw new FechaNoDisponibleException(fecha, "La fecha está bloqueada por el administrador.");
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

    private EventoPrivadoQuery toQuery(EventoPrivado e, Cliente c, Turno t) {
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
                .notasInternas(e.getNotasInternas())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}