package com.playzone.pems.infrastructure.persistence.finanzas.entity;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tipoegreso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoEgresoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipoegreso")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 30)
    private CategoriaEgreso categoria;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(name = "idusuariocreador")
    private Long idUsuarioCreador;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
