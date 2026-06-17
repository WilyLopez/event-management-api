package com.playzone.pems.infrastructure.persistence.cms.mapper;

import com.playzone.pems.domain.cms.model.*;
import com.playzone.pems.infrastructure.persistence.cms.entity.*;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import org.springframework.stereotype.Component;

@Component
public class CmsEntityMapper {

    public Banner toDomain(BannerEntity e) {
        if (e == null) return null;
        return Banner.builder()
                .id(e.getId())
                .idSede(e.getSede() != null ? e.getSede().getId() : null)
                .titulo(e.getTitulo())
                .descripcion(e.getDescripcion())
                .imagenUrl(e.getImagenUrl())
                .imagenMovilUrl(e.getImagenMovilUrl())
                .enlaceDestino(e.getEnlaceDestino())
                .textoBoton(e.getTextoBoton())
                .colorOverlay(e.getColorOverlay())
                .tipoBanner(e.getTipoBanner())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .activo(e.isActivo())
                .orden(e.getOrden())
                .prioridad(e.getPrioridad())
                .soloMovil(e.isSoloMovil())
                .soloDesktop(e.isSoloDesktop())
                .fechaCreacion(e.getCreatedAt())
                .build();
    }

    public BannerEntity toEntity(Banner d, SedeEntity sede) {
        if (d == null) return null;
        return BannerEntity.builder()
                .id(d.getId())
                .sede(sede)
                .titulo(d.getTitulo())
                .descripcion(d.getDescripcion())
                .imagenUrl(d.getImagenUrl())
                .imagenMovilUrl(d.getImagenMovilUrl())
                .enlaceDestino(d.getEnlaceDestino())
                .textoBoton(d.getTextoBoton())
                .colorOverlay(d.getColorOverlay())
                .tipoBanner(d.getTipoBanner())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .activo(d.isActivo())
                .orden(d.getOrden())
                .prioridad(d.getPrioridad())
                .soloMovil(d.isSoloMovil())
                .soloDesktop(d.isSoloDesktop())
                .build();
    }

    public ContenidoLegal toDomain(ContenidoLegalEntity e) {
        if (e == null) return null;
        return ContenidoLegal.builder()
                .id(e.getId())
                .tipo(e.getTipo())
                .titulo(e.getTitulo())
                .contenido(e.getContenido())
                .version(e.getVersion())
                .activo(e.isActivo())
                .fechaActualizacion(e.getUpdatedAt())
                .build();
    }

    public ContenidoLegalEntity toEntity(ContenidoLegal d) {
        if (d == null) return null;
        return ContenidoLegalEntity.builder()
                .id(d.getId())
                .tipo(d.getTipo())
                .titulo(d.getTitulo())
                .contenido(d.getContenido())
                .version(d.getVersion())
                .activo(d.isActivo())
                .build();
    }

    public ContenidoWeb toDomain(ContenidoWebEntity e) {
        if (e == null) return null;
        return ContenidoWeb.builder()
                .id(e.getId())
                .seccionCodigo(e.getSeccionCodigo())
                .tipoContenidoCodigo(e.getTipoContenidoCodigo())
                .clave(e.getClave())
                .valorEs(e.getValorEs())
                .valorEn(e.getValorEn())
                .imagenUrl(e.getImagenUrl())
                .descripcion(e.getDescripcion())
                .ordenVisualizacion(e.getOrden())
                .visible(e.isVisible())
                .version(e.getVersion())
                .metadatos(e.getMetadatos())
                .idUsuarioEditor(e.getUpdatedBy())
                .fechaActualizacion(e.getUpdatedAt())
                .build();
    }

    public ContenidoWebEntity toEntity(ContenidoWeb d) {
        if (d == null) return null;
        return ContenidoWebEntity.builder()
                .id(d.getId())
                .seccionCodigo(d.getSeccionCodigo())
                .tipoContenidoCodigo(d.getTipoContenidoCodigo())
                .clave(d.getClave())
                .valorEs(d.getValorEs())
                .valorEn(d.getValorEn())
                .imagenUrl(d.getImagenUrl())
                .descripcion(d.getDescripcion())
                .orden(d.getOrdenVisualizacion())
                .visible(d.isVisible())
                .version(d.getVersion())
                .metadatos(d.getMetadatos())
                .updatedBy(d.getIdUsuarioEditor())
                .build();
    }

