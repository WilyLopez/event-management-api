package com.playzone.pems.infrastructure.persistence.marketing.adapter;

import com.playzone.pems.domain.marketing.model.EnvioEmail;
import com.playzone.pems.domain.marketing.repository.EnvioEmailRepository;
import com.playzone.pems.infrastructure.persistence.marketing.jpa.EnvioEmailJpaRepository;
import com.playzone.pems.infrastructure.persistence.marketing.mapper.MarketingEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EnvioPersistenceAdapter implements EnvioEmailRepository {

    private final EnvioEmailJpaRepository jpa;
    private final MarketingEntityMapper   mapper;

    @Override
    public Optional<EnvioEmail> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<EnvioEmail> findByCampana(Long idCampana, Pageable pageable) {
        return jpa.findByCampanaId(idCampana, pageable).map(mapper::toDomain);
    }

    @Override
    public List<EnvioEmail> findPendientesByCampana(Long idCampana, int limite) {
        return jpa.findPendientesByCampana(idCampana, PageRequest.of(0, limite))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<EnvioEmail> findParaReintentar(int maxIntentos) {
        return jpa.findParaReintentar(maxIntentos, PageRequest.of(0, 100))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public EnvioEmail save(EnvioEmail envio) {
        return mapper.toDomain(jpa.save(mapper.toEntity(envio)));
    }

    @Override
    @Transactional
    public void guardarTodos(List<EnvioEmail> envios) {
        jpa.saveAll(envios.stream().map(mapper::toEntity).toList());
    }

    @Override
    public long countByCampanaAndEstado(Long idCampana, String estado) {
        return jpa.countByCampanaIdAndEstado(idCampana, estado);
    }
}
