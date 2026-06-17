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
import com.playzone.pems.domain.venta.model.Venta;
import com.playzone.pems.domain.venta.model.VentaPago;
import com.playzone.pems.domain.venta.repository.VentaPagoRepository;
import com.playzone.pems.domain.venta.repository.VentaRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.domain.calendario.exception.FechaNoDisponibleException;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.model.Tarifa;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.calendario.repository.TarifaRepository;
import com.playzone.pems.domain.evento.exception.ReservaNotFoundException;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.storage.StoragePort;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.domain.usuario.repository.SedeRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaPublicaService
        implements CrearReservaPublicaUseCase,
        ReprogramarReservaUseCase,
        CancelarReservaUseCase,
        ConsultarReservasUseCase {

    private final ReservaPublicaRepository          reservaRepository;
    private final EventoPrivadoRepository           eventoRepository;
    private final ClientePerfilRepository           clientePerfilRepository;
    private final SedeRepository                    sedeRepository;
    private final TarifaRepository                  tarifaRepository;
    private final FeriadoRepository                 feriadoRepository;
    private final BloqueCalendarioRepository        bloqueRepository;
    private final ConfiguracionCalendarioRepository configRepository;
    private final EnviarTicketPorCorreoPort         correoPort;
    private final StoragePort                       storagePort;
    private final VentaRepository                   ventaRepository;
    private final VentaPagoRepository               ventaPagoRepository;
    private final SupabaseAuthFacade                supabaseAuthFacade;

    @Value("${playzone.negocio.max-reprogramaciones:1}")
    private int maxReprogramaciones;

    // ── Consultas ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaPublicaQuery> consultarPorCliente(Long idCliente, Pageable pageable) {
        ClientePerfil cliente = clientePerfilRepository.buscarPorId(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", idCliente));
        return reservaRepository.findByCliente(idCliente, pageable)
                .map(r -> toQuery(r, cliente.nombreCompleto(), cliente.getCorreo(), fetchNombreSede(r.getIdSede()), null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaPublicaQuery> consultarPorSedeYFecha(Long idSede, LocalDate fecha, Pageable pageable) {
        String nombreSede = fetchNombreSede(idSede);
        return reservaRepository.findBySedeAndFecha(idSede, fecha, pageable)
                .map(r -> toQuery(r, fetchNombreCliente(r.getIdCliente()), null, nombreSede, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaPublicaQuery> consultarPorSedeYEstado(Long idSede, String estado, Pageable pageable) {
        String nombreSede = fetchNombreSede(idSede);
        return reservaRepository.findBySedeAndEstado(idSede, EstadoReservaPublica.valueOf(estado), pageable)
                .map(r -> toQuery(r, fetchNombreCliente(r.getIdCliente()), null, nombreSede, null));
    }

    @Transactional(readOnly = true)
    public ReservaPublicaQuery consultarPorId(Long idReserva) {
        ReservaPublica r = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));
        return enriquecerQuery(r);
    }

    @Transactional(readOnly = true)
    public ReservaPublicaQuery consultarPorNumeroTicket(String numeroTicket) {
        ReservaPublica r = reservaRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "numeroTicket", numeroTicket));
        return enriquecerQuery(r);
    }

    // ── Comandos ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ReservaPublicaQuery ejecutar(CrearReservaPublicaCommand command) {
        validarFechaDisponible(command.getIdSede(), command.getFechaEvento());

        TipoDia tipoDia = resolverTipoDia(command.getFechaEvento());

        Tarifa tarifa = tarifaRepository
                .findVigenteBySedeAndTipoDiaAndFecha(command.getIdSede(), tipoDia, command.getFechaEvento())
                .orElseThrow(() -> new ValidationException("No existe tarifa vigente para esa fecha."));

        ClientePerfil cliente = clientePerfilRepository.buscarPorId(command.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", command.getIdCliente()));

        BigDecimal precio    = tarifa.getPrecio();
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal total     = precio.subtract(descuento);

        Venta venta = ventaRepository.save(Venta.builder()
                .idSede(command.getIdSede())
                .clienteId(command.getIdCliente())
                .tipo("RESERVA")
                .canalCodigo("WEB")
                .fechaVisita(command.getFechaEvento())
                .subtotal(precio)
                .descuento(descuento)
                .total(total)
                .efectivoRecibido(BigDecimal.ZERO)
                .vuelto(BigDecimal.ZERO)
                .actaFirmada(false)
                .esAnticipada(true)
                .createdBy(cliente.getUsuarioId())
                .build());

        ReservaPublica reserva = ReservaPublica.builder()
                .ventaId(venta.getId())
                .idCliente(command.getIdCliente())
                .idSede(command.getIdSede())
                .estado(EstadoReservaPublica.PENDIENTE)
                .canalReserva(command.getCanalReserva())
                .tipoDia(tipoDia)
                .fechaEvento(command.getFechaEvento())
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

        ReservaPublicaQuery query = toQuery(guardada, cliente.nombreCompleto(), cliente.getCorreo(),
                fetchNombreSede(command.getIdSede()), null);
        if (cliente.getCorreo() != null) {
            correoPort.enviarTicket(cliente.getCorreo(), cliente.nombreCompleto(), query);
        } else {
            log.warn("Reserva {} sin correo de cliente {}, no se envia ticket", guardada.getId(), command.getIdCliente());
        }

        return query;
    }

    @Override
    @Transactional
    public ReservaPublicaQuery ejecutar(ReprogramarReservaCommand command) {
        ReservaPublica original = reservaRepository.findById(command.getIdReservaOriginal())
                .orElseThrow(() -> new ReservaNotFoundException(command.getIdReservaOriginal()));

        if (!original.puedeReprogramarse(maxReprogramaciones)) {
            throw new ValidationException("La reserva no puede reprogramarse en su estado actual o supero el limite.");
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

        ClientePerfil cliente = clientePerfilRepository.buscarPorId(guardada.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", guardada.getIdCliente()));

        ReservaPublicaQuery query = toQuery(guardada, cliente.nombreCompleto(), cliente.getCorreo(),
                fetchNombreSede(guardada.getIdSede()), null);
        if (cliente.getCorreo() != null) {
            correoPort.enviarTicket(cliente.getCorreo(), cliente.nombreCompleto(), query);
        } else {
            log.warn("Reprogramacion {} sin correo de cliente {}, no se envia ticket", guardada.getId(), guardada.getIdCliente());
        }

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

        ClientePerfil cliente = clientePerfilRepository.buscarPorId(guardada.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", guardada.getIdCliente()));

        return toQuery(guardada, cliente.nombreCompleto(), cliente.getCorreo(),
                fetchNombreSede(guardada.getIdSede()), null);
    }

    @Transactional
    public ReservaPublicaQuery confirmarPago(Long idReserva, String medioPago) {
        ReservaPublica reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));
        if (reserva.getEstado() != EstadoReservaPublica.PENDIENTE) {
            throw new ValidationException("Solo se pueden confirmar reservas en estado PENDIENTE.");
        }
        ReservaPublica guardada = reservaRepository.save(
                reserva.toBuilder().estado(EstadoReservaPublica.CONFIRMADA).build());
        ventaPagoRepository.save(VentaPago.builder()
                .ventaId(guardada.getVentaId())
                .medioPagoCodigo(medioPago)
                .monto(guardada.getTotalPagado())
                .esValidado(true)
                .validadoPor(supabaseAuthFacade.usuarioActualId().orElse(null))
                .validadoAt(java.time.OffsetDateTime.now())
                .build());
        return enriquecerQuery(guardada);
    }

    @Transactional
    public ReservaPublicaQuery actualizarReferenciaPago(Long idReserva, MultipartFile archivo) {
        try {
            byte[] bytes = archivo.getBytes();
            String key = "comprobantes/" + UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            String mime = archivo.getContentType() != null ? archivo.getContentType() : "application/octet-stream";
            storagePort.upload("comprobantes", key, bytes, mime);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el comprobante", e);
        }
        ReservaPublica reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));
        return enriquecerQuery(reserva);
    }

    // ── Validaciones internas ────────────────────────────────────────────────────

    private void validarFechaDisponible(Long idSede, LocalDate fecha) {
        ConfiguracionCalendario cfg = configRepository.obtener(idSede);
        LocalDate hoy   = FechaUtil.hoy();
        LocalDate max   = hoy.plusDays(cfg.getDiasMaxReservaPublica());

        if (fecha.isBefore(hoy)) {
            throw new FechaNoDisponibleException(fecha, "No se puede reservar en fechas pasadas.");
        }
        if (fecha.isAfter(max)) {
            throw new ValidationException(
                    "Las reservas solo se permiten hasta " + cfg.getDiasMaxReservaPublica()
                    + " dias de anticipacion.");
        }
        if (fecha.isEqual(hoy) && FechaUtil.ahora().toLocalTime().isAfter(cfg.getHoraCierre())) {
            throw new FechaNoDisponibleException(fecha, "El local ya cerro por hoy. Elige otra fecha.");
        }
        if (feriadoRepository.existsByFecha(fecha)) {
            throw new FechaNoDisponibleException(fecha, "Esta fecha es feriado.");
        }
        if (bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha)) {
            throw new FechaNoDisponibleException(fecha, "La fecha esta bloqueada.");
        }
        if (eventoRepository.existsActivoBySedeAndFecha(idSede, fecha)) {
            throw new ValidationException(
                    "Esta fecha esta reservada para un evento privado. Elige otra fecha.");
        }
        int activas = reservaRepository.countActivasBySedeAndFecha(idSede, fecha);
        if (activas >= cfg.getAforoMaximo()) {
            throw new AforoExcedidoException(fecha, cfg.getAforoMaximo());
        }
    }

    private TipoDia resolverTipoDia(LocalDate fecha) {
        boolean esFeriado = feriadoRepository.findByFecha(fecha).isPresent();
        return (FechaUtil.esFindeSemana(fecha) || esFeriado)
                ? TipoDia.FIN_SEMANA_FERIADO
                : TipoDia.SEMANA;
    }

    private String fetchNombreCliente(Long idCliente) {
        return clientePerfilRepository.buscarPorId(idCliente)
                .map(ClientePerfil::nombreCompleto)
                .orElse("Cliente Desconocido");
    }

    private String fetchNombreSede(Long idSede) {
        return sedeRepository.findById(idSede)
                .map(s -> s.getNombre())
                .orElse("Sede Principal");
    }

    // ── Mapeo ────────────────────────────────────────────────────────────────────

    private ReservaPublicaQuery enriquecerQuery(ReservaPublica r) {
        var cliente = clientePerfilRepository.buscarPorId(r.getIdCliente()).orElse(null);
        return toQuery(r,
                cliente != null ? cliente.nombreCompleto() : null,
                cliente != null ? cliente.getCorreo() : null,
                fetchNombreSede(r.getIdSede()),
                fetchMedioPago(r.getVentaId()));
    }

    private ReservaPublicaQuery toQuery(ReservaPublica r, String nombreCliente,
                                        String correoCliente, String nombreSede,
                                        String medioPago) {
        return ReservaPublicaQuery.builder()
                .id(r.getId())
                .idCliente(r.getIdCliente())
                .nombreCliente(nombreCliente)
                .correoCliente(correoCliente)
                .idSede(r.getIdSede())
                .nombreSede(nombreSede)
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
                .ingresado(r.isIngresado())
                .fechaIngreso(r.getIngresoAt())
                .codigoQr(r.getCodigoQr())
                .medioPago(medioPago)
                .fechaCreacion(r.getCreatedAt())
                .build();
    }

    private String fetchMedioPago(Long idVenta) {
        if (idVenta == null) return null;
        var pagos = ventaPagoRepository.findByVentaId(idVenta);
        if (pagos.isEmpty()) return null;
        if (pagos.size() == 1) return pagos.get(0).getMedioPagoCodigo();
        return "MULTIPLE";
    }
}
