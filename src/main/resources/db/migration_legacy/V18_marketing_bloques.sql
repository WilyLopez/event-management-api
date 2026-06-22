ALTER TABLE plantilla_email
    ADD COLUMN IF NOT EXISTS contenido_bloques JSONB;

ALTER TABLE plantilla_email
    ALTER COLUMN contenido_html DROP NOT NULL;

INSERT INTO tipo_email (codigo, nombre, descripcion, es_sistema, orden)
VALUES
    ('PROMOCION',             'Promoción',             'Ofertas y descuentos para clientes',              FALSE, 10),
    ('NEWSLETTER',            'Newsletter',             'Boletines informativos periódicos',               FALSE, 11),
    ('CAMPANA_REACTIVACION',  'Reactivación',          'Recuperar clientes inactivos',                    FALSE, 12),
    ('RECORDATORIO_COMERCIAL','Recordatorio comercial', 'Recordatorios no críticos de reservas próximas', FALSE, 13),
    ('COMUNICADO',            'Comunicado',             'Anuncios y comunicaciones generales',             FALSE, 14),
    ('CONFIRMACION_RESERVA',  'Confirmación de reserva','Email automático al confirmar una reserva',       TRUE,  20),
    ('TICKET_QR',             'Ticket con QR',          'Entrega del QR de acceso al evento',              TRUE,  21),
    ('COMPROBANTE_PAGO',      'Comprobante de pago',    'Recibo automático tras pago exitoso',             TRUE,  22),
    ('CANCELACION_RESERVA',   'Cancelación de reserva', 'Notificación automática de cancelación',          TRUE,  23),
    ('REPROGRAMACION',        'Reprogramación',         'Notificación de cambio de fecha',                 TRUE,  24),
    ('REEMBOLSO',             'Reembolso',              'Confirmación de devolución de pago',              TRUE,  25),
    ('BIENVENIDA_CLIENTE',    'Bienvenida al cliente',  'Email de bienvenida al registrarse',              TRUE,  26),
    ('RECORDATORIO_EVENTO',   'Recordatorio de evento', 'Recordatorio automático 24h antes del evento',    TRUE,  27)
ON CONFLICT (codigo) DO NOTHING;

UPDATE tipo_email SET es_sistema = TRUE
WHERE codigo IN ('TRANSACCIONAL', 'SISTEMA');
