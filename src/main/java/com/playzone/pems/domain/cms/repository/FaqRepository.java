package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FaqRepository {

    Optional<Faq> findById(Long id);

    Page<Faq> findAll(Pageable pageable);

    List<Faq> findVisibles();

    Faq save(Faq faq);

    void deleteById(Long id);
}
