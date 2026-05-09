package com.playzone.pems.infrastructure.persistence.contrato.entity;

import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcontrato")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ideventoprivado", nullable = false, unique = true)
    private EventoPrivadoEntity eventoPrivado;

    @Enumerated(EnumType.STRING)
    @Column(name = "idestado", nullable = false, length = 40)
    private EstadoContrato estado;

    @Column(name = "contenidotexto", nullable = false, columnDefinition = "TEXT")
    private String contenidoTexto;

    @Column(name = "archivopdfurl", length = 500)
    private String archivoPdfUrl;

    @Column(name = "fechafirma")
    private LocalDate fechaFirma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuarioredactor", nullable = false)
    private UsuarioAdminEntity usuarioRedactor;

    @Column(name = "plantilla", length = 100)
    private String plantilla;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "version", nullable = false)
    private int version;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}