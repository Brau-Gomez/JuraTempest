# JuraTempest

## Descripcion del proyecto

JuraTempest es una aplicacion construida con arquitectura de microservicios para administrar un centro arcade. El sistema permite gestionar usuarios, autenticacion, maquinas, bloques horarios, reservas y puntos de fidelizacion.

El proyecto corresponde al proyecto semestral para Desarrollo FullStack 1. Su objetivo es demostrar dominio de Spring Boot, persistencia con JPA + Hibernate, migraciones con Liquibase, patron CSR, validaciones, manejo centralizado de errores, respuestas REST, comunicacion entre microservicios, API Gateway, Eureka Server.
```
Integrantes
- Braulio Gomez
- Lukas Meza

Docente
Carlos Alberto Abarzua Castro

Asignatura & Seccion
Desarrollo FullStack 1 DSY1103 - 010D
```
| Integrante | Rol dentro del proyecto | Aporte tecnico |
|---|---|---|
| Braulio Gomez | Developer | autenticacion JWT, usuarios, gateway, pruebas Postman, horarios |
| Lukas Meza | Developer | maquinas, reservas, fidelizacion |

## Contexto funcional

El dominio elegido es un arcade o centro de entretenimiento. Un usuario puede registrarse, iniciar sesion, consultar servicios disponibles y realizar reservas para usar maquinas arcade en bloques horarios definidos. El sistema tambien permite asignar puntos de fidelizacion asociados a usuarios existentes.

Flujo principal del negocio:

1. Se registra un usuario en `ms_usuarios_auth`.
2. El usuario inicia sesion y obtiene un token JWT.
3. Se crean maquinas disponibles en `ms_maquinas`.
4. Se crean bloques horarios en `ms-horarios`.
5. Se crea una reserva en `ms_reservas`.
6. `ms_reservas` valida remotamente que el usuario exista, que la maquina este activa y que el bloque horario exista.
7. Se registran puntos en `ms_fidelizacion`.
8. `ms_fidelizacion` valida remotamente que el usuario exista antes de guardar puntos.

## Arquitectura general

El sistema esta compuesto por 5 microservicios de negocio, un servidor Eureka y un API Gateway.

| Componente | Puerto | Responsabilidad |
|---|---:|---|
| `eureka_server` | 8761 | Registro y descubrimiento de servicios |
| `api-gateway` | 9090 | Punto unico de entrada a las APIs |
| `ms_usuarios_auth` | 9091 | Usuarios, roles, login, validacion de token y JWT |
| `ms_maquinas` | 9092 | Administracion de maquinas arcade |
| `ms-horarios` | 9093 | Administracion de bloques horarios |
| `ms_reservas` | 9094 | Reservas y validaciones remotas |
| `ms_fidelizacion` | 9095 | Puntos de fidelizacion por usuario |

### Rutas expuestas por API Gateway

Todas las pruebas se pueden realizar desde:

```text
http://localhost:9090
```

| Ruta | Microservicio destino |
|---|---|
| `/auth/**` | `ms_usuarios_auth` |
| `/users/**` | `ms_usuarios_auth` |
| `/maquinas/**` | `ms_maquinas` |
| `/horarios/**` | `ms-horarios` |
| `/reservas/**` | `ms_reservas` |
| `/fidelizacion/**` | `ms_fidelizacion` |

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Web
- Spring WebFlux / WebClient
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Data JPA
- Hibernate
- MySQL
- Apache
- Xampp
- Liquibase
- Bean Validation
- Spring Security
- JWT
- Lombok
- Maven
- Postman
- Git y GitHub

## Bases de datos

Cada microservicio de negocio trabaja con su propia base de datos. Esto respeta la independencia de datos esperada en una arquitectura de microservicios.

| Microservicio | Base de datos | Migraciones |
|---|---|---|
| `ms_usuarios_auth` | `usuarios_auth_db` | `src/main/resources/db/changelog/db.changelog.sql` |
| `ms_maquinas` | `maquinas_db` | `src/main/resources/db/changelog/db.changelog.sql` |
| `ms-horarios` | `horarios_db` | `src/main/resources/db/changelog/db.changelog.sql` |
| `ms_reservas` | `reservas_db` | `src/main/resources/db/changelog/db.changelog.sql` |
| `ms_fidelizacion` | `fidelizacion_db` |


