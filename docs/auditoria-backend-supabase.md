# Auditoría Backend PEMS — Preparación para Migración a Supabase

> Generado el 2026-06-03 para preparar migración a Supabase (BaaS - Opción B).
> No se modificó ningún archivo del proyecto.

---

## 1. Resumen ejecutivo

El backend PEMS es una aplicación Spring Boot 3.4.0 / Java 21 con arquitectura hexagonal
razonablemente bien estructurada (domain → application → infrastructure → interfaces).
La lógica de negocio nuclear (aforo, exclusión público/privado, tarifas, SUNAT) está
correctamente encapsulada en la capa de aplicación y es migrable a Edge Functions.

**El problema central es la capa de identidad:** todo el sistema de autenticación
(JWT propio, BCrypt, tabla `refresh_token`, tabla `usuarioadmin`) debe ser eliminado
completamente y sustituido por Supabase Auth. Esto arrastra consigo las entidades JPA
`UsuarioAdminEntity` y `ClienteEntity`, cuyos mapeos de columna no coinciden con el
nuevo esquema (`idusuarioadmin`, `contresenahash`, `fotoperfilurl` — todo camelCase/sin
underscores).

Los dominios de **inventario y proveedores** (que ya fueron eliminados del esquema
nuevo) siguen presentes en el backend: `proveedor`, `contrato/ContratoProveedor`,
`venta/DetalleVenta` con `idProducto`. Deben eliminarse.

La **integración con S3/MinIO** (AWS SDK v2) se reemplaza íntegra por Supabase Storage;
la interfaz `StoragePort` ya abstrae el almacenamiento, lo que simplifica el cambio.

La **integración SUNAT vía NubeFact** es compleja y crítica; migrar a Edge Function
tiene riesgo alto. Se recomienda conservarla en un backend mínimo Java o en una
Edge Function TypeScript bien testeada.

**Veredicto general:** ~30 % del código se elimina, ~50 % se reescribe/refactoriza,
~20 % se conserva directamente (lógica de negocio). No es una migración trivial —
se estima entre 6 y 10 semanas de esfuerzo full-time para un desarrollador.

---

## 2. Estructura del repositorio

El proyecto está organizado como **dos repositorios Git separados** dentro de un
directorio común:

```
gestion-eventos/
├── pems/                          ← Backend Spring Boot (este repo)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/playzone/pems/
│       │   ├── domain/            ← Modelos y puertos de repositorio
│       │   ├── application/       ← Casos de uso y servicios
│       │   ├── infrastructure/    ← JPA, seguridad, S3, SUNAT, correo
│       │   ├── interfaces/        ← Controladores REST y schedulers
│       │   └── shared/            ← Excepciones, utils, response wrapper
│       └── resources/
│           ├── application.yaml
│           ├── application-dev.yml
│           ├── application-prod.yml
│           └── db/migration/      ← Migraciones Flyway (V1–V14, V99)
└── pems-web/                      ← Frontend Next.js (repo separado)
```

**Árbol de dominios relevantes:**

```
domain/
├── auditoria/model+repository
├── auth/model+repository          ← ELIMINAR (Supabase Auth)
├── calendario/model+repository+exception+enums
├── cms/model+repository+exception+enums
├── comercial/model+repository
├── configuracion/model+repository
├── contrato/model+repository+exception+enums
├── evento/model+repository+exception+enums
├── facturacion/model+repository+exception+enums
├── fidelizacion/model+repository+exception
├── finanzas/model+repository+enums
├── marketing/model+repository
├── pago/model+repository+exception+enums
├── preferencia/model+repository+enums
├── promocion/model+repository+exception+enums
├── proveedor/model+repository+exception  ← ELIMINAR
├── storage/StoragePort+StorageCarpeta    ← REESCRIBIR (→ Supabase Storage)
├── usuario/model+repository+exception
└── venta/model+repository+exception
```

**Migraciones Flyway actuales** (esquema *viejo*, no el nuevo Supabase):

```
db/migration/
├── V1_extensions_and_helpers.sql
├── V2_catalogos.sql
├── V3_sede_y_configuracion.sql
├── V4_identidad.sql
├── V5_calendario.sql
├── V6_catalogo_comercial.sql
├── V7_sitio_web.sql
├── V8_operacion.sql
├── V9_finanzas.sql
├── V10_facturacion.sql
├── V11_notificaciones_y_marketing.sql
├── V12_auditoria_y_cache.sql
├── V13_rls_policies.sql    ← parcial, no cubre nuevo esquema
├── V14_funciones_y_triggers.sql
└── V99_seed.sql
```

---

## 3. Inventario de dominios

### 3.1 `auditoria`
| Elemento | Descripción |
|---|---|
| **Modelo** | `LogAuditoria` — registro de acción, tabla, entidad, usuario, ip |
| **Repositorio** | `LogAuditoriaRepository` |

### 3.2 `auth`
| Elemento | Descripción |
|---|---|
| **Modelo** | `RefreshToken` — token UUID, idUsuario, correo, tipoUsuario, fechaExpira, revocado |
| **Repositorio** | `RefreshTokenRepository` — métodos: findByToken, revocarPorUsuario |

### 3.3 `calendario`
| Elemento | Descripción |
|---|---|
| **Modelos** | `BloqueCalendario`, `ConfiguracionCalendario`, `DisponibilidadDiaria`, `Feriado`, `OcupacionDia` (value object), `Tarifa`, `Turno` |
| **Repositorios** | `BloqueCalendarioRepository`, `ConfiguracionCalendarioRepository`, `DisponibilidadDiariaRepository`, `FeriadoRepository`, `TarifaRepository`, `TurnoRepository` |
| **Enums** | `TipoDia` (SEMANA / FIN_SEMANA_FERIADO), `TipoFeriado`, `TipoOcupacionDia` |
| **Excepciones** | `AforoExcedidoException`, `ConflictoActividadException`, `DisponibilidadNotFoundException`, `FechaNoDisponibleException` |

### 3.4 `cms`
| Elemento | Descripción |
|---|---|
| **Modelos** | `Banner`, `ConfiguracionPublica`, `ContenidoLegal`, `ContenidoWeb`, `Faq`, `ImagenGaleria`, `Resena`, `SeccionWeb`, `TipoContenido` |
| **Repositorios** | Uno por modelo (9 total) |
| **Enums** | `CategoriaImagen` |
| **Excepciones** | `ContenidoNotFoundException` |

### 3.5 `comercial`
| Elemento | Descripción |
|---|---|
| **Modelos** | `ActividadLocal`, `ExtraPaquete`, `NovedadLocal`, `PaqueteEvento`, `ServicioCotizacion`, `ZonaJuego` |
| **Repositorios** | Uno por modelo (6 total) |

