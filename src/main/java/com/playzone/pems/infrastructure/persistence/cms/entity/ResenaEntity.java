package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "resena")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResenaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "evento_id")
    private Long eventoId;

    @Column(name = "nombre_autor", nullable = false, length = 120)
    private String nombreAutor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private int calificacion;

    @Column(name = "es_aprobada", nullable = false)
    private boolean aprobada = false;

    @Column(name = "foto_path", length = 500)
    private String fotoUrl;

    @Column(name = "respuesta_admin", columnDefinition = "TEXT")
    private String respuestaAdmin;

    @Column(name = "respondida_at")
    private OffsetDateTime fechaRespuesta;

    @Column(name = "es_destacada", nullable = false)
    private boolean destacada = false;

    @Column(name = "mostrar_home", nullable = false)
    private boolean mostrarHome = true;

    @Column(name = "aprobada_por", columnDefinition = "uuid")
    private UUID aprobadaPor;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
