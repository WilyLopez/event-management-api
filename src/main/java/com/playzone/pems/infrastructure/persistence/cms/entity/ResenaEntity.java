package com.playzone.pems.infrastructure.persistence.cms.entity;

import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "resena")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResenaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idresena")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente")
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ideventoprivado")
    private EventoPrivadoEntity eventoPrivado;

    @Column(name = "nombreautor", nullable = false, length = 120)
    private String nombreAutor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private int calificacion;

    @Column(nullable = false)
    private boolean aprobada = false;

    @Column(name = "fotourl", length = 500)
    private String fotoUrl;

    @Column(name = "respuestaadmin", columnDefinition = "TEXT")
    private String respuestaAdmin;

    @Column(name = "fecharespuesta")
    private LocalDateTime fechaRespuesta;

    @Column(nullable = false)
    private boolean destacada = false;

    @Column(name = "mostrarhome", nullable = false)
    private boolean mostrarHome = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuarioaprueba")
    private UsuarioAdminEntity usuarioAprueba;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}