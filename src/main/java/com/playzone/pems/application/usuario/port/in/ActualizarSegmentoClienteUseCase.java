package com.playzone.pems.application.usuario.port.in;

public interface ActualizarSegmentoClienteUseCase {

    void ejecutar(Long idCliente, String segmento);
}
