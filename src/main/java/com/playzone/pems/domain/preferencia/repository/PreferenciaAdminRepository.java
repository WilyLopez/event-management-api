package com.playzone.pems.domain.preferencia.repository;

import com.playzone.pems.domain.preferencia.model.PreferenciaAdmin;

import java.util.Optional;

public interface PreferenciaAdminRepository {

    Optional<PreferenciaAdmin> findByIdUsuarioAdmin(Long idUsuarioAdmin);

    boolean existsByIdUsuarioAdmin(Long idUsuarioAdmin);

    PreferenciaAdmin save(PreferenciaAdmin preferencia);

    void deleteByIdUsuarioAdmin(Long idUsuarioAdmin);
}
