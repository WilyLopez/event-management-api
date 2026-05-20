package com.playzone.pems.application.marketing.port.in;

import com.playzone.pems.application.marketing.dto.query.CampanaEmailQuery;
import com.playzone.pems.shared.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface ListarCampanasUseCase {

    PagedResponse<CampanaEmailQuery> listarCampanas(Pageable pageable);
}