### 3.6 `configuracion`
| Elemento | Descripción |
|---|---|
| **Modelo** | `ConfiguracionSistema` — pares clave/valor del sistema |
| **Repositorio** | `ConfiguracionSistemaRepository` |

### 3.7 `contrato`
| Elemento | Descripción |
|---|---|
| **Modelos** | `Contrato`, `ActividadContrato`, `ContratoProveedor` ← depende de proveedor eliminado, `DocumentoContrato` |
| **Repositorios** | `ContratoRepository`, `ActividadContratoRepository`, `ContratoProveedorRepository`, `DocumentoContratoRepository` |
| **Enums** | `ContratadoPor` (EVENTO / SEDE), `EstadoContrato` |
| **Excepciones** | `ContratoNotFoundException` |

### 3.8 `evento`
| Elemento | Descripción |
|---|---|
| **Modelos** | `EventoPrivado`, `ReservaPublica`, `VentaPresencial`, `EventoExtra`, `PagoVenta`, `ChecklistEvento` |
| **Repositorios** | `EventoPrivadoRepository`, `ReservaPublicaRepository`, `VentaPresencialRepository`, `EventoExtraRepository`, `PagoVentaRepository`, `ChecklistEventoRepository` |
| **Enums** | `CanalReserva`, `EstadoEventoPrivado`, `EstadoReservaPublica` |
| **Excepciones** | `ReservaNotFoundException` |

### 3.9 `facturacion`
| Elemento | Descripción |
|---|---|
| **Modelos** | `Comprobante`, `SerieComprobante` |
| **Repositorios** | `ComprobanteRepository`, `SerieComprobanteRepository` |
| **Enums** | `EstadoComprobante`, `TipoComprobante` (BOLETA / FACTURA), `TipoDocReceptor` |
| **Excepciones** | `ComprobanteNotFoundException`, `SunatRechazadoException` |

### 3.10 `fidelizacion`
| Elemento | Descripción |
|---|---|
| **Modelo** | `HistorialFidelizacion` — contador de visitas, puntos, nivel cliente |
| **Repositorio** | `HistorialFidelizacionRepository` |
| **Excepciones** | `BeneficioNoAplicableException` |

### 3.11 `finanzas`
| Elemento | Descripción |
|---|---|
| **Modelos** | `AperturaCaja`, `MovimientoCaja`, `RegistroIngreso`, `RegistroEgreso`, `GastoEventoPrivado`, `GastoOperativoDiario`, `PresupuestoEvento`, `TipoIngreso`, `TipoEgreso` |
| **Repositorios** | Uno por modelo (9 total) |
| **Enums** | `CategoriaIngreso`, `CategoriaEgreso`, `EstadoCaja` (ABIERTA/CERRADA), `EstadoPresupuesto`, `TipoMovimientoCaja` |

### 3.12 `marketing`
| Elemento | Descripción |
|---|---|
| **Modelos** | `CampanaEmail`, `EnvioEmail`, `PlantillaEmail`, `TipoEmail` |
| **Repositorios** | Uno por modelo (4 total) |

### 3.13 `pago`
| Elemento | Descripción |
|---|---|
| **Modelo** | `Pago` |
| **Repositorio** | `PagoRepository` |
| **Enums** | `MedioPago`, `TipoPago` |
| **Excepciones** | `PagoInvalidoException` |

### 3.14 `preferencia`
| Elemento | Descripción |
|---|---|
| **Modelo** | `PreferenciaAdmin` — tema, fuente, ancho contenido, primer día semana |
| **Repositorio** | `PreferenciaAdminRepository` |
| **Enums** | `AnchoContenido`, `PrimerDiaSemana`, `RadiosBordes`, `TamanioFuente`, `TemaAdmin` |

### 3.15 `promocion`
| Elemento | Descripción |
|---|---|
| **Modelo** | `Promocion` — tipo, vigencia, descuento, límite usos, condiciones visuales |
| **Repositorio** | `PromocionRepository` |
| **Enums** | `TipoPromocion` |
| **Excepciones** | `PromocionNotFoundException` |

### 3.16 `proveedor`
| Elemento | Descripción |
|---|---|
| **Modelo** | `Proveedor` |
| **Repositorio** | `ProveedorRepository` |
| **Excepciones** | `ProveedorNotFoundException` |

### 3.17 `storage`
| Elemento | Descripción |
|---|---|
| **Puerto** | `StoragePort` — guardar, eliminar, obtenerUrlPublica |
| **Enum** | `StorageCarpeta` — categorías de carpeta |

### 3.18 `usuario`
| Elemento | Descripción |
|---|---|
| **Modelos** | `UsuarioAdmin` (rol, contrasenaHash, bloqueadoHasta, intentosFallidos), `Cliente` (VIP, descuento, correoVerificado, segmento), `Sede` |
| **Repositorios** | `UsuarioAdminRepository`, `ClienteRepository`, `SedeRepository` |
| **Excepciones** | `ClienteNotFoundException`, `UsuarioBlockedException` |

### 3.19 `venta`
| Elemento | Descripción |
|---|---|
| **Modelos** | `Venta`, `DetalleVenta` (idProducto — campo huérfano sin inventario) |
| **Repositorios** | `VentaRepository`, `DetalleVentaRepository` |
| **Excepciones** | `VentaNotFoundException` |

---

## 4. Inventario de casos de uso

### 4.1 `application/auditoria`
| Servicio | Implementa | Métodos principales |
|---|---|---|
| `AuditoriaService` | `@Service`, `@Transactional` | `registrar`, `buscar`, `listarPorEntidad` |

### 4.2 `application/auth`
| Servicio | Implementa | Métodos principales |
|---|---|---|
| `RefreshTokenService` | `@Service` | `crear`, `renovar`, `revocar` |

### 4.3 `application/calendario`
| Servicio | Implementa | Métodos principales |
|---|---|---|
| `CalendarioService` | `ConsultarDisponibilidadUseCase`, `BloquearFechasUseCase` | `consultarPorFecha`, `consultarRango`, `ocupacionDia`, `disponibleParaReservaPublica`, `disponibleParaEventoPrivado`, `ejecutar (bloquear)`, `desactivar` |
| `ConfiguracionCalendarioService` | — | `obtener`, `actualizar` |
| `FeriadoService` | — | `listar`, `crear`, `eliminar` |
| `ResumenDiaService` | — | `resumen` |
| `TarifaService` | `ConfigurarTarifaUseCase` | `ejecutar` (desactiva anterior, guarda nueva) |

