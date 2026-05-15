package com.playzone.pems.infrastructure.persistence.cms.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idbanner")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsede")
    private SedeEntity sede;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 400)
    private String descripcion;

    @Column(name = "imagenurl", nullable = false, length = 500)
    private String imagenUrl;

    @Column(name = "imagemovilurl", length = 500)
    private String imagenMovilUrl;

    @Column(name = "enlacedestino", length = 500)
    private String enlaceDestino;

    @Column(name = "textoboton", length = 80)
    private String textoBoton;

    @Column(name = "coloroverlay", length = 20)
    private String colorOverlay;

    @Column(name = "tipobanner", nullable = false, length = 40)
    private String tipoBanner = "HOME";

    @Column(name = "fechainicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fechafin")
    private LocalDate fechaFin;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private int orden = 0;

    @Column(nullable = false)
    private int prioridad = 0;

    @Column(name = "solomovil", nullable = false)
    private boolean soloMovil = false;

    @Column(name = "solodesktop", nullable = false)
    private boolean soloDesktop = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuariocreador")
    private UsuarioAdminEntity usuarioCreador;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}