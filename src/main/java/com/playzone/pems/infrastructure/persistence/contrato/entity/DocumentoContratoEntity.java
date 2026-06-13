package com.playzone.pems.infrastructure.persistence.contrato.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contrato_documento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoContratoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoEntity contrato;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "archivo_path", nullable = false)
    private String archivoUrl;

    @Column(name = "tipo_archivo", nullable = false, length = 50)
    private String tipoArchivo;

    @Column(name = "tamano_bytes")
    private Long tamanobytes;

    @Column(name = "subido_por", nullable = false, columnDefinition = "uuid")
    private UUID subidoPor;

    @CreationTimestamp
    @Column(name = "subido_at", nullable = false, updatable = false)
    private OffsetDateTime fechaCarga;
}
