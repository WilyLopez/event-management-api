ALTER TABLE public.evento
    ADD COLUMN IF NOT EXISTS origen_contacto VARCHAR(30);
