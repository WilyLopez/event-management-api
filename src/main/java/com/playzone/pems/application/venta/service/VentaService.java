package com.playzone.pems.application.venta.service;

import com.playzone.pems.application.venta.dto.command.ProcesarVentaCommand;
import com.playzone.pems.application.venta.dto.query.VentaQuery;
import com.playzone.pems.application.venta.port.in.ConsultarVentasUseCase;
import com.playzone.pems.application.venta.port.in.ProcesarVentaUseCase;
import com.playzone.pems.domain.venta.exception.VentaNotFoundException;
import com.playzone.pems.domain.venta.model.Venta;
import com.playzone.pems.domain.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class VentaService implements ProcesarVentaUseCase, ConsultarVentasUseCase {

    private final VentaRepository ventaRepository;

    @Override
    @Transactional
    public VentaQuery ejecutar(ProcesarVentaCommand command) {
        BigDecimal subtotal = command.getLineas().stream()
                .map(l -> l.getPrecioUnitario().multiply(BigDecimal.valueOf(l.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = command.getDescuento() != null ? command.getDescuento() : BigDecimal.ZERO;
        BigDecimal total     = subtotal.subtract(descuento);

        Venta venta = Venta.builder()
                .idSede(command.getIdSede())
                .createdBy(command.getCreatedBy())
                .clienteId(command.getClienteId())
                .eventoId(command.getEventoId())
                .tipo(command.getTipo())
                .canalCodigo(command.getCanalCodigo())
                .fechaVisita(command.getFechaVisita())
                .nombreAcompanante(command.getNombreAcompanante())
                .dniAcompanante(command.getDniAcompanante())
                .telefonoAcompanante(command.getTelefonoAcompanante())
                .promocionId(command.getPromocionId())
                .efectivoRecibido(command.getEfectivoRecibido() != null ? command.getEfectivoRecibido() : BigDecimal.ZERO)
                .vuelto(command.getVuelto() != null ? command.getVuelto() : BigDecimal.ZERO)
                .actaFirmada(command.isActaFirmada())
                .esAnticipada(command.isEsAnticipada())
                .notas(command.getNotas())
                .subtotal(subtotal)
                .descuento(descuento)
                .total(total)
                .build();

        return toQuery(ventaRepository.save(venta));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaQuery consultarPorId(Long idVenta) {
        return toQuery(ventaRepository.findById(idVenta)
                .orElseThrow(() -> new VentaNotFoundException(idVenta)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaQuery> consultarPorSedeYFechas(
            Long idSede, LocalDate desde, LocalDate hasta, Pageable pageable) {
        OffsetDateTime inicio = desde.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime fin    = hasta.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);
        return ventaRepository.findBySedeAndFechasBetween(idSede, inicio, fin, pageable)
                .map(this::toQuery);
    }

    private VentaQuery toQuery(Venta v) {
        return VentaQuery.builder()
                .id(v.getId())
                .idSede(v.getIdSede())
                .clienteId(v.getClienteId())
                .eventoId(v.getEventoId())
                .tipo(v.getTipo())
                .canalCodigo(v.getCanalCodigo())
                .fechaVisita(v.getFechaVisita())
                .subtotal(v.getSubtotal())
                .descuento(v.getDescuento())
                .total(v.getTotal())
                .notas(v.getNotas())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
