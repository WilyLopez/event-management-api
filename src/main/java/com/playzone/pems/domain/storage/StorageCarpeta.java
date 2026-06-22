package com.playzone.pems.domain.storage;

public enum StorageCarpeta {
    BANNERS,
    GALERIA,
    LOGOS,
    PAQUETES,
    ZONAS,
    ACTIVIDADES,
    NOVEDADES,
    RESENAS,
    LEGAL,
    PERFILES;

    public String valor() {
        return name().toLowerCase();
    }
}
