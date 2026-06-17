package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.comercial.port.in.GestionarServiciosCotizacionUseCase;
import com.playzone.pems.domain.comercial.model.ServicioCotizacion;
import com.playzone.pems.domain.comercial.repository.ServicioCotizacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioCotizacionService implements GestionarServiciosCotizacionUseCase {

    private final ServicioCotizacionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ServicioCotizacion> listarActivos() {
        return repository.findAllActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioCotizacion> listarTodos() {
        return repository.findAll();
    }

    @Override
    public ServicioCotizacion crear(ServicioCotizacion s) {
        return repository.save(s);
    }

    @Override
    public ServicioCotizacion actualizar(ServicioCotizacion s) {
        repository.findById(s.getId())
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado: " + s.getId()));
        return repository.save(s);
    }

    @Override
    public void eliminar(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado: " + id));
        repository.deleteById(id);
    }
}
