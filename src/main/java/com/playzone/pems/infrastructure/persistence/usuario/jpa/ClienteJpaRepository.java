package com.playzone.pems.infrastructure.persistence.usuario.jpa;

import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    Optional<ClienteEntity> findByTelefono(String telefono);

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
            AND (:esVip               IS NULL OR c.esVip               = :esVip)
            AND (:activo              IS NULL OR c.activo               = :activo)
            AND (:verificado          IS NULL OR c.correoVerificado     = :verificado)
            AND (:frecuente           IS NULL OR c.contadorVisitas     >= :minVisitas)
            AND (:tieneAccesoWeb      IS NULL OR c.tieneAccesoWeb       = :tieneAccesoWeb)
            AND (:aceptaComunicaciones IS NULL OR c.aceptaComunicaciones = :aceptaComunicaciones)
            AND (CAST(:origenRegistro  AS string) IS NULL OR c.origenRegistro   = :origenRegistro)
            AND (CAST(:segmentoCliente AS string) IS NULL OR c.segmentoCliente  = :segmentoCliente)
        """)
    Page<ClienteEntity> buscarConFiltrosCrm(
            @Param("search")               String  search,
            @Param("esVip")                Boolean esVip,
            @Param("activo")               Boolean activo,
            @Param("verificado")           Boolean verificado,
            @Param("frecuente")            Boolean frecuente,
            @Param("tieneAccesoWeb")       Boolean tieneAccesoWeb,
            @Param("aceptaComunicaciones") Boolean aceptaComunicaciones,
            @Param("origenRegistro")       String  origenRegistro,
            @Param("segmentoCliente")      String  segmentoCliente,
            @Param("minVisitas")           int     minVisitas,
            Pageable pageable
    );

    @Query("""
        SELECT c FROM ClienteEntity c
        WHERE c.activo = true
          AND c.aceptaComunicaciones = true
          AND c.correo IS NOT NULL
          AND (:soloVip         IS NULL OR (:soloVip         = true AND c.esVip = true))
          AND (:soloFrecuentes  IS NULL OR (:soloFrecuentes  = true AND c.contadorVisitas >= :minVisitas))
          AND (:soloNuevos      IS NULL OR (:soloNuevos      = true AND c.segmentoCliente = 'NUEVO'))
          AND (:soloInactivos   IS NULL OR (:soloInactivos   = true AND c.segmentoCliente = 'INACTIVO'))
          AND (:soloCorporativos IS NULL OR (:soloCorporativos = true AND c.tipoCliente = 'EMPRESA'))
          AND (:soloConAccesoWeb IS NULL OR (:soloConAccesoWeb = true AND c.tieneAccesoWeb = true))
          AND (:soloPresenciales IS NULL OR (:soloPresenciales = true AND c.origenRegistro IN ('PRESENCIAL','ADMIN')))
        """)
    List<ClienteEntity> findDestinatariosCampana(
            @Param("soloVip")          Boolean soloVip,
            @Param("soloFrecuentes")   Boolean soloFrecuentes,
            @Param("soloNuevos")       Boolean soloNuevos,
            @Param("soloInactivos")    Boolean soloInactivos,
            @Param("soloCorporativos") Boolean soloCorporativos,
            @Param("soloConAccesoWeb") Boolean soloConAccesoWeb,
            @Param("soloPresenciales") Boolean soloPresenciales,
            @Param("minVisitas")       int     minVisitas
    );

    @Modifying
    @Query("UPDATE ClienteEntity c SET c.segmentoCliente = :segmento WHERE c.id = :id")
    void actualizarSegmento(@Param("id") Long id, @Param("segmento") String segmento);

    @Modifying
    @Query("UPDATE ClienteEntity c SET c.totalGastado = c.totalGastado + :monto WHERE c.id = :id")
    void actualizarTotalGastado(@Param("id") Long id, @Param("monto") java.math.BigDecimal monto);

    @Modifying
    @Query("UPDATE ClienteEntity c SET c.ultimaVisita = CURRENT_TIMESTAMP WHERE c.id = :id")
    void actualizarUltimaVisita(@Param("id") Long id);
}