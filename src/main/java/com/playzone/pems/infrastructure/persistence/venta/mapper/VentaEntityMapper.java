package com.playzone.pems.infrastructure.persistence.venta.mapper;

import com.playzone.pems.domain.venta.model.DetalleVenta;
import com.playzone.pems.domain.venta.model.Venta;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.inventario.entity.ProductoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import com.playzone.pems.infrastructure.persistence.venta.entity.DetalleVentaEntity;
import com.playzone.pems.infrastructure.persistence.venta.entity.VentaEntity;
import org.springframework.stereotype.Component;

@Component
public class VentaEntityMapper {

    public Venta toDomain(VentaEntity e) {
        if (e == null) return null;
        return Venta.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .idUsuario(e.getUsuario().getId())
                .idReservaPublica(e.getReservaPublica() != null ? e.getReservaPublica().getId() : null)
                .idEventoPrivado(e.getEventoPrivado() != null ? e.getEventoPrivado().getId() : null)
                .subtotal(e.getSubtotal())
                .descuento(e.getDescuento())
                .total(e.getTotal())
                .fechaVenta(e.getFechaVenta())
                .build();
    }

    public VentaEntity toEntity(Venta d,
                                SedeEntity sede,
                                UsuarioAdminEntity usuario,
                                ReservaPublicaEntity reserva,
                                EventoPrivadoEntity evento) {
        if (d == null) return null;
        return VentaEntity.builder()
                .id(d.getId())
                .sede(sede)
                .usuario(usuario)
                .reservaPublica(reserva)
                .eventoPrivado(evento)
                .subtotal(d.getSubtotal())
                .descuento(d.getDescuento())
                .total(d.getTotal())
                .build();
    }

    public DetalleVenta toDomain(DetalleVentaEntity e) {
        if (e == null) return null;
        return DetalleVenta.builder()
                .id(e.getId())
                .idVenta(e.getVenta().getId())
                .idProducto(e.getProducto().getId())
                .cantidad(e.getCantidad())
                .precioUnitario(e.getPrecioUnitario())
                .subtotalLinea(e.getSubtotalLinea())
                .build();
    }

    public DetalleVentaEntity toEntity(DetalleVenta d, VentaEntity venta, ProductoEntity producto) {
        if (d == null) return null;
        return DetalleVentaEntity.builder()
                .id(d.getId())
                .venta(venta)
                .producto(producto)
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .subtotalLinea(d.getSubtotalLinea())
                .build();
    }
}