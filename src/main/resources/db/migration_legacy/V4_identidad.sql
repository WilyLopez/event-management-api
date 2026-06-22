CREATE TABLE perfil_usuario (
    id                UUID         PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nombre_completo   TEXT         NOT NULL,
    correo            CITEXT       NOT NULL,
    telefono          TEXT,
    foto_perfil_path  TEXT,
    ultimo_login_at   TIMESTAMPTZ,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted_at        TIMESTAMPTZ
);

CREATE INDEX idx_perfil_usuario_correo     ON perfil_usuario (correo) WHERE deleted_at IS NULL;
CREATE INDEX idx_perfil_usuario_deleted_at ON perfil_usuario (deleted_at) WHERE deleted_at IS NULL;

CREATE TRIGGER trg_perfil_usuario_updated_at
    BEFORE UPDATE ON perfil_usuario
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE preferencia_usuario (
    usuario_id           UUID         PRIMARY KEY REFERENCES perfil_usuario(id) ON DELETE CASCADE,
    tema                 TEXT         NOT NULL DEFAULT 'SYSTEM',
    idioma               TEXT         NOT NULL DEFAULT 'es',
    zona_horaria         TEXT         NOT NULL DEFAULT 'America/Lima',
    formato_fecha        TEXT         NOT NULL DEFAULT 'DD/MM/YYYY',
    formato_hora         TEXT         NOT NULL DEFAULT '24H',
    sidebar_colapsado    BOOLEAN      NOT NULL DEFAULT FALSE,
    autorefresh_dashboard BOOLEAN     NOT NULL DEFAULT FALSE,
    preferencias_extras  JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_pref_tema   CHECK (tema   IN ('LIGHT','DARK','SYSTEM')),
    CONSTRAINT ck_pref_idioma CHECK (idioma IN ('es','en')),
    CONSTRAINT ck_pref_fhora  CHECK (formato_hora IN ('12H','24H'))
);

CREATE TRIGGER trg_preferencia_usuario_updated_at
    BEFORE UPDATE ON preferencia_usuario
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE usuario_rol (
    usuario_id    UUID         NOT NULL REFERENCES perfil_usuario(id) ON DELETE CASCADE,
    rol_codigo    TEXT         NOT NULL REFERENCES rol(codigo) ON UPDATE CASCADE ON DELETE RESTRICT,
    asignado_por  UUID         REFERENCES perfil_usuario(id),
    asignado_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, rol_codigo)
);

CREATE INDEX idx_usuario_rol_rol ON usuario_rol (rol_codigo);


CREATE TABLE cliente_perfil (
    id                     BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id             UUID         REFERENCES perfil_usuario(id) ON DELETE SET NULL,

    tipo_documento_codigo  TEXT         NOT NULL REFERENCES tipo_documento(codigo) ON UPDATE CASCADE,
    numero_documento       TEXT         NOT NULL,

    nombres                TEXT         NOT NULL,
    apellido_paterno       TEXT,
    apellido_materno       TEXT,
    nombre_completo        TEXT         GENERATED ALWAYS AS (
                                            trim(both ' ' from
                                                nombres
                                                || COALESCE(' ' || apellido_paterno, '')
                                                || COALESCE(' ' || apellido_materno, '')
                                            )
                                        ) STORED,

    correo                 CITEXT,
    telefono               TEXT,
    fecha_nacimiento       DATE,

    ruc                    TEXT,
    razon_social           TEXT,
    direccion_fiscal       TEXT,

    segmento_codigo        TEXT         NOT NULL DEFAULT 'NUEVO' REFERENCES segmento_cliente(codigo) ON UPDATE CASCADE,
    es_vip                 BOOLEAN      NOT NULL DEFAULT FALSE,
    descuento_vip          NUMERIC(5,2),
    acepta_comunicaciones  BOOLEAN      NOT NULL DEFAULT TRUE,
    foto_perfil_path       TEXT,
    observaciones          TEXT,

    contador_visitas       INT          NOT NULL DEFAULT 0,
    total_gastado          NUMERIC(12,2) NOT NULL DEFAULT 0,
    ultima_visita_at       TIMESTAMPTZ,

    origen                 TEXT         NOT NULL DEFAULT 'MOSTRADOR',

    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by             UUID         REFERENCES perfil_usuario(id),
    updated_by             UUID         REFERENCES perfil_usuario(id),
    deleted_at             TIMESTAMPTZ,

    CONSTRAINT uk_cliente_documento     UNIQUE (tipo_documento_codigo, numero_documento),
    CONSTRAINT uk_cliente_usuario       UNIQUE (usuario_id),
    CONSTRAINT ck_cliente_origen        CHECK (origen IN ('WEB','MOSTRADOR','ADMIN','IMPORTACION')),
    CONSTRAINT ck_cliente_vip_descuento CHECK (es_vip = FALSE OR descuento_vip IS NOT NULL),
    CONSTRAINT ck_cliente_descuento_vip CHECK (descuento_vip IS NULL OR descuento_vip BETWEEN 0 AND 100),
    CONSTRAINT ck_cliente_ruc_razon     CHECK (ruc IS NULL OR razon_social IS NOT NULL),
    CONSTRAINT ck_cliente_ruc_longitud  CHECK (ruc IS NULL OR length(ruc) = 11),
    CONSTRAINT ck_cliente_contador      CHECK (contador_visitas >= 0),
    CONSTRAINT ck_cliente_total         CHECK (total_gastado    >= 0)
);

