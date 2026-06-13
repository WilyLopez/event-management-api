package com.playzone.pems.domain.usuario.repository;

import com.playzone.pems.domain.usuario.model.StaffPerfil;

import java.util.Optional;
import java.util.UUID;

public interface StaffPerfilRepository {

    Optional<StaffPerfil> buscarPorUsuarioId(UUID usuarioId);
}
