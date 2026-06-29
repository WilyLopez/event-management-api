CREATE TABLE public.sede_integracion (
    id                 BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idsede             BIGINT       NOT NULL REFERENCES public.sede(id) ON DELETE CASCADE,
    proveedor_codigo   VARCHAR(50)  NOT NULL,
    api_url            VARCHAR(255) NOT NULL,
    api_token_cifrado  TEXT         NOT NULL,
    limite_mensual     INT          NOT NULL DEFAULT 100,
    activo             BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_actualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    
    CONSTRAINT uq_sede_proveedor UNIQUE (idsede, proveedor_codigo)
);

ALTER TABLE public.sede_integracion ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow all for authenticated users" ON public.sede_integracion
    FOR ALL
    TO authenticated
    USING (true)
    WITH CHECK (true);

CREATE TABLE public.registro_consulta_documento (
    id                BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idsede            BIGINT       NOT NULL REFERENCES public.sede(id) ON DELETE CASCADE,
    proveedor_codigo  VARCHAR(50)  NOT NULL,
    tipo_documento    VARCHAR(10)  NOT NULL,
    numero_documento  VARCHAR(20)  NOT NULL,
    creado_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

ALTER TABLE public.registro_consulta_documento ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow all for authenticated users" ON public.registro_consulta_documento
    FOR ALL
    TO authenticated
    USING (true)
    WITH CHECK (true);
