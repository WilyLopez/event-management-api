package com.playzone.pems.infrastructure.persistence.usuario.jpa;

import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByCorreo(String correo);

    Optional<ClienteEntity> findByDni(String dni);

    Optional<ClienteEntity> findByTokenVerificacion(String token);

    @Query("SELECT c FROM ClienteEntity c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%',:texto,'%')) OR LOWER(c.correo) LIKE LOWER(CONCAT('%',:texto,'%'))")
    Page<ClienteEntity> findByNombreOrCorreo(@Param("texto") String texto, Pageable pageable);

    boolean existsByCorreo(String correo);

    boolean existsByDni(String dni);

    @Query("""
        SELECT c FROM ClienteEntity c
        WHERE
            (CAST(:search AS string) IS NULL OR
            LOWER(c.nombre)   LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
            LOWER(c.correo)   LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
            LOWER(c.telefono) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
            c.dni             LIKE CONCAT('%', CAST(:search AS string), '%')        OR
            c.ruc             LIKE CONCAT('%', CAST(:search AS string), '%')
            )
            AND (:esVip     IS NULL OR c.esVip             = :esVip)
            AND (:activo    IS NULL OR c.activo             = :activo)
            AND (:verificado IS NULL OR c.correoVerificado  = :verificado)
            AND (:frecuente IS NULL OR c.contadorVisitas   >= :minVisitas)
        """)
    Page<ClienteEntity> buscarConFiltros(
        @Param("search")    String  search,
        @Param("esVip")     Boolean esVip,
        @Param("activo")    Boolean activo,
        @Param("verificado")Boolean verificado,
        @Param("frecuente") Boolean frecuente,
        @Param("minVisitas")int     minVisitas,
        Pageable pageable
    );

    @Modifying
    @Query("UPDATE ClienteEntity c SET c.contadorVisitas = c.contadorVisitas + 1 WHERE c.id = :id")
    void incrementarContadorVisitas(@Param("id") Long id);
}