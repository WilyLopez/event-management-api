package com.playzone.pems.domain.usuario.repository;

import com.playzone.pems.domain.usuario.model.StaffPerfil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffPerfilRepository {

    Optional<StaffPerfil> buscarPorId(Long id);

    Optional<StaffPerfil> buscarPorUsuarioId(UUID usuarioId);

    Optional<StaffPerfil> buscarPorCorreo(String correo);

    List<StaffPerfil> listarTodos();

    StaffPerfil guardar(StaffPerfil staffPerfil);

    long contarActivosPorRol(String rolCodigo);

    long contarActivosPorRolExcluyendo(String rolCodigo, Long excludeId);
}
