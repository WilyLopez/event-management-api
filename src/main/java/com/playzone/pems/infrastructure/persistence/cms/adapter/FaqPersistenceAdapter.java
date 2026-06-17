package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.Faq;
import com.playzone.pems.domain.cms.repository.FaqRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.FaqJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FaqPersistenceAdapter implements FaqRepository {

    private final FaqJpaRepository faqJpa;
    private final CmsEntityMapper  mapper;

    @Override
    public Optional<Faq> findById(Long id) {
        return faqJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Faq> findAll(Pageable pageable) {
        return faqJpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<Faq> findVisibles() {
        return faqJpa.findByVisibleTrueOrderByOrdenAsc()
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Faq save(Faq faq) {
        return mapper.toDomain(faqJpa.save(mapper.toEntity(faq)));
    }

    @Override
    public void deleteById(Long id) {
        faqJpa.deleteById(id);
    }
}
