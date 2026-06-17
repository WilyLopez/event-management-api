package com.playzone.pems.application.venta.service;

import com.playzone.pems.application.venta.dto.command.NinoMostradorCommand;
import com.playzone.pems.application.venta.dto.command.PagoMostradorCommand;
import com.playzone.pems.application.venta.dto.command.RegistrarVentaMostradorCommand;
import com.playzone.pems.application.venta.dto.query.VentaMostradorQuery;
import com.playzone.pems.domain.calendario.model.Tarifa;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.calendario.repository.TarifaRepository;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.CanalReserva;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.finanzas.repository.AperturaCajaRepository;
import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.domain.promocion.repository.PromocionRepository;
import com.playzone.pems.domain.venta.model.Venta;
import com.playzone.pems.domain.venta.model.VentaPago;
import com.playzone.pems.domain.venta.repository.VentaPagoRepository;
import com.playzone.pems.domain.venta.repository.VentaRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VentaMostradorService {

    private static final Long ID_CLIENTE_ANONIMO = 1L;

    private final VentaRepository          ventaRepository;
    private final ReservaPublicaRepository reservaRepository;
    private final VentaPagoRepository      ventaPagoRepository;
    private final AperturaCajaRepository   aperturaCajaRepository;
    private final TarifaRepository         tarifaRepository;
    private final FeriadoRepository        feriadoRepository;
    private final PromocionRepository      promocionRepository;
    private final SupabaseAuthFacade       authFacade;

    @Transactional
    public VentaMostradorQuery registrar(RegistrarVentaMostradorCommand cmd) {

        aperturaCajaRepository.findActivaBySede(cmd.getSedeId())
                .orElseThrow(() -> new ValidationException(
                        "No hay caja abierta para esta sede. Abrir caja antes de registrar ventas."));

        boolean esFeriado = feriadoRepository.existsByFecha(cmd.getFechaVisita());
        TipoDia tipoDia = (FechaUtil.esFindeSemana(cmd.getFechaVisita()) || esFeriado)
                ? TipoDia.FIN_SEMANA_FERIADO
                : TipoDia.SEMANA;

        Tarifa tarifa = tarifaRepository
                .findVigenteBySedeAndTipoDiaAndFecha(cmd.getSedeId(), tipoDia, cmd.getFechaVisita())
                .orElseThrow(() -> new ValidationException(
                        "No existe tarifa vigente para la fecha indicada."));

        BigDecimal precioBase    = tarifa.getPrecio();
        int        cantidadNinos = cmd.getNinos().size();
        BigDecimal subtotal      = precioBase.multiply(BigDecimal.valueOf(cantidadNinos));

        BigDecimal descuento = BigDecimal.ZERO;
        if (cmd.getIdPromocion() != null) {
            Promocion promo = promocionRepository.findById(cmd.getIdPromocion())
                    .orElseThrow(() -> new ValidationException("Promocion no valida o expirada."));
            if (!promo.estaVigenteEn(cmd.getFechaVisita())) {
                throw new ValidationException("La promocion no esta vigente para esa fecha.");
            }
            if (!promo.cumpleMinimoPersonas(cantidadNinos)) {
                throw new ValidationException(
                        "La promocion requiere al menos " + promo.getMinimoPersonas() + " personas.");
            }
            descuento = promo.calcularDescuento(subtotal);
        }

        BigDecimal total = subtotal.subtract(descuento).max(BigDecimal.ZERO);

        BigDecimal sumaPagos = cmd.getPagos().stream()
                .map(PagoMostradorCommand::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumaPagos.compareTo(total) != 0) {
            throw new ValidationException(
                    "La suma de pagos (" + sumaPagos + ") no coincide con el total (" + total + ").");
        }

        BigDecimal efectivoEnPagos = cmd.getPagos().stream()
                .filter(p -> "EFECTIVO".equals(p.getMedioPago()))
                .map(PagoMostradorCommand::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal efectivoRecibido = cmd.getEfectivoRecibido() != null
                ? cmd.getEfectivoRecibido() : BigDecimal.ZERO;

        if (efectivoEnPagos.compareTo(BigDecimal.ZERO) > 0
                && efectivoRecibido.compareTo(efectivoEnPagos) < 0) {
            throw new ValidationException("Efectivo recibido insuficiente.");
        }

        BigDecimal vuelto = efectivoRecibido.subtract(efectivoEnPagos).max(BigDecimal.ZERO);

        UUID usuarioActual = authFacade.usuarioActualId()
                .orElseThrow(() -> new ValidationException(
                        "Sesion requerida para registrar venta en mostrador."));

        Long clienteEfectivo = cmd.getClienteId() != null ? cmd.getClienteId() : ID_CLIENTE_ANONIMO;

        Venta ventaGuardada = ventaRepository.save(Venta.builder()
                .idSede(cmd.getSedeId())
                .clienteId(clienteEfectivo)
                .tipo("RESERVA")
                .canalCodigo("MOSTRADOR")
                .fechaVisita(cmd.getFechaVisita())
                .nombreAcompanante(cmd.getNombreAcompanante())
                .dniAcompanante(cmd.getDniAcompanante())
                .telefonoAcompanante(cmd.getTelefonoAcompanante())
                .promocionId(cmd.getIdPromocion())
                .subtotal(subtotal)
                .descuento(descuento)
                .total(total)
                .efectivoRecibido(efectivoRecibido)
                .vuelto(vuelto)
                .actaFirmada(cmd.isActaFirmada())
                .esAnticipada(cmd.getFechaVisita().isAfter(LocalDate.now()))
                .notas(cmd.getNotas())
                .createdBy(usuarioActual)
                .build());

        BigDecimal descuentoPorNino = cantidadNinos > 0
                ? descuento.divide(BigDecimal.valueOf(cantidadNinos), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal totalPorNino = precioBase.subtract(descuentoPorNino).max(BigDecimal.ZERO);

        List<ReservaPublica> reservas = new ArrayList<>();
        for (NinoMostradorCommand nino : cmd.getNinos()) {
            ReservaPublica reserva = reservaRepository.save(ReservaPublica.builder()
                    .ventaId(ventaGuardada.getId())
                    .idSede(cmd.getSedeId())
                    .idCliente(clienteEfectivo)
                    .canalReserva(CanalReserva.MOSTRADOR)
                    .tipoDia(tipoDia)
                    .fechaEvento(cmd.getFechaVisita())
                    .estado(EstadoReservaPublica.CONFIRMADA)
                    .precioHistorico(precioBase)
                    .descuentoAplicado(descuentoPorNino)
                    .totalPagado(totalPorNino)
                    .nombreNino(nino.getNombreNino())
                    .edadNino(nino.getEdadNino())
                    .nombreAcompanante(cmd.getNombreAcompanante())
                    .dniAcompanante(cmd.getDniAcompanante())
                    .firmoConsentimiento(cmd.isActaFirmada())
                    .ingresado(false)
                    .esReprogramacion(false)
                    .vecesReprogramada(0)
                    .createdBy(usuarioActual)
                    .build());
            reservas.add(reserva);
        }

        List<VentaPago> pagosGuardados = new ArrayList<>();
        for (PagoMostradorCommand pagoCmd : cmd.getPagos()) {
            VentaPago pago = ventaPagoRepository.save(VentaPago.builder()
                    .ventaId(ventaGuardada.getId())
                    .medioPagoCodigo(pagoCmd.getMedioPago())
                    .monto(pagoCmd.getMonto())
                    .referencia(pagoCmd.getReferencia())
                    .esValidado(true)
                    .validadoPor(usuarioActual)
                    .validadoAt(OffsetDateTime.now())
                    .build());
            pagosGuardados.add(pago);
        }

        List<VentaMostradorQuery.TicketMostradorQuery> tickets = reservas.stream()
                .map(r -> VentaMostradorQuery.TicketMostradorQuery.builder()
                        .reservaId(r.getId())
                        .numeroTicket(r.getNumeroTicket())
                        .codigoQr(r.getCodigoQr())
                        .nombreNino(r.getNombreNino())
                        .edadNino(r.getEdadNino())
                        .build())
                .toList();

        List<VentaMostradorQuery.PagoMostradorResultQuery> pagoResults = pagosGuardados.stream()
                .map(p -> VentaMostradorQuery.PagoMostradorResultQuery.builder()
                        .pagoId(p.getId())
                        .medioPago(p.getMedioPagoCodigo())
                        .monto(p.getMonto())
                        .build())
                .toList();

        return VentaMostradorQuery.builder()
                .ventaId(ventaGuardada.getId())
                .sedeId(ventaGuardada.getIdSede())
                .fechaVisita(ventaGuardada.getFechaVisita())
                .subtotal(ventaGuardada.getSubtotal())
                .descuento(ventaGuardada.getDescuento())
                .total(ventaGuardada.getTotal())
                .efectivoRecibido(ventaGuardada.getEfectivoRecibido())
                .vuelto(ventaGuardada.getVuelto())
                .createdAt(ventaGuardada.getCreatedAt())
                .tickets(tickets)
                .pagos(pagoResults)
                .build();
    }
}
