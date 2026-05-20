package com.playzone.pems.infrastructure.persistence.finanzas.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registroegreso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroEgresoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idregistroegreso")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idtipoegreso", nullable = false)
    private TipoEgresoEntity tipoEgreso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "periodoaño")
    private Integer periodoAnio;

    @Column(name = "periodomes")
    private Integer periodoMes;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "comprobanteurl", length = 500)
    private String comprobanteUrl;

    @Column(name = "esrecurrente", nullable = false)
    @Builder.Default
    private boolean esRecurrente = false;

    @Column(name = "idusuarioregistra", nullable = false)
    private Long idUsuarioRegistra;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
