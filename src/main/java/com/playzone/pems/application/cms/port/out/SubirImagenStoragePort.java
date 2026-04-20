package com.playzone.pems.application.cms.port.out;

public interface SubirImagenStoragePort {

    String subir(byte[] contenido, String nombreArchivo, String contentType);

    void eliminar(String urlImagen);
}