    public Faq toDomain(FaqEntity e) {
        if (e == null) return null;
        return Faq.builder()
                .id(e.getId())
                .pregunta(e.getPregunta())
                .respuesta(e.getRespuesta())
                .ordenVisualizacion(e.getOrden())
                .visible(e.isVisible())
                .idUsuarioEditor(e.getUpdatedBy())
                .fechaActualizacion(e.getUpdatedAt())
                .build();
    }

    public FaqEntity toEntity(Faq d) {
        if (d == null) return null;
        return FaqEntity.builder()
                .id(d.getId())
                .pregunta(d.getPregunta())
                .respuesta(d.getRespuesta())
                .orden(d.getOrdenVisualizacion())
                .visible(d.isVisible())
                .updatedBy(d.getIdUsuarioEditor())
                .build();
    }

    public Resena toDomain(ResenaEntity e) {
        if (e == null) return null;
        return Resena.builder()
                .id(e.getId())
                .idCliente(e.getClienteId())
                .idEventoPrivado(e.getEventoId())
                .nombreAutor(e.getNombreAutor())
                .contenido(e.getContenido())
                .calificacion(e.getCalificacion())
                .aprobada(e.isAprobada())
                .fotoUrl(e.getFotoUrl())
                .respuestaAdmin(e.getRespuestaAdmin())
                .fechaRespuesta(e.getFechaRespuesta())
                .destacada(e.isDestacada())
                .mostrarHome(e.isMostrarHome())
                .idUsuarioAprueba(e.getAprobadaPor())
                .fechaCreacion(e.getCreatedAt())
                .build();
    }

    public ResenaEntity toEntity(Resena d) {
        if (d == null) return null;
        return ResenaEntity.builder()
                .id(d.getId())
                .clienteId(d.getIdCliente())
                .eventoId(d.getIdEventoPrivado())
                .nombreAutor(d.getNombreAutor())
                .contenido(d.getContenido())
                .calificacion(d.getCalificacion())
                .aprobada(d.isAprobada())
                .fotoUrl(d.getFotoUrl())
                .respuestaAdmin(d.getRespuestaAdmin())
                .fechaRespuesta(d.getFechaRespuesta())
                .destacada(d.isDestacada())
                .mostrarHome(d.isMostrarHome())
                .aprobadaPor(d.getIdUsuarioAprueba())
                .build();
    }

    public ImagenGaleria toDomain(ImagenGaleriaEntity e) {
        if (e == null) return null;
        return ImagenGaleria.builder()
                .id(e.getId())
                .idSede(e.getSede() != null ? e.getSede().getId() : null)
                .urlImagen(e.getUrlImagen())
                .altTexto(e.getAltTexto())
                .titulo(e.getTitulo())
                .descripcion(e.getDescripcion())
                .categoriaImagen(e.getCategoriaImagen())
                .tipoMime(e.getTipoMime())
                .tamanioBytes(e.getTamanioBytes())
                .ordenVisualizacion(e.getOrdenVisualizacion())
                .activo(e.isActivo())
                .destacada(e.isDestacada())
                .eliminada(e.getDeletedAt() != null)
                .idUsuarioSubio(e.getSubidaPor())
                .fechaSubida(e.getFechaSubida())
                .build();
    }

    public ImagenGaleriaEntity toEntity(ImagenGaleria d, SedeEntity sede) {
        if (d == null) return null;
        return ImagenGaleriaEntity.builder()
                .id(d.getId())
                .sede(sede)
                .urlImagen(d.getUrlImagen())
                .altTexto(d.getAltTexto())
                .titulo(d.getTitulo())
                .descripcion(d.getDescripcion())
                .categoriaImagen(d.getCategoriaImagen())
                .tipoMime(d.getTipoMime())
                .tamanioBytes(d.getTamanioBytes())
                .ordenVisualizacion(d.getOrdenVisualizacion())
                .activo(d.isActivo())
                .destacada(d.isDestacada())
                .subidaPor(d.getIdUsuarioSubio())
                .build();
    }