## Patron CSR aplicado

Cada microservicio mantiene separacion por responsabilidades:

| Capa | Responsabilidad | Ejemplo en el proyecto |
|---|---|---|
| Controller | Recibir solicitudes REST, validar entrada y retornar `ResponseEntity` | `MaquinaController`, `ReservaController`, `AuthController` |
| Service | Aplicar reglas de negocio y coordinar validaciones | `MaquinaService`, `ReservaService`, `UsuarioService` |
| Repository | Acceder a datos mediante `JpaRepository` | `MaquinaRepository`, `ReservaRepository`, `UsuarioRepository` |
| Model | Representar entidades persistentes con JPA | `Maquina`, `Reserva`, `Usuario`, `BloqueHorario` |
| DTO | Transportar datos sin exponer directamente la entidad | `MaquinaDTO`, `ReservaDTO`, `RegistroRequestDTO` |
| Exception | Centralizar errores y respuestas controladas | `GlobalExceptionHandler`, `ResourceNotFoundException`, `BadRequestException` |

Regla de limpieza usada: el controller no debe contener reglas de negocio. El controller solo recibe, delega al service y responde. Las decisiones como validar duplicados, verificar existencia remota o asignar fechas deben vivir en el service.

## Funcionalidades implementadas

### `ms_usuarios_auth`

Responsabilidades:

- Registro de usuarios.
- Login.
- Generacion de token JWT.
- Validacion de token.
- CRUD administrativo de usuarios.
- Busqueda por email, rol y usuarios frecuentes.
- Relacion entre usuarios y roles.

Endpoints principales:

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/auth/register` | Registrar usuario |
| POST | `/auth/login` | Iniciar sesion |
| GET | `/auth/validate` | Validar token JWT |
| GET | `/users` | Listar usuarios |
| GET | `/users/{id}` | Buscar usuario por id |
| GET | `/users/{id}/exists` | Verificar existencia de usuario |
| GET | `/users/email/{email}` | Buscar usuario por email |
| GET | `/users/rol/{rol}` | Buscar usuarios por rol |
| GET | `/users/frecuentes` | Listar usuarios frecuentes |
| GET | `/users/total` | Contar usuarios |
| POST | `/users` | Crear usuario desde administracion |
| PUT | `/users/{id}` | Actualizar usuario |
| DELETE | `/users/{id}` | Eliminar usuario |

Ejemplo de registro:

```json
{
  "nombre": "Ana",
  "apellido": "Perez",
  "email": "ana.perez@correo.cl",
  "password": "123456",
  "frecuente": true,
  "roles": ["CLIENTE"]
}
```

Ejemplo de login:

```json
{
  "email": "ana.perez@correo.cl",
  "password": "123456"
}
```

### `ms_maquinas`

Responsabilidades:

- CRUD de maquinas arcade.
- Busqueda por estado.
- Busqueda por tipo.
- Validacion de maquina activa para reservas.
- Conteo total de maquinas.

Endpoints principales:

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/maquinas` | Listar maquinas |
| GET | `/maquinas/{id}` | Buscar maquina por id |
| GET | `/maquinas/{id}/existe` | Verificar existencia |
| GET | `/maquinas/activa/{id}` | Verificar si esta activa |
| GET | `/maquinas/estado/{estado}` | Buscar por estado |
| GET | `/maquinas/tipo/{tipo}` | Buscar por tipo |
| GET | `/maquinas/total` | Contar maquinas |
| POST | `/maquinas` | Crear maquina |
| PUT | `/maquinas/{id}` | Actualizar maquina |
| DELETE | `/maquinas/{id}` | Eliminar maquina |

Ejemplo de creacion:

