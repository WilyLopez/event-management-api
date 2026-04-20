package com.playzone.pems.infrastructure.persistence.proveedor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "proveedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproveedor")
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(unique = true, length = 11)
    private String ruc;

    @Column(name = "contactonombre", length = 120)
    private String contactoNombre;

    @Column(name = "contactotelefono", length = 20)
    private String contactoTelefono;

    @Column(name = "contactocorreo", length = 120)
    private String contactoCorreo;

    @Column(name = "tiposervicio", nullable = false, length = 200)
    private String tipoServicio;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}