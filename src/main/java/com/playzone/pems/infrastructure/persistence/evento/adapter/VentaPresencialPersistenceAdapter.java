package com.playzone.pems.infrastructure.persistence.evento.adapter;

import com.playzone.pems.domain.evento.model.VentaPresencial;
import com.playzone.pems.domain.evento.repository.VentaPresencialRepository;
import com.playzone.pems.infrastructure.persistence.evento.entity.VentaPresencialEntity;
import com.playzone.pems.infrastructure.persistence.evento.jpa.VentaPresencialJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.ClienteJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VentaPresencialPersistenceAdapter implements VentaPresencialRepository {

    private final VentaPresencialJpaRepository ventaJpa;
    private final SedeJpaRepository            sedeJpa;
    private final ClienteJpaRepository         clienteJpa;
    private final UsuarioAdminJpaRepository    usuarioAdminJpa;

    @Override
    public Optional<VentaPresencial> findById(Long id) {
        return ventaJpa.findById(id).map(this::toDomain);
    }

    @Override
    public VentaPresencial save(VentaPresencial venta) {
        var sede = sedeJpa.findById(venta.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", venta.getIdSede()));
        var cliente = venta.getIdCliente() != null
                ? clienteJpa.findById(venta.getIdCliente()).orElse(null)
                : null;
        var usuarioRegistra = usuarioAdminJpa.findById(venta.getIdUsuarioRegistra())
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", venta.getIdUsuarioRegistra()));

        VentaPresencialEntity entity = VentaPresencialEntity.builder()
                .id(venta.getId())
                .sede(sede)
                .cliente(cliente)
                .fechaVisita(venta.getFechaVisita())
                .nombreAcompanante(venta.getNombreAcompanante())
                .dniAcompanante(venta.getDniAcompanante())
                .subtotal(venta.getSubtotal())
                .idPromocion(venta.getIdPromocion())
                .descuento(venta.getDescuento())
                .total(venta.getTotal())
                .efectivoRecibido(venta.getEfectivoRecibido())
                .vuelto(venta.getVuelto())
                .actaFirmada(venta.isActaFirmada())
                .esAnticipada(venta.isEsAnticipada())
                .usuarioRegistra(usuarioRegistra)
                .build();

        return toDomain(ventaJpa.save(entity));
    }

    private VentaPresencial toDomain(VentaPresencialEntity e) {
        return VentaPresencial.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .idCliente(e.getCliente() != null ? e.getCliente().getId() : null)
                .fechaVisita(e.getFechaVisita())
                .nombreAcompanante(e.getNombreAcompanante())
                .dniAcompanante(e.getDniAcompanante())
                .subtotal(e.getSubtotal())
                .idPromocion(e.getIdPromocion())
                .descuento(e.getDescuento())
                .total(e.getTotal())
                .efectivoRecibido(e.getEfectivoRecibido())
                .vuelto(e.getVuelto())
                .actaFirmada(e.isActaFirmada())
                .esAnticipada(e.isEsAnticipada())
                .idUsuarioRegistra(e.getUsuarioRegistra().getId())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
