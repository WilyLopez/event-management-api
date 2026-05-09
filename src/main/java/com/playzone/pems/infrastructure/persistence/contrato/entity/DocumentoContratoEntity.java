package com.playzone.pems.infrastructure.persistence.contrato.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documentocontrato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoContratoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddocumento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcontrato", nullable = false)
    private ContratoEntity contrato;

    @Column(nullable = false, length = 300)
    private String nombre;

    @Column(name = "archivourl", nullable = false, length = 500)
    private String archivoUrl;

    @Column(name = "tipoarchivo", nullable = false, length = 50)
    private String tipoArchivo;

    @Column(name = "tamanobytes")
    private Long tamanobytes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuariocarga", nullable = false)
    private UsuarioAdminEntity usuarioCarga;

    @CreationTimestamp
    @Column(name = "fechacarga", nullable = false, updatable = false)
    private LocalDateTime fechaCarga;
}