### 4.4 `application/cms`
Múltiples servicios CRUD: BannerService, ContenidoWebService, GaleriaService, FaqService, ResenaService, ConfiguracionPublicaService, ContenidoLegalService, SeccionWebService, TipoContenidoService.

### 4.5 `application/comercial`
Múltiples servicios CRUD: ActividadLocalService, NovedadLocalService, PaqueteEventoService, ServicioCotizacionService, ZonaJuegoService.

### 4.6 `application/configuracion`
| Servicio | Métodos principales |
|---|---|
| `ConfiguracionService` | `obtener`, `actualizar`, `listar` |

### 4.7 `application/contrato`
| Servicio | Métodos principales |
|---|---|
| `ContratoService` | `generar`, `actualizar`, `cambiarEstado`, `listar`, `obtener` |

### 4.8 `application/dashboard`
| Servicio | Métodos principales |
|---|---|
| `DashboardAdminService` | `resumenAdmin` (agrega métricas de reservas, ingresos del día, eventos) |

### 4.9 `application/evento`
| Servicio | Implementa | Transaccional | Métodos principales |
|---|---|---|---|
| `ReservaPublicaService` | `CrearReservaPublicaUseCase`, `ReprogramarReservaUseCase`, `CancelarReservaUseCase`, `ConsultarReservasUseCase` | Sí (`@Transactional`) | `ejecutar(crear)`, `ejecutar(reprogramar)`, `ejecutar(cancelar)`, `consultarPorCliente`, `consultarPorSedeYFecha`, `consultarPorSedeYEstado` |
| `EventoPrivadoService` | `SolicitarEventoPrivadoUseCase`, `ConfirmarEventoPrivadoUseCase`, `CancelarEventoPrivadoUseCase`, `ConsultarEventosPrivadosUseCase`, `BuscarEventosAdminUseCase` | Sí | `ejecutar(solicitar)`, `ejecutar(confirmar)`, `ejecutar(cancelar)`, `completar`, `registrarSaldo`, `buscar`, `consultarPorCliente`, `consultarPorSedeYEstado` |
| `VentaPresencialService` | — | Sí | `calcular`, `registrar` |
| `ReservaAdminService` | — | Sí | `buscarTicketDetalle`, `marcarEntrada`, `editarFecha` |
| `ChecklistService` | `GestionarChecklistUseCase` | Sí | `listar`, `completar`, `descompletar` |

### 4.10 `application/facturacion`
| Servicio | Implementa | Métodos principales |
|---|---|---|
| `FacturacionService` | `EmitirComprobanteUseCase`, `AnularComprobanteUseCase` | `ejecutar(emitir)` (correlativo atómico + envío SUNAT), `ejecutar(anular)` |

### 4.11 `application/fidelizacion`
| Servicio | Métodos principales |
|---|---|
| `FidelizacionService` | `acumular`, `consultarHistorial`, `aplicarBeneficio` |

### 4.12 `application/finanzas`
| Servicio | Métodos principales |
|---|---|
| `CajaService` | `abrir`, `cerrar`, `registrarMovimiento`, `listarMovimientos`, `eliminarMovimiento`, `obtenerPorSedeYFecha`, `listarPorRango` |
| `RegistroIngresoService` | `registrar`, `registrarAutomatico`, `listar` |
| `RegistroEgresoService` | `registrar`, `listar` |
| `GastoEventoService` | `registrar`, `listar` |
| `GastoOperativoService` | `registrar`, `listar` |
| `PresupuestoEventoService` | `guardar`, `ejecutar`, `listar` |
| `ResumenFinancieroService` | `resumenDiario`, `resumenRango`, `resumenEventoFinanciero` |

### 4.13 `application/marketing`
| Servicio | Métodos principales |
|---|---|
| `MarketingService` | `crearCampana`, `enviarCampana`, `guardarPlantilla`, `listarCampanas` |

### 4.14 `application/pago`
| Servicio | Métodos principales |
|---|---|
| `PagoService` | `registrar`, `consultarPorId` |

### 4.15 `application/preferencia`
| Servicio | Métodos principales |
|---|---|
| `PreferenciaAdminService` | `obtener`, `actualizar` |

### 4.16 `application/promocion`
| Servicio | Implementa | Métodos principales |
|---|---|---|
| `PromocionService` | `CrearPromocionUseCase`, `ListarPromocionesUseCase`, `AplicarPromocionUseCase`, `DesactivarPromocionUseCase` | `ejecutar(crear/editar)`, `listar`, `aplicar`, `ejecutar(desactivar)` |

### 4.17 `application/proveedor`
| Servicio | Métodos principales |
|---|---|
| `ProveedorService` | `crear`, `actualizar`, `listar`, `obtener` |

### 4.18 `application/usuario`
| Servicio | Métodos principales |
|---|---|
| `UsuarioAdminService` | `autenticar`, `listar`, `obtener`, `crear`, `actualizarPerfil`, `cambiarContrasena`, `desactivar`, `activar` |
| `ClienteService` | `registrar`, `verificarCorreo`, `autenticar`, `actualizar`, `listar`, `obtener`, `marcarVip` |
| `SedeService` | `listar`, `obtener` |

### 4.19 `application/venta`
| Servicio | Métodos principales |
|---|---|
| `VentaService` | `ejecutar(procesar)`, `consultarPorId`, `consultarPorSedeYFechas` |

---

## 5. Inventario de infraestructura

### 5.1 Entidades JPA (persistence layer)

Cada dominio tiene su carpeta `persistence/<dominio>/entity/` con la entidad JPA
correspondiente. Se listan las críticas con observaciones de mapeo:

| Entidad JPA | Tabla BD | Observaciones de mapeo |
|---|---|---|
| `UsuarioAdminEntity` | `usuarioadmin` | Columnas en camelCase sin underscores: `idusuarioadmin`, `contresenahash`, `fotoperfilurl`, `idsede`. **Incompatible con nuevo esquema.** |
| `ClienteEntity` | `cliente` (presumible) | Tiene `contrasenaHash`, `tokenVerificacion`. **Incompatible con Supabase Auth.** |
| `RefreshTokenEntity` | `refresh_token` | `idrefreshtoken`. **Tabla a eliminar.** |
| `SedeEntity` | `sede` | Relativamente estable |
| `ReservaPublicaEntity` | `reservapublica` (sin underscore) | **Nombre de tabla no coincide con nuevo esquema `reserva`.** |
| `EventoPrivadoEntity` | `eventoprivado` | **Nombre no coincide con `evento`.** |
| `AperturaCajaEntity` | `aperturacaja` | **Nombre no coincide con `apertura_caja`.** |
| `MovimientoCajaEntity` | `movimientocaja` | **Nombre no coincide con `movimiento_caja`.** |
| Todos los demás | Nombres sin underscore | Problema generalizado: el esquema viejo usa camelCase, el nuevo usa snake_case |

