package com.playzone.pems.infrastructure.persistence.inventario.entity;

import com.playzone.pems.domain.inventario.model.enums.TipoMovimiento;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientoinventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idmovimiento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idproducto", nullable = false)
    private ProductoEntity producto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipomovimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "stockanterior", nullable = false)
    private int stockAnterior;

    @Column(name = "stockresultante", nullable = false)
    private int stockResultante;

    @Column(nullable = false, length = 200)
    private String motivo;

    @Column(name = "idventa")
    private Long idVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario")
    private UsuarioAdminEntity usuario;

    @CreationTimestamp
    @Column(name = "fechamovimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;
}