package com.playzone.pems.infrastructure.persistence.usuario_supabase.jpa;

import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.UsuarioRolEntity;
import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UsuarioRolJpaRepository extends JpaRepository<UsuarioRolEntity, UsuarioRolId> {

    @Query("SELECT ur.rolCodigo FROM UsuarioRolEntity ur WHERE ur.usuarioId = :usuarioId")
    List<String> findRolCodigosByUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query(value = """
            SELECT rp.permiso_codigo
            FROM public.usuario_rol ur
            JOIN public.rol_permiso rp ON ur.rol_codigo = rp.rol_codigo
            WHERE ur.usuario_id = :usuarioId
            """, nativeQuery = true)
    List<String> findPermisoCodigosByUsuarioId(@Param("usuarioId") UUID usuarioId);
}
