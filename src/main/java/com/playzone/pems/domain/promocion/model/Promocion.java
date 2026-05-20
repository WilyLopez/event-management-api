package com.playzone.pems.domain.promocion.model;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.enums.TipoPromocion;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Promocion {

    private Long           id;
    private TipoPromocion  tipoPromocion;
    private Long           idSede;
    private String         nombre;
    private String         descripcion;
    private BigDecimal     valorDescuento;
    private String         condicion;
    private Integer        minimoPersonas;
    private TipoDia        soloTipoDia;
    private LocalDate      fechaInicio;
    private LocalDate      fechaFin;
    private boolean        activo;
    private boolean        esAutomatica;
    private Long           idUsuarioCreador;
    private LocalDateTime  fechaCreacion;

    // CMS / display
    private String         imagenUrl;
    private String         bannerUrl;
    private String         colorDestacado;
    private int            prioridad;
    private String         textoPublicitario;
    private String         textoBoton;
    private String         urlBoton;
    private boolean        mostrarEnInicio;
    private boolean        mostrarEnCarrusel;
    private boolean        mostrarEnPaginaPromociones;
    private boolean        mostrarEnCheckout;
    private boolean        mostrarDestacado;
    private boolean        soloMovil;

    // Reglas de negocio
    private Integer        limiteUsos;
    private Integer        limitePorCliente;
    private Integer        minimoAsistentes;
    private BigDecimal     montoMinimo;

    public boolean estaVigenteEn(LocalDate fecha) {
        if (!activo) return false;
        boolean despuesDeInicio = !fecha.isBefore(fechaInicio);
        boolean antesDeVencimiento = fechaFin == null || !fecha.isAfter(fechaFin);
        return despuesDeInicio && antesDeVencimiento;
    }

    public boolean aplicaParaTipoDia(TipoDia tipoDia) {
        return soloTipoDia == null || soloTipoDia == tipoDia;
    }

    public boolean aplicaParaSede(Long idSedeConsulta) {
        return idSede == null || idSede.equals(idSedeConsulta);
    }

    public boolean cumpleMinimoPersonas(int numeroPersonas) {
        if (!tipoPromocion.requiereMinimoPersonas()) return true;
        return minimoPersonas != null && numeroPersonas >= minimoPersonas;
    }

    public BigDecimal calcularDescuento(BigDecimal precioBase) {
        return tipoPromocion.calcularDescuento(precioBase, valorDescuento);
    }
}
