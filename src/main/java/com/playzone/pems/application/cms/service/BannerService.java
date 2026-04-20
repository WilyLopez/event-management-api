package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.port.in.GestionarBannerUseCase;
import com.playzone.pems.domain.cms.model.Banner;
import com.playzone.pems.domain.cms.repository.BannerRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BannerService implements GestionarBannerUseCase {

    private final BannerRepository bannerRepository;

    @Override
    @Transactional
    public Banner crear(CrearCommand command) {
        if (command.fechaFin() != null && command.fechaFin().isBefore(command.fechaInicio())) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la de inicio.");
        }

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

        return bannerRepository.save(banner);
    }

    @Override
    @Transactional
    public void desactivar(Long idBanner) {
        Banner banner = bannerRepository.findById(idBanner)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", idBanner));
        bannerRepository.save(banner.toBuilder().activo(false).build());
    }

    @Override
    @Transactional
    public void eliminar(Long idBanner) {
        bannerRepository.findById(idBanner)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", idBanner));
        bannerRepository.deleteById(idBanner);
    }
}