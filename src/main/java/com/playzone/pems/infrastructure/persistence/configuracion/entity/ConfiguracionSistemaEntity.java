package com.playzone.pems.infrastructure.persistence.configuracion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "configuracionsistema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionSistemaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idconfiguracion")
    private Long id;

    @Column(nullable = false, length = 80, unique = true)
    private String clave;

    @Column(nullable = false, length = 500)
    private String valor;

    @Column(length = 300)
    private String descripcion;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String tipo = "TEXTO";

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;
}
