package com.playzone.pems.infrastructure.persistence.cms.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id")
    private SedeEntity sede;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 400)
    private String descripcion;

    @Column(name = "imagen_path", nullable = false, length = 500)
    private String imagenUrl;

    @Column(name = "imagen_movil_path", length = 500)
    private String imagenMovilUrl;

    @Column(name = "enlace_destino", length = 500)
    private String enlaceDestino;

    @Column(name = "texto_boton", length = 80)
    private String textoBoton;

    @Column(name = "color_overlay", length = 20)
    private String colorOverlay;

    @Column(name = "tipo", nullable = false, length = 40)
    private String tipoBanner = "HOME";

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "es_activo", nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private int orden = 0;

    @Column(nullable = false)
    private int prioridad = 0;

    @Column(name = "solo_movil", nullable = false)
    private boolean soloMovil = false;

    @Column(name = "solo_desktop", nullable = false)
    private boolean soloDesktop = false;

    @Column(name = "created_by", columnDefinition = "uuid")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
