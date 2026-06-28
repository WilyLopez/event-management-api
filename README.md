# Kiki y Lala PEMS — Backend

Sistema de gestión de eventos para Kiki y Lala. API REST construida con Spring Boot 3.4 / Java 21, desplegada en Railway con base de datos PostgreSQL en Supabase.

**Producción:** https://app-kikiylala.up.railway.app  
**Frontend:** https://kikiylala.vercel.app

## Stack

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 3.4.0 | Framework principal |
| Spring Security | — | Autenticación JWT (Supabase) |
| Spring Data JPA | — | Persistencia |
| PostgreSQL | 17 | Base de datos (Supabase) |
| Flyway | — | Migraciones de BD |
| AWS SDK S3 | 2.26 | Almacenamiento (Supabase Storage) |
| OpenHTML to PDF | 1.0 | Generación de comprobantes PDF |
| SpringDoc OpenAPI | 2.8 | Documentación Swagger |
| Spring Mail | — | Envío de correos (Gmail SMTP) |
| Maven | 3.9 | Gestión de dependencias |

## Arquitectura

Arquitectura hexagonal (ports & adapters):

```
src/main/java/com/playzone/pems/
├── application/        # Casos de uso, DTOs, puertos de entrada/salida
├── domain/             # Modelos de dominio, repositorios (interfaces)
├── infrastructure/     # Adaptadores: JPA, S3, email, Supabase, seguridad
├── interfaces/         # Controllers REST, schedulers
└── shared/             # Configuración global, utilidades
```

## Módulos principales

| Módulo | Descripción |
|---|---|
| `calendario` | Disponibilidad, tarifas, bloqueos, feriados |
| `evento` | Reservas públicas y eventos privados |
| `cliente` | Registro y perfil de clientes |
| `caja` | Punto de venta presencial (POS) |
| `cms` | Banners, galerías, contenido público |
| `comercial` | Paquetes, promociones, cotizaciones |
| `facturacion` | Boletas y facturas SUNAT vía NubeFact |
| `notificaciones` | Notificaciones en tiempo real |
| `marketing` | Campañas de email masivo |
| `auditoria` | Registro de acciones del sistema |

## Requisitos locales

- Java 21+
- Maven 3.9+ (o usar `./mvnw`)
- PostgreSQL 17 (o conexión a Supabase)
- Variables de entorno configuradas (ver `.env.example`)

## Configuración

Copiar `.env.example` a `.env` y completar los valores:

```bash
cp .env.example .env
```

Variables requeridas:

```env
DB_URL=jdbc:postgresql://...
DB_USERNAME=...
DB_PASSWORD=...
MAIL_USERNAME=...
MAIL_PASSWORD=...
JWT_SECRET=...
SUPABASE_JWT_ISSUER=...
SUPABASE_JWKS_URL=...
SUPABASE_ANON_KEY=...
SUPABASE_STORAGE_S3_ENDPOINT=...
SUPABASE_STORAGE_ACCESS_KEY=...
SUPABASE_STORAGE_SECRET_KEY=...
CORS_ORIGINS=http://localhost:3000
NUBEFACT_TOKEN=...
NUBEFACT_RUC=...
```

## Ejecutar en desarrollo

```bash
./mvnw spring-boot:run
# o con perfil explícito
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

La API queda disponible en `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Build

```bash
./mvnw clean package -DskipTests
```

El JAR se genera en `target/pems-0.0.1-SNAPSHOT.jar`.

## Tests

```bash
./mvnw test
```

Los tests usan H2 en memoria (perfil `test`). No requieren conexión a Supabase.

## Perfiles Spring

| Perfil | Descripción |
|---|---|
| `dev` | Desarrollo local — SQL logging, Swagger, DevTools activos |
| `prod` | Producción — sin SQL logging, Swagger deshabilitado, compresión HTTP |
| `test` | Tests — H2 en memoria |

Activar perfil en producción: variable de entorno `SPRING_PROFILES_ACTIVE=prod`.

## Despliegue en Railway

1. Conectar el repositorio en Railway.
2. Railway detecta el `Dockerfile` automáticamente.
3. Configurar todas las variables de entorno en el panel de Railway.
4. El health check se realiza en `GET /api/v1/health/ping`.

Ver `railway.toml` para la configuración de build y despliegue.

## Base de datos

- Motor: PostgreSQL 17 en Supabase
- Esquema: `public`
- Migraciones: Flyway (directorio `src/main/resources/db/migration_legacy/`)
- Las migraciones se aplican **manualmente** en Supabase SQL Editor (`flyway.enabled=false`)
- JPA con `ddl-auto: validate` — nunca altera el esquema automáticamente

## Seguridad

- Autenticación vía JWT emitidos por Supabase Auth
- Validación con JWKS público del proyecto Supabase
- CORS configurable vía variable `CORS_ORIGINS`
- Endpoints públicos declarados en `SecurityConfig`
- Usuario no-root en el contenedor Docker (`spring:spring`)

## Endpoints públicos (no requieren autenticación)

```
GET  /api/v1/health/ping
GET  /api/v1/sedes/**
GET  /api/v1/calendario/**
GET  /api/v1/banners
GET  /api/v1/cms/**
GET  /api/v1/promociones/**
GET  /api/v1/paquetes/**
GET  /api/v1/actividades/**
GET  /api/v1/novedades/**
GET  /api/v1/resenas/**
POST /api/v1/resenas
POST /api/v1/clientes/registro
POST /api/v1/auth/**
```

## Variables de entorno en producción (Railway)

| Variable | Descripción |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Debe ser `prod` |
| `DB_URL` | JDBC URL de Supabase con `?sslmode=require` |
| `DB_USERNAME` | Usuario de Supabase |
| `DB_PASSWORD` | Contraseña de Supabase |
| `MAIL_USERNAME` | Cuenta Gmail para envíos |
| `MAIL_PASSWORD` | Google App Password |
| `JWT_SECRET` | Secreto JWT (mínimo 64 caracteres) |
| `SUPABASE_JWT_ISSUER` | URL del issuer de Supabase Auth |
| `SUPABASE_JWKS_URL` | URL del JWKS de Supabase Auth |
| `SUPABASE_ANON_KEY` | Clave anon de Supabase |
| `SUPABASE_STORAGE_*` | Credenciales S3 de Supabase Storage |
| `CORS_ORIGINS` | URL del frontend en Vercel |
| `NUBEFACT_TOKEN` | Token de NubeFact (SUNAT) |
| `NUBEFACT_RUC` | RUC del negocio |
