package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.query.ClientePerfilQuery;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarClientesPerfilUseCase {

    Page<ClientePerfil> ejecutar(ClientePerfilQuery query, Pageable pageable);
}
