package com.playzone.pems.application.auditoria.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.domain.auditoria.model.LogAuditoria;
import com.playzone.pems.domain.auditoria.repository.LogAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService implements RegistrarLogUseCase {

    private final LogAuditoriaRepository logRepository;
    private final ObjectMapper           objectMapper;

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ejecutar(Command command) {
        try {
            String anterior = toJson(command.valorAnterior());
            String nuevo    = toJson(command.valorNuevo());

            LogAuditoria log = LogAuditoria.builder()
                    .idUsuarioAdmin(command.idUsuarioAdmin())
                    .accion(command.accion())
                    .modulo(command.modulo())
                    .entidadAfectada(command.entidadAfectada())
                    .idEntidad(command.idEntidad())
                    .valorAnterior(anterior)
                    .valorNuevo(nuevo)
                    .descripcion(command.descripcion())
                    .ipOrigen(command.ipOrigen())
                    .userAgent(command.userAgent())
                    .build();

            logRepository.save(log);
        } catch (Exception e) {
            log.error("Error al registrar log de auditoría: {}", e.getMessage(), e);
        }
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
}