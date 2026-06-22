package com.playzone.pems.application.marketing.port.in;

import com.playzone.pems.application.marketing.dto.query.PlantillaEmailQuery;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListarPlantillasUseCase {

    List<PlantillaEmailQuery> listarPlantillas(Pageable pageable);
}
