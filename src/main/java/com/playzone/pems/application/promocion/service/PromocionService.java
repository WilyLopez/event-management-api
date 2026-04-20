package com.playzone.pems.application.promocion.service;

import com.playzone.pems.application.promocion.dto.command.CrearPromocionCommand;
import com.playzone.pems.application.promocion.dto.query.PromocionQuery;
import com.playzone.pems.application.promocion.port.in.AplicarPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.CrearPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.DesactivarPromocionUseCase;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.promocion.exception.PromocionNotFoundException;
import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.domain.promocion.repository.PromocionRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PromocionService
        implements CrearPromocionUseCase,
        AplicarPromocionUseCase,
        DesactivarPromocionUseCase {

    private final PromocionRepository       promocionRepository;
    private final ReservaPublicaRepository  reservaRepository;

    @Override
    @Transactional
    public PromocionQuery ejecutar(CrearPromocionCommand command) {
        if (command.getFechaFin() != null
                && command.getFechaFin().isBefore(command.getFechaInicio())) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la de inicio.");
        }

        Promocion promocion = Promocion.builder()
                .tipoPromocion(command.getTipoPromocion())
                .idSede(command.getIdSede())
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .valorDescuento(command.getValorDescuento())
                .condicion(command.getCondicion())
                .minimoPersonas(command.getMinimoPersonas())
                .soloTipoDia(command.getSoloTipoDia())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .activo(true)
                .esAutomatica(command.getEsAutomatica())
                .idUsuarioCreador(command.getIdUsuarioCreador())
                .build();

        return toQuery(promocionRepository.save(promocion));
    }

    @Override
    @Transactional
    public Resultado aplicar(Long idPromocion, Long idReservaPublica) {
        Promocion promocion = promocionRepository.findById(idPromocion)
                .orElseThrow(() -> new PromocionNotFoundException(idPromocion));

        ReservaPublica reserva = reservaRepository.findById(idReservaPublica)
                .orElseThrow(() -> new ResourceNotFoundException("ReservaPublica", idReservaPublica));

        if (!promocion.estaVigenteEn(reserva.getFechaEvento())) {
            throw new ValidationException("La promoción no está vigente para la fecha de la reserva.");
        }
        if (!promocion.aplicaParaTipoDia(reserva.getTipoDia())) {
            throw new ValidationException("La promoción no aplica para el tipo de día de la reserva.");
        }
        if (!promocion.aplicaParaSede(reserva.getIdSede())) {
            throw new ValidationException("La promoción no aplica para la sede de la reserva.");
        }

        BigDecimal descuento = promocion.calcularDescuento(reserva.getPrecioHistorico());

        return new Resultado(descuento, promocion.getId(), promocion.getNombre());
    }

    @Override
    @Transactional
    public void ejecutar(Long idPromocion) {
        promocionRepository.findById(idPromocion)
                .orElseThrow(() -> new PromocionNotFoundException(idPromocion));
        promocionRepository.desactivar(idPromocion);
    }

    private PromocionQuery toQuery(Promocion p) {
        return PromocionQuery.builder()
                .id(p.getId())
                .tipoPromocion(p.getTipoPromocion().getCodigo())
                .idSede(p.getIdSede())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .valorDescuento(p.getValorDescuento())
                .condicion(p.getCondicion())
                .minimoPersonas(p.getMinimoPersonas())
                .soloTipoDia(p.getSoloTipoDia() != null ? p.getSoloTipoDia().getCodigo() : null)
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .activo(p.isActivo())
                .esAutomatica(p.isEsAutomatica())
                .fechaCreacion(p.getFechaCreacion())
                .build();
    }
}