package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.ZonaJuego;
import com.playzone.pems.domain.comercial.repository.ZonaJuegoRepository;
import com.playzone.pems.infrastructure.persistence.comercial.entity.MedioZonaJuegoEntity;
import com.playzone.pems.infrastructure.persistence.comercial.entity.ZonaJuegoEntity;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.MedioZonaJuegoJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.ZonaJuegoJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.mapper.ZonaJuegoEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ZonaJuegoPersistenceAdapter implements ZonaJuegoRepository {

    private static final Pattern VIDEO_PATTERN = Pattern.compile(
            "^https://(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/|tiktok\\.com/).+"
    );

    private final ZonaJuegoJpaRepository     jpaRepo;
    private final MedioZonaJuegoJpaRepository medioRepo;
    private final ZonaJuegoEntityMapper       mapper;

    @Override
    public Optional<ZonaJuego> findById(Long id) {
        return jpaRepo.findById(id).map(e -> {
            List<MedioZonaJuegoEntity> medios = medioRepo.findByZona_IdOrderByOrdenAsc(id);
            return mapper.toDomain(e, medios);
        });
    }

    @Override
    public List<ZonaJuego> findAllActivas() {
        return jpaRepo.findByActivaTrueOrderByOrdenAsc().stream()
                .map(e -> mapper.toDomain(e, medioRepo.findByZona_IdOrderByOrdenAsc(e.getId())))
                .toList();
    }

    @Override
    public List<ZonaJuego> findAll() {
        return jpaRepo.findAllByOrderByOrdenAsc().stream()
                .map(e -> mapper.toDomain(e, medioRepo.findByZona_IdOrderByOrdenAsc(e.getId())))
                .toList();
    }

    @Override
    @Transactional
    public ZonaJuego save(ZonaJuego zona) {
        ZonaJuegoEntity entity = mapper.toEntity(zona);
        ZonaJuegoEntity saved = jpaRepo.save(entity);

        List<MedioZonaJuegoEntity> nuevosMedios = new ArrayList<>();
        AtomicInteger idx = new AtomicInteger(0);

        if (zona.getImagenes() != null) {
            zona.getImagenes().forEach(url -> nuevosMedios.add(
                    MedioZonaJuegoEntity.builder().zona(saved).tipo("IMAGEN").url(url).orden(idx.getAndIncrement()).build()
            ));
        }
        if (zona.getVideos() != null) {
            zona.getVideos().forEach(url -> nuevosMedios.add(
                    MedioZonaJuegoEntity.builder().zona(saved).tipo("VIDEO").url(url).orden(idx.getAndIncrement()).build()
            ));
        }

        medioRepo.deleteByZona_Id(saved.getId());
        medioRepo.saveAll(nuevosMedios);

        List<MedioZonaJuegoEntity> savedMedios = medioRepo.findByZona_IdOrderByOrdenAsc(saved.getId());
        return mapper.toDomain(saved, savedMedios);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        medioRepo.deleteByZona_Id(id);
        jpaRepo.deleteById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepo.existsBySlug(slug);
    }
}
