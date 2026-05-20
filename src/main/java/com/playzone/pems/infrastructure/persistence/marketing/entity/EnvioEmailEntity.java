package com.playzone.pems.infrastructure.persistence.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "envioemail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioEmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idenvioemail")
    private Long id;

    @Column(name = "idcampanaemail")
    private Long idCampanaEmail;

    @Column(name = "idcliente")
    private Long idCliente;

    @Column(nullable = false, length = 120)
    private String destinatario;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String estado = "PENDIENTE";

    @Column(nullable = false)
    @Builder.Default
    private int intentos = 0;

    @Column(name = "fechaenvio")
    private Instant fechaEnvio;

    @Column(name = "mensajeerror", length = 500)
    private String mensajeError;

    @Column(name = "proveedormensajeid", length = 200)
    private String proveedorMensajeId;

    @CreationTimestamp
    @Column(name = "fechacreacion", updatable = false)
    private Instant fechaCreacion;
}
