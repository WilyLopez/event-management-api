package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.query.DashboardFinancieroQuery;

public interface ConsultarDashboardFinancieroUseCase {
    DashboardFinancieroQuery consultar(Long idSede, int anio, int mes);
}
