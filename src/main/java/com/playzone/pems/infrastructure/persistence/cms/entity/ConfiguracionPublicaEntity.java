package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuracionpublica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionPublicaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idconfiguracionpublica")
    private Long id;

    @Column(name = "nombrenegocio", nullable = false, length = 150)
    private String nombreNegocio;

    @Column(length = 250)
    private String slogan;

    @Column(name = "logourl", length = 500)
    private String logoUrl;

    @Column(name = "faviconurl", length = 500)
    private String faviconUrl;

    @Column(length = 20)
    private String telefono;

    @Column(name = "telefonosecundario", length = 20)
    private String telefonoSecundario;

    @Column(length = 20)
    private String whatsapp;

    @Column(length = 120)
    private String correo;

    @Column(name = "correosecundario", length = 120)
    private String correoSecundario;

    @Column(length = 300)
    private String direccion;

    @Column(name = "facebookurl", length = 300)
    private String facebookUrl;

    @Column(name = "instagramurl", length = 300)
    private String instagramUrl;

    @Column(name = "tiktokurl", length = 300)
    private String tiktokUrl;

    @Column(name = "youtubeurl", length = 300)
    private String youtubeUrl;

    @Column(name = "googlemapsurl", length = 500)
    private String googleMapsUrl;

    @Column(name = "horariosemana", length = 120)
    private String horarioSemana;

    @Column(name = "horariofindesemana", length = 120)
    private String horarioFinDeSemana;

    @Column(name = "copyrighttexto", length = 300)
    private String copyrightTexto;

    @Column(name = "metatitle", length = 200)
    private String metaTitle;

    @Column(name = "metadescription", length = 500)
    private String metaDescription;

    @Column(name = "metakeywords", length = 500)
    private String metaKeywords;

    @Column(name = "opengraphtitle", length = 200)
    private String openGraphTitle;

    @Column(name = "opengraphdescription", length = 500)
    private String openGraphDescription;

    @Column(name = "opengraphimageurl", length = 500)
    private String openGraphImageUrl;

    @Column(name = "googleanalyticsid", length = 120)
    private String googleAnalyticsId;

    @Column(name = "metapixelid", length = 120)
    private String metaPixelId;

    @Column(name = "colortema", length = 20)
    private String colorTema;

    @Column(name = "colorsecundario", length = 20)
    private String colorSecundario;

    @Column(name = "mantenimientoactivo", nullable = false)
    private boolean mantenimientoActivo = false;

    @Column(name = "mensajemantenimiento", length = 500)
    private String mensajeMantenimiento;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
