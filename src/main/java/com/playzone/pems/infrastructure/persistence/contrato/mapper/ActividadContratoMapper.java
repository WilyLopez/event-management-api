package com.playzone.pems.infrastructure.persistence.contrato.mapper;

import com.playzone.pems.domain.contrato.model.ActividadContrato;
import com.playzone.pems.domain.usuario.repository.PerfilUsuarioRepository;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ActividadContratoEntity;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActividadContratoMapper {

    private final PerfilUsuarioRepository perfilUsuarioRepository;

    public ActividadContrato toDomain(ActividadContratoEntity e) {
        String nombreUsuario = e.getUsuarioId() != null
                ? perfilUsuarioRepository.buscarPorId(e.getUsuarioId())
                        .map(u -> u.getNombreCompleto()).orElse(null)
                : null;
        return ActividadContrato.builder()
                .id(e.getId())
                .idContrato(e.getContrato().getId())
                .accion(e.getAccion())
                .descripcion(e.getDescripcion())
                .idUsuario(e.getUsuarioId())
                .nombreUsuario(nombreUsuario)
                .fechaAccion(e.getFechaAccion() != null ? e.getFechaAccion() : null)
                .build();
    }

    public ActividadContratoEntity toEntity(ActividadContrato a, ContratoEntity contrato) {
        return ActividadContratoEntity.builder()
                .contrato(contrato)
                .accion(a.getAccion())
                .descripcion(a.getDescripcion())
                .usuarioId(a.getIdUsuario())
                .build();
    }
}
