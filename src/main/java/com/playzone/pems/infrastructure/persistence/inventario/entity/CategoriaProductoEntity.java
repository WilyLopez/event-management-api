package com.playzone.pems.infrastructure.persistence.inventario.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoriaproducto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategoriaproducto")
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;
}