package com.playzone.pems.domain.usuario.repository;

import com.playzone.pems.domain.usuario.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteRepository {

    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByCorreo(String correo);

    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByTokenVerificacion(String token);

    Page<Cliente> findAll(Pageable pageable);

    Page<Cliente> findByNombreOrCorreo(String texto, Pageable pageable);

    Cliente save(Cliente cliente);

    boolean existsByCorreo(String correo);

    boolean existsByDni(String dni);

    void incrementarContadorVisitas(Long idCliente);
}