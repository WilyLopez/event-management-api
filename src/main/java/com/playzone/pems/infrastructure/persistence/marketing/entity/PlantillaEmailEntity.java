package com.playzone.pems.infrastructure.persistence.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "plantillaemail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantillaEmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idplantillaemail")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtipoemail", nullable = false)
    private TipoEmailEntity tipoEmail;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(name = "contenidohtml", nullable = false, columnDefinition = "TEXT")
    private String contenidoHtml;

    @Column(name = "contenidofallback", columnDefinition = "TEXT")
    private String contenidoFallback;

    @Column(name = "variablespermitidas", columnDefinition = "JSONB")
    private String variablesPermitidas;

    @Column(nullable = false)
    @Builder.Default
    private boolean activa = true;

    @Column(name = "idusuarioeditor")
    private Long idUsuarioEditor;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion")
    private Instant fechaActualizacion;
}
