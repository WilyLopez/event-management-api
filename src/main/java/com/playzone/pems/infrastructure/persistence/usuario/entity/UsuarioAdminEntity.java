package com.playzone.pems.infrastructure.persistence.usuario.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarioadmin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioAdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuarioadmin")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(name = "contresenahash", nullable = false, length = 255)
    private String contrasenaHash;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String rol = "ADMINISTRATIVO";

    @Column(name = "fotoperfilurl", length = 500)
    private String fotoPerfilUrl;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "debecambiarcontrasena", nullable = false)
    @Builder.Default
    private boolean debeCambiarContrasena = true;

    @Column(name = "intentosfallidos", nullable = false)
    private int intentosFallidos = 0;

    @Column(name = "bloqueadohasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "ultimoacceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "ultimocambiocontrasena")
    private LocalDateTime ultimoCambioContrasena;

    @Column(name = "creado_por")
    private Long creadoPor;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
