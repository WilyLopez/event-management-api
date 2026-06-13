package com.playzone.pems.infrastructure.persistence.usuario_supabase.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cliente_perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientePerfilEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "usuario_id", columnDefinition = "uuid")
    private UUID usuarioId;

    @Column(name = "tipo_documento_codigo", nullable = false)
    private String tipoDocumentoCodigo;

    @Column(name = "numero_documento", nullable = false)
    private String numeroDocumento;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellido_paterno")
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    @Column(name = "nombre_completo", insertable = false, updatable = false)
    private String nombreCompleto;

    @Column(name = "correo")
    private String correo;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "segmento_codigo", nullable = false)
    private String segmentoCodigo;

    @Column(name = "es_vip", nullable = false)
    private boolean esVip;

    @Column(name = "descuento_vip", precision = 5, scale = 2)
    private BigDecimal descuentoVip;

    @Column(name = "acepta_comunicaciones", nullable = false)
    private boolean aceptaComunicaciones;

    @Column(name = "contador_visitas", nullable = false)
    private int contadorVisitas;

    @Column(name = "total_gastado", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalGastado;

    @Column(name = "origen", nullable = false)
    private String origen;

    @Column(name = "ultima_visita_at")
    private OffsetDateTime ultimaVisitaAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