```json
{
  "nombre": "Street Fighter II",
  "tipo": "Lucha",
  "ubicacion": "Zona A",
  "estado": "ACTIVA",
  "costoPorBloque": 1500,
  "fechaInstalacion": "2026-05-13"
}
```

### `ms-horarios`

Responsabilidades:

- CRUD de bloques horarios.
- Busqueda por fecha.
- Busqueda de bloques disponibles.
- Busqueda por rango de fechas.
- Validacion de existencia para reservas.
- Control de capacidad y cupos disponibles.

Endpoints principales:

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/horarios` | Listar bloques |
| GET | `/horarios/{id}` | Buscar bloque por id |
| GET | `/horarios/{id}/existe` | Verificar existencia |
| GET | `/horarios/fecha/{fecha}` | Buscar por fecha |
| GET | `/horarios/disponibles` | Listar disponibles |
| GET | `/horarios/rango?inicio=YYYY-MM-DD&fin=YYYY-MM-DD` | Buscar por rango |
| GET | `/horarios/total` | Contar bloques |
| POST | `/horarios` | Crear bloque |
| PUT | `/horarios/{id}` | Actualizar bloque |
| DELETE | `/horarios/{id}` | Eliminar bloque |

Ejemplo de creacion:

```json
{
  "fecha": "2026-05-15",
  "horaInicio": "10:00:00",
  "horaFin": "11:00:00",
  "disponible": true,
  "estado": "DISPONIBLE",
  "capacidadMaquina": 4,
  "cuposDisponibles": 4
}
```

### `ms_reservas`

Responsabilidades:

- CRUD de reservas.
- Busqueda por usuario.
- Busqueda por estado.
- Validacion de usuario existente.
- Validacion de maquina activa.
- Validacion de bloque horario existente.
- Prevencion de reservas duplicadas para la misma maquina y horario.

Comunicacion remota mediante `WebClient`:

| Validacion | Servicio consultado | Endpoint remoto |
|---|---|---|
| Usuario existe | `ms_usuarios_auth` | `GET /users/{id}/exists` |
| Maquina activa | `ms_maquinas` | `GET /maquinas/activa/{id}` |
| Bloque existe | `ms-horarios` | `GET /horarios/{id}/existe` |

Endpoints principales:

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/reservas` | Listar reservas |
| GET | `/reservas/{id}` | Buscar reserva por id |
| GET | `/reservas/usuario/{usuarioId}` | Buscar reservas por usuario |
| GET | `/reservas/estado/{estado}` | Buscar por estado |
| GET | `/reservas/total` | Contar reservas |
| POST | `/reservas` | Crear reserva |
| PUT | `/reservas/{id}` | Actualizar reserva |
| DELETE | `/reservas/{id}` | Eliminar reserva |

Ejemplo de creacion:

```json
{
  "usuarioId": 1,
  "maquinaId": 1,
  "horarioId": 1,
  "estado": "CONFIRMADA"
}
```

### `ms_fidelizacion`

Responsabilidades:

- Registrar puntos por usuario.
- Listar registros de fidelizacion.
- Buscar puntos por usuario.
- Calcular total de puntos.
- Validar remotamente que el usuario exista.

Comunicacion remota mediante `WebClient`:

| Validacion | Servicio consultado | Endpoint remoto |
|---|---|---|
| Usuario existe | `ms_usuarios_auth` | `GET /users/{id}/exists` |

