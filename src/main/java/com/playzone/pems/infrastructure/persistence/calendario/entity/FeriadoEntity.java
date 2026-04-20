package com.playzone.pems.infrastructure.persistence.calendario.entity;

import com.playzone.pems.domain.calendario.model.enums.TipoFeriado;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Column(name = "idferiado")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "idtipoferiado", nullable = false, length = 20)
    private TipoFeriado tipoFeriado;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(nullable = false, length = 120)
    private String descripcion;

    @Column(nullable = false)
    private int anio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creadopor")
    private UsuarioAdminEntity creadoPor;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}