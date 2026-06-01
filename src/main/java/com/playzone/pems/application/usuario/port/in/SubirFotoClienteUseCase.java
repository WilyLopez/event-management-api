package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.query.ClienteQuery;
import org.springframework.web.multipart.MultipartFile;

public interface SubirFotoClienteUseCase {
    ClienteQuery ejecutar(Long idCliente, MultipartFile foto);
}
