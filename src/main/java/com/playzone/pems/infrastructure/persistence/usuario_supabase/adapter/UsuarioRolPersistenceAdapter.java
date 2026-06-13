package com.playzone.pems.infrastructure.persistence.usuario_supabase.adapter;

import com.playzone.pems.domain.usuario.repository.UsuarioRolRepository;
import com.playzone.pems.infrastructure.persistence.usuario_supabase.jpa.UsuarioRolJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioRolPersistenceAdapter implements UsuarioRolRepository {

    private final UsuarioRolJpaRepository jpa;

    @Override
    public List<String> listarCodigosRolPorUsuario(UUID usuarioId) {
        return jpa.findRolCodigosByUsuarioId(usuarioId);
    }

    @Override
    public List<String> listarCodigosPermisoPorUsuario(UUID usuarioId) {
        return jpa.findPermisoCodigosByUsuarioId(usuarioId);
    }
}
