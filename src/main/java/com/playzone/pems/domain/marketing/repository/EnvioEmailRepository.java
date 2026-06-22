package com.playzone.pems.domain.marketing.repository;

import com.playzone.pems.domain.marketing.model.EnvioEmail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EnvioEmailRepository {

    Optional<EnvioEmail> findById(Long id);

    Page<EnvioEmail> findByCampana(Long idCampana, Pageable pageable);

    List<EnvioEmail> findPendientesByCampana(Long idCampana, int limite);

    List<EnvioEmail> findParaReintentar(int maxIntentos);

    EnvioEmail save(EnvioEmail envio);

    void guardarTodos(List<EnvioEmail> envios);

    long countByCampanaAndEstado(Long idCampana, String estado);
}
