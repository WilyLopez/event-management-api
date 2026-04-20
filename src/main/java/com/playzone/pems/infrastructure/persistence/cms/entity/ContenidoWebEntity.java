package com.playzone.pems.infrastructure.persistence.cms.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contenidoweb",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idseccion", "clave"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoWebEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcontenidoweb")
    private Long id;

    @Column(name = "idseccion", nullable = false)
    private Long idSeccion;

    @Column(name = "idtipocontenido", nullable = false)
    private Long idTipoContenido;

    @Column(nullable = false, length = 100)
    private String clave;

    @Column(name = "valores", nullable = false, columnDefinition = "TEXT")
    private String valorEs;

    @Column(name = "valoren", columnDefinition = "TEXT")
    private String valorEn;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuarioeditor")
    private UsuarioAdminEntity usuarioEditor;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}