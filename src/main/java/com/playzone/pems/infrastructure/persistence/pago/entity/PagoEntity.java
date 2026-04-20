package com.playzone.pems.infrastructure.persistence.pago.entity;

import com.playzone.pems.domain.pago.model.enums.MedioPago;
import com.playzone.pems.domain.pago.model.enums.TipoPago;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpago")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "idmediopago", nullable = false, length = 30)
    private MedioPago medioPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idreservapublica")
    private ReservaPublicaEntity reservaPublica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ideventoprivado")
    private EventoPrivadoEntity eventoPrivado;

    @Column(name = "idventa")
    private Long idVenta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "referenciapago", length = 100)
    private String referenciaPago;

    @Column(name = "esparcial", nullable = false)
    private boolean esParcial = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipopago", nullable = false, length = 30)
    private TipoPago tipoPago = TipoPago.UNICO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuarioregistra")
    private UsuarioAdminEntity usuarioRegistra;

    @CreationTimestamp
    @Column(name = "fechapago", nullable = false, updatable = false)
    private LocalDateTime fechaPago;
}