Endpoints principales:

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/fidelizacion` | Listar registros |
| GET | `/fidelizacion/{id}` | Buscar registro por id |
| GET | `/fidelizacion/usuario/{usuarioId}` | Buscar registros por usuario |
| GET | `/fidelizacion/total-puntos/{usuarioId}` | Calcular puntos acumulados |
| POST | `/fidelizacion` | Crear registro de puntos |
| PUT | `/fidelizacion/{id}` | Actualizar registro |
| DELETE | `/fidelizacion/{id}` | Eliminar registro |

Ejemplo de creacion:

```json
{
  "usuarioId": 1,
  "puntos": 50,
  "descripcion": "Reserva completada"
}
```

## Validaciones

El proyecto utiliza Bean Validation en DTOs para controlar datos de entrada antes de llegar a la capa de servicio.

Ejemplos implementados:

- `@NotBlank` para nombre, apellido, email y password en registro/login.
- `@Email` para validar formato de correo.
- `@NotNull` para campos obligatorios como ids, fechas, estado y costo.
- `@Min` para evitar valores numericos invalidos, como costo menor a 1 o puntos menores a 1.

Ejemplo de error esperado al enviar una maquina sin nombre:

```json
{
  "timestamp": "2026-05-13T12:00:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "mensaje": "El nombre es obligatorio"
}
```

## Manejo de excepciones

Cada microservicio usa excepciones propias y un `GlobalExceptionHandler` para entregar respuestas controladas.

Excepciones esperadas:

- `ResourceNotFoundException`: cuando un recurso no existe.
- `BadRequestException`: cuando la solicitud rompe una regla de negocio.
- `MethodArgumentNotValidException`: cuando falla una validacion de DTO.

Buenas practicas aplicadas:

- No retornar errores crudos de Java al cliente.
- Usar codigos HTTP correctos.
- Responder en JSON.
- Mantener mensajes claros para Postman y defensa tecnica.

## Logs

El proyecto integra logs con SLF4J en puntos relevantes del flujo. Los logs permiten explicar que ocurrio en una operacion y ayudan a detectar errores durante la defensa.

Ejemplos de eventos que se deben poder observar:

- Creacion de registros.
- Actualizacion de registros.
- Eliminacion de registros.
- Busquedas por id.
- Validaciones fallidas.
- Errores controlados.

## Requisitos previos

Antes de ejecutar el sistema, tener instalado o disponible:

- Java 17.
- Maven o los wrappers `mvnw` incluidos en cada modulo.
- MySQL activo, por ejemplo mediante XAMPP.
- Puertos libres: `8761`, `9090`, `9091`, `9092`, `9093`, `9094`, `9095`.
- Postman u otra herramienta para probar endpoints REST.

## Orden de ejecucion

Iniciar primero MySQL. Luego ejecutar los proyectos en este orden:

1. `eureka_server`
2. `ms_usuarios_auth`
3. `ms_maquinas`
4. `ms-horarios`
5. `ms_reservas`
6. `ms_fidelizacion`
7. `api-gateway`

Comando desde cada carpeta:

```bash
./mvnw spring-boot:run
```

Validar Eureka en:

```text
http://localhost:8761
```

Validar API Gateway en:

```text
http://localhost:9090
```

## Secuencia recomendada de pruebas en Postman

1. Registrar usuario con `POST /auth/register`.
2. Iniciar sesion con `POST /auth/login`.
3. Copiar el token retornado.
4. Validar token con `GET /auth/validate`.
5. Crear una maquina con `POST /maquinas`.
6. Crear un bloque horario con `POST /horarios`.
7. Crear una reserva con `POST /reservas`.
8. Crear puntos de fidelizacion con `POST /fidelizacion`.
9. Probar errores controlados:
   - Crear reserva con `usuarioId` inexistente.
   - Crear reserva con `maquinaId` inactiva o inexistente.
   - Crear maquina con `costoPorBloque` igual a `0`.
   - Enviar email con formato incorrecto.

## Ejemplos de URLs usando API Gateway

```text
POST http://localhost:9090/auth/register
POST http://localhost:9090/auth/login
GET  http://localhost:9090/users/1
POST http://localhost:9090/maquinas
POST http://localhost:9090/horarios
POST http://localhost:9090/reservas
POST http://localhost:9090/fidelizacion
```

## Reglas de negocio defendibles

- No se puede crear una reserva si el usuario no existe.
- No se puede crear una reserva si la maquina no esta activa.
- No se puede crear una reserva si el bloque horario no existe.
- No se puede duplicar una reserva para la misma maquina en el mismo horario.
- No se pueden registrar puntos de fidelizacion para usuarios inexistentes.
- No se aceptan datos obligatorios nulos o vacios.
- No se aceptan valores numericos negativos o iguales a cero cuando el dominio exige valores positivos.

