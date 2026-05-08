package com.playzone.pems.infrastructure.persistence.facturacion.entity;

import com.playzone.pems.domain.facturacion.model.enums.EstadoComprobante;
import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import com.playzone.pems.domain.facturacion.model.enums.TipoDocReceptor;
import com.playzone.pems.infrastructure.persistence.pago.entity.PagoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcomprobante")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idpago", nullable = false, unique = true)
    private PagoEntity pago;

    @Enumerated(EnumType.STRING)
    @Column(name = "idtipo", nullable = false, length = 30)
    private TipoComprobante tipoComprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "idestado", nullable = false, length = 40)
    private EstadoComprobante estadoComprobante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idserie", nullable = false)
    private SerieComprobanteEntity serie;

    @Column(name = "serienum", nullable = false, length = 4)
    private String serieNum;

    @Column(nullable = false, length = 8)
    private String correlativo;

    @Column(name = "numerocompleto", nullable = false, unique = true, length = 20)
    private String numeroCompleto;

    @Column(name = "rucemisor", nullable = false, length = 11)
    private String rucEmisor;

    @Column(name = "razonsocialemisor", nullable = false, length = 200)
    private String razonSocialEmisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipodocreceptor", nullable = false, length = 20)
    private TipoDocReceptor tipoDocReceptor;

    @Column(name = "nrodocreceptor", length = 20)
    private String nroDocReceptor;

    @Column(name = "razonsocialreceptor", length = 200)
    private String razonSocialReceptor;

    @Column(name = "direccionreceptor", length = 300)
    private String direccionReceptor;

    @Column(name = "montobase", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoBase;

    @Column(name = "montoigv", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoIgv = BigDecimal.ZERO;

    @Column(name = "montototal", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "xmlurl", length = 500)
    private String xmlUrl;

    @Column(name = "pdfurl", length = 500)
    private String pdfUrl;

    @Column(name = "hashsunat", length = 200)
    private String hashSunat;

    @Column(name = "cdrestado", length = 50)
    private String cdrEstado;

    @Column(name = "cdrdescripcion", length = 500)
    private String cdrDescripcion;

    @Column(name = "motivoanulacion", length = 300)
    private String motivoAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcomprobantenta")
    private ComprobanteEntity comprobanteNota;

    @CreationTimestamp
    @Column(name = "fechaemision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}