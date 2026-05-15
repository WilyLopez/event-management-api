package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seccionweb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeccionWebEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idseccion")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(name = "ordenvisualizacion", nullable = false)
    private int ordenVisualizacion = 0;

    @Column(nullable = false)
    private boolean visible = true;
}