    public SeccionWeb toDomain(SeccionWebEntity e) {
        if (e == null) return null;
        return SeccionWeb.builder()
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .esSistema(e.isEsSistema())
                .activo(e.isActivo())
                .orden(e.getOrden())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public SeccionWebEntity toEntity(SeccionWeb d) {
        if (d == null) return null;
        return SeccionWebEntity.builder()
                .codigo(d.getCodigo())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .esSistema(d.isEsSistema())
                .activo(d.isActivo())
                .orden(d.getOrden())
                .build();
    }

    public TipoContenido toDomain(TipoContenidoEntity e) {
        if (e == null) return null;
        return TipoContenido.builder()
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .esSistema(e.isEsSistema())
                .activo(e.isActivo())
                .orden(e.getOrden())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public TipoContenidoEntity toEntity(TipoContenido d) {
        if (d == null) return null;
        return TipoContenidoEntity.builder()
                .codigo(d.getCodigo())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .esSistema(d.isEsSistema())
                .activo(d.isActivo())
                .orden(d.getOrden())
                .build();
    }

    public ConfiguracionPublica toDomain(ConfiguracionPublicaEntity e) {
        if (e == null) return null;
        return ConfiguracionPublica.builder()
                .id(e.getId())
                .nombreNegocio(e.getNombreNegocio())
                .slogan(e.getSlogan())
                .logoPath(e.getLogoPath())
                .faviconPath(e.getFaviconPath())
                .telefono(e.getTelefono())
                .telefonoSecundario(e.getTelefonoSecundario())
                .whatsapp(e.getWhatsapp())
                .correo(e.getCorreo())
                .correoSecundario(e.getCorreoSecundario())
                .direccion(e.getDireccion())
                .facebookUrl(e.getFacebookUrl())
                .instagramUrl(e.getInstagramUrl())
                .tiktokUrl(e.getTiktokUrl())
                .youtubeUrl(e.getYoutubeUrl())
                .googleMapsUrl(e.getGoogleMapsUrl())
                .horarioSemana(e.getHorarioSemana())
                .horarioFinSemana(e.getHorarioFinSemana())
                .copyrightTexto(e.getCopyrightTexto())
                .metaTitle(e.getMetaTitle())
                .metaDescription(e.getMetaDescription())
                .metaKeywords(e.getMetaKeywords())
                .openGraphTitle(e.getOpenGraphTitle())
                .openGraphDescription(e.getOpenGraphDescription())
                .openGraphImagePath(e.getOpenGraphImagePath())
                .googleAnalyticsId(e.getGoogleAnalyticsId())
                .metaPixelId(e.getMetaPixelId())
                .colorTema(e.getColorTema())
                .colorSecundario(e.getColorSecundario())
                .esMantenimientoActivo(e.isEsMantenimientoActivo())
                .mensajeMantenimiento(e.getMensajeMantenimiento())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .updatedBy(e.getUpdatedBy())
                .build();
    }

    public ConfiguracionPublicaEntity toEntity(ConfiguracionPublica d) {
        if (d == null) return null;
        return ConfiguracionPublicaEntity.builder()
                .id(d.getId())
                .nombreNegocio(d.getNombreNegocio())
                .slogan(d.getSlogan())
                .logoPath(d.getLogoPath())
                .faviconPath(d.getFaviconPath())
                .telefono(d.getTelefono())
                .telefonoSecundario(d.getTelefonoSecundario())
                .whatsapp(d.getWhatsapp())
                .correo(d.getCorreo())
                .correoSecundario(d.getCorreoSecundario())
                .direccion(d.getDireccion())
                .facebookUrl(d.getFacebookUrl())
                .instagramUrl(d.getInstagramUrl())
                .tiktokUrl(d.getTiktokUrl())
                .youtubeUrl(d.getYoutubeUrl())
                .googleMapsUrl(d.getGoogleMapsUrl())
                .horarioSemana(d.getHorarioSemana())
                .horarioFinSemana(d.getHorarioFinSemana())
                .copyrightTexto(d.getCopyrightTexto())
                .metaTitle(d.getMetaTitle())
                .metaDescription(d.getMetaDescription())
                .metaKeywords(d.getMetaKeywords())
                .openGraphTitle(d.getOpenGraphTitle())
                .openGraphDescription(d.getOpenGraphDescription())
                .openGraphImagePath(d.getOpenGraphImagePath())
                .googleAnalyticsId(d.getGoogleAnalyticsId())
                .metaPixelId(d.getMetaPixelId())
                .colorTema(d.getColorTema())
                .colorSecundario(d.getColorSecundario())
                .esMantenimientoActivo(d.isEsMantenimientoActivo())
                .mensajeMantenimiento(d.getMensajeMantenimiento())
                .updatedBy(d.getUpdatedBy())
                .build();
    }
}