CREATE INDEX idx_cliente_perfil_usuario    ON cliente_perfil (usuario_id)       WHERE usuario_id IS NOT NULL;
CREATE INDEX idx_cliente_perfil_correo     ON cliente_perfil (correo)           WHERE correo     IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_cliente_perfil_telefono   ON cliente_perfil (telefono)         WHERE telefono   IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_cliente_perfil_segmento   ON cliente_perfil (segmento_codigo);
CREATE INDEX idx_cliente_perfil_vip        ON cliente_perfil (es_vip)           WHERE es_vip = TRUE;
CREATE INDEX idx_cliente_perfil_origen     ON cliente_perfil (origen);
CREATE INDEX idx_cliente_perfil_nacimiento ON cliente_perfil (fecha_nacimiento) WHERE fecha_nacimiento IS NOT NULL;
CREATE INDEX idx_cliente_perfil_deleted_at ON cliente_perfil (deleted_at)       WHERE deleted_at IS NULL;
CREATE INDEX idx_cliente_perfil_nombre_trgm ON cliente_perfil USING gin (nombre_completo gin_trgm_ops);

CREATE TRIGGER trg_cliente_perfil_updated_at
    BEFORE UPDATE ON cliente_perfil
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE staff_perfil (
    id                    BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id            UUID         NOT NULL REFERENCES perfil_usuario(id) ON DELETE CASCADE,
    sede_id               BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,

    codigo_empleado       TEXT,
    fecha_ingreso         DATE,
    telefono_emergencia   TEXT,
    observaciones         TEXT,
    es_activo             BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by            UUID         REFERENCES perfil_usuario(id),
    updated_by            UUID         REFERENCES perfil_usuario(id),
    deleted_at            TIMESTAMPTZ,

    CONSTRAINT uk_staff_usuario        UNIQUE (usuario_id),
    CONSTRAINT uk_staff_codigo         UNIQUE (codigo_empleado)
);

CREATE INDEX idx_staff_perfil_sede       ON staff_perfil (sede_id);
CREATE INDEX idx_staff_perfil_activo     ON staff_perfil (es_activo)  WHERE es_activo = TRUE;
CREATE INDEX idx_staff_perfil_deleted_at ON staff_perfil (deleted_at) WHERE deleted_at IS NULL;

CREATE TRIGGER trg_staff_perfil_updated_at
    BEFORE UPDATE ON staff_perfil
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE cache_dni (
    id                    BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_documento_codigo TEXT         NOT NULL REFERENCES tipo_documento(codigo) ON UPDATE CASCADE,
    numero_documento      TEXT         NOT NULL,
    nombres               TEXT,
    apellido_paterno      TEXT,
    apellido_materno      TEXT,
    razon_social          TEXT,
    proveedor             TEXT         NOT NULL DEFAULT 'DECOLECTA',
    respuesta_json        JSONB,
    consultado_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    expira_at             TIMESTAMPTZ  NOT NULL DEFAULT (NOW() + INTERVAL '180 days'),
    CONSTRAINT uk_cache_dni_documento UNIQUE (tipo_documento_codigo, numero_documento)
);

CREATE INDEX idx_cache_dni_expira ON cache_dni (expira_at);


CREATE EXTENSION IF NOT EXISTS pg_trgm;


CREATE OR REPLACE FUNCTION app.usuario_tiene_rol(p_rol_codigo TEXT)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM usuario_rol ur
        WHERE ur.usuario_id = auth.uid()
          AND ur.rol_codigo = p_rol_codigo
    );
$$;


CREATE OR REPLACE FUNCTION app.usuario_tiene_permiso(p_permiso_codigo TEXT)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM usuario_rol ur
        JOIN rol_permiso rp ON rp.rol_codigo = ur.rol_codigo
        WHERE ur.usuario_id = auth.uid()
          AND rp.permiso_codigo = p_permiso_codigo
    );
$$;