### 5.2 Repositorios JPA

Cada dominio tiene un `<Dominio>JpaRepository` que extiende `JpaRepository<Entity, Long>`
y un adaptador `<Dominio>PersistenceAdapter` que implementa el puerto de dominio.
El patrón es uniforme en todo el proyecto.

### 5.3 Adaptadores externos

| Adaptador | Puerto que implementa | Descripción |
|---|---|---|
| `S3StorageClient` | — | Upload/delete en S3/MinIO usando AWS SDK v2. Configurable vía `STORAGE_ENDPOINT` para apuntar a MinIO local. |
| `StorageAdapter` | `StoragePort` | Decide entre LOCAL (disco) y S3 según `storage.tipo`. En producción usa `S3StorageClient`. |
| `NubefactClient` | — | HTTP client hacia `api.nubefact.com` para emisión y anulación de comprobantes SUNAT. |
| `SunatAdapter` | `EnviarComprobanteSunatPort` | Orquesta NubefactClient + NubefactMapper. |
| `JavaMailCorreoClient` | — | Envío de emails vía JavaMail/SMTP. |
| `CorreoAdapter` | puerto de correo | Wrappea JavaMailCorreoClient. |
| `PdfTicketService` | — | Genera PDFs con openhtmltopdf + Thymeleaf. |

### 5.4 Seguridad

| Clase | Rol |
|---|---|
| `SecurityConfig` | Define `SecurityFilterChain`: stateless, CSRF desactivado, rutas públicas/protegidas por rol. |
| `JwtTokenProvider` | Implementa `GenerarTokenPort`: firma HMAC-SHA (JJWT 0.12.6), extrae claims, valida. Secret desde `JWT_SECRET`. |
| `JwtAuthenticationFilter` | `OncePerRequestFilter`: extrae token del header `Authorization: Bearer`, valida, inyecta `idUsuario` en request attribute. |
| `CustomUserDetailsService` | Carga usuario (admin o cliente) desde BD por correo para Spring Security. |
| `CustomUserDetails` | Adaptador entre el dominio y `UserDetails` de Spring. |
| `UrlSaneamientoFilter` | Normaliza URLs antes del procesamiento. |
| `BCryptPasswordEncoder` | BCrypt strength 12. Configurado como `@Bean` en `SecurityConfig`. |

**Hallazgo crítico de seguridad:** `SecurityConfig.java` contiene rutas duplicadas con
prefijo doble (`/api/v1/api/v1/banners/**`, etc.) desde la línea 63. Estas rutas no
coinciden con ningún endpoint real pero son una señal de copia/pegado defectuosa;
en la migración se elimina toda esta configuración.

### 5.5 Schedulers

| Scheduler | Descripción |
|---|---|
| `DisponibilidadScheduler` | Cron: genera/actualiza registros de disponibilidad diaria futuros. |
| `EnvioEmailScheduler` | Cron: procesa cola de envíos de email pendientes. |
| `PromocionScheduler` | Cron: desactiva promociones vencidas. |

---

## 6. Tabla de endpoints REST

| Controlador | Base path | Métodos principales | Roles |
|---|---|---|---|
| `AuthController` | `/api/v1/auth` | loginCliente, loginAdmin, refresh, logout | Público |
| `UsuarioAdminController` | `/api/v1/usuarios-admin` | listar, obtener, crear, actualizarPerfil, cambiarContrasena, desactivar, activar | ADMIN |
| `ClienteController` | `/api/v1/clientes` | registro, verificar, listar, obtener, actualizar, hacerVip, migrarWeb | CLIENTE+ADMIN / parcial público |
| `SedeController` | `/api/v1/sedes` | listar, obtener | ADMIN |
| `CalendarioController` | `/api/v1/calendario` | consultarFecha, consultarRango, bloquear, desbloquear | GET público; POST ADMIN |
| `ConfiguracionCalendarioController` | `/api/v1/calendario` | obtener, actualizar | ADMIN |
| `FeriadoController` | `/api/v1/feriados` | listar, crear, eliminar | GET público; mutación ADMIN |
| `TarifaController` | `/api/v1/tarifas` | listar, configurar | GET público; POST ADMIN |
| `BannerController` | `/api/v1/banners` | listar, obtener, crear, actualizar, eliminar | GET público; mutación ADMIN |
| `GaleriaController` | `/api/v1/galeria` | listar, subir, eliminar | GET público; mutación ADMIN |
| `FaqController` | `/api/v1/cms/faqs` | listar, crear, actualizar, eliminar | GET público; mutación ADMIN |
| `ContenidoWebController` | `/api/v1/contenido` | listar, obtener, editar | GET público; PUT ADMIN |
| `ConfiguracionPublicaController` | `/api/v1/cms/configuracion` | obtener, actualizar | GET público; PUT ADMIN |
| `ContenidoLegalController` | `/api/v1/cms/legal` | obtener, actualizar | GET público; PUT ADMIN |
| `ResenaController` | `/api/v1/resenas` | listar, crear, aprobar, eliminar | GET+POST público; PATCH/DELETE ADMIN |
| `SeccionWebController` | `/api/v1/cms/secciones` | listar, obtener, actualizar | GET público; PUT ADMIN |
| `TipoContenidoController` | `/api/v1/cms/tipos-contenido` | listar | GET público |
| `ActividadController` | `/api/v1/actividades` | listar, obtener, crear, actualizar, eliminar | GET público; mutación ADMIN |
| `NovedadController` | `/api/v1/novedades` | listar, obtener, crear, actualizar, eliminar | GET público; mutación ADMIN |
| `PaqueteController` | `/api/v1/paquetes` | listar, obtener, crear, actualizar, eliminar | GET público; mutación ADMIN |
| `ServicioCotizacionController` | `/api/v1/servicios-cotizacion` | listar, crear, actualizar | GET público; mutación ADMIN |
| `ZonaController` | `/api/v1/zonas` | listar, obtener, crear, actualizar, eliminar | GET público; mutación ADMIN |
| `ConfiguracionController` | `/api/v1/configuracion` | obtener, actualizar | ADMIN |
| `ContratoController` | `/api/v1/contratos` | generar, obtener, listar, actualizar, cambiarEstado | ADMIN |
| `DashboardController` | `/api/v1/dashboard` | resumenAdmin | ADMIN |
| `ReservaPublicaController` | `/api/v1/reservas` | listar, listarAdmin, metricas, obtenerPorTicket, obtener, crear, reprogramar, cancelar, confirmarIngreso, confirmarPago, subirComprobante, buscarTicketDetalle, marcarEntrada, editarFecha | CLIENTE+ADMIN / ADMIN |
| `EventoPrivadoController` | `/api/v1/eventos-privados` | listar, buscarAdmin, obtener, solicitar, confirmar, completar, registrarSaldo, cancelar, checklist, extras | CLIENTE+ADMIN / ADMIN |
| `VentaPresencialController` | `/api/v1/ventas-presenciales` | calcular, registrar | ADMIN |
| `ComprobanteController` | `/api/v1/comprobantes` | emitir, anular | ADMIN |
| `FidelizacionController` | `/api/v1/fidelizacion` | consultarHistorial, acumular | ADMIN |
| `CajaController` | `/api/v1/caja` | abrir, cerrar, obtenerPorFecha, listarPorRango, listarMovimientos, registrarMovimiento, eliminarMovimiento | ADMIN |
| `IngresoController` | `/api/v1/ingresos` | registrar, listar | ADMIN |
| `EgresoController` | `/api/v1/egresos` | registrar, listar | ADMIN |
| `GastoEventoController` | `/api/v1/eventos-privados/{id}/gastos` | registrar, listar | ADMIN |
| `GastoOperativoController` | `/api/v1/gastos-operativos` | registrar, listar | ADMIN |
| `PresupuestoEventoController` | `/api/v1/presupuesto-eventos` | guardar, ejecutar, listar | ADMIN |
| `ResumenFinancieroController` | `/api/v1/finanzas` | resumenDiario, resumenRango, resumenEventoFinanciero | ADMIN |
| `DashboardFinancieroController` | `/api/v1/dashboard-financiero` | dashboard | ADMIN |
| `TipoIngresoController` | `/api/v1/tipos-ingreso` | listar, crear | ADMIN |
| `TipoEgresoController` | `/api/v1/tipos-egreso` | listar, crear | ADMIN |
| `MarketingController` | `/api/v1/marketing` | crearCampana, enviarCampana, guardarPlantilla, listarCampanas | ADMIN |
| `MediaController` | `/api/v1/media` | subir, eliminar | ADMIN |
| `PagoController` | `/api/v1/pagos` | registrar, obtener | ADMIN |
| `PreferenciaAdminController` | `/api/v1/admin/preferencias` | obtener, actualizar | ADMIN |
| `PromocionController` | `/api/v1/promociones` | listar, obtener, crear, actualizar, desactivar, aplicar | GET público; mutación ADMIN |
| `ProveedorController` | `/api/v1/proveedores` | listar, obtener, crear, actualizar | ADMIN |
| `VentaController` | `/api/v1/ventas` | procesar, obtener, listar | ADMIN |
| `AuditoriaController` | `/api/v1/auditoria` | listar, buscar | ADMIN |

