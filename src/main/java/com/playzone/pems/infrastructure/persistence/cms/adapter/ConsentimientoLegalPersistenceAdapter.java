package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.ConsentimientoLegal;
import com.playzone.pems.domain.cms.repository.ConsentimientoLegalRepository;
import com.playzone.pems.infrastructure.persistence.cms.entity.ConsentimientoLegalEntity;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ConsentimientoLegalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsentimientoLegalPersistenceAdapter implements ConsentimientoLegalRepository {

    private final ConsentimientoLegalJpaRepository consentimientoJpa;

    @Override
    @Transactional
    public void guardar(ConsentimientoLegal consentimiento) {
        consentimientoJpa.save(ConsentimientoLegalEntity.builder()
                .origen(consentimiento.getOrigen())
                .referenciaId(consentimiento.getReferenciaId())
                .tipo(consentimiento.getTipo())
                .version(consentimiento.getVersion())
                .ip(consentimiento.getIp())
                .build());
    }
}
