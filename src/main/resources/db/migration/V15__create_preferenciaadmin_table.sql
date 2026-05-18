CREATE TABLE IF NOT EXISTS preferenciaadmin (
    idpreferenciaadmin BIGSERIAL PRIMARY KEY,
    idusuarioadmin     BIGINT NOT NULL REFERENCES usuarioadmin(idusuarioadmin) ON DELETE CASCADE,

    tema               VARCHAR(20)  NOT NULL DEFAULT 'SYSTEM'
                           CHECK (tema IN ('LIGHT','DARK','SYSTEM')),
    colorprimario      VARCHAR(20),
    colorsecundario    VARCHAR(20),
    colorsidebar       VARCHAR(20),
    coloracento        VARCHAR(20),

    tipografia         VARCHAR(50)  NOT NULL DEFAULT 'INTER',
    tamanofuente       VARCHAR(20)  NOT NULL DEFAULT 'NORMAL'
                           CHECK (tamanofuente IN ('SMALL','NORMAL','LARGE')),
    radiosbordes       VARCHAR(20)  NOT NULL DEFAULT 'NORMAL'
                           CHECK (radiosbordes IN ('SMALL','NORMAL','LARGE')),

    sidebarcolapsado   BOOLEAN NOT NULL DEFAULT FALSE,
    sidebarflotante    BOOLEAN NOT NULL DEFAULT FALSE,
    modocompacto       BOOLEAN NOT NULL DEFAULT FALSE,
    anchocontenido     VARCHAR(20)  NOT NULL DEFAULT 'FULL'
                           CHECK (anchocontenido IN ('BOXED','FULL')),
    mostrarmigaspan    BOOLEAN NOT NULL DEFAULT TRUE,
    mostrariconosmenu  BOOLEAN NOT NULL DEFAULT TRUE,

    mostraranimaciones BOOLEAN NOT NULL DEFAULT TRUE,
    animacionsidebar   BOOLEAN NOT NULL DEFAULT TRUE,
    hovereffects       BOOLEAN NOT NULL DEFAULT TRUE,
    loadersanimados    BOOLEAN NOT NULL DEFAULT TRUE,

    confirmaracciones      BOOLEAN NOT NULL DEFAULT TRUE,
    recordarultimapagina   BOOLEAN NOT NULL DEFAULT TRUE,
    restaurartabs          BOOLEAN NOT NULL DEFAULT FALSE,
    autorefreshdashboard   BOOLEAN NOT NULL DEFAULT FALSE,
    intervalorefreshseg    INT DEFAULT 60,

    dashboardpersonalizado JSONB,
    widgetsvisibles        JSONB,
    accesosrapidos         JSONB,
    ordenwidgets           JSONB,
    layoutdashboard        JSONB,

    filtrospersistentes    JSONB,
    columnasvisibles       JSONB,
    ordenamientotablas     JSONB,
    elementosportabla      INT NOT NULL DEFAULT 10,

    sonidonotificaciones   BOOLEAN NOT NULL DEFAULT TRUE,
    notificacionespush     BOOLEAN NOT NULL DEFAULT TRUE,
    notificacionesemail    BOOLEAN NOT NULL DEFAULT TRUE,
    notificacionesvisuales BOOLEAN NOT NULL DEFAULT TRUE,
    badgesdinamicos        BOOLEAN NOT NULL DEFAULT TRUE,

    idioma             VARCHAR(10)  NOT NULL DEFAULT 'es',
    zonahoraria        VARCHAR(60)  NOT NULL DEFAULT 'America/Lima',
    formatofecha       VARCHAR(30)  NOT NULL DEFAULT 'DD/MM/YYYY',
    formatohora        VARCHAR(20)  NOT NULL DEFAULT '24H',
    primerdiasemana    VARCHAR(20)  NOT NULL DEFAULT 'MONDAY'
                           CHECK (primerdiasemana IN ('SUNDAY','MONDAY')),

    altocontraste      BOOLEAN NOT NULL DEFAULT FALSE,
    reduciranimaciones BOOLEAN NOT NULL DEFAULT FALSE,
    aumentarespaciado  BOOLEAN NOT NULL DEFAULT FALSE,
    cursorgrande       BOOLEAN NOT NULL DEFAULT FALSE,

    fechacreacion      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE(idusuarioadmin)
);

CREATE INDEX idx_preferenciaadmin_usuario ON preferenciaadmin(idusuarioadmin);
CREATE INDEX idx_preferenciaadmin_tema    ON preferenciaadmin(tema);
CREATE INDEX idx_preferenciaadmin_idioma  ON preferenciaadmin(idioma);
