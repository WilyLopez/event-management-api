package com.playzone.pems.application.evento.service;

import com.playzone.pems.application.evento.dto.command.CrearReservaPublicaCommand;
import com.playzone.pems.application.evento.dto.command.ReprogramarReservaCommand;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.port.in.CancelarReservaUseCase;
import com.playzone.pems.application.evento.port.in.ConsultarReservasUseCase;
import com.playzone.pems.application.evento.port.in.CrearReservaPublicaUseCase;
import com.playzone.pems.application.evento.port.in.ReprogramarReservaUseCase;
import com.playzone.pems.application.evento.port.out.EnviarTicketPorCorreoPort;
import com.playzone.pems.domain.calendario.exception.AforoExcedidoException;
import com.playzone.pems.domain.calendario.exception.FechaNoDisponibleException;
import com.playzone.pems.domain.calendario.model.Tarifa;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.DisponibilidadDiariaRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.calendario.repository.TarifaRepository;
import com.playzone.pems.domain.evento.exception.ReservaNotFoundException;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import com.playzone.pems.shared.util.TicketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReservaPublicaService
        implements CrearReservaPublicaUseCase,
        ReprogramarReservaUseCase,
        CancelarReservaUseCase,
        ConsultarReservasUseCase {

    private final ReservaPublicaRepository   reservaRepository;
    private final ClienteRepository          clienteRepository;
    private final TarifaRepository           tarifaRepository;
    private final FeriadoRepository          feriadoRepository;
    private final BloqueCalendarioRepository bloqueRepository;
    private final DisponibilidadDiariaRepository disponibilidadRepository;
    private final EnviarTicketPorCorreoPort  correoPort;

    @Value("${playzone.negocio.aforo-maximo:60}")
    private int aforoMaximo;

    @Value("${playzone.negocio.anticipacion-min-horas:1}")
    private int anticipacionMinHoras;

    @Value("${playzone.negocio.max-reprogramaciones:1}")
    private int maxReprogramaciones;

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaPublicaQuery> consultarPorCliente(Long idCliente, Pageable pageable) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", idCliente));
        return reservaRepository.findByCliente(idCliente, pageable)
                .map(r -> toQuery(r, cliente.getNombre()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaPublicaQuery> consultarPorSedeYFecha(Long idSede, LocalDate fecha, Pageable pageable) {
        return reservaRepository.findBySedeAndFecha(idSede, fecha, pageable)
                .map(r -> toQuery(r, fetchNombreCliente(r.getIdCliente())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaPublicaQuery> consultarPorSedeYEstado(Long idSede, String estado, Pageable pageable) {
        return reservaRepository.findBySedeAndEstado(idSede, EstadoReservaPublica.valueOf(estado), pageable)
                .map(r -> toQuery(r, fetchNombreCliente(r.getIdCliente())));
    }

    private String fetchNombreCliente(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .map(Cliente::getNombre)
                .orElse("Cliente Desconocido");
    }

    @Override
    @Transactional
    public ReservaPublicaQuery ejecutar(CrearReservaPublicaCommand command) {
        validarFechaDisponible(command.getIdSede(), command.getFechaEvento());

        TipoDia tipoDia = resolverTipoDia(command.getFechaEvento());

        Tarifa tarifa = tarifaRepository
                .findVigenteBySedeAndTipoDiaAndFecha(command.getIdSede(), tipoDia, command.getFechaEvento())
                .orElseThrow(() -> new ValidationException("No existe tarifa vigente para esa fecha."));

        Cliente cliente = clienteRepository.findById(command.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", command.getIdCliente()));

        BigDecimal precio    = tarifa.getPrecio();
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal total     = precio.subtract(descuento);

        long secuencia = reservaRepository.countConfirmadasBySedeAndFecha(
                command.getIdSede(), command.getFechaEvento()) + 1L;

        String ticket = TicketUtil.generar(
                command.getIdSede().intValue(), command.getFechaEvento(), secuencia);

        ReservaPublica reserva = ReservaPublica.builder()
                .idCliente(command.getIdCliente())
                .idSede(command.getIdSede())
                .estado(EstadoReservaPublica.PENDIENTE)
                .canalReserva(command.getCanalReserva())
                .tipoDia(tipoDia)
                .fechaEvento(command.getFechaEvento())
                .numeroTicket(ticket)
                .precioHistorico(precio)
                .descuentoAplicado(descuento)
                .totalPagado(total)
                .nombreNino(command.getNombreNino())
                .edadNino(command.getEdadNino())
                .nombreAcompanante(command.getNombreAcompanante())
                .dniAcompanante(command.getDniAcompanante())
                .firmoConsentimiento(command.getFirmoConsentimiento())
                .esReprogramacion(false)
                .vecesReprogramada(0)
                .build();

        ReservaPublica guardada = reservaRepository.save(reserva);
        disponibilidadRepository.incrementarAforo(command.getIdSede(), command.getFechaEvento());

        ReservaPublicaQuery query = toQuery(guardada, cliente.getNombre());

        correoPort.enviarTicket(cliente.getCorreo(), cliente.getNombre(), query);

        return query;
    }

    @Override
    @Transactional
    public ReservaPublicaQuery ejecutar(ReprogramarReservaCommand command) {
        ReservaPublica original = reservaRepository.findById(command.getIdReservaOriginal())
                .orElseThrow(() -> new ReservaNotFoundException(command.getIdReservaOriginal()));

        if (!original.puedeReprogramarse(maxReprogramaciones)) {
            throw new ValidationException("La reserva no puede reprogramarse en su estado actual o superó el límite.");
        }

        validarFechaDisponible(original.getIdSede(), command.getNuevaFechaEvento());

        TipoDia tipoDia = resolverTipoDia(command.getNuevaFechaEvento());

        Tarifa tarifa = tarifaRepository
                .findVigenteBySedeAndTipoDiaAndFecha(
                        original.getIdSede(), tipoDia, command.getNuevaFechaEvento())
                .orElseThrow(() -> new ValidationException("No existe tarifa vigente para la nueva fecha."));

        ReservaPublica originalActualizada = original.toBuilder()
                .estado(EstadoReservaPublica.REPROGRAMADA)
                .build();
        reservaRepository.save(originalActualizada);

        long secuencia = reservaRepository.countConfirmadasBySedeAndFecha(
                original.getIdSede(), command.getNuevaFechaEvento()) + 1L;

        String ticket = TicketUtil.generar(
                original.getIdSede().intValue(), command.getNuevaFechaEvento(), secuencia);

        BigDecimal precio = tarifa.getPrecio();

        ReservaPublica nueva = ReservaPublica.builder()
                .idCliente(original.getIdCliente())
                .idSede(original.getIdSede())
                .estado(EstadoReservaPublica.PENDIENTE)
                .canalReserva(original.getCanalReserva())
                .tipoDia(tipoDia)
                .idReservaOriginal(original.getId())
                .esReprogramacion(true)
                .vecesReprogramada(original.getVecesReprogramada() + 1)
                .fechaEvento(command.getNuevaFechaEvento())
                .numeroTicket(ticket)
                .precioHistorico(precio)
                .descuentoAplicado(BigDecimal.ZERO)
                .totalPagado(precio)
                .nombreNino(original.getNombreNino())
                .edadNino(original.getEdadNino())
                .nombreAcompanante(original.getNombreAcompanante())
                .dniAcompanante(original.getDniAcompanante())
                .firmoConsentimiento(original.isFirmoConsentimiento())
                .build();

        ReservaPublica guardada = reservaRepository.save(nueva);
        disponibilidadRepository.decrementarAforo(original.getIdSede(), original.getFechaEvento());
        disponibilidadRepository.incrementarAforo(original.getIdSede(), command.getNuevaFechaEvento());

        Cliente cliente = clienteRepository.findById(guardada.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", guardada.getIdCliente()));

        ReservaPublicaQuery query = toQuery(guardada, cliente.getNombre());
        correoPort.enviarTicket(cliente.getCorreo(), cliente.getNombre(), query);

        return query;
    }

    @Override
    @Transactional
    public ReservaPublicaQuery ejecutar(Long idReserva, String motivo) {
        ReservaPublica reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (!reserva.puedeCancelarse()) {
            throw new ValidationException("La reserva no puede cancelarse en su estado actual.");
        }

        ReservaPublica cancelada = reserva.toBuilder()
                .estado(EstadoReservaPublica.CANCELADA)
                .motivoCancelacion(motivo)
                .build();

        ReservaPublica guardada = reservaRepository.save(cancelada);
        disponibilidadRepository.decrementarAforo(reserva.getIdSede(), reserva.getFechaEvento());

        Cliente cliente = clienteRepository.findById(guardada.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", guardada.getIdCliente()));

        return toQuery(guardada, cliente.getNombre());
    }

    private void validarFechaDisponible(Long idSede, LocalDate fecha) {
        if (FechaUtil.esPasado(fecha)) {
            throw new FechaNoDisponibleException(fecha, "La fecha ya pasó.");
        }
        // Solo validamos anticipación en horas si la fecha es futura (no hoy)
        if (fecha.isAfter(FechaUtil.hoyPeru()) && !FechaUtil.superaAnticipacionMinima(fecha, anticipacionMinHoras)) {
            throw new FechaNoDisponibleException(fecha,
                    "Debe reservar con al menos " + anticipacionMinHoras + " hora(s) de anticipación.");
        }
        if (bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha)) {
            throw new FechaNoDisponibleException(fecha, "La fecha está bloqueada por el administrador.");
        }
        int confirmadas = reservaRepository.countConfirmadasBySedeAndFecha(idSede, fecha);
        if (confirmadas >= aforoMaximo) {
            throw new AforoExcedidoException(fecha, aforoMaximo);
        }
    }

    private TipoDia resolverTipoDia(LocalDate fecha) {
        boolean esFeriado = feriadoRepository.findByFecha(fecha).isPresent();
        return (FechaUtil.esFindeSemana(fecha) || esFeriado)
                ? TipoDia.FIN_SEMANA_FERIADO
                : TipoDia.SEMANA;
    }

    private ReservaPublicaQuery toQuery(ReservaPublica r, String nombreCliente) {
        return ReservaPublicaQuery.builder()
                .id(r.getId())
                .idCliente(r.getIdCliente())
                .nombreCliente(nombreCliente)
                .idSede(r.getIdSede())
                .estado(r.getEstado().getCodigo())
                .canalReserva(r.getCanalReserva().getCodigo())
                .tipoDia(r.getTipoDia().getCodigo())
                .fechaEvento(r.getFechaEvento())
                .numeroTicket(r.getNumeroTicket())
                .precioHistorico(r.getPrecioHistorico())
                .descuentoAplicado(r.getDescuentoAplicado())
                .totalPagado(r.getTotalPagado())
                .nombreNino(r.getNombreNino())
                .edadNino(r.getEdadNino())
                .nombreAcompanante(r.getNombreAcompanante())
                .dniAcompanante(r.getDniAcompanante())
                .firmoConsentimiento(r.isFirmoConsentimiento())
                .esReprogramacion(r.isEsReprogramacion())
                .vecesReprogramada(r.getVecesReprogramada())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}