---

## 7. Mapeo de dominios vs nuevo esquema Supabase

| Dominio | Estado vs nuevo esquema | Acción recomendada |
|---|---|---|
| **auth** | **ROTO COMPLETAMENTE.** JWT propio (JJWT), BCrypt, tabla `refresh_token`, dos flujos de login separados (admin/cliente). Todo reemplazado por Supabase Auth. | ELIMINAR |
| **usuario/admin** | **ROTO.** Entidad `usuarioadmin` con columnas en camelCase, campo `contresenahash`, rol como VARCHAR simple. Nuevo esquema: `perfil_usuario` + `staff_perfil` + `usuario_rol` + `rol` + `permiso`. | REESCRIBIR |
| **usuario/cliente** | **ROTO.** Tabla `cliente` con `contrasenaHash`, `tokenVerificacion`. Nuevo: `perfil_usuario` + `cliente_perfil`. La identidad pasa a Supabase Auth. | REESCRIBIR |
| **usuario/sede** | **Conservable.** Modelo `Sede` relativamente alineado con nuevo esquema. | REFACTORIZAR (nombres columna) |
| **calendario** | **Parcialmente roto.** Modelos bien diseñados; `ConfiguracionCalendario` mapea a `configuracion_sede` (nuevo). Nombres de columnas en BD no coinciden (camelCase vs snake_case). | REFACTORIZAR |
| **evento/reserva** | **Parcialmente roto.** `reservapublica` → `reserva` (+ `venta` + `venta_pago` en nuevo esquema). La lógica del servicio es sólida. | REFACTORIZAR (mapeo de entidades) |
| **evento/privado** | **Parcialmente roto.** `eventoprivado` → `evento`. Lógica del servicio conservable. | REFACTORIZAR |
| **evento/ventaPresencial** | **Conservable con ajustes.** La VentaPresencial nueva absorbe parte de `reserva` + `venta_pago`. | REFACTORIZAR |
| **facturacion** | **Conservable.** Lógica de correlativo SUNAT y emisión/anulación de comprobantes es compleja y bien encapsulada. Debe migrar a Edge Function o backend mínimo. | CONSERVAR → Edge Function |
| **finanzas/caja** | **Conservable.** `aperturacaja`/`movimientocaja` → `apertura_caja`/`movimiento_caja`. Solo cambio de nombres. | REFACTORIZAR |
| **finanzas/registros** | **Conservable.** `registroingreso`/`registroegreso` → `registro_ingreso`/`registro_egreso`. | REFACTORIZAR |
| **finanzas/presupuesto y gastos** | **Conservable.** | REFACTORIZAR |
| **cms** | **Conservable.** Modelos bien diseñados; imágenes migran a Supabase Storage. | REFACTORIZAR (Storage URLs) |
| **comercial** | **Conservable.** Paquetes, zonas, actividades, novedades bien modelados. | REFACTORIZAR |
| **configuracion** | **Parcialmente roto.** `configuracionsistema` (una tabla genérica) → nuevo tiene `configuracion_sede` + `configuracion_global` + `configuracion_publica`. | REFACTORIZAR |
| **contrato** | **Depende.** `ContratoProveedor` referencia al dominio `proveedor` que se elimina. `Contrato` de evento puede conservarse. | REFACTORIZAR (eliminar ContratoProveedor) |
| **proveedor** | **ELIMINADO en nuevo esquema.** | ELIMINAR |
| **venta** | **Conservable.** `DetalleVenta.idProducto` es campo huérfano (inventario eliminado) — debe limpiarse. | REFACTORIZAR |
| **marketing** | **Conservable.** Puede quedarse en Java o migrar a Supabase + Resend/SendGrid. | CONSERVAR o migrar a Resend |
| **pago** | **Conservable.** | REFACTORIZAR |
| **promocion** | **Conservable.** Lógica de cálculo de descuento bien encapsulada. | REFACTORIZAR |
| **fidelizacion** | **Conservable.** | REFACTORIZAR |
| **auditoria** | **Conservable.** Nuevo esquema tiene auditoría particionada por mes, misma semántica. | REFACTORIZAR |
| **preferencia** | **Conservable.** Preferencias de UI del admin. | REFACTORIZAR |
| **dashboard** | **Conservable.** Agrega datos de otros dominios; depende de repositorios. | REFACTORIZAR |
| **storage** | **ROTO.** AWS SDK S3/MinIO → Supabase Storage SDK. La interfaz `StoragePort` es un buen punto de corte. | REESCRIBIR (solo implementación) |

