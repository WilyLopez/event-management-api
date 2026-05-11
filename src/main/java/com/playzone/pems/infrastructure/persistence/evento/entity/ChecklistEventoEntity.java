package com.playzone.pems.infrastructure.persistence.evento.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "checklistevento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idchecklist")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ideventoprivado", nullable = false)
    private EventoPrivadoEntity eventoPrivado;

    @Column(nullable = false, length = 200)
    private String tarea;

    @Column(nullable = false)
    @Builder.Default
    private boolean completada = false;

    @Column(nullable = false)
    @Builder.Default
    private int orden = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuariocompleto")
    private UsuarioAdminEntity usuarioCompleto;

    @Column(name = "fechacompletado")
    private LocalDateTime fechaCompletado;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}