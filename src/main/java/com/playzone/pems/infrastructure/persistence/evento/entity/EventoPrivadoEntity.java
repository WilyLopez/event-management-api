package com.playzone.pems.infrastructure.persistence.evento.entity;

import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.infrastructure.persistence.calendario.entity.TurnoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventoprivado",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idsede", "fechaevento", "idturno"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoPrivadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ideventoprivado")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcliente", nullable = false)
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Enumerated(EnumType.STRING)
    @Column(name = "idestado", nullable = false, length = 40)
    private EstadoEventoPrivado estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idturno", nullable = false)
    private TurnoEntity turno;

    @Column(name = "fechaevento", nullable = false)
    private LocalDate fechaEvento;

    @Column(name = "tipoevento", nullable = false, length = 200)
    private String tipoEvento;

    @Column(name = "contactoadicional", length = 200)
    private String contactoAdicional;

    @Column(name = "aforodeclarado")
    private Integer aforoDeclarado;

    @Column(name = "preciototalcontrato", precision = 10, scale = 2)
    private BigDecimal precioTotalContrato;

    @Column(name = "montoadelanto", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal montoAdelanto = BigDecimal.ZERO;

    @Column(name = "motivocancelacion", length = 500)
    private String motivoCancelacion;

    @Column(name = "notasinternas", columnDefinition = "TEXT")
    private String notasInternas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuariogestor")
    private UsuarioAdminEntity usuarioGestor;

    @Column(name = "estadooperativo", length = 40)
    private String estadoOperativo;

    @Column(name = "checklistcompleto", nullable = false)
    @Builder.Default
    private boolean checklistCompleto = false;

    @Column(name = "horainicioreal")
    private LocalDateTime horaInicioReal;

    @Column(name = "horafinreal")
    private LocalDateTime horaFinReal;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}