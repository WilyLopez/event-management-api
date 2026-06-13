package com.playzone.pems.infrastructure.persistence.usuario_supabase.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "staff_perfil")
@Getter
@NoArgsConstructor
public class StaffPerfilEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "usuario_id", columnDefinition = "uuid", nullable = false)
    private UUID usuarioId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "codigo_empleado")
    private String codigoEmpleado;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "telefono_emergencia")
    private String telefonoEmergencia;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "es_activo", nullable = false)
    private boolean esActivo;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
