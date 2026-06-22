package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.RegistrarIngresoManualCommand;
import com.playzone.pems.application.finanzas.dto.query.RegistroIngresoQuery;
import com.playzone.pems.application.finanzas.port.in.RegistrarIngresoUseCase;
import com.playzone.pems.domain.finanzas.model.RegistroIngreso;
import com.playzone.pems.domain.finanzas.model.TipoIngreso;
import com.playzone.pems.domain.finanzas.repository.RegistroIngresoRepository;
import com.playzone.pems.domain.finanzas.repository.TipoIngresoRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IngresoService implements RegistrarIngresoUseCase {

    private final RegistroIngresoRepository registroIngresoRepository;
    private final TipoIngresoRepository     tipoIngresoRepository;

    @Override
    public RegistroIngresoQuery registrar(RegistrarIngresoManualCommand command) {
        TipoIngreso tipo = tipoIngresoRepository.findById(command.getTipoIngresoCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ingreso no encontrado."));
        if (!tipo.isActivo()) {
            throw new ValidationException("El tipo de ingreso seleccionado no está activo.");
        }
        RegistroIngreso ingreso = RegistroIngreso.builder()
                .tipoIngresoCodigo(command.getTipoIngresoCodigo())
                .idSede(command.getIdSede())
                .monto(command.getMonto())
                .fecha(command.getFecha())
                .medioPago(command.getMedioPago())
                .descripcion(command.getDescripcion())
                .esAutomatico(false)
                .idUsuarioRegistra(command.getIdUsuarioRegistra())
                .build();
        return toQuery(registroIngresoRepository.save(ingreso));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistroIngresoQuery> listar(Long idSede, Pageable pageable) {
        return registroIngresoRepository.findBySede(idSede, pageable).map(this::toQuery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroIngresoQuery> listarPorRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return registroIngresoRepository.findBySedeAndRangoFecha(idSede, inicio, fin)
                .stream().map(this::toQuery).toList();
    }

    @Override
    public void eliminar(Long id) {
        registroIngresoRepository.deleteById(id);
    }

    private RegistroIngresoQuery toQuery(RegistroIngreso r) {
        return RegistroIngresoQuery.builder()
                .id(r.getId())
                .tipoIngresoCodigo(r.getTipoIngresoCodigo())
                .idSede(r.getIdSede())
                .idReservaPublica(r.getIdReservaPublica())
                .idEventoPrivado(r.getIdEventoPrivado())
                .monto(r.getMonto())
                .fecha(r.getFecha())
                .medioPago(r.getMedioPago())
                .descripcion(r.getDescripcion())
                .esAutomatico(r.isEsAutomatico())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}
