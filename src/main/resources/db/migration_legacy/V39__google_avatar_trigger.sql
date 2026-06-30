-- Modificar el trigger para capturar la URL del avatar desde Google (Supabase Auth)
CREATE OR REPLACE FUNCTION app.handle_new_user()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_nombre TEXT;
    v_avatar TEXT;
BEGIN
    v_nombre := COALESCE(
        NEW.raw_user_meta_data->>'full_name',
        NEW.raw_user_meta_data->>'name',
        split_part(NEW.email, '@', 1)
    );

    v_avatar := NEW.raw_user_meta_data->>'avatar_url';

    IF TG_OP = 'INSERT' THEN
        INSERT INTO public.perfil_usuario (id, nombre_completo, correo, foto_perfil_path)
        VALUES (NEW.id, v_nombre, NEW.email::citext, v_avatar)
        ON CONFLICT (id) DO UPDATE 
        SET foto_perfil_path = COALESCE(public.perfil_usuario.foto_perfil_path, EXCLUDED.foto_perfil_path);

        INSERT INTO public.preferencia_usuario (usuario_id)
        VALUES (NEW.id)
        ON CONFLICT (usuario_id) DO NOTHING;

        INSERT INTO public.usuario_rol (usuario_id, rol_codigo)
        VALUES (NEW.id, 'CLIENTE')
        ON CONFLICT (usuario_id, rol_codigo) DO NOTHING;
    ELSIF TG_OP = 'UPDATE' THEN
        UPDATE public.perfil_usuario
        SET foto_perfil_path = COALESCE(foto_perfil_path, v_avatar)
        WHERE id = NEW.id;
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION app.handle_new_user();

DROP TRIGGER IF EXISTS on_auth_user_updated_avatar ON auth.users;
CREATE TRIGGER on_auth_user_updated_avatar
    AFTER UPDATE OF raw_user_meta_data ON auth.users
    FOR EACH ROW EXECUTE FUNCTION app.handle_new_user();

-- Actualización retroactiva para los usuarios que ya existían y tenían foto de Google
UPDATE public.perfil_usuario p
SET foto_perfil_path = u.raw_user_meta_data->>'avatar_url'
FROM auth.users u
WHERE p.id = u.id
  AND u.raw_user_meta_data->>'avatar_url' IS NOT NULL
  AND p.foto_perfil_path IS NULL;
