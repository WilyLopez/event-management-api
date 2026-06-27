-- V27: Mejoras al módulo de Caja
-- Nuevas columnas en apertura_caja + tabla arqueo_caja

ALTER TABLE apertura_caja
  ADD COLUMN IF NOT EXISTS saldo_esperado NUMERIC(10,2),
  ADD COLUMN IF NOT EXISTS diferencia     NUMERIC(10,2);

CREATE TABLE IF NOT EXISTS arqueo_caja (
  id               BIGSERIAL     PRIMARY KEY,
  apertura_caja_id BIGINT        NOT NULL REFERENCES apertura_caja(id),
  saldo_esperado   NUMERIC(10,2) NOT NULL,
  saldo_contado    NUMERIC(10,2) NOT NULL,
  diferencia       NUMERIC(10,2) NOT NULL,
  observaciones    TEXT,
  realizado_por    UUID          NOT NULL,
  created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_arqueo_caja_apertura ON arqueo_caja(apertura_caja_id);
