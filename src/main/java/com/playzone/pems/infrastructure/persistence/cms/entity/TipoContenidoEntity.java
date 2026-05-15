package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipocontenido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoContenidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipocontenido")
    private Long id;

    @Column(unique = true, nullable = false, length = 40)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String descripcion;
}
