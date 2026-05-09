package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.query.BannerQuery;
import com.playzone.pems.application.cms.port.in.GestionarBannerUseCase;
import com.playzone.pems.domain.cms.model.Banner;
import com.playzone.pems.domain.cms.repository.BannerRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class BannerService implements GestionarBannerUseCase {

    private final BannerRepository bannerRepository;

    @Override
    @Transactional
    public BannerQuery crear(CrearCommand command) {
        validateFechas(command.fechaInicio(), command.fechaFin());
        Banner banner = Banner.builder()
                .idSede(command.idSede())
                .titulo(command.titulo())
                .descripcion(command.descripcion())
                .imagenUrl(command.imagenUrl())
                .enlaceDestino(command.enlaceDestino())
                .fechaInicio(command.fechaInicio())
                .fechaFin(command.fechaFin())
                .activo(true)
                .orden(command.orden())
                .idUsuarioCreador(command.idUsuario())
                .build();
        return BannerQuery.from(bannerRepository.save(banner));
    }

    @Override
    @Transactional
    public BannerQuery actualizar(ActualizarCommand command) {
        Banner existente = findOrThrow(command.idBanner());
        validateFechas(command.fechaInicio(), command.fechaFin());
        Banner actualizado = existente.toBuilder()
                .idSede(command.idSede())
                .titulo(command.titulo())
                .descripcion(command.descripcion())
                .imagenUrl(command.imagenUrl())
                .enlaceDestino(command.enlaceDestino())
                .fechaInicio(command.fechaInicio())
                .fechaFin(command.fechaFin())
                .orden(command.orden())
                .build();
        return BannerQuery.from(bannerRepository.save(actualizado));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BannerQuery> listar(Pageable pageable) {
        return bannerRepository.findAll(pageable).map(BannerQuery::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerQuery> listarPublicos(Long idSede) {
        return bannerRepository.findVisiblesBySedeAndFecha(idSede, LocalDate.now())
                .stream().map(BannerQuery::from).toList();
    }

    @Override
    @Transactional
    public void activar(Long idBanner) {
        Banner banner = findOrThrow(idBanner);
        bannerRepository.save(banner.toBuilder().activo(true).build());
    }

    @Override
    @Transactional
    public void desactivar(Long idBanner) {
        Banner banner = findOrThrow(idBanner);
        bannerRepository.save(banner.toBuilder().activo(false).build());
    }

    @Override
    @Transactional
    public BannerQuery duplicar(Long idBanner, Long idUsuario) {
        Banner original = findOrThrow(idBanner);
        Banner copia = Banner.builder()
                .idSede(original.getIdSede())
                .titulo(original.getTitulo() + " (copia)")
                .descripcion(original.getDescripcion())
                .imagenUrl(original.getImagenUrl())
                .enlaceDestino(original.getEnlaceDestino())
                .fechaInicio(original.getFechaInicio())
                .fechaFin(original.getFechaFin())
                .activo(false)
                .orden(original.getOrden() + 1)
                .idUsuarioCreador(idUsuario)
                .build();
        return BannerQuery.from(bannerRepository.save(copia));
    }

    @Override
    @Transactional
    public void reordenar(ReordenarCommand command) {
        AtomicInteger pos = new AtomicInteger(0);
        for (Long id : command.idsOrdenados()) {
            Banner b = findOrThrow(id);
            bannerRepository.save(b.toBuilder().orden(pos.getAndIncrement()).build());
        }
    }

    @Override
    @Transactional
    public void eliminar(Long idBanner) {
        findOrThrow(idBanner);
        bannerRepository.deleteById(idBanner);
    }

    private Banner findOrThrow(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", id));
    }

    private void validateFechas(LocalDate inicio, LocalDate fin) {
        if (fin != null && fin.isBefore(inicio)) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la de inicio.");
        }
    }
}
