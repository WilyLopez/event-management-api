package com.playzone.pems.infrastructure.persistence.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrefreshtoken")
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "idusuario", nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private String correo;

    @Column(name = "tipo_usuario", nullable = false)
    private String tipoUsuario;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_expira", nullable = false)
    private Instant fechaExpira;

    @Column(nullable = false)
    private boolean revocado;

    @Column(name = "ultimo_uso")
    private Instant ultimoUso;
}
