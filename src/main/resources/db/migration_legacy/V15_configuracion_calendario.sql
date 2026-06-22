DROP TABLE IF EXISTS public.configuracion_calendario CASCADE;

CREATE TABLE public.configuracion_calendario (
    idconfig                   BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idsede                     BIGINT       NOT NULL UNIQUE REFERENCES public.sede(id) ON DELETE CASCADE,
    dias_min_reserva_publica   INT          NOT NULL DEFAULT 0,
    dias_max_reserva_publica   INT          NOT NULL DEFAULT 14,
    dias_min_evento_privado    INT          NOT NULL DEFAULT 15,
    dias_max_evento_privado    INT          NOT NULL DEFAULT 365,
    aforo_maximo               INT          NOT NULL DEFAULT 60,
    hora_apertura              TIME         NOT NULL DEFAULT '10:00',
    hora_cierre                TIME         NOT NULL DEFAULT '20:00',
    turno_t1_inicio            TIME         NOT NULL DEFAULT '10:00',
    turno_t1_fin               TIME         NOT NULL DEFAULT '14:00',
    turno_t2_inicio            TIME         NOT NULL DEFAULT '16:00',
    turno_t2_fin               TIME         NOT NULL DEFAULT '20:00',
    dias_operacion             VARCHAR(20)  NOT NULL DEFAULT '1,2,3,4,5,6,7',
    rango_max_bloqueo_dias     INT          NOT NULL DEFAULT 90,
    fechaactualizacion         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO public.configuracion_calendario (idsede)
SELECT id FROM public.sede WHERE deleted_at IS NULL LIMIT 1;
