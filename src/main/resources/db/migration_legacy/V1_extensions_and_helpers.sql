CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "citext";  
CREATE EXTENSION IF NOT EXISTS "btree_gist";

CREATE SCHEMA IF NOT EXISTS app;

COMMENT ON SCHEMA app IS 'Funciones, vistas y utilidades del dominio. No expuesto vía PostgREST.';

CREATE OR REPLACE FUNCTION app.set_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

COMMENT ON FUNCTION app.set_updated_at() IS
    'Trigger genérico BEFORE UPDATE: actualiza updated_at = NOW().';

CREATE OR REPLACE FUNCTION app.prevent_sistema_codigo_change()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.es_sistema = TRUE AND NEW.codigo <> OLD.codigo THEN
        RAISE EXCEPTION 'No se puede modificar el codigo de un registro de sistema: %', OLD.codigo
            USING ERRCODE = 'check_violation';
    END IF;
    RETURN NEW;
END;
$$;

COMMENT ON FUNCTION app.prevent_sistema_codigo_change() IS
    'Trigger BEFORE UPDATE para catálogos: prohíbe cambiar codigo cuando es_sistema = TRUE.';

CREATE OR REPLACE FUNCTION app.prevent_sistema_delete()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.es_sistema = TRUE THEN
        RAISE EXCEPTION 'No se puede eliminar un registro de sistema: %', OLD.codigo
            USING ERRCODE = 'check_violation';
    END IF;
    RETURN OLD;
END;
$$;

COMMENT ON FUNCTION app.prevent_sistema_delete() IS
    'Trigger BEFORE DELETE para catálogos: prohíbe eliminar cuando es_sistema = TRUE.';


CREATE OR REPLACE FUNCTION app.current_user_id()
RETURNS UUID
LANGUAGE sql
STABLE
AS $$
    SELECT auth.uid();
$$;

COMMENT ON FUNCTION app.current_user_id() IS
    'Retorna el UUID del usuario autenticado actual (wrapper de auth.uid()).';


CREATE OR REPLACE FUNCTION app.usuario_tiene_rol(p_rol_codigo TEXT)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
    SELECT FALSE;
$$;

COMMENT ON FUNCTION app.usuario_tiene_rol(TEXT) IS
    'Verifica si el usuario actual tiene un rol específico. Stub - se reemplaza en Lote 2.';

CREATE OR REPLACE FUNCTION app.usuario_tiene_permiso(p_permiso_codigo TEXT)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
    -- Implementación real en Lote 2.
    SELECT FALSE;
$$;

COMMENT ON FUNCTION app.usuario_tiene_permiso(TEXT) IS
    'Verifica si el usuario actual tiene un permiso específico. Stub - se reemplaza en Lote 2.';

CREATE OR REPLACE FUNCTION app.hoy()
RETURNS DATE
LANGUAGE sql
STABLE
AS $$
    SELECT (NOW() AT TIME ZONE 'America/Lima')::DATE;
$$;

CREATE OR REPLACE FUNCTION app.ahora()
RETURNS TIMESTAMPTZ
LANGUAGE sql
STABLE
AS $$
    SELECT NOW();
$$;

COMMENT ON FUNCTION app.hoy() IS    'Fecha actual en zona horaria America/Lima.';
COMMENT ON FUNCTION app.ahora() IS  'Timestamp actual (TIMESTAMPTZ, almacenado en UTC).';