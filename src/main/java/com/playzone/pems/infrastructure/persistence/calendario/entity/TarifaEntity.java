package com.playzone.pems.infrastructure.persistence.calendario.entity;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarifa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idsede", "idtipodiacod", "vigenciadesde"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtarifa")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Enumerated(EnumType.STRING)
    @Column(name = "idtipodiacod", nullable = false, length = 30)
    private TipoDia tipoDia;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "vigenciadesde", nullable = false)
    private LocalDate vigenciaDesde;

    @Column(name = "vigenciahasta")
    private LocalDate vigenciaHasta;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuariocreador", nullable = false)
    private UsuarioAdminEntity usuarioCreador;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}