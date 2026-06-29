package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalHistorialQuery;
import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;
import com.playzone.pems.application.cms.dto.query.ContenidoLegalResumenQuery;
import com.playzone.pems.application.cms.dto.query.TipoLegalQuery;
import com.playzone.pems.application.cms.port.in.GestionarContenidoLegalUseCase;
import com.playzone.pems.domain.cms.model.ContenidoLegal;
import com.playzone.pems.domain.cms.model.ContenidoLegalHistorial;
import com.playzone.pems.domain.cms.model.TipoLegal;
import com.playzone.pems.domain.cms.repository.ContenidoLegalHistorialRepository;
import com.playzone.pems.domain.cms.repository.ContenidoLegalRepository;
import com.playzone.pems.domain.cms.repository.TipoLegalRepository;
import com.playzone.pems.shared.exception.BusinessException;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContenidoLegalService implements GestionarContenidoLegalUseCase {

    private final ContenidoLegalRepository          legalRepository;
    private final TipoLegalRepository               tipoLegalRepository;
    private final ContenidoLegalHistorialRepository historialRepository;

    @Override
    @Transactional(readOnly = true)
    public ContenidoLegalQuery obtenerPorTipo(String tipo) {
        return ContenidoLegalQuery.from(
                legalRepository.findActivoByTipo(tipo.toUpperCase())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "ContenidoLegal tipo=" + tipo)));
    }

    @Override
    @Transactional(readOnly = true)
    public ContenidoLegalQuery obtenerPublicoPorSlug(String slug) {
        TipoLegal tipo = tipoLegalRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TipoLegal slug=" + slug));
        return ContenidoLegalQuery.from(
                legalRepository.findActivoByTipo(tipo.getCodigo())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "ContenidoLegal slug=" + slug)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContenidoLegalResumenQuery> listarPublico() {
        Map<String, ContenidoLegal> activosPorTipo = legalRepository.findActivos().stream()
                .collect(Collectors.toMap(ContenidoLegal::getTipo, Function.identity()));

        return tipoLegalRepository.findAllOrdenado().stream()
                .filter(tipo -> activosPorTipo.containsKey(tipo.getCodigo()))
                .map(tipo -> {
                    ContenidoLegal doc = activosPorTipo.get(tipo.getCodigo());
                    return ContenidoLegalResumenQuery.builder()
                            .tipo(tipo.getCodigo())
                            .etiqueta(tipo.getEtiqueta())
                            .slug(tipo.getSlug())
                            .titulo(doc.getTitulo())
                            .version(doc.getVersion())
                            .visibleFooter(tipo.isVisibleFooter())
                            .fechaActualizacion(doc.getFechaActualizacion())
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoLegalQuery> listarTipos() {
        var codigosCreados = legalRepository.findAll().stream()
                .map(ContenidoLegal::getTipo)
                .collect(Collectors.toSet());

        return tipoLegalRepository.findAllOrdenado().stream()
                .map(tipo -> TipoLegalQuery.from(tipo, codigosCreados.contains(tipo.getCodigo())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContenidoLegalHistorialQuery> listarHistorial(String tipo) {
        return historialRepository.findByTipo(tipo.toUpperCase()).stream()
                .map(ContenidoLegalHistorialQuery::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContenidoLegalQuery> listar() {
        return legalRepository.findAll().stream().map(ContenidoLegalQuery::from).toList();
    }

    @Override
    @Transactional
    public ContenidoLegalQuery crear(CrearCommand command) {
        String tipo = command.tipo().toUpperCase();
        if (!tipoLegalRepository.existsByCodigo(tipo)) {
            throw new BusinessException(
                    "El tipo de documento legal '" + tipo + "' no esta permitido.");
        }
        if (legalRepository.findByTipo(tipo).isPresent()) {
            throw new BusinessException("Ya existe un documento legal de tipo: " + tipo);
        }
        ContenidoLegal nuevo = ContenidoLegal.builder()
                .tipo(tipo)
                .titulo(command.titulo())
                .contenido(command.contenido() != null ? command.contenido() : "")
                .version(1)
                .activo(true)
                .idUsuarioEditor(command.idUsuario())
                .build();
        return ContenidoLegalQuery.from(legalRepository.save(nuevo));
    }

    @Override
    @Transactional
    public ContenidoLegalQuery actualizar(ActualizarCommand command) {
        ContenidoLegal existente = legalRepository.findByTipo(command.tipo().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContenidoLegal tipo=" + command.tipo()));

        historialRepository.guardar(ContenidoLegalHistorial.builder()
                .legalId(existente.getId())
                .tipo(existente.getTipo())
                .titulo(existente.getTitulo())
                .contenido(existente.getContenido())
                .version(existente.getVersion())
                .createdBy(command.idUsuario())
                .build());

        ContenidoLegal actualizado = existente.toBuilder()
                .titulo(command.titulo())
                .contenido(command.contenido())
                .version(existente.getVersion() + 1)
                .idUsuarioEditor(command.idUsuario())
                .build();

        return ContenidoLegalQuery.from(legalRepository.save(actualizado));
    }

    @Override
    @Transactional
    public ContenidoLegalQuery activar(String tipo) {
        ContenidoLegal doc = legalRepository.findByTipo(tipo.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContenidoLegal tipo=" + tipo));
        return ContenidoLegalQuery.from(
                legalRepository.save(doc.toBuilder().activo(true).build()));
    }

    @Override
    @Transactional
    public ContenidoLegalQuery desactivar(String tipo) {
        String tipoUpper = tipo.toUpperCase();
        if (esSistema(tipoUpper)) {
            throw new BusinessException(
                    "El documento '" + tipoUpper + "' es parte del sistema y no puede desactivarse.");
        }
        ContenidoLegal doc = legalRepository.findByTipo(tipoUpper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContenidoLegal tipo=" + tipo));
        return ContenidoLegalQuery.from(
                legalRepository.save(doc.toBuilder().activo(false).build()));
    }

    @Override
    @Transactional
    public void eliminar(String tipo) {
        String tipoUpper = tipo.toUpperCase();
        TipoLegal catalogo = tipoLegalRepository.findByCodigo(tipoUpper).orElse(null);
        if (catalogo != null && (catalogo.isEsSistema() || catalogo.isRequerido())) {
            throw new BusinessException(
                    "El documento '" + tipoUpper + "' es obligatorio y no puede eliminarse.");
        }
        legalRepository.findByTipo(tipoUpper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContenidoLegal tipo=" + tipo));
        legalRepository.deleteByTipo(tipoUpper);
    }

    private boolean esSistema(String tipoUpper) {
        return tipoLegalRepository.findByCodigo(tipoUpper)
                .map(TipoLegal::isEsSistema)
                .orElse(false);
    }
}
