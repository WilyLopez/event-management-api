package com.playzone.pems.application.promocion.service;

import com.playzone.pems.application.promocion.dto.command.CrearPromocionCommand;
import com.playzone.pems.application.promocion.dto.query.PromocionQuery;
import com.playzone.pems.application.promocion.port.in.AplicarPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.CrearPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.DesactivarPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.ListarPromocionesUseCase;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.promocion.exception.PromocionNotFoundException;
import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.domain.promocion.model.PromocionMarketing;
import com.playzone.pems.domain.promocion.repository.PromocionRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromocionService
        implements CrearPromocionUseCase,
        ListarPromocionesUseCase,
        AplicarPromocionUseCase,
        DesactivarPromocionUseCase {

    private final PromocionRepository      promocionRepository;
    private final ReservaPublicaRepository reservaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PromocionQuery> listar() {
        return promocionRepository.findAll(Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::toQuery)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PromocionQuery ejecutar(CrearPromocionCommand command) {
        if (command.getFechaFin() != null
                && command.getFechaFin().isBefore(command.getFechaInicio())) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la de inicio.");
        }

        boolean existente = command.getId() != null;
        boolean activoActual = true;
        UUID creadorActual = command.getIdUsuarioCreador();

        if (existente) {
            Promocion actual = promocionRepository.findById(command.getId())
                    .orElseThrow(() -> new PromocionNotFoundException(command.getId()));
            activoActual = actual.isActivo();
            creadorActual = actual.getIdUsuarioCreador();
        }

        PromocionMarketing marketing = buildMarketing(command);

        Promocion promocion = Promocion.builder()
                .id(command.getId())
                .tipoPromocion(command.getTipoPromocion())
                .idSede(command.getIdSede())
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .valorDescuento(command.getValorDescuento())
                .minimoPersonas(command.getMinimoPersonas())
                .soloTipoDia(command.getSoloTipoDia())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .activo(activoActual)
                .esAutomatica(command.getEsAutomatica())
                .idUsuarioCreador(creadorActual)
                .prioridad(command.getPrioridad() != null ? command.getPrioridad() : 0)
                .limiteUsos(command.getLimiteUsos())
                .limitePorCliente(command.getLimitePorCliente())
                .montoMinimo(command.getMontoMinimo())
                .marketing(marketing)
                .build();

        return toQuery(promocionRepository.save(promocion));
    }

    private PromocionMarketing buildMarketing(CrearPromocionCommand cmd) {
        if (cmd.getImagenUrl() == null && cmd.getBannerUrl() == null
                && cmd.getColorDestacado() == null && cmd.getTextoPublicitario() == null
                && cmd.getTextoBoton() == null && cmd.getUrlBoton() == null
                && !Boolean.TRUE.equals(cmd.getMostrarEnInicio())
                && !Boolean.TRUE.equals(cmd.getMostrarEnCarrusel())
                && !Boolean.TRUE.equals(cmd.getMostrarEnCheckout())
                && !Boolean.TRUE.equals(cmd.getSoloMovil())) {
            return null;
        }
        return PromocionMarketing.builder()
                .imagenPath(cmd.getImagenUrl())
                .bannerPath(cmd.getBannerUrl())
                .colorDestacado(cmd.getColorDestacado())
                .textoPublicitario(cmd.getTextoPublicitario())
                .textoBoton(cmd.getTextoBoton())
                .urlBoton(cmd.getUrlBoton())
                .mostrarEnInicio(Boolean.TRUE.equals(cmd.getMostrarEnInicio()))
                .mostrarEnCarrusel(Boolean.TRUE.equals(cmd.getMostrarEnCarrusel()))
                .mostrarEnPromociones(cmd.getMostrarEnPaginaPromociones() == null || cmd.getMostrarEnPaginaPromociones())
                .mostrarEnCheckout(Boolean.TRUE.equals(cmd.getMostrarEnCheckout()))
                .soloMovil(Boolean.TRUE.equals(cmd.getSoloMovil()))
                .build();
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
        PromocionMarketing mkt = p.getMarketing();
        return PromocionQuery.builder()
                .id(p.getId())
                .tipoPromocion(p.getTipoPromocion().getCodigo())
                .idSede(p.getIdSede())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .valorDescuento(p.getValorDescuento())
                .minimoPersonas(p.getMinimoPersonas())
                .soloTipoDia(p.getSoloTipoDia() != null ? p.getSoloTipoDia().getCodigo() : null)
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .activo(p.isActivo())
                .esAutomatica(p.isEsAutomatica())
                .fechaCreacion(p.getFechaCreacion())
                .prioridad(p.getPrioridad())
                .limiteUsos(p.getLimiteUsos())
                .limitePorCliente(p.getLimitePorCliente())
                .montoMinimo(p.getMontoMinimo())
                .imagenUrl(mkt != null ? mkt.getImagenPath() : null)
                .bannerUrl(mkt != null ? mkt.getBannerPath() : null)
                .colorDestacado(mkt != null ? mkt.getColorDestacado() : null)
                .textoPublicitario(mkt != null ? mkt.getTextoPublicitario() : null)
                .textoBoton(mkt != null ? mkt.getTextoBoton() : null)
                .urlBoton(mkt != null ? mkt.getUrlBoton() : null)
                .mostrarEnInicio(mkt != null && mkt.isMostrarEnInicio())
                .mostrarEnCarrusel(mkt != null && mkt.isMostrarEnCarrusel())
                .mostrarEnPaginaPromociones(mkt != null && mkt.isMostrarEnPromociones())
                .mostrarEnCheckout(mkt != null && mkt.isMostrarEnCheckout())
                .soloMovil(mkt != null && mkt.isSoloMovil())
                .vecesUsado(0)
                .montoAhorrado(BigDecimal.ZERO)
                .clientesAtraidos(0)
                .build();
    }
}
