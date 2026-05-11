package com.playzone.pems.domain.usuario.repository;

import com.playzone.pems.domain.usuario.model.UsuarioAdmin;

import java.util.List;
import java.util.Optional;

public interface UsuarioAdminRepository {

    List<UsuarioAdmin> findAll();

    Optional<UsuarioAdmin> findById(Long id);

    Optional<UsuarioAdmin> findByCorreo(String correo);

    List<UsuarioAdmin> findAllBySede(Long idSede);

    UsuarioAdmin save(UsuarioAdmin usuarioAdmin);

    boolean existsByCorreo(String correo);

    void incrementarIntentosFallidos(Long id);

    void reiniciarIntentosFallidos(Long id);
}