---

## 8. Código marcado para eliminación

### 8.1 Paquetes completos a eliminar

```
infrastructure/security/
├── JwtTokenProvider.java
├── JwtAuthenticationFilter.java
├── CustomUserDetailsService.java
├── CustomUserDetails.java
└── SecurityConfig.java          (completo — Spring Security no se usa con Supabase)
    UrlSaneamientoFilter.java     (revisar si es necesario en nuevo stack)

domain/auth/
├── model/RefreshToken.java
└── repository/RefreshTokenRepository.java

application/auth/
└── service/RefreshTokenService.java

domain/proveedor/
├── model/Proveedor.java
├── repository/ProveedorRepository.java
└── exception/ProveedorNotFoundException.java

application/proveedor/
└── service/ProveedorService.java

interfaces/rest/usuario/
└── AuthController.java           (login/logout/refresh → Supabase Auth)
    (los endpoints /api/v1/auth/** desaparecen completamente)

interfaces/rest/proveedor/
└── ProveedorController.java

infrastructure/persistence/auth/   (entidad RefreshTokenEntity + adaptador)
infrastructure/persistence/proveedor/
```

### 8.2 Clases individuales a eliminar

```
domain/contrato/model/ContratoProveedor.java         (depende de proveedor)
domain/contrato/repository/ContratoProveedorRepository.java
domain/venta/model/DetalleVenta.java → campo idProducto (inventario eliminado)
domain/usuario/model/UsuarioAdmin.java → campo contrasenaHash
domain/usuario/model/Cliente.java → campos contrasenaHash, tokenVerificacion
```

### 8.3 Dependencias Maven a eliminar

```xml
<!-- JWT — eliminado, Supabase Auth lo maneja -->
io.jsonwebtoken:jjwt-api
io.jsonwebtoken:jjwt-impl
io.jsonwebtoken:jjwt-jackson

<!-- BCrypt — eliminado, Supabase Auth lo maneja -->
(incluido en spring-boot-starter-security)

<!-- Spring Security — revisar si se conserva para RBAC local -->
org.springframework.boot:spring-boot-starter-security

<!-- AWS S3 — reemplazado por Supabase Storage client -->
software.amazon.awssdk:s3

<!-- Thymeleaf — solo si se elimina generación de PDF de Java -->
org.springframework.boot:spring-boot-starter-thymeleaf
```

---

## 9. Lógica de negocio crítica

Lógica que NO puede expresarse como políticas RLS y debe migrar a Edge Functions
o conservarse en un backend mínimo Java:

| Lógica | Servicio Java actual | Complejidad | Recomendación |
|---|---|---|---|
| **Validación de aforo concurrente** (countActivasBySedeAndFecha vs aforoMaximo) | `ReservaPublicaService.validarFechaDisponible` + `CalendarioService.disponibleParaReservaPublica` | Media | **Edge Function** — requiere SELECT FOR UPDATE o locking a nivel fila en Supabase |
| **Exclusión mutua público/privado** (si hay reservas públicas, no admite evento privado; y viceversa) | `EventoPrivadoService.validarFechaEvento` + `ReservaPublicaService.validarFechaDisponible` | Alta | **Edge Function** — lectura cruzada de dos tablas con condición de exclusión |
| **Cálculo de tarifa por TipoDia** (semana vs fin de semana/feriado) | `ReservaPublicaService.resolverTipoDia` + `TarifaService` | Simple | **Edge Function** — lookup en tabla `tarifa` |
| **Aplicación de promociones** (vigencia, tipo de día, sede, descuento porcentual/fijo) | `PromocionService.aplicar` + `Promocion.calcularDescuento` | Media | **Edge Function** — validaciones múltiples pero todas en BD |
| **Generación de número de ticket único atómico** (`TKT-{idSede}-{fecha}-{secuencia}` con loop anti-colisión) | `ReservaPublicaService.ejecutar` (bloque while con incremento de secuencia) | Media | **Edge Function** — `SELECT COUNT` + loop; considerar secuencia PostgreSQL |
| **Generación de correlativo SUNAT atómico** (`serieRepository.incrementarCorrelativoYRetornar`) | `FacturacionService.ejecutar` | Alta | **Edge Function** o **Java backend mínimo** — debe ser atómica (SELECT ... FOR UPDATE) |
| **Emisión/anulación SUNAT vía NubeFact** (HTTP a api.nubefact.com, manejo CDR) | `FacturacionService` + `SunatAdapter` + `NubefactClient` | Alta | **Mantener en Java** (o Edge Function TypeScript cuidadosamente testeada) |
| **Apertura/cierre de caja con validación** (no abrir si ya existe, no mover en caja cerrada) | `CajaService.abrir` + `CajaService.cerrar` | Simple | **Edge Function** — validaciones simples |
| **Venta presencial multi-niño** (calcula por cada niño, aplica promoción, genera ticket por niño, registra ingreso en caja) | `VentaPresencialService.registrar` | Alta | **Edge Function** — orquesta 4+ tablas en una transacción |
| **Confirmación de evento + registro automático de ingreso** (al confirmar evento privado, auto-registra el adelanto como ingreso) | `EventoPrivadoService.ejecutar(confirmar)` + `RegistrarIngresoUseCase.registrarAutomatico` | Media | **Edge Function** — 2 writes en una transacción |
| **Descuento VIP en cliente** (`Cliente.aplicarDescuentoVip`) | `Cliente.java` (lógica de dominio) | Simple | **Edge Function** — lookup del % de descuento VIP |
| **Bloqueo de calendario con detección de conflictos** (verifica reservas/eventos en rango, pide confirmación) | `CalendarioService.ejecutar(bloquear)` | Media | **Edge Function** |

