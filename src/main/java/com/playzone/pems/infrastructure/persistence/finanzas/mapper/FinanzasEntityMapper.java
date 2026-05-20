package com.playzone.pems.infrastructure.persistence.finanzas.mapper;

import com.playzone.pems.domain.finanzas.model.GastoEventoPrivado;
import com.playzone.pems.domain.finanzas.model.GastoOperativoDiario;
import com.playzone.pems.domain.finanzas.model.RegistroEgreso;
import com.playzone.pems.domain.finanzas.model.TipoEgreso;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.GastoEventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.GastoOperativoDiarioEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.RegistroEgresoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.TipoEgresoEntity;
import org.springframework.stereotype.Component;

@Component
public class FinanzasEntityMapper {

    public TipoEgreso toDomain(TipoEgresoEntity e) {
        return TipoEgreso.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .categoria(e.getCategoria())
                .activo(e.isActivo())
                .idUsuarioCreador(e.getIdUsuarioCreador())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public TipoEgresoEntity toEntity(TipoEgreso d) {
        return TipoEgresoEntity.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .categoria(d.getCategoria())
                .activo(d.isActivo())
                .idUsuarioCreador(d.getIdUsuarioCreador())
                .build();
    }

    public RegistroEgreso toDomain(RegistroEgresoEntity e) {
        return RegistroEgreso.builder()
                .id(e.getId())
                .idTipoEgreso(e.getTipoEgreso().getId())
                .idSede(e.getSede().getId())
                .monto(e.getMonto())
                .fecha(e.getFecha())
                .periodoAnio(e.getPeriodoAnio())
                .periodoMes(e.getPeriodoMes())
                .descripcion(e.getDescripcion())
                .comprobanteUrl(e.getComprobanteUrl())
                .esRecurrente(e.isEsRecurrente())
                .idUsuarioRegistra(e.getIdUsuarioRegistra())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public GastoEventoPrivado toDomain(GastoEventoPrivadoEntity e) {
        return GastoEventoPrivado.builder()
                .id(e.getId())
                .idEventoPrivado(e.getEventoPrivado().getId())
                .descripcion(e.getDescripcion())
                .monto(e.getMonto())
                .comprobanteUrl(e.getComprobanteUrl())
                .idUsuarioRegistra(e.getIdUsuarioRegistra())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public GastoOperativoDiario toDomain(GastoOperativoDiarioEntity e) {
        return GastoOperativoDiario.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .fecha(e.getFecha())
                .descripcion(e.getDescripcion())
                .monto(e.getMonto())
                .comprobanteUrl(e.getComprobanteUrl())
                .idUsuarioRegistra(e.getIdUsuarioRegistra())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
