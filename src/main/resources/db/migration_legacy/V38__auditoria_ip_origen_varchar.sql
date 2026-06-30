ALTER TABLE public.auditoria_log ALTER COLUMN ip_origen TYPE VARCHAR(45) USING ip_origen::varchar;
