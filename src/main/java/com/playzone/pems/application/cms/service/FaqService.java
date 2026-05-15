package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.query.FaqQuery;
import com.playzone.pems.application.cms.port.in.GestionarFaqUseCase;
import com.playzone.pems.domain.cms.model.Faq;
import com.playzone.pems.domain.cms.repository.FaqRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class FaqService implements GestionarFaqUseCase {

    private final FaqRepository faqRepository;

    @Override
    @Transactional
    public FaqQuery crear(CrearCommand command) {
        Faq faq = Faq.builder()
                .pregunta(command.pregunta())
                .respuesta(command.respuesta())
                .ordenVisualizacion(command.ordenVisualizacion())
                .visible(true)
                .idUsuarioEditor(command.idUsuario())
                .build();
        return FaqQuery.from(faqRepository.save(faq));
    }

    @Override
    @Transactional
    public FaqQuery actualizar(ActualizarCommand command) {
        Faq existente = findOrThrow(command.idFaq());
        Faq actualizado = existente.toBuilder()
                .pregunta(command.pregunta())
                .respuesta(command.respuesta())
                .ordenVisualizacion(command.ordenVisualizacion())
                .idUsuarioEditor(command.idUsuario())
                .build();
        return FaqQuery.from(faqRepository.save(actualizado));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FaqQuery> listar(Pageable pageable) {
        return faqRepository.findAll(pageable).map(FaqQuery::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaqQuery> listarVisibles() {
        return faqRepository.findVisibles().stream().map(FaqQuery::from).toList();
    }

    @Override
    @Transactional
    public void activar(Long idFaq) {
        Faq faq = findOrThrow(idFaq);
        faqRepository.save(faq.toBuilder().visible(true).build());
    }

    @Override
    @Transactional
    public void desactivar(Long idFaq) {
        Faq faq = findOrThrow(idFaq);
        faqRepository.save(faq.toBuilder().visible(false).build());
    }

    @Override
    @Transactional
    public void reordenar(ReordenarCommand command) {
        AtomicInteger pos = new AtomicInteger(0);
        for (Long id : command.idsOrdenados()) {
            Faq faq = findOrThrow(id);
            faqRepository.save(faq.toBuilder().ordenVisualizacion(pos.getAndIncrement()).build());
        }
    }

    @Override
    @Transactional
    public void eliminar(Long idFaq) {
        findOrThrow(idFaq);
        faqRepository.deleteById(idFaq);
    }

    private Faq findOrThrow(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", id));
    }
}
