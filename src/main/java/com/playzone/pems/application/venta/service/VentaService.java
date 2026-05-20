package com.playzone.pems.application.venta.service;

import com.playzone.pems.application.venta.dto.command.ProcesarVentaCommand;
import com.playzone.pems.application.venta.dto.query.VentaQuery;
import com.playzone.pems.application.venta.port.in.ConsultarVentasUseCase;
import com.playzone.pems.application.venta.port.in.ProcesarVentaUseCase;
import com.playzone.pems.domain.venta.exception.VentaNotFoundException;
import com.playzone.pems.domain.venta.model.DetalleVenta;
import com.playzone.pems.domain.venta.model.Venta;
import com.playzone.pems.domain.venta.repository.DetalleVentaRepository;
import com.playzone.pems.domain.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService implements ProcesarVentaUseCase, ConsultarVentasUseCase {

    private final VentaRepository        ventaRepository;
    private final DetalleVentaRepository detalleRepository;

    @Override
    @Transactional
    public VentaQuery ejecutar(ProcesarVentaCommand command) {
        BigDecimal subtotal = BigDecimal.ZERO;

        List<DetalleVenta> detalles = command.getLineas().stream()
                .map((ProcesarVentaCommand.LineaVentaCommand linea) -> {
                    BigDecimal subtotalLinea = linea.getPrecioUnitario()
                            .multiply(BigDecimal.valueOf(linea.getCantidad()));
                    return DetalleVenta.builder()
                            .idProducto(linea.getIdProducto())
                            .cantidad(linea.getCantidad())
                            .precioUnitario(linea.getPrecioUnitario())
                            .subtotalLinea(subtotalLinea)
                            .build();
                })
                .toList();

        for (DetalleVenta d : detalles) {
            subtotal = subtotal.add(d.getSubtotalLinea());
        }

        BigDecimal descuento = command.getDescuento() != null ? command.getDescuento() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(descuento);

        Venta venta = Venta.builder()
                .idSede(command.getIdSede())
                .idUsuario(command.getIdUsuario())
                .idReservaPublica(command.getIdReservaPublica())
                .idEventoPrivado(command.getIdEventoPrivado())
                .subtotal(subtotal)
                .descuento(descuento)
                .total(total)
                .build();

        Venta guardada = ventaRepository.save(venta);

        List<DetalleVenta> detallesGuardados = detalles.stream()
                .map(d -> detalleRepository.save(d.toBuilder().idVenta(guardada.getId()).build()))
                .toList();

        return toQuery(guardada, detallesGuardados);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaQuery consultarPorId(Long idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new VentaNotFoundException(idVenta));
        List<DetalleVenta> detalles = detalleRepository.findByVenta(idVenta);
        return toQuery(venta, detalles);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaQuery> consultarPorSedeYFechas(
            Long idSede, LocalDate desde, LocalDate hasta, Pageable pageable) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin    = hasta.atTime(LocalTime.MAX);
        return ventaRepository.findBySedeAndFechasBetween(idSede, inicio, fin, pageable)
                .map(v -> toQuery(v, detalleRepository.findByVenta(v.getId())));
    }

    private VentaQuery toQuery(Venta v, List<DetalleVenta> detalles) {
        return VentaQuery.builder()
                .id(v.getId())
                .idSede(v.getIdSede())
                .idReservaPublica(v.getIdReservaPublica())
                .idEventoPrivado(v.getIdEventoPrivado())
                .subtotal(v.getSubtotal())
                .descuento(v.getDescuento())
                .total(v.getTotal())
                .fechaVenta(v.getFechaVenta())
                .detalles(detalles.stream()
                        .map(d -> VentaQuery.DetalleVentaQuery.builder()
                                .idProducto(d.getIdProducto())
                                .cantidad(d.getCantidad())
                                .precioUnitario(d.getPrecioUnitario())
                                .subtotalLinea(d.getSubtotalLinea())
                                .build())
                        .toList())
                .build();
    }
}
