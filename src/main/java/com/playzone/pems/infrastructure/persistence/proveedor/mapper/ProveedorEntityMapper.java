package com.playzone.pems.infrastructure.persistence.proveedor.mapper;

import com.playzone.pems.domain.proveedor.model.Proveedor;
import com.playzone.pems.infrastructure.persistence.proveedor.entity.ProveedorEntity;
import org.springframework.stereotype.Component;

@Component
public class ProveedorEntityMapper {

    public Proveedor toDomain(ProveedorEntity e) {
        if (e == null) return null;
        return Proveedor.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .ruc(e.getRuc())
                .contactoNombre(e.getContactoNombre())
                .contactoTelefono(e.getContactoTelefono())
                .contactoCorreo(e.getContactoCorreo())
                .tipoServicio(e.getTipoServicio())
                .notas(e.getNotas())
                .activo(e.isActivo())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ProveedorEntity toEntity(Proveedor d) {
        if (d == null) return null;
        return ProveedorEntity.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .ruc(d.getRuc())
                .contactoNombre(d.getContactoNombre())
                .contactoTelefono(d.getContactoTelefono())
                .contactoCorreo(d.getContactoCorreo())
                .tipoServicio(d.getTipoServicio())
                .notas(d.getNotas())
                .activo(d.isActivo())
                .build();
    }
}