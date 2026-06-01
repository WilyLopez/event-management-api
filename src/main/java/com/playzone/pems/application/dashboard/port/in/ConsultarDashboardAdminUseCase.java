package com.playzone.pems.application.dashboard.port.in;

import com.playzone.pems.application.dashboard.dto.query.DashboardAdminQuery;

public interface ConsultarDashboardAdminUseCase {
    DashboardAdminQuery obtener(Long idSede);
}