CREATE OR REPLACE FUNCTION app.es_superadmin()
RETURNS BOOLEAN
LANGUAGE sql STABLE
AS $$
    SELECT app.usuario_tiene_rol('SUPERADMIN');
$$;


CREATE OR REPLACE FUNCTION app.es_admin()
RETURNS BOOLEAN
LANGUAGE sql STABLE
AS $$
    SELECT app.usuario_tiene_rol('SUPERADMIN') OR app.usuario_tiene_rol('ADMIN');
$$;


CREATE OR REPLACE FUNCTION app.es_cajero()
RETURNS BOOLEAN
LANGUAGE sql STABLE
AS $$
    SELECT app.usuario_tiene_rol('CAJERO');
$$;


CREATE OR REPLACE FUNCTION app.es_cliente()
RETURNS BOOLEAN
LANGUAGE sql STABLE
AS $$
    SELECT app.usuario_tiene_rol('CLIENTE');
$$;


CREATE OR REPLACE FUNCTION app.es_staff()
RETURNS BOOLEAN
LANGUAGE sql STABLE
AS $$
    SELECT app.usuario_tiene_rol('SUPERADMIN')
        OR app.usuario_tiene_rol('ADMIN')
        OR app.usuario_tiene_rol('CAJERO');
$$;


CREATE OR REPLACE FUNCTION app.sede_actual()
RETURNS BIGINT
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
    SELECT sede_id
    FROM staff_perfil
    WHERE usuario_id = auth.uid()
      AND deleted_at IS NULL
      AND es_activo = TRUE
    LIMIT 1;
$$;


CREATE OR REPLACE FUNCTION app.handle_new_user()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_nombre TEXT;
BEGIN
    v_nombre := COALESCE(
        NEW.raw_user_meta_data->>'full_name',
        NEW.raw_user_meta_data->>'name',
        split_part(NEW.email, '@', 1)
    );

    INSERT INTO public.perfil_usuario (id, nombre_completo, correo)
    VALUES (NEW.id, v_nombre, NEW.email::citext)
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO public.preferencia_usuario (usuario_id)
    VALUES (NEW.id)
    ON CONFLICT (usuario_id) DO NOTHING;

    INSERT INTO public.usuario_rol (usuario_id, rol_codigo)
    VALUES (NEW.id, 'CLIENTE')
    ON CONFLICT (usuario_id, rol_codigo) DO NOTHING;

    RETURN NEW;
END;
$$;


DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW
    EXECUTE FUNCTION app.handle_new_user();


CREATE OR REPLACE FUNCTION app.handle_user_login()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
BEGIN
    IF NEW.last_sign_in_at IS DISTINCT FROM OLD.last_sign_in_at
       AND NEW.last_sign_in_at IS NOT NULL THEN
        UPDATE public.perfil_usuario
        SET ultimo_login_at = NEW.last_sign_in_at
        WHERE id = NEW.id;
    END IF;
    RETURN NEW;
END;
$$;


DROP TRIGGER IF EXISTS on_auth_user_login ON auth.users;
CREATE TRIGGER on_auth_user_login
    AFTER UPDATE ON auth.users
    FOR EACH ROW
    EXECUTE FUNCTION app.handle_user_login();


CREATE OR REPLACE FUNCTION app.asignar_rol(p_usuario_id UUID, p_rol_codigo TEXT)
RETURNS VOID
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
BEGIN
    IF NOT app.es_superadmin() AND NOT app.usuario_tiene_permiso('rol.gestionar') THEN
        RAISE EXCEPTION 'Permisos insuficientes para asignar roles'
            USING ERRCODE = 'insufficient_privilege';
    END IF;

    INSERT INTO usuario_rol (usuario_id, rol_codigo, asignado_por)
    VALUES (p_usuario_id, p_rol_codigo, auth.uid())
    ON CONFLICT (usuario_id, rol_codigo) DO NOTHING;
END;
$$;


CREATE OR REPLACE FUNCTION app.revocar_rol(p_usuario_id UUID, p_rol_codigo TEXT)
RETURNS VOID
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
BEGIN
    IF NOT app.es_superadmin() AND NOT app.usuario_tiene_permiso('rol.gestionar') THEN
        RAISE EXCEPTION 'Permisos insuficientes para revocar roles'
            USING ERRCODE = 'insufficient_privilege';
    END IF;

    IF p_rol_codigo = 'SUPERADMIN' AND p_usuario_id = auth.uid() THEN
        RAISE EXCEPTION 'No puedes revocar tu propio rol SUPERADMIN';
    END IF;

    DELETE FROM usuario_rol
    WHERE usuario_id = p_usuario_id
      AND rol_codigo = p_rol_codigo;
END;
$$;