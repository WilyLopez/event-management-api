package com.playzone.pems.infrastructure.persistence.facturacion.entity;

import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "serie_comprobante",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sede_id", "serie"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerieComprobanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sede_id", nullable = false)
    private SedeEntity sede;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comp_codigo", nullable = false, length = 30)
    private TipoComprobante tipoComprobante;

    @Column(name = "serie", nullable = false, length = 4)
    private String serie;

    @Column(name = "correlativo_actual", nullable = false)
    private int correlativoActual = 0;

    @Column(name = "es_activa", nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
