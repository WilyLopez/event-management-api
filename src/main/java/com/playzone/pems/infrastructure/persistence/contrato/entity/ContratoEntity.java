package com.playzone.pems.infrastructure.persistence.contrato.entity;

import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contrato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false, unique = true)
    private EventoPrivadoEntity eventoPrivado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_codigo", nullable = false, length = 40)
    private EstadoContrato estado;

    @Column(name = "contenido_texto", columnDefinition = "TEXT")
    private String contenidoTexto;

    @Column(name = "archivo_pdf_path", length = 500)
    private String archivoPdfUrl;

    @Column(name = "fecha_firma")
    private LocalDate fechaFirma;

    @Column(name = "redactor_id", columnDefinition = "uuid")
    private UUID redactorId;

    @Column(name = "plantilla", length = 100)
    private String plantilla;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "version", nullable = false)
    private int version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime fechaActualizacion;
}
