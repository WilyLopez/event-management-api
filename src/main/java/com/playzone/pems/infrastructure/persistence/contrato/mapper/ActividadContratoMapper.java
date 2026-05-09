package com.playzone.pems.infrastructure.persistence.contrato.mapper;

import com.playzone.pems.domain.contrato.model.ActividadContrato;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ActividadContratoEntity;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class ActividadContratoMapper {

    public ActividadContrato toDomain(ActividadContratoEntity e) {
        return ActividadContrato.builder()
                .id(e.getId())
                .idContrato(e.getContrato().getId())
                .accion(e.getAccion())
                .descripcion(e.getDescripcion())
                .idUsuario(e.getUsuario() != null ? e.getUsuario().getId() : null)
                .nombreUsuario(e.getUsuario() != null ? e.getUsuario().getNombre() : "Sistema")
                .fechaAccion(e.getFechaAccion())
                .build();
    }

    public ActividadContratoEntity toEntity(
            ActividadContrato a,
            ContratoEntity contrato,
            UsuarioAdminEntity usuario) {
        return ActividadContratoEntity.builder()
                .contrato(contrato)
                .accion(a.getAccion())
                .descripcion(a.getDescripcion())
                .usuario(usuario)
                .build();
    }
}