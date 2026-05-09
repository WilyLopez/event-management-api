package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.command.SubirDocumentoCommand;
import com.playzone.pems.application.contrato.dto.query.DocumentoContratoQuery;

public interface SubirDocumentoContratoUseCase {
    DocumentoContratoQuery ejecutar(SubirDocumentoCommand command);
    void eliminar(Long idDocumento);
}