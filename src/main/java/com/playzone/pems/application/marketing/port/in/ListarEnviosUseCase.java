package com.playzone.pems.application.marketing.port.in;

import com.playzone.pems.application.marketing.dto.query.EnvioEmailQuery;
import com.playzone.pems.shared.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface ListarEnviosUseCase {

    PagedResponse<EnvioEmailQuery> ejecutar(Long idCampana, Pageable pageable);
}
