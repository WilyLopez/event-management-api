package com.playzone.pems.infrastructure.persistence.promocion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "promocion_marketing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromocionMarketingEntity {

    @Id
    @Column(name = "promocion_id")
    private Long promocionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "promocion_id")
    private PromocionEntity promocion;

    @Column(name = "imagen_path", length = 500)
    private String imagenPath;

    @Column(name = "banner_path", length = 500)
    private String bannerPath;

    @Column(name = "color_destacado", length = 20)
    private String colorDestacado;

    @Column(name = "texto_publicitario", length = 300)
    private String textoPublicitario;

    @Column(name = "texto_boton", length = 100)
    private String textoBoton;

    @Column(name = "url_boton", length = 500)
    private String urlBoton;

    @Column(name = "mostrar_en_inicio", nullable = false)
    @Builder.Default
    private boolean mostrarEnInicio = false;

    @Column(name = "mostrar_en_carrusel", nullable = false)
    @Builder.Default
    private boolean mostrarEnCarrusel = false;

    @Column(name = "mostrar_en_promociones", nullable = false)
    @Builder.Default
    private boolean mostrarEnPromociones = true;

    @Column(name = "mostrar_en_checkout", nullable = false)
    @Builder.Default
    private boolean mostrarEnCheckout = false;

    @Column(name = "solo_movil", nullable = false)
    @Builder.Default
    private boolean soloMovil = false;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
