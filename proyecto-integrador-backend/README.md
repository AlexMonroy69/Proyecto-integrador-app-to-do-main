# Proyecto Backend II

API REST para la gestión de estudiantes, cursos y matrículas, más autenticación (JWT), usuarios y tareas para el frontend.

**Stack:** Java 21, Spring Boot 4, Spring Security, Spring Data JPA, MySQL (XAMPP).

## Cómo ejecutarlo

1. Iniciar MySQL desde XAMPP y crear la base de datos `mi_base_de_datos`.
2. Revisar el usuario y la contraseña en `src/main/resources/application.yml`.
3. Ejecutar desde IntelliJ (`PbaApplication`) o con:

```
powershell -ExecutionPolicy Bypass -File run.ps1
```

La API queda en `http://localhost:8080` (prefijo `/api`). El frontend usa `VITE_API_BASE_URL=http://localhost:8080`.

Al arrancar se crea un usuario ADMIN inicial si no existe ninguno: `admin` / `admin@example.com` / `Admin12345`
(se cambia con las variables `ADMIN_USERNAME`, `ADMIN_EMAIL` y `ADMIN_PASSWORD`). Otras variables útiles:
`JWT_SECRET` (mínimo 32 caracteres), `CORS_ALLOWED_ORIGINS`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`.

## Endpoints

Todas las rutas llevan el prefijo `/api`. Excepto login, registro, refresh y logout, requieren
`Authorization: Bearer <token>`.

### Autenticación y cuenta

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registro (rol USER), devuelve tokens |
| POST | `/api/auth/login` | Login con `usernameOrEmail` y `password` |
| POST | `/api/auth/refresh` | Renueva tokens (refresh token rotativo) |
| POST | `/api/auth/logout` | Revoca la sesión del refresh token |
| GET | `/api/auth/me` | Perfil del usuario autenticado |
| PUT | `/api/account/password` | Cambio de contraseña propio |

### Tareas (solo las del usuario autenticado)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/tasks` | Listar (`page`, `size`, `q`, `completed`, `sort`, `dir`) |
| GET | `/api/tasks/{id}` | Obtener tarea |
| POST | `/api/tasks` | Crear tarea |
| PUT | `/api/tasks/{id}` | Actualizar tarea |
| PATCH | `/api/tasks/{id}/complete` | Marcar como completada |
| DELETE | `/api/tasks/{id}` | Eliminar tarea |

### Usuarios (solo ADMIN)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/users` | Listar (`page`, `size`, `q`, `sort`, `dir`) |
| GET | `/api/users/{id}` | Obtener usuario |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario (409 si tiene tareas) |
| PUT | `/api/users/{id}/password` | Fijar contraseña |

### Estudiantes, cursos y matrículas (requieren login)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/students` | Listar estudiantes |
| GET | `/api/students/{id}` | Obtener estudiante |
| POST | `/api/students` | Crear estudiante |
| PUT | `/api/students/{id}` | Actualizar estudiante |
| DELETE | `/api/students/{id}` | Eliminar estudiante |
| GET | `/api/courses` | Listar cursos |
| GET | `/api/courses/{id}` | Obtener curso |
| POST | `/api/courses` | Crear curso |
| PUT | `/api/courses/{id}` | Actualizar curso |
| DELETE | `/api/courses/{id}` | Eliminar curso |
| GET | `/api/enrollments` | Listar matrículas |
| GET | `/api/enrollments/{id}` | Obtener matrícula |
| POST | `/api/enrollments` | Matricular estudiante |
| PUT | `/api/enrollments/{id}` | Cambiar estado de la matrícula |
| DELETE | `/api/enrollments/{id}` | Eliminar matrícula |

Documentación interactiva: `http://localhost:8080/swagger-ui.html`.

### Ejemplos

Crear estudiante:

```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "phone": "3001234567",
  "birthDate": "2000-01-15"
}
```

Crear curso:

```json
{
  "code": "BACK-II",
  "name": "Backend II",
  "description": "Spring Boot y JPA",
  "maxCapacity": 30
}
```

Matricular (la fecha es opcional, por defecto es hoy):

```json
{
  "studentId": 1,
  "courseId": 1,
  "enrollmentDate": "2026-10-15"
}
```

Cambiar estado (`ACTIVE`, `CANCELLED`, `COMPLETE`):

```json
{
  "status": "CANCELLED"
}
```

## Reglas de negocio

- El email del estudiante y el código del curso no se pueden repetir.
- Un estudiante no puede tener dos matrículas vigentes en el mismo curso (las canceladas no cuentan).
- No se puede matricular si el curso ya llegó a su cupo máximo (`maxCapacity`).
- No se puede bajar el cupo de un curso por debajo de las matrículas activas que tiene.
- Una matrícula completada no se puede modificar y una cancelada no se puede pasar a completada.

## Respuestas de error

| Código | Cuándo |
|--------|--------|
| 400 | Datos inválidos o JSON mal formado |
| 404 | El recurso no existe |
| 409 | Email o código duplicado, matrícula repetida o curso sin cupo |