**Nota sobre generación de tickets:** El método actual usa un loop `while` que re-genera
el ticket si hay colisión. Esto es vulnerable a race conditions bajo alta concurrencia.
En Supabase, conviene sustituirlo por una secuencia PostgreSQL nativa: 
`nextval('ticket_seq')` dentro de una Edge Function transaccional.

---

## 10. Configuración y secretos

### 10.1 Variables de entorno usadas

| Variable | Valor por defecto | Migración |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/BDGestionEvento` | → Supabase connection string (pooler) |
| `DB_USERNAME` | `postgres` | → Supabase user |
| `DB_PASSWORD` | `123456` | → Supabase password (mover a secrets) |
| `JWT_SECRET` | `W7kP9mN2xQzL4rT8uV1sB6cY3eH0jA5fG` ← **hardcoded en YAML** | → ELIMINAR (Supabase Auth lo gestiona) |
| `JWT_EXP_MS` | `1800000` (30 min) | → ELIMINAR |
| `JWT_VER_EXP_MS` | `86400000` (24h) | → ELIMINAR |
| `SERVER_PORT` | `8080` | → Mantener si hay backend mínimo |
| `MAIL_HOST` | `smtp.gmail.com` | → Mantener (JavaMail) o migrar a Resend |
| `MAIL_PORT` | `587` | → Mantener |
| `MAIL_USERNAME` | `tucorreo@gmail.com` | → Mantener |
| `MAIL_PASSWORD` | — | → Edge Function env var / Supabase secrets |
| `STORAGE_ACCESS_KEY` | `minioadmin` | → ELIMINAR (Supabase Storage usa service_role key) |
| `STORAGE_SECRET_KEY` | `minioadmin` | → ELIMINAR |
| `STORAGE_ENDPOINT` | `http://localhost:9000` | → ELIMINAR |
| `STORAGE_BUCKET` | `playzone-media` | → Nombre del bucket en Supabase Storage |
| `STORAGE_URL_PUBLICA` | `http://localhost:9000/playzone-media` | → URL pública de Supabase Storage |
| `STORAGE_TIPO` | `LOCAL` | → ELIMINAR (solo S3/Supabase) |
| `NUBEFACT_URL` | `https://api.nubefact.com` | → Edge Function env var |
| `NUBEFACT_TOKEN` | `changeme` | → Supabase secrets |
| `NUBEFACT_RUC` | `20000000001` | → Edge Function env var |
| `CORS_ORIGINS` | `http://localhost:3000,...` | → Supabase CORS config |
| `APP_URL` | `http://localhost:8080` | → URL de producción |

### 10.2 Valores hardcodeados críticos

- **`JWT_SECRET` en application.yaml** tiene un valor por defecto inseguro. En producción
  se lee de `JWT_SECRET` env var, pero cualquier deploy que no sobreescriba la variable
  queda con el secret expuesto. Al migrar a Supabase Auth, este riesgo desaparece.
- **`playzone.negocio.aforo-maximo: 60`** — hardcodeado en YAML, debería vivir en
  `ConfiguracionSistema` o `configuracion_sede` en BD.
- **`id-cliente-mostrador: 1`** en `application-dev.yml` — un ID de cliente hardcodeado
  para el mostrador físico. Esto es frágil; debe almacenarse en `configuracion_sede`.

---

## 11. Dependencias a eliminar o reemplazar

### 11.1 A eliminar completamente

| Dependencia | Razón |
|---|---|
| `io.jsonwebtoken:jjwt-api/impl/jackson` (0.12.6) | JWT propio → Supabase Auth |
| `software.amazon.awssdk:s3` (2.26.27) | S3/MinIO → Supabase Storage |

### 11.2 A revisar (posible eliminación)

| Dependencia | Razón |
|---|---|
| `spring-boot-starter-security` | Si Spring Security se elimina completamente (reemplazado por validación del JWT de Supabase en un filter o middleware), esta dep puede salir. Si se mantiene un backend mínimo que valide el JWT de Supabase, puede reducirse a solo la validación de tokens. |
| `spring-boot-starter-thymeleaf` | Solo se usa para `PdfTicketService` (templates de PDF). Si los tickets PDF migran a Edge Function, sale. Si se mantiene, se conserva. |

### 11.3 A conservar sin cambios

| Dependencia | Razón |
|---|---|
| `spring-boot-starter-web` | Sigue siendo necesario si hay backend mínimo |
| `spring-boot-starter-data-jpa` | Sigue usando PostgreSQL de Supabase |
| `spring-boot-starter-mail` | Envío de emails (JavaMail) — puede migrar a Resend pero no es urgente |
| `spring-boot-starter-validation` | Jakarta Bean Validation en DTOs |
| `spring-boot-starter-aop` | Auditoría AOP |
| `flyway-database-postgresql` | Migraciones de BD |
| `org.postgresql:postgresql` | Driver JDBC |
| `org.projectlombok:lombok` | Generación de código |
| `com.openhtmltopdf:*` | Generación de tickets PDF |
| `org.springdoc:springdoc-openapi-starter-webmvc-ui` | Swagger — útil en desarrollo |

### 11.4 A agregar

| Dependencia | Razón |
|---|---|
| Cliente SDK de Supabase para Java (o validación manual del JWT RS256 de Supabase Auth) | Para verificar tokens Supabase en el backend mínimo |
| Cliente HTTP de Supabase Storage (o AWS SDK apuntando al endpoint de Supabase Storage) | Supabase Storage es compatible con S3; puede reusar AWS SDK cambiando el endpoint |

---

## 12. Riesgos detectados

### R1 — Generación de ticket bajo concurrencia (ALTO)
`ReservaPublicaService` genera el número de ticket con un loop `while + countActivas`.
Bajo concurrencia simultánea, dos threads pueden leer el mismo `count`, generar el
mismo ticket, y el loop de retry no está dentro de una transacción serializable.
**Acción:** usar `SELECT nextval(...)` de una secuencia PostgreSQL atómica.

### R2 — Correlativo SUNAT sin lock explícito (ALTO)
`serieRepository.incrementarCorrelativoYRetornar` debe ejecutarse con `SELECT ... FOR UPDATE`
o equivalente atómico. Si dos emisiones concurrentes leen el mismo correlativo antes de
incrementar, se genera un duplicado rechazado por SUNAT.
**Acción:** verificar que el adaptador JPA usa una transacción serializable o un `UPDATE ... RETURNING`.

