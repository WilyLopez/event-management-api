package com.playzone.pems.infrastructure.persistence.finanzas.entity;

import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gastoeventoprivado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoEventoPrivadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idgasto")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ideventoprivado", nullable = false)
    private EventoPrivadoEntity eventoPrivado;

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
