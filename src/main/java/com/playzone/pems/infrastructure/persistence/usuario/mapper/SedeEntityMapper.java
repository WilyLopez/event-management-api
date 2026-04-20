package com.playzone.pems.infrastructure.persistence.usuario.mapper;

import com.playzone.pems.domain.usuario.model.Sede;
import com.playzone.pems.domain.usuario.model.UsuarioAdmin;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SedeEntityMapper {

    public Sede toDomain(SedeEntity e) {
        if (e == null) return null;
        return Sede.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .direccion(e.getDireccion())
                .ciudad(e.getCiudad())
                .departamento(e.getDepartamento())
                .telefono(e.getTelefono())
                .correo(e.getCorreo())
                .ruc(e.getRuc())
                .activo(e.isActivo())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public SedeEntity toEntity(Sede d) {
        if (d == null) return null;
        return SedeEntity.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .direccion(d.getDireccion())
                .ciudad(d.getCiudad())
                .departamento(d.getDepartamento())
                .telefono(d.getTelefono())
                .correo(d.getCorreo())
                .ruc(d.getRuc())
                .activo(d.isActivo())
                .build();
    }

    public UsuarioAdmin toDomain(UsuarioAdminEntity e) {
        if (e == null) return null;
        return UsuarioAdmin.builder()
                .id(e.getId())
                .idSede(e.getSede() != null ? e.getSede().getId() : null)
                .nombre(e.getNombre())
                .correo(e.getCorreo())
                .contrasenaHash(e.getContrasenaHash())
                .activo(e.isActivo())
                .intentosFallidos(e.getIntentosFallidos())
                .bloqueadoHasta(e.getBloqueadoHasta())
                .ultimoAcceso(e.getUltimoAcceso())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }
}