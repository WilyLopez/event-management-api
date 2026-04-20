package com.playzone.pems.application.inventario.service;

import com.playzone.pems.application.inventario.dto.command.MovimientoInventarioCommand;
import com.playzone.pems.application.inventario.dto.query.AlertaStockQuery;
import com.playzone.pems.application.inventario.dto.query.ProductoQuery;
import com.playzone.pems.application.inventario.port.in.AjustarStockUseCase;
import com.playzone.pems.application.inventario.port.in.ConsultarAlertasStockUseCase;
import com.playzone.pems.application.inventario.port.in.RegistrarEntradaStockUseCase;
import com.playzone.pems.application.inventario.port.in.RegistrarSalidaStockUseCase;
import com.playzone.pems.domain.inventario.exception.ProductoNotFoundException;
import com.playzone.pems.domain.inventario.exception.StockInsuficienteException;
import com.playzone.pems.domain.inventario.model.MovimientoInventario;
import com.playzone.pems.domain.inventario.model.Producto;
import com.playzone.pems.domain.inventario.model.enums.TipoMovimiento;
import com.playzone.pems.domain.inventario.repository.MovimientoInventarioRepository;
import com.playzone.pems.domain.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService
        implements RegistrarEntradaStockUseCase,
        RegistrarSalidaStockUseCase,
        AjustarStockUseCase,
        ConsultarAlertasStockUseCase {

    private final ProductoRepository            productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    @Override
    @Transactional
    public ProductoQuery ejecutar(MovimientoInventarioCommand command) {
        Producto producto = productoRepository.findById(command.getIdProducto())
                .orElseThrow(() -> new ProductoNotFoundException(command.getIdProducto()));

        TipoMovimiento tipo = command.getTipoMovimiento();

        if (tipo.decrementaStock() && !producto.tieneStockSuficiente(command.getCantidad())) {
            throw new StockInsuficienteException(
                    producto.getNombre(), producto.getStockActual(), command.getCantidad());
        }

        int stockResultante = tipo.calcularStockResultante(
                producto.getStockActual(), command.getCantidad());

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .idProducto(command.getIdProducto())
                .tipoMovimiento(tipo)
                .cantidad(command.getCantidad())
                .stockAnterior(producto.getStockActual())
                .stockResultante(stockResultante)
                .motivo(command.getMotivo())
                .idVenta(command.getIdVenta())
                .idUsuario(command.getIdUsuario())
                .build();

        movimientoRepository.save(movimiento);

        Producto actualizado = productoRepository.findById(command.getIdProducto())
                .orElseThrow(() -> new ProductoNotFoundException(command.getIdProducto()));

        return toQuery(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaStockQuery> ejecutar(Long idSede) {
        return productoRepository.findEnAlertaDeStock(idSede)
                .stream()
                .map(p -> AlertaStockQuery.builder()
                        .idProducto(p.getId())
                        .idSede(p.getIdSede())
                        .nombre(p.getNombre())
                        .stockActual(p.getStockActual())
                        .stockMinimo(p.getStockMinimo())
                        .unidadesParaReponer(p.unidadesParaReponer())
                        .fechaActualizacion(p.getFechaActualizacion())
                        .build())
                .toList();
    }

    private ProductoQuery toQuery(Producto p) {
        return ProductoQuery.builder()
                .id(p.getId())
                .idSede(p.getIdSede())
                .idCategoria(p.getIdCategoria())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .stockActual(p.getStockActual())
                .stockMinimo(p.getStockMinimo())
                .unidadMedida(p.getUnidadMedida())
                .activo(p.isActivo())
                .enAlertaDeStock(p.estaEnAlertaDeStock())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }
}