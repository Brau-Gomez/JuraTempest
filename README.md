# JuraTempest

## Descripcion

JuraTempest es una aplicacion de microservicios para administrar un centro arcade. Permite gestionar usuarios, autenticacion JWT, maquinas, bloques horarios, reservas, fidelizacion, notificaciones, pagos, promociones, mantenimientos y torneos.

El trabajo corresponde al proyecto semestral de Desarrollo FullStack 1. La solucion aplica Spring Boot, JPA/Hibernate, migraciones con Liquibase y Flyway, validacion con DTOs, manejo centralizado de errores, comunicacion entre microservicios, API Gateway y Eureka Server.

## Equipo

| Integrante | Rol dentro del proyecto | Aporte tecnico |
|---|---|---|
| Braulio Gomez | Developer | Autenticacion JWT, usuarios, gateway, horarios, pagos, notificaciones, promociones|
| Lukas Meza | Developer | Maquinas, reservas, fidelizacion, mantenimiento, eventos_torneos|

Docente: Carlos Alberto Abarzua Castro
Asignatura y seccion: Desarrollo FullStack 1 DSY1103 - 010D

## Estructura del repositorio

El codigo fuente vive dentro de `JuraTempest/`. La raiz mantiene este README y archivos de apoyo.

```text
/
├─ README.md
└─ JuraTempest/
   ├─ api-gateway/
   ├─ eureka_server/
   ├─ ms_auth/
   ├─ ms_usuarios/
   ├─ ms_maquinas/
   ├─ ms-horarios/
   ├─ ms_reservas/
   ├─ ms_fidelizacion/
   ├─ ms_notificaciones/
   ├─ ms_pagos/
   ├─ ms_promociones/
   ├─ ms_mantenimiento/
   ├─ ms_eventos_torneos/
   ├─ docker-compose.yaml
   └─ scripts/
```

Nota: el servicio antiguo `ms_usuarios_auth` fue separado en dos servicios actuales: `ms_auth` y `ms_usuarios`.

## Arquitectura actual

| Componente | Puerto | Responsabilidad |
|---|---:|---|
| `api-gateway` | 9090 | Entrada unica a las APIs, rutas y validacion JWT |
| `eureka_server` | 8761 | Registro y descubrimiento de servicios |
| `ms_usuarios` | 9091 | Usuarios, perfiles, roles y consultas internas |
| `ms_maquinas` | 9092 | Administracion de maquinas arcade |
| `ms-horarios` | 9093 | Bloques horarios, cupos y disponibilidad |
| `ms_reservas` | 9094 | Reservas y validaciones remotas |
| `ms_fidelizacion` | 9095 | Puntos de fidelizacion por usuario |
| `ms_notificaciones` | 9096 | Notificaciones por usuario, tipo y lectura |
| `ms_pagos` | 9097 | Pagos de reservas, calculos, estados y notificaciones |
| `ms_promociones` | 9098 | Promociones, descuentos y validacion de codigos |
| `ms_mantenimiento` | 9099 | Mantenimientos preventivos/correctivos de maquinas |
| `ms_eventos_torneos` | 9100 | Torneos, inscripciones, ganadores y puntos |
| `ms_auth` | 9101 | Login, registro, validacion y emision de JWT |

## Rutas por API Gateway

Todas las pruebas externas se realizan desde:

```text
http://localhost:9090
```

| Ruta | Servicio destino |
|---|---|
| `/auth/**` | `ms-auth` |
| `/users/**` | `ms-usuarios` |
| `/maquinas/**` | `ms-maquinas` |
| `/horarios/**` | `ms-horarios` |
| `/reservas/**` | `ms-reservas` |
| `/fidelizacion/**` | `ms-fidelizacion` |
| `/notificaciones/**` | `ms-notificaciones` |
| `/pagos/**` | `ms-pagos` |
| `/promociones/**` | `ms-promociones` |
| `/mantenimientos/**` | `ms-mantenimiento` |
| `/torneos/**` | `ms-eventos-torneos` |

Rutas publicas del gateway: `OPTIONS`, `/auth/login`, `/auth/register`, `/auth/validate`, `/swagger-ui/**` y `/v3/api-docs/**`. El resto requiere `Authorization: Bearer <token>`.

Swagger UI de cada microservicio esta configurado directamente en `/doc/swagger-ui.html`. El gateway no enruta `/doc/**`.

## Tecnologias

