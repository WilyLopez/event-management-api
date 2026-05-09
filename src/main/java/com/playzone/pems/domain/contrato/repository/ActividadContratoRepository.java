package com.playzone.pems.domain.contrato.repository;

import com.playzone.pems.domain.contrato.model.ActividadContrato;

import java.util.List;

public interface ActividadContratoRepository {

    List<ActividadContrato> findByContrato(Long idContrato);

    ActividadContrato save(ActividadContrato actividad);
}