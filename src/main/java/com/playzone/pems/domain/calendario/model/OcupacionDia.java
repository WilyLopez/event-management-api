package com.playzone.pems.domain.calendario.model;

import com.playzone.pems.domain.calendario.model.enums.TipoOcupacionDia;
import lombok.Getter;

@Getter
public final class OcupacionDia {

    private final TipoOcupacionDia tipo;
    private final boolean          turnoT1Ocupado;
    private final boolean          turnoT2Ocupado;
    private final int              cantidadReservas;
    private final Long             idEventoT1;
    private final String           tituloEventoT1;
    private final Long             idEventoT2;
    private final String           tituloEventoT2;

    private OcupacionDia(TipoOcupacionDia tipo, boolean t1, boolean t2,
                         int reservas,
                         Long idT1, String titT1, Long idT2, String titT2) {
        this.tipo             = tipo;
        this.turnoT1Ocupado   = t1;
        this.turnoT2Ocupado   = t2;
        this.cantidadReservas = reservas;
        this.idEventoT1       = idT1;
        this.tituloEventoT1   = titT1;
        this.idEventoT2       = idT2;
        this.tituloEventoT2   = titT2;
    }

    public static OcupacionDia libre() {
        return new OcupacionDia(TipoOcupacionDia.LIBRE, false, false, 0, null, null, null, null);
    }

    public static OcupacionDia publico(int reservas) {
        return new OcupacionDia(TipoOcupacionDia.PUBLICO, false, false, reservas, null, null, null, null);
    }

    public static OcupacionDia privado(TipoOcupacionDia tipo, boolean t1, boolean t2,
                                       Long idT1, String titT1, Long idT2, String titT2) {
        return new OcupacionDia(tipo, t1, t2, 0, idT1, titT1, idT2, titT2);
    }

    public static OcupacionDia bloqueado() {
        return new OcupacionDia(TipoOcupacionDia.BLOQUEADO, false, false, 0, null, null, null, null);
    }

    public static OcupacionDia feriado() {
        return new OcupacionDia(TipoOcupacionDia.FERIADO, false, false, 0, null, null, null, null);
    }
}