- Java 17
- Spring Boot 4.0.6
- Spring Cloud 2025.1.1
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Web MVC y WebFlux/WebClient
- Spring Data JPA e Hibernate
- Spring Security y JWT
- MySQL 8.4 en Docker Compose
- Liquibase y Flyway
- Bean Validation
- HATEOAS en endpoints `/v2`
- Lombok
- Maven Wrapper por servicio
- Docker y Docker Compose

## Bases de datos y migraciones

Cada microservicio mantiene su propia base de datos.

| Servicio | Base de datos | Migraciones / esquema |
|---|---|---|
| `ms_usuarios` | `usuarios_db` | Liquibase `src/main/resources/db/changelog/db.changelog.sql` |
| `ms_auth` | `auth_db` | Liquibase `src/main/resources/db/changelog/db.changelog.sql` |
| `ms_maquinas` | `maquinas_db` | Liquibase `src/main/resources/db/changelog/db.changelog.sql` |
| `ms-horarios` | `horarios_db` | Liquibase `src/main/resources/db/changelog/db.changelog.sql` |
| `ms_reservas` | `reservas_db` | Liquibase `src/main/resources/db/changelog/db.changelog.sql` |
| `ms_fidelizacion` | `fidelizacion_db` | Liquibase `src/main/resources/db/changelog/db.changelog.sql` |
| `ms_notificaciones` | `notificaciones_db` | `ddl-auto=create-drop`, en perfil `dev` usa `update` por defecto |
| `ms_pagos` | `pagos_db` | `ddl-auto=create-drop` |
| `ms_promociones` | `promociones_db` | `ddl-auto=create-drop` |
| `ms_mantenimiento` | `mantenimiento_db` | Flyway `src/main/resources/db/migration/V...sql` |
| `ms_eventos_torneos` | `eventos_torneos_db` | Flyway `src/main/resources/db/migration/V...sql` |

En ejecucion local los `application.properties` esperan MySQL en `localhost:3306` con usuario `root` y password vacia. En Docker Compose se usa password `admin123` y se sobreescriben las URLs de datasource.

## Comunicacion entre microservicios

Los servicios usan `@LoadBalanced WebClient.Builder` y nombres de Eureka, por ejemplo `http://ms-usuarios/users/{id}/exists`, no puertos fijos.

| Servicio | Dependencias principales |
|---|---|
| `ms_auth` | Crea y consulta perfiles en `ms-usuarios` |
| `ms_reservas` | Valida usuario, maquina activa y bloque horario |
| `ms_fidelizacion` | Valida usuario antes de registrar puntos |
| `ms_notificaciones` | Valida usuario antes de crear notificaciones |
| `ms_pagos` | Consulta reservas, maquinas, promociones, fidelizacion y notificaciones |
| `ms_mantenimiento` | Valida maquinas/usuarios y crea notificaciones |
| `ms_eventos_torneos` | Valida usuarios, maquinas, horarios, fidelizacion y notificaciones |

## Requisitos previos

- Java 17.
- Docker y Docker Compose para levantar la pila completa.
- Maven no es obligatorio si se usan los `mvnw` incluidos en cada servicio.
- Puertos libres: `3306`, `8761`, `9090` a `9101`.
- Postman, Insomnia o similar para probar endpoints REST.

## Comandos utiles

No existe un `pom.xml` agregador en la raiz. Los comandos Maven se ejecutan desde la carpeta de cada servicio.

Compilar un servicio:

```bash
./mvnw -DskipTests compile
```

Ejecutar tests de un servicio:

```bash
./mvnw test
```

Ejecutar un test puntual:

```bash
./mvnw -Dtest=PagoServiceTest test
```

Ejecutar un servicio localmente:

```bash
./mvnw spring-boot:run
```

Construir todos los jars requeridos por Docker, desde `JuraTempest/`:

```bash
./scripts/build-targets.sh
```

Levantar la pila completa, desde `JuraTempest/`, despues de generar los jars:

```bash
docker compose up --build
```

Limpiar contenedores e imagenes del proyecto sin borrar volumenes de base de datos:

```bash
./scripts/clean-images.sh
```

## Orden de ejecucion local

Si no se usa Docker Compose, iniciar en este orden:

1. MySQL.
2. `eureka_server`.
3. `ms_usuarios`.
4. `ms_auth`.
5. Servicios de dominio: `ms_maquinas`, `ms-horarios`, `ms_reservas`, `ms_fidelizacion`, `ms_notificaciones`, `ms_promociones`, `ms_pagos`, `ms_mantenimiento`, `ms_eventos_torneos`.
6. `api-gateway`.

