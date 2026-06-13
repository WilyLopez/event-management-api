package com.playzone.pems.infrastructure.persistence.usuario_supabase.adapter;

import com.playzone.pems.domain.usuario.model.StaffPerfil;
import com.playzone.pems.domain.usuario.repository.StaffPerfilRepository;
import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.StaffPerfilEntity;
import com.playzone.pems.infrastructure.persistence.usuario_supabase.jpa.StaffPerfilJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StaffPerfilPersistenceAdapter implements StaffPerfilRepository {

    private final StaffPerfilJpaRepository jpa;

    @Override
    public Optional<StaffPerfil> buscarPorUsuarioId(UUID usuarioId) {
        return jpa.findByUsuarioIdAndDeletedAtIsNull(usuarioId).map(this::toDomain);
    }

    private StaffPerfil toDomain(StaffPerfilEntity e) {
        return StaffPerfil.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .sedeId(e.getSedeId())
                .codigoEmpleado(e.getCodigoEmpleado())
                .fechaIngreso(e.getFechaIngreso())
                .telefonoEmergencia(e.getTelefonoEmergencia())
                .observaciones(e.getObservaciones())
                .esActivo(e.isEsActivo())
                .build();
    }
}
