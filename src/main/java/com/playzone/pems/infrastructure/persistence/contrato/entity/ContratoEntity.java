package com.playzone.pems.infrastructure.persistence.contrato.entity;

import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contrato_evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoPrivadoEntity eventoPrivado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoContrato estado;

    @Column(name = "contenido_texto", columnDefinition = "TEXT")
    private String contenidoTexto;

    @Column(name = "archivo_pdf_path", length = 500)
    private String archivoPdfUrl;

    @Column(name = "firmado_at")
    private OffsetDateTime fechaFirma;

    @Column(name = "redactor_id", columnDefinition = "uuid")
    private UUID redactorId;

    @Column(length = 60)
    private String plantilla;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "version_v")
    private int version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
