package com.playzone.pems.domain.preferencia.repository;

import com.playzone.pems.domain.preferencia.model.PreferenciaUsuario;

import java.util.Optional;
import java.util.UUID;

public interface PreferenciaUsuarioRepository {

    Optional<PreferenciaUsuario> buscarPorUsuarioId(UUID usuarioId);

    PreferenciaUsuario guardar(PreferenciaUsuario preferencia);
}
