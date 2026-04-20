package com.playzone.pems.infrastructure.persistence.inventario.mapper;

import com.playzone.pems.domain.inventario.model.CategoriaProducto;
import com.playzone.pems.domain.inventario.model.MovimientoInventario;
import com.playzone.pems.domain.inventario.model.Producto;
import com.playzone.pems.infrastructure.persistence.inventario.entity.CategoriaProductoEntity;
import com.playzone.pems.infrastructure.persistence.inventario.entity.MovimientoInventarioEntity;
import com.playzone.pems.infrastructure.persistence.inventario.entity.ProductoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductoEntityMapper {

    public Producto toDomain(ProductoEntity e) {
        if (e == null) return null;
        return Producto.builder()
                .id(e.getId())
                .idCategoria(e.getCategoria().getId())
                .idSede(e.getSede().getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .precio(e.getPrecio())
                .stockActual(e.getStockActual())
                .stockMinimo(e.getStockMinimo())
                .unidadMedida(e.getUnidadMedida())
                .activo(e.isActivo())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ProductoEntity toEntity(Producto d, CategoriaProductoEntity categoria, SedeEntity sede) {
        if (d == null) return null;
        return ProductoEntity.builder()
                .id(d.getId())
                .categoria(categoria)
                .sede(sede)
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .precio(d.getPrecio())
                .stockActual(d.getStockActual())
                .stockMinimo(d.getStockMinimo())
                .unidadMedida(d.getUnidadMedida())
                .activo(d.isActivo())
                .build();
    }

    public CategoriaProducto toDomain(CategoriaProductoEntity e) {
        if (e == null) return null;
        return CategoriaProducto.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .activo(e.isActivo())
                .build();
    }

    public MovimientoInventario toDomain(MovimientoInventarioEntity e) {
        if (e == null) return null;
        return MovimientoInventario.builder()
                .id(e.getId())
                .idProducto(e.getProducto().getId())
                .tipoMovimiento(e.getTipoMovimiento())
                .cantidad(e.getCantidad())
                .stockAnterior(e.getStockAnterior())
                .stockResultante(e.getStockResultante())
                .motivo(e.getMotivo())
                .idVenta(e.getIdVenta())
                .idUsuario(e.getUsuario() != null ? e.getUsuario().getId() : null)
                .fechaMovimiento(e.getFechaMovimiento())
                .build();
    }

    public MovimientoInventarioEntity toEntity(MovimientoInventario d,
                                               ProductoEntity producto,
                                               UsuarioAdminEntity usuario) {
        if (d == null) return null;
        return MovimientoInventarioEntity.builder()
                .id(d.getId())
                .producto(producto)
                .tipoMovimiento(d.getTipoMovimiento())
                .cantidad(d.getCantidad())
                .stockAnterior(d.getStockAnterior())
                .stockResultante(d.getStockResultante())
                .motivo(d.getMotivo())
                .idVenta(d.getIdVenta())
                .usuario(usuario)
                .build();
    }
}