### R3 — Secret JWT con valor por defecto expuesto (MEDIO)
`playzone.seguridad.jwt-secret` tiene valor por defecto `W7kP9mN2xQzL4rT8uV1sB6cY3eH0jA5fG`
en `application.yaml` — visible en el repo. Cualquier deploy sin `JWT_SECRET` en env
usa este secret. Al migrar a Supabase Auth esto desaparece, pero **mientras coexista
el sistema viejo** es un riesgo activo.

### R4 — Rutas duplicadas en SecurityConfig (BAJO-MEDIO)
Las líneas 63–70 de `SecurityConfig` tienen paths `"/api/v1/api/v1/..."` que son
duplicados con prefijo doble. No bloquean el funcionamiento pero indican que la
configuración de seguridad fue modificada sin pruebas sistemáticas.

### R5 — `id-cliente-mostrador: 1` hardcodeado (BAJO)
El ID del cliente del mostrador físico (para ventas presenciales sin cliente registrado)
está hardcodeado en `application-dev.yml`. Si la BD se reinicia o ese cliente se elimina,
el POS falla silenciosamente.

### R6 — Migraciones Flyway del esquema VIEJO en el repo (MEDIO)
Las migraciones `V1–V14` reflejan el esquema antiguo (camelCase, tablas distintas). Al
migrar a Supabase, Flyway deja de gestionar el esquema (Supabase tiene su propio DDL).
Estas migraciones NO deben aplicarse sobre la BD nueva. Riesgo de confusión si alguien
intenta levantar el proyecto Java contra la BD Supabase nueva sin entender esto.

### R7 — `ContratoProveedor` referencia dominio eliminado (BAJO)
`ContratoProveedor.java` y `ContratoProveedorRepository.java` referencian el dominio
`Proveedor` que se elimina. Si se compila el proyecto sin eliminar estos archivos antes,
hay error de compilación al borrar `Proveedor`.

### R8 — `DetalleVenta.idProducto` campo huérfano (BAJO)
El modelo `DetalleVenta` tiene campo `idProducto` que apunta a una tabla de inventario
eliminada en el nuevo esquema. El servicio `VentaService` popula este campo. En la
migración, `venta`/`detalle_venta` del nuevo esquema no tiene esta relación.

### R9 — Schedulers con estado en BD (MEDIO)
`DisponibilidadScheduler` y `EnvioEmailScheduler` procesan registros de la BD y
actualizan su estado. Al migrar a Supabase + Edge Functions, estos schedulers deben
convertirse en pg_cron jobs o cron externo. El estado que gestionan debe revisarse
contra el nuevo esquema.

### R10 — Lógica de negocio mezclada en controladores (BAJO)
`ReservaPublicaController` contiene lógica de negocio directamente (ej. confirmación
de pago con `CONFIRMADA`, subida de comprobante). Esto no es clean architecture y
dificulta la migración a Edge Functions. Las partes afectadas deben extraerse al
servicio antes de migrar.

---

## 13. Estimación de esfuerzo por dominio

| Dominio | Acción | Esfuerzo | Justificación |
|---|---|---|---|
| **auth** | ELIMINAR | Bajo | Solo borrar clases; el nuevo flujo lo da Supabase |
| **usuario/admin** | REESCRIBIR | Medio | Nuevo modelo con `perfil_usuario + staff_perfil + usuario_rol`; entidades JPA y mappers completos |
| **usuario/cliente** | REESCRIBIR | Medio | Igual que admin; la contraseña desaparece del modelo |
| **usuario/sede** | REFACTORIZAR | Bajo | Solo ajuste de nombres de columnas JPA |
| **calendario** | REFACTORIZAR | Medio | Lógica sólida; ajuste de entidades JPA y nombres; CalendarioService es la pieza más crítica |
| **evento/reserva** | REFACTORIZAR | Medio-Alto | Servicio robusto; split de `reservapublica` en `reserva + venta_pago`; generación de ticket → secuencia PG |
| **evento/privado** | REFACTORIZAR | Medio | Lógica preservable; renombrar entidad y relaciones |
| **evento/VentaPresencial** | REFACTORIZAR | Medio | Multi-niño + multi-pago bien implementado; adaptar al nuevo esquema |
| **facturacion** | CONSERVAR → Edge Function | Alto | SUNAT/NubeFact es crítico y delicado; correlativo atómico debe ser probado exhaustivamente |
| **finanzas/caja** | REFACTORIZAR | Bajo | Lógica simple; solo renombrar tablas |
| **finanzas/registros** | REFACTORIZAR | Bajo | CRUD con renombrado |
| **finanzas/presupuesto y gastos** | REFACTORIZAR | Bajo | CRUD |
| **cms** | REFACTORIZAR | Bajo | Storage URLs apuntarán a Supabase Storage |
| **comercial** | REFACTORIZAR | Bajo | CRUD; alineado con nuevo esquema |
| **configuracion** | REFACTORIZAR | Bajo-Medio | Mapeo a tres tablas nuevas (`configuracion_sede`, `configuracion_global`, `configuracion_publica`) |
| **contrato** | REFACTORIZAR | Bajo | Eliminar `ContratoProveedor`; ajustar el resto |
| **proveedor** | ELIMINAR | Bajo | Borrar paquete completo |
| **venta** | REFACTORIZAR | Bajo | Limpiar `idProducto` huérfano |
| **marketing** | CONSERVAR o migrar a Resend | Bajo-Medio | Si se migra a Resend, reescribir el adaptador de correo |
| **pago** | REFACTORIZAR | Bajo | CRUD |
| **promocion** | REFACTORIZAR | Bajo | Lógica bien encapsulada; ajuste de entidades |
| **fidelizacion** | REFACTORIZAR | Bajo | Ajuste de entidades |
| **auditoria** | REFACTORIZAR | Bajo | Nuevo esquema usa particionado por mes; adaptar queries |
| **preferencia** | REFACTORIZAR | Bajo | CRUD de UI |
| **dashboard** | REFACTORIZAR | Bajo | Depende de otros repos; refactorizar junto con ellos |
| **storage** | REESCRIBIR (implementación) | Bajo | `StoragePort` ya abstrae el storage; solo reescribir `StorageAdapter` + `S3StorageClient` por `SupabaseStorageClient` |
| **security stack** | ELIMINAR + nuevo validador | Medio | Suprimir Spring Security JWT y reemplazar por un filter que valide el JWT RS256 de Supabase |
| **schedulers** | REFACTORIZAR → pg_cron | Bajo-Medio | Convertir los 3 schedulers a pg_cron jobs en Supabase o a cron externo |

---

*Reporte generado para preparar migración a Supabase (BaaS - Opción B).
No se modificó ningún archivo del proyecto.*
