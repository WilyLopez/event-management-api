package com.playzone.pems.infrastructure.persistence.cms.entity;

import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "imagengaleria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenGaleriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idimagengaleria")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Column(name = "urlimagen", nullable = false, length = 500)
    private String urlImagen;

    @Column(name = "alttexto", length = 200)
    private String altTexto;

    @Column(length = 150)
    private String titulo;

    @Column(length = 300)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoriaimagen", nullable = false, length = 20)
    private CategoriaImagen categoriaImagen;

    @Column(name = "tipomime", length = 50)
    private String tipoMime;

    @Column(name = "tamanobytes")
    private Long tamanioBytes;

    @Column(name = "ordenvisualizacion", nullable = false)
    private int ordenVisualizacion = 0;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private boolean destacada = false;

    @Column(nullable = false)
    private boolean eliminada = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuariosubio")
    private UsuarioAdminEntity usuarioSubio;

    @CreationTimestamp
    @Column(name = "fechasubida", nullable = false, updatable = false)
    private LocalDateTime fechaSubida;
}