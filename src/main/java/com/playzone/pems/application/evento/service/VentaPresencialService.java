package com.playzone.pems.application.evento.service;

import com.playzone.pems.application.evento.dto.command.CalcularVentaCommand;
import com.playzone.pems.application.evento.dto.command.NinoVentaCommand;
import com.playzone.pems.application.evento.dto.command.PagoLineaCommand;
import com.playzone.pems.application.evento.dto.command.RegistrarVentaPresencialCommand;
import com.playzone.pems.application.evento.dto.query.ResumenVentaQuery;
import com.playzone.pems.application.evento.dto.query.TicketResumenQuery;
import com.playzone.pems.application.evento.dto.query.VentaPresencialQuery;
import com.playzone.pems.application.finanzas.port.in.RegistrarIngresoUseCase;
import com.playzone.pems.domain.calendario.exception.AforoExcedidoException;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.model.Tarifa;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.DisponibilidadDiariaRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.calendario.repository.TarifaRepository;
import com.playzone.pems.domain.evento.exception.ReservaNotFoundException;
import com.playzone.pems.domain.evento.model.PagoVenta;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.VentaPresencial;
import com.playzone.pems.domain.evento.model.enums.CanalReserva;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.PagoVentaRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.evento.repository.VentaPresencialRepository;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.domain.promocion.repository.PromocionRepository;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import com.playzone.pems.shared.util.TicketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaPresencialService {

    private static final Long ID_CLIENTE_MOSTRADOR = 1L;

    private final VentaPresencialRepository       ventaRepository;
    private final PagoVentaRepository             pagoVentaRepository;
    private final ReservaPublicaRepository        reservaRepository;
    private final EventoPrivadoRepository         eventoRepository;
    private final TarifaRepository                tarifaRepository;
    private final FeriadoRepository               feriadoRepository;
    private final BloqueCalendarioRepository      bloqueRepository;
    private final DisponibilidadDiariaRepository  disponibilidadRepository;
    private final ConfiguracionCalendarioRepository configRepository;
    private final PromocionRepository             promocionRepository;
    private final RegistrarIngresoUseCase         ingresoService;

    @Transactional(readOnly = true)
    public ResumenVentaQuery calcular(CalcularVentaCommand cmd) {
        TipoDia tipoDia = resolverTipoDia(cmd.getFechaVisita());
        Tarifa tarifa = tarifaRepository
                .findVigenteBySedeAndTipoDiaAndFecha(cmd.getIdSede(), tipoDia, cmd.getFechaVisita())
                .orElseThrow(() -> new ValidationException("No existe tarifa vigente para esa fecha."));

        int cantidad = cmd.getNinos() == null ? 0 : cmd.getNinos().size();
        BigDecimal precioUnitario = tarifa.getPrecio();
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        BigDecimal descuento = BigDecimal.ZERO;
        if (cmd.getIdPromocion() != null) {
            Promocion promo = promocionRepository.findById(cmd.getIdPromocion())
                    .orElseThrow(() -> new ValidationException("Promocion no valida o expirada."));
            if (!promo.estaVigenteEn(cmd.getFechaVisita())) {
                throw new ValidationException("La promocion no esta vigente para esa fecha.");
            }
            if (!promo.cumpleMinimoPersonas(cantidad)) {
                throw new ValidationException(
                        "La promocion requiere al menos " + promo.getMinimoPersonas() + " personas.");
            }
            descuento = promo.calcularDescuento(subtotal);
        }

        BigDecimal total = subtotal.subtract(descuento).max(BigDecimal.ZERO);

        ConfiguracionCalendario cfg = configRepository.obtener(cmd.getIdSede());
        int usados = reservaRepository.countActivasBySedeAndFecha(cmd.getIdSede(), cmd.getFechaVisita());
        int aforoMax = cfg.getAforoMaximo();
        int aforoDisponible = Math.max(0, aforoMax - usados);

        return ResumenVentaQuery.builder()
                .precioUnitario(precioUnitario)
                .cantidadNinos(cantidad)
                .subtotal(subtotal)
                .descuento(descuento)
                .total(total)
                .tipoDia(tipoDia)
                .aforoDisponible(aforoDisponible)
                .aforoMaximo(aforoMax)
                .build();
    }

    @Transactional
    public VentaPresencialQuery registrar(RegistrarVentaPresencialCommand cmd, Long idUsuarioAdmin) {
        ResumenVentaQuery resumen = calcular(CalcularVentaCommand.builder()
                .idSede(cmd.getIdSede())
                .fechaVisita(cmd.getFechaVisita())
                .ninos(cmd.getNinos())
                .idPromocion(cmd.getIdPromocion())
                .build());

        BigDecimal sumaPagos = cmd.getPagos().stream()
                .map(PagoLineaCommand::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumaPagos.compareTo(resumen.getTotal()) != 0) {
            throw new ValidationException("La suma de los pagos no coincide con el total.");
        }

        if (!cmd.isActaFirmada()) {
            throw new ValidationException("Debe confirmar la firma del Acta de Responsabilidad.");
        }

        ConfiguracionCalendario cfg = configRepository.obtener(cmd.getIdSede());
        int usados = reservaRepository.countActivasBySedeAndFecha(cmd.getIdSede(), cmd.getFechaVisita());
        if (usados + cmd.getNinos().size() > cfg.getAforoMaximo()) {
            int restante = cfg.getAforoMaximo() - usados;
            throw new AforoExcedidoException(cmd.getFechaVisita(), cfg.getAforoMaximo());
        }

        validarFechaVenta(cmd.getIdSede(), cmd.getFechaVisita(), cfg);

        if (eventoRepository.existsActivoBySedeAndFecha(cmd.getIdSede(), cmd.getFechaVisita())) {
            throw new ValidationException("Esta fecha esta reservada para un evento privado.");
        }

        BigDecimal efectivoAplicado = cmd.getPagos().stream()
                .filter(p -> "EFECTIVO".equals(p.getMetodo()))
                .map(PagoLineaCommand::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal efectivoRecibido = cmd.getEfectivoRecibido() != null
                ? cmd.getEfectivoRecibido() : BigDecimal.ZERO;
        BigDecimal vuelto = efectivoRecibido.subtract(efectivoAplicado).max(BigDecimal.ZERO);

        boolean esAnticipada = cmd.getFechaVisita().isAfter(FechaUtil.hoy());

        Long idClienteEfectivo = cmd.getIdCliente() != null ? cmd.getIdCliente() : ID_CLIENTE_MOSTRADOR;

        VentaPresencial venta = ventaRepository.save(VentaPresencial.builder()
                .idSede(cmd.getIdSede())
                .idCliente(idClienteEfectivo)
                .fechaVisita(cmd.getFechaVisita())
                .nombreAcompanante(cmd.getNombreAcompanante())
                .dniAcompanante(cmd.getDniAcompanante())
                .subtotal(resumen.getSubtotal())
                .idPromocion(cmd.getIdPromocion())
                .descuento(resumen.getDescuento())
                .total(resumen.getTotal())
                .efectivoRecibido(efectivoRecibido)
                .vuelto(vuelto)
                .actaFirmada(true)
                .esAnticipada(esAnticipada)
                .idUsuarioRegistra(idUsuarioAdmin)
                .build());

        TipoDia tipoDia = resumen.getTipoDia();
        List<ReservaPublica> reservas = new ArrayList<>();

        for (NinoVentaCommand nino : cmd.getNinos()) {
            long secuencia = reservaRepository.countActivasBySedeAndFecha(
                    cmd.getIdSede(), cmd.getFechaVisita()) + 1L;
            String ticket = TicketUtil.generar(cmd.getIdSede().intValue(), cmd.getFechaVisita(), secuencia);
            while (reservaRepository.existsByNumeroTicket(ticket)) {
                secuencia++;
                ticket = TicketUtil.generar(cmd.getIdSede().intValue(), cmd.getFechaVisita(), secuencia);
            }

            ReservaPublica r = reservaRepository.save(ReservaPublica.builder()
                    .idVenta(venta.getId())
                    .idCliente(idClienteEfectivo)
                    .idSede(cmd.getIdSede())
                    .estado(EstadoReservaPublica.CONFIRMADA)
                    .canalReserva(CanalReserva.PRESENCIAL)
                    .tipoDia(tipoDia)
                    .fechaEvento(cmd.getFechaVisita())
                    .numeroTicket(ticket)
                    .precioHistorico(resumen.getPrecioUnitario())
                    .descuentoAplicado(BigDecimal.ZERO)
                    .totalPagado(resumen.getPrecioUnitario())
                    .nombreNino(nino.getNombre())
                    .edadNino(nino.getEdad())
                    .nombreAcompanante(cmd.getNombreAcompanante())
                    .dniAcompanante(cmd.getDniAcompanante())
                    .firmoConsentimiento(true)
                    .esReprogramacion(false)
                    .vecesReprogramada(0)
                    .ingresado(false)
                    .build());

            disponibilidadRepository.incrementarAforo(cmd.getIdSede(), cmd.getFechaVisita());
            reservas.add(r);
        }

        for (PagoLineaCommand pago : cmd.getPagos()) {
            pagoVentaRepository.save(PagoVenta.builder()
                    .idVenta(venta.getId())
                    .metodo(pago.getMetodo())
                    .monto(pago.getMonto())
                    .build());
        }

        for (PagoLineaCommand pago : cmd.getPagos()) {
            ingresoService.registrarAutomatico(
                    CategoriaIngreso.RESERVA_PUBLICA,
                    cmd.getIdSede(),
                    reservas.get(0).getId(),
                    null,
                    pago.getMonto(),
                    cmd.getFechaVisita(),
                    pago.getMetodo());
        }

        List<TicketResumenQuery> ticketResumenes = reservas.stream()
                .map(r -> TicketResumenQuery.builder()
                        .idReserva(r.getId())
                        .numeroTicket(r.getNumeroTicket())
                        .nombreNino(r.getNombreNino())
                        .edadNino(r.getEdadNino())
                        .codigoQr(r.getCodigoQr())
                        .build())
                .toList();

        return VentaPresencialQuery.builder()
                .idVenta(venta.getId())
                .idSede(venta.getIdSede())
                .idCliente(venta.getIdCliente())
                .fechaVisita(venta.getFechaVisita())
                .nombreAcompanante(venta.getNombreAcompanante())
                .dniAcompanante(venta.getDniAcompanante())
                .subtotal(venta.getSubtotal())
                .idPromocion(venta.getIdPromocion())
                .descuento(venta.getDescuento())
                .total(venta.getTotal())
                .efectivoRecibido(venta.getEfectivoRecibido())
                .vuelto(venta.getVuelto())
                .esAnticipada(venta.isEsAnticipada())
                .fechaCreacion(venta.getFechaCreacion())
                .tickets(ticketResumenes)
                .build();
    }

    private void validarFechaVenta(Long idSede, LocalDate fecha, ConfiguracionCalendario cfg) {
        LocalDate hoy = FechaUtil.hoy();
        LocalDate max = hoy.plusDays(cfg.getDiasMaxReservaPublica());

        if (fecha.isBefore(hoy)) {
            throw new ValidationException("No se puede registrar una venta para fechas pasadas.");
        }
        if (fecha.isAfter(max)) {
            throw new ValidationException(
                    "Solo se permiten ventas hasta " + cfg.getDiasMaxReservaPublica() + " dias de anticipacion.");
        }
        if (fecha.isEqual(hoy) && FechaUtil.ahora().toLocalTime().isAfter(cfg.getHoraCierre())) {
            throw new ValidationException("El local ya cerro por hoy.");
        }
        if (feriadoRepository.existsByFecha(fecha)) {
            throw new ValidationException("Esta fecha es feriado.");
        }
        if (bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha)) {
            throw new ValidationException("La fecha esta bloqueada.");
        }
    }

    private TipoDia resolverTipoDia(LocalDate fecha) {
        boolean esFeriado = feriadoRepository.findByFecha(fecha).isPresent();
        return (FechaUtil.esFindeSemana(fecha) || esFeriado)
                ? TipoDia.FIN_SEMANA_FERIADO
                : TipoDia.SEMANA;
    }
}
