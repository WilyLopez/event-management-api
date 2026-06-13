package com.playzone.pems.domain.usuario.repository;

import java.util.List;
import java.util.UUID;

public interface UsuarioRolRepository {

    List<String> listarCodigosRolPorUsuario(UUID usuarioId);

    List<String> listarCodigosPermisoPorUsuario(UUID usuarioId);
}
