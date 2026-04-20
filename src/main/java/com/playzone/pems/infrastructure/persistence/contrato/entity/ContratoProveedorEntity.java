package com.playzone.pems.infrastructure.persistence.contrato.entity;

import com.playzone.pems.domain.contrato.model.enums.ContratadoPor;
import com.playzone.pems.infrastructure.persistence.proveedor.entity.ProveedorEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contratoproveedor",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idcontrato", "idproveedor"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoProveedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcontratoproveedor")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcontrato", nullable = false)
    private ContratoEntity contrato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idproveedor", nullable = false)
    private ProveedorEntity proveedor;

    @Column(name = "serviciodescripcion", length = 300)
    private String servicioDescripcion;

    @Column(name = "montoacordado", precision = 10, scale = 2)
    private BigDecimal montoAcordado;

    @Enumerated(EnumType.STRING)
    @Column(name = "contratadopor", nullable = false, length = 30)
    private ContratadoPor contratadoPor = ContratadoPor.EMPRESA;
}