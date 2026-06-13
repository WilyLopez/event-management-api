package com.playzone.pems.infrastructure.persistence.usuario.mapper;

import com.playzone.pems.domain.usuario.model.Sede;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
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
                .fechaCreacion(e.getCreatedAt() != null ? e.getCreatedAt() : null)
                .deletedAt(e.getDeletedAt() != null ? e.getDeletedAt() : null)
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
                .build();
    }

}
