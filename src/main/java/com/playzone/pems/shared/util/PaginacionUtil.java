package com.playzone.pems.shared.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginacionUtil {

    public static final int PAGINA_POR_DEFECTO = 0;

    public static final int TAMANO_POR_DEFECTO = 20;

    public static final int TAMANO_MAXIMO      = 100;

    private PaginacionUtil() {}

    public static Pageable construir(int pagina, int tamano, String campo, String orden) {
        int paginaSegura = Math.max(pagina, PAGINA_POR_DEFECTO);
        int tamanoSeguro = (tamano <= 0 || tamano > TAMANO_MAXIMO) ? TAMANO_POR_DEFECTO : tamano;

        Sort sort = "asc".equalsIgnoreCase(orden)
                ? Sort.by(campo).ascending()
                : Sort.by(campo).descending();

        return PageRequest.of(paginaSegura, tamanoSeguro, sort);
    }

    public static Pageable construir(int pagina, int tamano, Sort sort) {
        int paginaSegura = Math.max(pagina, PAGINA_POR_DEFECTO);
        int tamanoSeguro = (tamano <= 0 || tamano > TAMANO_MAXIMO) ? TAMANO_POR_DEFECTO : tamano;
        return PageRequest.of(paginaSegura, tamanoSeguro, sort);
    }

    public static Pageable porDefecto() {
        return PageRequest.of(PAGINA_POR_DEFECTO, TAMANO_POR_DEFECTO);
    }

    public static Pageable descendentePor(String campo) {
        return PageRequest.of(PAGINA_POR_DEFECTO, TAMANO_POR_DEFECTO,
                Sort.by(campo).descending());
    }

    public static Pageable ascendentePor(String campo) {
        return PageRequest.of(PAGINA_POR_DEFECTO, TAMANO_POR_DEFECTO,
                Sort.by(campo).ascending());
    }

    public static Pageable paraReporte(String campo) {
        return PageRequest.of(PAGINA_POR_DEFECTO, TAMANO_MAXIMO,
                Sort.by(campo).descending());
    }
}