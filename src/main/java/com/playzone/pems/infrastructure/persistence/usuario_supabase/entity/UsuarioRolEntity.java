package com.playzone.pems.infrastructure.persistence.usuario_supabase.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario_rol")
@IdClass(UsuarioRolId.class)
@Getter
@NoArgsConstructor
public class UsuarioRolEntity {

    @Id
    @Column(name = "usuario_id", columnDefinition = "uuid", nullable = false)
    private UUID usuarioId;

    @Id
    @Column(name = "rol_codigo", nullable = false)
    private String rolCodigo;

    @Column(name = "asignado_at", nullable = false)
    private OffsetDateTime asignadoAt;
}
