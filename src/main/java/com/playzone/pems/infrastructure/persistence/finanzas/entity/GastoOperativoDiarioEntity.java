package com.playzone.pems.infrastructure.persistence.finanzas.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gastoopertaiviodiario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoOperativoDiarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idgastooperativo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "comprobanteurl", length = 500)
    private String comprobanteUrl;

    @Column(name = "idusuarioregistra", nullable = false)
    private Long idUsuarioRegistra;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
