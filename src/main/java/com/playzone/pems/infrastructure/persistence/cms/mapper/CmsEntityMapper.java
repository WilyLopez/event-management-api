package com.playzone.pems.infrastructure.persistence.cms.mapper;

import com.playzone.pems.domain.cms.model.Banner;
import com.playzone.pems.domain.cms.model.ContenidoWeb;
import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.Resena;
import com.playzone.pems.infrastructure.persistence.cms.entity.*;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class CmsEntityMapper {

    public ContenidoWeb toDomain(ContenidoWebEntity e) {
        if (e == null) return null;
        return ContenidoWeb.builder()
                .id(e.getId())
                .idSeccion(e.getIdSeccion() != null ? e.getIdSeccion().longValue() : null)
                .idTipoContenido(e.getIdTipoContenido() != null ? e.getIdTipoContenido().longValue() : null)
                .clave(e.getClave())
                .valorEs(e.getValorEs())
                .valorEn(e.getValorEn())
                .activo(e.isActivo())
                .idUsuarioEditor(e.getUsuarioEditor() != null ? e.getUsuarioEditor().getId() : null)
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ContenidoWebEntity toEntity(ContenidoWeb d, UsuarioAdminEntity editor) {
        if (d == null) return null;
        return ContenidoWebEntity.builder()
                .id(d.getId())
                .idSeccion(d.getIdSeccion() != null ? d.getIdSeccion().intValue() : null)
                .idTipoContenido(d.getIdTipoContenido() != null ? d.getIdTipoContenido().intValue() : null)
                .clave(d.getClave())
                .valorEs(d.getValorEs())
                .valorEn(d.getValorEn())
                .activo(d.isActivo())
                .usuarioEditor(editor)
                .build();
    }

    public ImagenGaleria toDomain(ImagenGaleriaEntity e) {
        if (e == null) return null;
        return ImagenGaleria.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .urlImagen(e.getUrlImagen())
                .altTexto(e.getAltTexto())
                .categoriaImagen(e.getCategoriaImagen())
                .ordenVisualizacion(e.getOrdenVisualizacion())
                .activo(e.isActivo())
                .idUsuarioSubio(e.getUsuarioSubio() != null ? e.getUsuarioSubio().getId() : null)
                .fechaSubida(e.getFechaSubida())
                .build();
    }

    public ImagenGaleriaEntity toEntity(ImagenGaleria d, SedeEntity sede, UsuarioAdminEntity usuario) {
        if (d == null) return null;
        return ImagenGaleriaEntity.builder()
                .id(d.getId())
                .sede(sede)
                .urlImagen(d.getUrlImagen())
                .altTexto(d.getAltTexto())
                .categoriaImagen(d.getCategoriaImagen())
                .ordenVisualizacion(d.getOrdenVisualizacion())
                .activo(d.isActivo())
                .usuarioSubio(usuario)
                .build();
    }

    public Banner toDomain(BannerEntity e) {
        if (e == null) return null;
        return Banner.builder()
                .id(e.getId())
                .idSede(e.getSede() != null ? e.getSede().getId() : null)
                .titulo(e.getTitulo())
                .descripcion(e.getDescripcion())
                .imagenUrl(e.getImagenUrl())
                .enlaceDestino(e.getEnlaceDestino())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .activo(e.isActivo())
                .orden(e.getOrden())
                .idUsuarioCreador(e.getUsuarioCreador() != null ? e.getUsuarioCreador().getId() : null)
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public BannerEntity toEntity(Banner d, SedeEntity sede, UsuarioAdminEntity creador) {
        if (d == null) return null;
        return BannerEntity.builder()
                .id(d.getId())
                .sede(sede)
                .titulo(d.getTitulo())
                .descripcion(d.getDescripcion())
                .imagenUrl(d.getImagenUrl())
                .enlaceDestino(d.getEnlaceDestino())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .activo(d.isActivo())
                .orden(d.getOrden())
                .usuarioCreador(creador)
                .build();
    }

    public Resena toDomain(ResenaEntity e) {
        if (e == null) return null;
        return Resena.builder()
                .id(e.getId())
                .idCliente(e.getCliente() != null ? e.getCliente().getId() : null)
                .nombreAutor(e.getNombreAutor())
                .contenido(e.getContenido())
                .calificacion(e.getCalificacion())
                .aprobada(e.isAprobada())
                .idUsuarioAprueba(e.getUsuarioAprueba() != null ? e.getUsuarioAprueba().getId() : null)
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public ResenaEntity toEntity(Resena d, ClienteEntity cliente, UsuarioAdminEntity aprueba) {
        if (d == null) return null;
        return ResenaEntity.builder()
                .id(d.getId())
                .cliente(cliente)
                .nombreAutor(d.getNombreAutor())
                .contenido(d.getContenido())
                .calificacion(d.getCalificacion())
                .aprobada(d.isAprobada())
                .usuarioAprueba(aprueba)
                .build();
    }
}