Validar Eureka en:

```text
http://localhost:8761
```

Validar gateway en:

```text
http://localhost:9090
```

## Flujo principal de pruebas

1. Registrar usuario con `POST /auth/register`.
2. Iniciar sesion con `POST /auth/login`.
3. Copiar el JWT retornado y usarlo como `Authorization: Bearer <token>`.
4. Crear maquinas con `POST /maquinas`.
5. Crear bloques horarios con `POST /horarios`.
6. Crear reservas con `POST /reservas`.
7. Crear promociones con `POST /promociones` o validar codigos con `POST /promociones/validar`.
8. Crear pagos con `POST /pagos` y aprobarlos con `PUT /pagos/{id}/aprobar`.
9. Revisar puntos en `GET /fidelizacion/usuario/{usuarioId}` o `GET /fidelizacion/total/{usuarioId}`.
10. Revisar notificaciones en `GET /notificaciones/usuario/{usuarioId}`.
11. Probar mantenimientos con `POST /mantenimientos` y cambios de estado.
12. Probar torneos con `POST /torneos`, apertura de inscripciones e inscripcion de usuarios.

## Ejemplos rapidos

Registro:

```http
POST http://localhost:9090/auth/register
Content-Type: application/json
```

```json
{
  "nombre": "Ana",
  "apellido": "Perez",
  "email": "ana.perez@juratempest.cl",
  "password": "123456",
  "frecuente": true,
  "roles": ["CLIENTE"]
}
```

Login:

```http
POST http://localhost:9090/auth/login
Content-Type: application/json
```

```json
{
  "email": "ana.perez@juratempest.cl",
  "password": "123456"
}
```

Crear pago:

```json
{
  "usuarioId": 1,
  "reservaId": 1,
  "promocionId": 1,
  "metodoPago": "DEBITO"
}
```

Crear mantenimiento:

```json
{
  "maquinaId": 1,
  "usuarioOperadorId": 1,
  "tipo": "PREVENTIVO",
  "descripcion": "Limpieza preventiva de gabinete",
  "tecnico": "Equipo Tecnico Norte",
  "costo": 25000
}
```

Crear torneo:

```json
{
  "nombre": "Copa Arcade",
  "descripcion": "Torneo semanal",
  "maquinaId": 1,
  "horarioId": 1,
  "cuposMaximos": 8
}
```

## Endpoints principales

Los endpoints se muestran usando el gateway `http://localhost:9090`. Salvo las rutas publicas de `/auth`, enviar token JWT.

### Auth

- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/validate`

### Users

- `GET /users`
- `GET /users/{id}`
- `GET /users/{id}/exists`
- `GET /users/email/{email}`
- `GET /users/frecuentes`
- `GET /users/total`
- `POST /users`
- `PUT /users/{id}`
- `DELETE /users/{id}`
- `GET /users/v2`
- `GET /users/v2/{id}`
- `GET /users/v2/email/{email}`
- `GET /users/v2/frecuentes`

### Maquinas

- `GET /maquinas`
- `GET /maquinas/{id}`
- `GET /maquinas/{id}/existe`
- `GET /maquinas/activa/{id}`
- `GET /maquinas/estado/{estado}`
- `GET /maquinas/tipo/{tipo}`
- `GET /maquinas/total`
- `POST /maquinas`
- `PUT /maquinas/{id}`
- `DELETE /maquinas/{id}`
- `GET /maquinas/v2`

### Horarios

- `GET /horarios`
- `GET /horarios/{id}`
- `GET /horarios/{id}/existe`
- `GET /horarios/fecha/{fecha}`
- `GET /horarios/disponibles`
- `GET /horarios/rango?inicio=YYYY-MM-DD&fin=YYYY-MM-DD`
- `GET /horarios/total`
- `POST /horarios`
- `PUT /horarios/{id}`
- `DELETE /horarios/{id}`
- `GET /horarios/v2`

### Reservas

- `GET /reservas`
- `GET /reservas/{id}`
- `GET /reservas/usuario/{usuarioId}`
- `GET /reservas/estado/{estado}`
- `GET /reservas/total`
- `POST /reservas`
- `PUT /reservas/{id}`
- `DELETE /reservas/{id}`
- `GET /reservas/v2`

### Fidelizacion

- `GET /fidelizacion`
- `GET /fidelizacion/{id}`
- `GET /fidelizacion/usuario/{usuarioId}`
- `GET /fidelizacion/total/{usuarioId}`
- `POST /fidelizacion`
- `PUT /fidelizacion/{id}`
- `DELETE /fidelizacion/{id}`
- `GET /fidelizacion/v2`

### Notificaciones

- `GET /notificaciones`
- `GET /notificaciones/{id}`
- `GET /notificaciones/usuario/{usuarioId}`
- `GET /notificaciones/usuario/{usuarioId}/no-leidas`
- `GET /notificaciones/usuario/{usuarioId}/total-no-leidas`
- `GET /notificaciones/tipo/{tipo}`
- `GET /notificaciones/no-leidas`
- `POST /notificaciones`
- `PUT /notificaciones/{id}/leer`
- `PUT /notificaciones/usuario/{usuarioId}/leer-todas`
- `DELETE /notificaciones/{id}`
- `GET /notificaciones/v2`

### Pagos

- `GET /pagos`
- `GET /pagos/{id}`
- `GET /pagos/usuario/{usuarioId}`
- `GET /pagos/reserva/{reservaId}`
- `GET /pagos/estado/{estado}`
- `GET /pagos/metodo/{metodoPago}`
- `GET /pagos/total`
- `POST /pagos`
- `PUT /pagos/{id}`
- `PUT /pagos/{id}/aprobar`
- `PUT /pagos/{id}/rechazar`
- `PUT /pagos/{id}/anular`
- `DELETE /pagos/{id}`
- `GET /pagos/v2`

### Promociones

- `GET /promociones`
- `GET /promociones/{id}`
- `GET /promociones/codigo/{codigo}`
- `GET /promociones/vigentes`
- `GET /promociones/tipo/{tipo}`
- `POST /promociones`
- `POST /promociones/validar`
- `PUT /promociones/{id}`
- `PUT /promociones/{id}/activar`
- `PUT /promociones/{id}/desactivar`
- `DELETE /promociones/{id}`
- `GET /promociones/v2`

### Mantenimientos

- `GET /mantenimientos`
- `GET /mantenimientos/{id}`
- `GET /mantenimientos/maquina/{maquinaId}`
- `GET /mantenimientos/estado/{estado}`
- `GET /mantenimientos/tipo/{tipo}`
- `POST /mantenimientos`
- `PUT /mantenimientos/{id}`
- `PUT /mantenimientos/{id}/iniciar`
- `PUT /mantenimientos/{id}/cerrar`
- `PUT /mantenimientos/{id}/cancelar`
- `DELETE /mantenimientos/{id}`
- `GET /mantenimientos/v2`

### Torneos

- `GET /torneos`
- `GET /torneos/{id}`
- `GET /torneos/disponibles`
- `GET /torneos/estado/{estado}`
- `GET /torneos/{id}/inscritos`
- `GET /torneos/usuario/{usuarioId}/inscripciones`
- `POST /torneos`
- `POST /torneos/{id}/inscribir/{usuarioId}`
- `PUT /torneos/{id}`
- `PUT /torneos/{id}/abrir`
- `PUT /torneos/{id}/cerrar`
- `PUT /torneos/{id}/cancelar-inscripcion/{usuarioId}`
- `PUT /torneos/{id}/finalizar/{ganadorUsuarioId}`
- `PUT /torneos/{id}/cancelar`
- `DELETE /torneos/{id}`
- `GET /torneos/v2`

## Reglas de negocio destacadas

- No se crea una reserva si el usuario no existe, la maquina no esta activa o el bloque horario no existe.
- No se duplica una reserva para la misma maquina en el mismo horario.
- No se registran puntos de fidelizacion para usuarios inexistentes.
- Un pago se crea en estado pendiente y calcula montos desde la reserva y maquina asociada.
- Aprobar un pago registra puntos de fidelizacion y envia una notificacion.
- Las promociones se normalizan a mayusculas y se validan por vigencia, estado y monto.
- Los mantenimientos controlan transiciones de estado: `PENDIENTE`, `EN_PROCESO`, `FINALIZADO`, `CANCELADO`.
- Los torneos controlan cupos, inscripciones, cancelaciones, finalizacion y asignacion de ganador.
- Los controllers reciben solicitudes y delegan reglas de negocio a servicios.
- Las APIs usan DTOs para no exponer entidades JPA directamente.
