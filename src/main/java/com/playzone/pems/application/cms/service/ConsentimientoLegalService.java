package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.port.in.RegistrarConsentimientoUseCase;
import com.playzone.pems.domain.cms.model.ConsentimientoLegal;
import com.playzone.pems.domain.cms.repository.ConsentimientoLegalRepository;
import com.playzone.pems.domain.cms.repository.ContenidoLegalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsentimientoLegalService implements RegistrarConsentimientoUseCase {

    private final ContenidoLegalRepository      legalRepository;
    private final ConsentimientoLegalRepository consentimientoRepository;

    @Override
    @Transactional
    public void registrar(String origen, Long referenciaId, List<String> tipos) {
        if (tipos == null) return;
        for (String tipo : tipos) {
            legalRepository.findActivoByTipo(tipo.toUpperCase()).ifPresent(doc ->
                    consentimientoRepository.guardar(ConsentimientoLegal.builder()
                            .origen(origen)
                            .referenciaId(referenciaId)
                            .tipo(doc.getTipo())
                            .version(doc.getVersion())
                            .build()));
        }
    }
}
