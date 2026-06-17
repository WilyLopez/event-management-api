package com.playzone.pems.infrastructure.persistence.calendario.entity;

import com.playzone.pems.domain.calendario.model.enums.TipoFeriado;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "feriado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeriadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_codigo", nullable = false, length = 20)
    private TipoFeriado tipoFeriado;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(nullable = false, length = 120)
    private String descripcion;

    @Column(nullable = false, insertable = false, updatable = false)
    private int anio;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}