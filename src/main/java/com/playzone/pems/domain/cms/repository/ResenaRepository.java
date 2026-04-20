package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.Resena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ResenaRepository {

    Optional<Resena> findById(Long id);

    Page<Resena> findAprobadas(Pageable pageable);

    Page<Resena> findPendientes(Pageable pageable);

    Page<Resena> findAll(Pageable pageable);

    Resena save(Resena resena);

    void deleteById(Long id);
}