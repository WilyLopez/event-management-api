package com.playzone.pems.infrastructure.persistence.promocion.entity;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.enums.TipoPromocion;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "promocion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromocionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpromocion")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "idtipopromocion", nullable = false, length = 40)
    private TipoPromocion tipoPromocion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsede")
    private SedeEntity sede;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 400)
    private String descripcion;

    @Column(name = "valordescuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento;

    @Column(length = 300)
    private String condicion;

    @Column(name = "minimopersonas")
    private Integer minimoPersonas;

    @Enumerated(EnumType.STRING)
    @Column(name = "solotipodiacod", length = 30)
    private TipoDia soloTipoDia;

    @Column(name = "fechainicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fechafin")
    private LocalDate fechaFin;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "esautomatica", nullable = false)
    private boolean esAutomatica = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuariocreador", nullable = false)
    private UsuarioAdminEntity usuarioCreador;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}