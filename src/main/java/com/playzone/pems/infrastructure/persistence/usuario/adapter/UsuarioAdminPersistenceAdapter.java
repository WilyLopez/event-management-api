package com.playzone.pems.infrastructure.persistence.usuario.adapter;

import com.playzone.pems.domain.usuario.model.UsuarioAdmin;
import com.playzone.pems.domain.usuario.repository.UsuarioAdminRepository;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.mapper.SedeEntityMapper;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioAdminPersistenceAdapter implements UsuarioAdminRepository {

    private final UsuarioAdminJpaRepository adminJpa;
    private final SedeJpaRepository         sedeJpa;
    private final SedeEntityMapper          mapper;

    @Override
    public List<UsuarioAdmin> findAll() {
        return adminJpa.findAllByOrderByFechaCreacionDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UsuarioAdmin> findById(Long id) {
        return adminJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<UsuarioAdmin> findByCorreo(String correo) {
        return adminJpa.findByCorreo(correo).map(mapper::toDomain);
    }

    @Override
    public List<UsuarioAdmin> findAllBySede(Long idSede) {
        return adminJpa.findBySede_Id(idSede).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public UsuarioAdmin save(UsuarioAdmin admin) {
        SedeEntity sede = sedeJpa.findById(admin.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", admin.getIdSede()));

        UsuarioAdminEntity entity = UsuarioAdminEntity.builder()
                .id(admin.getId())
                .sede(sede)
                .nombre(admin.getNombre())
                .correo(admin.getCorreo())
                .contrasenaHash(admin.getContrasenaHash())
                .rol(admin.getRol() != null ? admin.getRol() : "ADMINISTRATIVO")
                .fotoPerfilUrl(admin.getFotoPerfilUrl())
                .telefono(admin.getTelefono())
                .activo(admin.isActivo())
                .debeCambiarContrasena(admin.isDebeCambiarContrasena())
                .intentosFallidos(admin.getIntentosFallidos())
                .bloqueadoHasta(admin.getBloqueadoHasta())
                .ultimoAcceso(admin.getUltimoAcceso())
                .ultimoCambioContrasena(admin.getUltimoCambioContrasena())
                .creadoPor(admin.getCreadoPor())
                .build();

        return mapper.toDomain(adminJpa.save(entity));
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return adminJpa.existsByCorreo(correo);
    }

    @Override
    @Transactional
    public void incrementarIntentosFallidos(Long id) {
        adminJpa.incrementarIntentosFallidos(id);
    }

    @Override
    @Transactional
    public void reiniciarIntentosFallidos(Long id) {
        adminJpa.reiniciarIntentosFallidos(id);
    }
}
