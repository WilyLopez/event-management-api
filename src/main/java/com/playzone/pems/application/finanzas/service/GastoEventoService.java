package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.RegistrarGastoEventoCommand;
import com.playzone.pems.application.finanzas.dto.query.GastoEventoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarGastoEventoUseCase;
import com.playzone.pems.domain.finanzas.model.GastoEventoPrivado;
import com.playzone.pems.domain.finanzas.repository.GastoEventoPrivadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GastoEventoService implements GestionarGastoEventoUseCase {

    private final GastoEventoPrivadoRepository gastoEventoRepository;

    @Override
    public GastoEventoQuery registrar(RegistrarGastoEventoCommand command) {
        GastoEventoPrivado gasto = GastoEventoPrivado.builder()
                .idEventoPrivado(command.getIdEventoPrivado())
                .descripcion(command.getDescripcion())
                .monto(command.getMonto())
                .comprobanteUrl(command.getComprobanteUrl())
                .idUsuarioRegistra(command.getIdUsuarioRegistra())
                .build();
        return toQuery(gastoEventoRepository.save(gasto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoEventoQuery> listarPorEvento(Long idEvento) {
        return gastoEventoRepository.findByEvento(idEvento).stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoEventoQuery> listarPorSedeYRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return gastoEventoRepository.findBySedeAndRango(idSede, inicio, fin)
                .stream().map(this::toQuery).toList();
    }

    @Override
    public void eliminar(Long id) {
        gastoEventoRepository.deleteById(id);
    }

    private GastoEventoQuery toQuery(GastoEventoPrivado g) {
        return GastoEventoQuery.builder()
                .id(g.getId())
                .idEventoPrivado(g.getIdEventoPrivado())
                .fechaEvento(g.getFechaEvento())
                .descripcion(g.getDescripcion())
                .monto(g.getMonto())
                .comprobanteUrl(g.getComprobanteUrl())
                .fechaCreacion(g.getFechaCreacion())
                .build();
    }
}
