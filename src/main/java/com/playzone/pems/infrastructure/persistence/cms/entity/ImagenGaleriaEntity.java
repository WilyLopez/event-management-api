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

    @Enumerated(EnumType.STRING)
    @Column(name = "categoriaimagen", nullable = false, length = 20)
    private CategoriaImagen categoriaImagen;

    @Column(name = "ordenvisualizacion", nullable = false)
    private int ordenVisualizacion = 0;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuariosubio")
    private UsuarioAdminEntity usuarioSubio;

    @CreationTimestamp
    @Column(name = "fechasubida", nullable = false, updatable = false)
    private LocalDateTime fechaSubida;
}