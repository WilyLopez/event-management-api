package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "beneficiopaquete")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficioPaqueteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idbeneficio")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpaquete", nullable = false)
    private PaqueteEventoEntity paquete;

    @Column(nullable = false, length = 60)
    private String descripcion;

    @Column(nullable = false)
    private int orden;
}
