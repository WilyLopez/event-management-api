package com.playzone.pems.application.evento.service;

import com.playzone.pems.application.fidelizacion.port.in.RegistrarVisitaUseCase;
import com.playzone.pems.application.evento.dto.query.MetricasReservaQuery;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.dto.query.TicketDetalleQuery;
import com.playzone.pems.application.evento.port.in.BuscarReservasAdminUseCase;
import com.playzone.pems.application.evento.port.in.ConfirmarIngresoUseCase;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.evento.exception.ReservaNotFoundException;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaAdminService
        implements ConfirmarIngresoUseCase,
                   BuscarReservasAdminUseCase {

    private final ReservaPublicaRepository          reservaRepository;
    private final ClientePerfilRepository            clientePerfilRepository;
    private final FeriadoRepository                 feriadoRepository;
    private final BloqueCalendarioRepository        bloqueRepository;
    private final ConfiguracionCalendarioRepository configRepository;
    private final EventoPrivadoRepository           eventoRepository;
    private final RegistrarVisitaUseCase            registrarVisitaUseCase;

    @Override
    @Transactional
    public ReservaPublicaQuery ejecutar(Long idReserva, UUID idUsuarioAdmin) {
        ReservaPublica reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (!reserva.puedeRegistrarIngreso()) {
            throw new ValidationException(
                    "La reserva ya fue ingresada o su estado no permite registrar ingreso.");
        }

        ReservaPublica actualizada = reserva.toBuilder()
                .estado(EstadoReservaPublica.COMPLETADA)
                .ingresado(true)
                .ingresoAt(OffsetDateTime.now(ZoneId.of("America/Lima")))
                .build();

        ReservaPublica guardada = reservaRepository.save(actualizada);

        try {
            registrarVisitaUseCase.registrarVisita(guardada.getId());
        } catch (Exception e) {
            System.err.println("Error fidelizacion: " + e.getMessage());
        }

        String nombre = clientePerfilRepository.buscarPorId(guardada.getIdCliente())
                .map(ClientePerfil::nombreCompleto).orElse(null);

        return toQuery(guardada, nombre, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaPublicaQuery> buscar(
            Long idSede, String estado, LocalDate fecha,
            Boolean ingresado, Boolean esReprogramacion,
            String search, Pageable pageable) {

        EstadoReservaPublica estadoEnum = null;
        if (estado != null && !estado.isBlank()) {
            try { estadoEnum = EstadoReservaPublica.valueOf(estado); }
            catch (IllegalArgumentException ignored) {}
        }

        String searchPattern = (search != null && !search.isBlank())
                ? "%" + search.toLowerCase() + "%" : null;

        return reservaRepository.buscarAdmin(
                idSede, estadoEnum, fecha, ingresado, esReprogramacion,
                searchPattern,
                pageable
        ).map(r -> {
            var cp = clientePerfilRepository.buscarPorId(r.getIdCliente()).orElse(null);
            return toQuery(r, cp != null ? cp.nombreCompleto() : null, cp != null ? cp.getCorreo() : null);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public MetricasReservaQuery metricas(Long idSede, LocalDate fecha) {
        LocalDate dia = fecha != null ? fecha : LocalDate.now(ZoneId.of("America/Lima"));
        return reservaRepository.calcularMetricas(idSede, dia);
    }

    @Transactional(readOnly = true)
    public TicketDetalleQuery buscarTicketDetalle(String numeroTicket) {
        ReservaPublica r = reservaRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new ReservaNotFoundException(0L));
        return toDetalle(r);
    }

    @Transactional
    public TicketDetalleQuery marcarEntrada(Long idReserva) {
        ReservaPublica r = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (r.isIngresado()) {
            throw new ValidationException("Este ticket ya registro su ingreso.");
        }
        if (r.getEstado() == EstadoReservaPublica.CANCELADA) {
            throw new ValidationException("Este ticket esta cancelado.");
        }
        if (r.getEstado() == EstadoReservaPublica.PENDIENTE) {
            throw new ValidationException("Este ticket tiene pago pendiente. Cobra antes de permitir el ingreso.");
        }

        ReservaPublica actualizada = r.toBuilder()
                .ingresado(true)
                .estado(EstadoReservaPublica.COMPLETADA)
                .ingresoAt(OffsetDateTime.now(ZoneId.of("America/Lima")))
                .build();
        return toDetalle(reservaRepository.save(actualizada));
    }

    @Transactional
    public TicketDetalleQuery editarFecha(Long idReserva, LocalDate nuevaFecha) {
        ReservaPublica r = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (r.isIngresado()) {
            throw new ValidationException("No se puede cambiar la fecha de un ticket ya ingresado.");
        }
        if (r.getEstado() == EstadoReservaPublica.CANCELADA) {
            throw new ValidationException("No se puede cambiar la fecha de un ticket cancelado.");
        }

        LocalDate hoy = FechaUtil.hoy();
        ConfiguracionCalendario cfg = configRepository.obtener(r.getIdSede());
        LocalDate max = hoy.plusDays(cfg.getDiasMaxReservaPublica());

        if (nuevaFecha.isBefore(hoy)) {
            throw new ValidationException("La nueva fecha no puede ser en el pasado.");
        }
        if (nuevaFecha.isAfter(max)) {
            throw new ValidationException("La nueva fecha excede el horizonte permitido.");
        }
        if (feriadoRepository.existsByFecha(nuevaFecha)) {
            throw new ValidationException("La nueva fecha es feriado.");
        }
        if (bloqueRepository.existsBloqueActivoEnFecha(r.getIdSede(), nuevaFecha)) {
            throw new ValidationException("La nueva fecha esta bloqueada.");
        }
        if (eventoRepository.existsActivoBySedeAndFecha(r.getIdSede(), nuevaFecha)) {
            throw new ValidationException("La nueva fecha esta reservada para un evento privado.");
        }
        int activas = reservaRepository.countActivasBySedeAndFecha(r.getIdSede(), nuevaFecha);
        if (activas >= cfg.getAforoMaximo()) {
            throw new ValidationException("No hay aforo disponible para la nueva fecha.");
        }

        ReservaPublica actualizada = r.toBuilder()
                .fechaEvento(nuevaFecha)
                .build();
        return toDetalle(reservaRepository.save(actualizada));
    }

    private TicketDetalleQuery toDetalle(ReservaPublica r) {
        LocalDate hoy = FechaUtil.hoy();
        String estadoPago = r.getEstado() == EstadoReservaPublica.PENDIENTE ? "PENDIENTE" : "PAGADO";
        return TicketDetalleQuery.builder()
                .idReserva(r.getId())
                .numeroTicket(r.getNumeroTicket())
                .estado(r.getEstado().getCodigo())
                .yaIngreso(r.isIngresado())
                .fechaIngreso(r.getIngresoAt())
                .fechaVisita(r.getFechaEvento())
                .esHoy(r.getFechaEvento().isEqual(hoy))
                .nombreNino(r.getNombreNino())
                .edadNino(r.getEdadNino())
                .nombreAcompanante(r.getNombreAcompanante())
                .dniAcompanante(r.getDniAcompanante())
                .montoPagado(r.getTotalPagado())
                .estadoPago(estadoPago)
                .codigoQr(r.getCodigoQr())
                .build();
    }

    private ReservaPublicaQuery toQuery(ReservaPublica r, String nombreCliente, String correoCliente) {
        return ReservaPublicaQuery.builder()
                .id(r.getId())
                .idCliente(r.getIdCliente())
                .nombreCliente(nombreCliente)
                .correoCliente(correoCliente)
                .idSede(r.getIdSede())
                .estado(r.getEstado().getCodigo())
                .canalReserva(r.getCanalReserva() != null ? r.getCanalReserva().getCodigo() : null)
                .tipoDia(r.getTipoDia() != null ? r.getTipoDia().getCodigo() : null)
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
                .ingresado(r.isIngresado())
                .fechaIngreso(r.getIngresoAt())
                .codigoQr(r.getCodigoQr())
                .fechaCreacion(r.getCreatedAt())
                .build();
    }
}