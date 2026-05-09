package com.playzone.pems.infrastructure.persistence.usuario.mapper;

import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.interfaces.rest.usuario.response.ClienteResponse;

import org.springframework.stereotype.Component;

@Component
public class ClienteEntityMapper {

    public ClienteResponse toResponse(Cliente c) {
        return ClienteResponse.builder()
            .id(c.getId())
            .nombre(c.getNombre())
            .correo(c.getCorreo())
            .telefono(c.getTelefono())
            .dni(c.getDni())
            .direccionFiscal(c.getDireccionFiscal())
            .ruc(c.getRuc())
            .razonSocial(c.getRazonSocial())
            .fotoPerfil(c.getFotoPerfil())
            .ultimoLogin(c.getUltimoLogin())
            .fechaNacimiento(c.getFechaNacimiento())
            .tipoCliente(c.getTipoCliente())
            .esVip(c.isEsVip())
            .descuentoVip(c.getDescuentoVip())
            .contadorVisitas(c.getContadorVisitas())
            .correoVerificado(c.isCorreoVerificado())
            .activo(c.isActivo())
            .fechaCreacion(c.getFechaCreacion())
            .build();
    }
    
    public Cliente toDomain(ClienteEntity e) {
        return Cliente.builder()
            .id(e.getId())
            .nombre(e.getNombre())
            .correo(e.getCorreo())
            .contrasenaHash(e.getContrasenaHash())
            .telefono(e.getTelefono())
            .dni(e.getDni())
            .direccionFiscal(e.getDireccionFiscal())
            .ruc(e.getRuc())
            .razonSocial(e.getRazonSocial())
            .fotoPerfil(e.getFotoPerfil())
            .ultimoLogin(e.getUltimoLogin())
            .fechaNacimiento(e.getFechaNacimiento())
            .tipoCliente(e.getTipoCliente())
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
    
    public ClienteEntity toEntity(Cliente c) {
        return ClienteEntity.builder()
            .id(c.getId())
            .nombre(c.getNombre())
            .correo(c.getCorreo())
            .contrasenaHash(c.getContrasenaHash())
            .telefono(c.getTelefono())
            .dni(c.getDni())
            .direccionFiscal(c.getDireccionFiscal())
            .ruc(c.getRuc())
            .razonSocial(c.getRazonSocial())
            .fotoPerfil(c.getFotoPerfil())
            .ultimoLogin(c.getUltimoLogin())
            .fechaNacimiento(c.getFechaNacimiento())
            .tipoCliente(c.getTipoCliente())
            .esVip(c.isEsVip())
            .descuentoVip(c.getDescuentoVip())
            .contadorVisitas(c.getContadorVisitas())
            .correoVerificado(c.isCorreoVerificado())
            .tokenVerificacion(c.getTokenVerificacion())
            .activo(c.isActivo())
            .build();
    }
}