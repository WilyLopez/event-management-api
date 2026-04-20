package com.playzone.pems.infrastructure.persistence.fidelizacion.entity;

import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historialfidelizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialFidelizacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idhistorialfidelizacion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcliente", nullable = false)
    private ClienteEntity cliente;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idreservapublica", nullable = false, unique = true)
    private ReservaPublicaEntity reservaPublica;

    @Column(name = "visitanumero", nullable = false)
    private int visitaNumero;

    @Column(name = "esbeneficioaplicado", nullable = false)
    private boolean esBeneficioAplicado = false;

    @CreationTimestamp
    @Column(name = "fecharegistro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
}