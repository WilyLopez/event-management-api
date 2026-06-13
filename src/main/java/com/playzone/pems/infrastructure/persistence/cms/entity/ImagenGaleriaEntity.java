package com.playzone.pems.infrastructure.persistence.cms.entity;

import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "galeria_imagen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenGaleriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sede_id", nullable = false)
    private SedeEntity sede;

    @Column(name = "archivo_path", nullable = false, length = 500)
    private String urlImagen;

    @Column(name = "alt_texto", length = 200)
    private String altTexto;

    @Column(length = 150)
    private String titulo;

    @Column(length = 300)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 20)
    private CategoriaImagen categoriaImagen;

    @Column(name = "tipo_mime", length = 50)
    private String tipoMime;

    @Column(name = "tamano_bytes")
    private Long tamanioBytes;

    @Column(name = "orden", nullable = false)
    private int ordenVisualizacion = 0;

    @Column(name = "es_activa", nullable = false)
    private boolean activo = true;

    @Column(name = "es_destacada", nullable = false)
    private boolean destacada = false;

    @Column(name = "subida_por", columnDefinition = "uuid")
    private UUID subidaPor;

    @CreationTimestamp
    @Column(name = "subida_at", nullable = false, updatable = false)
    private OffsetDateTime fechaSubida;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
