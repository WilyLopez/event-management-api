package com.playzone.pems.infrastructure.persistence.facturacion.entity;

import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seriecomprobante",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idsede", "serie"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerieComprobanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idserie")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Enumerated(EnumType.STRING)
    @Column(name = "idtipo", nullable = false, length = 30)
    private TipoComprobante tipoComprobante;

    @Column(nullable = false, length = 4)
    private String serie;

    @Column(name = "correlativoactual", nullable = false)
    private int correlativoActual = 0;

    @Column(nullable = false)
    private boolean activo = true;
}