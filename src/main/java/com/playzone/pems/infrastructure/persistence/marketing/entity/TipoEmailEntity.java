package com.playzone.pems.infrastructure.persistence.marketing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipoemail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoEmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipoemail")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
