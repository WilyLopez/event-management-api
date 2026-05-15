package com.playzone.pems.infrastructure.persistence.cms.mapper;

import com.playzone.pems.domain.cms.model.*;
import com.playzone.pems.infrastructure.persistence.cms.entity.*;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class CmsEntityMapper {

    // ── ContenidoWeb ─────────────────────────────────────────────────────────

    public ContenidoWeb toDomain(ContenidoWebEntity e) {
        if (e == null) return null;
        return ContenidoWeb.builder()
                .id(e.getId())
                .idSeccion(e.getIdSeccion() != null ? e.getIdSeccion().longValue() : null)
                .idTipoContenido(e.getIdTipoContenido() != null ? e.getIdTipoContenido().longValue() : null)
                .clave(e.getClave())
                .valorEs(e.getValorEs())
                .valorEn(e.getValorEn())
                .imagenUrl(e.getImagenUrl())
                .descripcion(e.getDescripcion())
                .ordenVisualizacion(e.getOrdenVisualizacion())
                .visible(e.isVisible())
                .version(e.getVersion())
                .metadatos(e.getMetadatos())
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
                .imagenUrl(d.getImagenUrl())
                .descripcion(d.getDescripcion())
                .ordenVisualizacion(d.getOrdenVisualizacion())
                .visible(d.isVisible())
                .version(d.getVersion())
                .metadatos(d.getMetadatos())
                .activo(d.isActivo())
                .usuarioEditor(editor)
                .build();
    }

    // ── ImagenGaleria ────────────────────────────────────────────────────────

    public ImagenGaleria toDomain(ImagenGaleriaEntity e) {
        if (e == null) return null;
        return ImagenGaleria.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
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
                .eliminada(e.isEliminada())
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
                .titulo(d.getTitulo())
                .descripcion(d.getDescripcion())
                .categoriaImagen(d.getCategoriaImagen())
                .tipoMime(d.getTipoMime())
                .tamanioBytes(d.getTamanioBytes())
                .ordenVisualizacion(d.getOrdenVisualizacion())
                .activo(d.isActivo())
                .destacada(d.isDestacada())
                .eliminada(d.isEliminada())
                .usuarioSubio(usuario)
                .build();
    }

    // ── Banner ───────────────────────────────────────────────────────────────

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
                .imagenMovilUrl(d.getImagenMovilUrl())
                .enlaceDestino(d.getEnlaceDestino())
                .textoBoton(d.getTextoBoton())
                .colorOverlay(d.getColorOverlay())
                .tipoBanner(d.getTipoBanner() != null ? d.getTipoBanner() : "HOME")
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .activo(d.isActivo())
                .orden(d.getOrden())
                .prioridad(d.getPrioridad())
                .soloMovil(d.isSoloMovil())
                .soloDesktop(d.isSoloDesktop())
                .usuarioCreador(creador)
                .build();
    }

    // ── Resena ───────────────────────────────────────────────────────────────

    public Resena toDomain(ResenaEntity e) {
        if (e == null) return null;
        return Resena.builder()
                .id(e.getId())
                .idCliente(e.getCliente() != null ? e.getCliente().getId() : null)
                .idEventoPrivado(e.getEventoPrivado() != null ? e.getEventoPrivado().getId() : null)
                .nombreAutor(e.getNombreAutor())
                .contenido(e.getContenido())
                .calificacion(e.getCalificacion())
                .aprobada(e.isAprobada())
                .fotoUrl(e.getFotoUrl())
                .respuestaAdmin(e.getRespuestaAdmin())
                .fechaRespuesta(e.getFechaRespuesta())
                .destacada(e.isDestacada())
                .mostrarHome(e.isMostrarHome())
                .idUsuarioAprueba(e.getUsuarioAprueba() != null ? e.getUsuarioAprueba().getId() : null)
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public ResenaEntity toEntity(Resena d, ClienteEntity cliente, UsuarioAdminEntity aprueba,
                                 EventoPrivadoEntity evento) {
        if (d == null) return null;
        return ResenaEntity.builder()
                .id(d.getId())
                .cliente(cliente)
                .eventoPrivado(evento)
                .nombreAutor(d.getNombreAutor())
                .contenido(d.getContenido())
                .calificacion(d.getCalificacion())
                .aprobada(d.isAprobada())
                .fotoUrl(d.getFotoUrl())
                .respuestaAdmin(d.getRespuestaAdmin())
                .fechaRespuesta(d.getFechaRespuesta())
                .destacada(d.isDestacada())
                .mostrarHome(d.isMostrarHome())
                .usuarioAprueba(aprueba)
                .build();
    }

    // ── SeccionWeb ───────────────────────────────────────────────────────────

    public SeccionWeb toDomain(SeccionWebEntity e) {
        if (e == null) return null;
        return SeccionWeb.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .ordenVisualizacion(e.getOrdenVisualizacion())
                .visible(e.isVisible())
                .build();
    }

    public SeccionWebEntity toEntity(SeccionWeb d) {
        if (d == null) return null;
        return SeccionWebEntity.builder()
                .id(d.getId())
                .codigo(d.getCodigo())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .ordenVisualizacion(d.getOrdenVisualizacion())
                .visible(d.isVisible())
                .build();
    }

    // ── TipoContenido ────────────────────────────────────────────────────────

    public TipoContenido toDomain(TipoContenidoEntity e) {
        if (e == null) return null;
        return TipoContenido.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .descripcion(e.getDescripcion())
                .build();
    }

    // ── ConfiguracionPublica ─────────────────────────────────────────────────

    public ConfiguracionPublica toDomain(ConfiguracionPublicaEntity e) {
        if (e == null) return null;
        return ConfiguracionPublica.builder()
                .id(e.getId())
                .nombreNegocio(e.getNombreNegocio())
                .slogan(e.getSlogan())
                .logoUrl(e.getLogoUrl())
                .faviconUrl(e.getFaviconUrl())
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
                .horarioFinDeSemana(e.getHorarioFinDeSemana())
                .copyrightTexto(e.getCopyrightTexto())
                .metaTitle(e.getMetaTitle())
                .metaDescription(e.getMetaDescription())
                .metaKeywords(e.getMetaKeywords())
                .openGraphTitle(e.getOpenGraphTitle())
                .openGraphDescription(e.getOpenGraphDescription())
                .openGraphImageUrl(e.getOpenGraphImageUrl())
                .googleAnalyticsId(e.getGoogleAnalyticsId())
                .metaPixelId(e.getMetaPixelId())
                .colorTema(e.getColorTema())
                .colorSecundario(e.getColorSecundario())
                .mantenimientoActivo(e.isMantenimientoActivo())
                .mensajeMantenimiento(e.getMensajeMantenimiento())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ConfiguracionPublicaEntity toEntity(ConfiguracionPublica d) {
        if (d == null) return null;
        return ConfiguracionPublicaEntity.builder()
                .id(d.getId())
                .nombreNegocio(d.getNombreNegocio())
                .slogan(d.getSlogan())
                .logoUrl(d.getLogoUrl())
                .faviconUrl(d.getFaviconUrl())
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
                .horarioFinDeSemana(d.getHorarioFinDeSemana())
                .copyrightTexto(d.getCopyrightTexto())
                .metaTitle(d.getMetaTitle())
                .metaDescription(d.getMetaDescription())
                .metaKeywords(d.getMetaKeywords())
                .openGraphTitle(d.getOpenGraphTitle())
                .openGraphDescription(d.getOpenGraphDescription())
                .openGraphImageUrl(d.getOpenGraphImageUrl())
                .googleAnalyticsId(d.getGoogleAnalyticsId())
                .metaPixelId(d.getMetaPixelId())
                .colorTema(d.getColorTema())
                .colorSecundario(d.getColorSecundario())
                .mantenimientoActivo(d.isMantenimientoActivo())
                .mensajeMantenimiento(d.getMensajeMantenimiento())
                .build();
    }

    // ── Faq ──────────────────────────────────────────────────────────────────

    public Faq toDomain(FaqEntity e) {
        if (e == null) return null;
        return Faq.builder()
                .id(e.getId())
                .pregunta(e.getPregunta())
                .respuesta(e.getRespuesta())
                .ordenVisualizacion(e.getOrdenVisualizacion())
                .visible(e.isVisible())
                .idUsuarioEditor(e.getUsuarioEditor() != null ? e.getUsuarioEditor().getId() : null)
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public FaqEntity toEntity(Faq d, UsuarioAdminEntity editor) {
        if (d == null) return null;
        return FaqEntity.builder()
                .id(d.getId())
                .pregunta(d.getPregunta())
                .respuesta(d.getRespuesta())
                .ordenVisualizacion(d.getOrdenVisualizacion())
                .visible(d.isVisible())
                .usuarioEditor(editor)
                .build();
    }

    // ── ContenidoLegal ───────────────────────────────────────────────────────

    public ContenidoLegal toDomain(ContenidoLegalEntity e) {
        if (e == null) return null;
        return ContenidoLegal.builder()
                .id(e.getId())
                .tipo(e.getTipo())
                .titulo(e.getTitulo())
                .contenido(e.getContenido())
                .version(e.getVersion())
                .activo(e.isActivo())
                .idUsuarioEditor(e.getUsuarioEditor() != null ? e.getUsuarioEditor().getId() : null)
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ContenidoLegalEntity toEntity(ContenidoLegal d, UsuarioAdminEntity editor) {
        if (d == null) return null;
        return ContenidoLegalEntity.builder()
                .id(d.getId())
                .tipo(d.getTipo())
                .titulo(d.getTitulo())
                .contenido(d.getContenido())
                .version(d.getVersion())
                .activo(d.isActivo())
                .usuarioEditor(editor)
                .build();
    }
}
