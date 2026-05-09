package com.playzone.pems.infrastructure.persistence.usuario.adapter;

import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.ClienteJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.mapper.ClienteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepository {

    private final ClienteJpaRepository clienteJpa;
    private final ClienteEntityMapper  mapper;

    @Override public Optional<Cliente> findById(Long id) {
        return clienteJpa.findById(id).map(mapper::toDomain);
    }

    @Override public Optional<Cliente> findByCorreo(String correo) {
        return clienteJpa.findByCorreo(correo).map(mapper::toDomain);
    }

    @Override public Optional<Cliente> findByDni(String dni) {
        return clienteJpa.findByDni(dni).map(mapper::toDomain);
    }

    @Override public Optional<Cliente> findByTokenVerificacion(String token) {
        return clienteJpa.findByTokenVerificacion(token).map(mapper::toDomain);
    }

    @Override public Page<Cliente> findAll(Pageable pageable) {
        return clienteJpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override public Page<Cliente> findByNombreOrCorreo(String texto, Pageable pageable) {
        return clienteJpa.findByNombreOrCorreo(texto, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Cliente save(Cliente cliente) {
        return mapper.toDomain(clienteJpa.save(mapper.toEntity(cliente)));
    }

    @Override public boolean existsByCorreo(String correo) { return clienteJpa.existsByCorreo(correo); }

    @Override public boolean existsByDni(String dni) { return clienteJpa.existsByDni(dni); }

    @Override
    @Transactional
    public void incrementarContadorVisitas(Long idCliente) {
        clienteJpa.incrementarContadorVisitas(idCliente);
    }

    @Override
    public Page<Cliente> buscarConFiltros(
            String search, Boolean esVip, Boolean activo,
            Boolean verificado, Boolean frecuente,
            int minVisitas, Pageable pageable) {

        return clienteJpa.buscarConFiltros(
                        search, esVip, activo, verificado, frecuente,
                        minVisitas, pageable)
                .map(mapper::toDomain);
    }
}