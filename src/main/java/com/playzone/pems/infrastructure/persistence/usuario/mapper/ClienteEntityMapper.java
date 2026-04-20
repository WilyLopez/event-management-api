package com.playzone.pems.infrastructure.persistence.usuario.mapper;

import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteEntityMapper {

    public Cliente toDomain(ClienteEntity e) {
        if (e == null) return null;
        return Cliente.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .correo(e.getCorreo())
                .contrasenaHash(e.getContrasenaHash())
                .telefono(e.getTelefono())
                .dni(e.getDni())
                .ruc(e.getRuc())
                .razonSocial(e.getRazonSocial())
                .direccionFiscal(e.getDireccionFiscal())
                .esVip(e.isEsVip())
                .descuentoVip(e.getDescuentoVip())
                .contadorVisitas(e.getContadorVisitas())
                .correoVerificado(e.isCorreoVerificado())
                .tokenVerificacion(e.getTokenVerificacion())
                .activo(e.isActivo())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ClienteEntity toEntity(Cliente d) {
        if (d == null) return null;
        return ClienteEntity.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .correo(d.getCorreo())
                .contrasenaHash(d.getContrasenaHash())
                .telefono(d.getTelefono())
                .dni(d.getDni())
                .ruc(d.getRuc())
                .razonSocial(d.getRazonSocial())
                .direccionFiscal(d.getDireccionFiscal())
                .esVip(d.isEsVip())
                .descuentoVip(d.getDescuentoVip())
                .contadorVisitas(d.getContadorVisitas())
                .correoVerificado(d.isCorreoVerificado())
                .tokenVerificacion(d.getTokenVerificacion())
                .activo(d.isActivo())
                .build();
    }
}