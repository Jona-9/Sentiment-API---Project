# 🎯 SentimentAPI Backend

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-Wrapper-red?style=flat-square)
![JWT](https://img.shields.io/badge/JWT-0.11.5-purple?style=flat-square)

**API REST Gateway para Análisis de Sentimientos con Autenticación JWT y Gestión de Productos**

Hackathon ONE - No Country

</div>

---

## 📖 Descripción

API REST desarrollada en **Spring Boot 4.0.1** que actúa como gateway para consumir un modelo de Machine Learning de análisis de sentimientos (Python/FastAPI). Incluye sistema completo de autenticación JWT, gestión de productos por categorías, análisis de comentarios asociados a productos, y persistencia de sesiones con historial completo.

**Stack Tecnológico:**
- ☕ Java 17
- 🍃 Spring Boot 4.0.1
- 🐘 PostgreSQL 15+
- 🔧 Maven Wrapper
- 🔄 WebFlux (WebClient para comunicación HTTP reactiva)
- 🗄️ Spring Data JPA + Hibernate
- 🔐 JWT (JSON Web Tokens) para autenticación
- 🔒 BCrypt (encriptación de contraseñas)
- ✅ Jakarta Validation API 3.0.2
- 🎯 Lombok (reducción de boilerplate)

---

## 🌐 URLs de Producción

| Servicio | URL |
|----------|-----|
| **Backend Java (este repo)** | https://sentiment-backend-java-production.up.railway.app |
| **API Base Path** | https://sentiment-backend-java-production.up.railway.app/project/api/v2 |
| **ML API Python** | https://sentiment-api-render.onrender.com |
| **Frontend React** | https://sentiment-dashboard-pi.vercel.app |
| **Base de Datos** | PostgreSQL en Render (conexión privada) |

---

## 🚀 Instalación Local

### Requisitos Previos
- Java 17+
- Maven 3.8+ (o usar el wrapper incluido)
- PostgreSQL 15+ (local o remoto)

### Configuración

1. **Clonar el repositorio:**
```bash
git clone https://github.com/GustavoVasquezS/sentiment-backend-java.git
cd sentiment-backend-java
```

2. **Configurar variables de entorno** (o editar `application.properties`):
```bash
# Base de datos
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=sentiment_db
export DB_USER=postgres
export DB_PASSWORD=tu_password

# API ML Python
export ML_API_URL=https://sentiment-api-render.onrender.com

# JWT
export JWT_SECRET=tu_clave_secreta_muy_larga_y_segura
```

3. **Ejecutar la aplicación:**
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080/project/api/v2`

---

## ☁️ Despliegue en Railway

### Variables de Entorno Requeridas

Configura estas variables en Railway Dashboard:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_HOST` | Host de PostgreSQL | `dpg-xxx.render.com` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `sentiment_db` |
| `DB_USER` | Usuario de PostgreSQL | `sentiment_user` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `***` |
| `ML_API_URL` | URL de la API ML Python | `https://sentiment-api-render.onrender.com` |
| `JWT_SECRET` | Clave secreta para tokens JWT | `clave_muy_larga_y_segura` |

### Dockerfile (incluido en el repo)

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Pasos para Desplegar

1. Crear proyecto en [Railway](https://railway.app)
2. Conectar repositorio de GitHub
3. Configurar variables de entorno en Settings > Variables
4. Railway detectará el Dockerfile automáticamente
5. El servicio se desplegará en ~3-5 minutos

---

## 📁 Estructura del Proyecto

```
sentimentapi/
├── .mvn/wrapper/                    # Maven Wrapper
├── src/main/java/com/project/sentimentapi/
│   ├── configuration/               # Configuración de WebClient y endpoints
│   │   ├── ConectarApi.java        # Cliente WebFlux configurado
│   │   └── EndPointConfg.java      # Propiedades de configuración
│   ├── controller/                  # Endpoints REST
│   │   ├── SentimentApiController.java      # Análisis de sentimientos
│   │   ├── UsuarioController.java           # Autenticación de usuarios
│   │   ├── CategoriaController.java         # Gestión de categorías
│   │   ├── ProductoController.java          # Gestión de productos
│   │   ├── SesionController.java            # Gestión de sesiones
│   │   └── DebugController.java             # Utilidades de debug
│   ├── service/                     # Lógica de negocio
│   │   ├── SentimentService.java
│   │   ├── SentimentServiceImplement.java
│   │   ├── UserService.java
│   │   ├── UserServiceImplement.java
│   │   ├── CategoriaService.java
│   │   ├── CategoriaServiceImplement.java
│   │   ├── ProductoService.java
│   │   ├── ProductoServiceImplement.java
│   │   ├── SesionService.java
│   │   └── SesionServiceImplement.java
│   ├── repository/                  # Capa de persistencia
│   │   ├── UserRepository.java
│   │   ├── RolRepository.java
│   │   ├── CategoriaRepository.java
│   │   ├── ProductoRepository.java
│   │   ├── SesionRepository.java
│   │   ├── SesionProductoRepository.java
│   │   └── ComentarioRepository.java
│   ├── entity/                      # Entidades JPA
│   │   ├── User.java
│   │   ├── Rol.java
│   │   ├── Categoria.java
│   │   ├── Producto.java
│   │   ├── Sesion.java
│   │   ├── SesionProducto.java
│   │   └── Comentario.java
│   ├── dto/                         # Data Transfer Objects
│   │   ├── ResponseDto.java
│   │   ├── SentimentsResponseDto.java
│   │   ├── UserDto.java
│   │   ├── UserDtoRegistro.java
│   │   ├── UserDtoLogin.java
│   │   ├── LoginResponseDto.java
│   │   ├── CategoriaDto.java
│   │   ├── ProductoDto.java
│   │   ├── ProductoRequestDto.java
│   │   ├── ProductoMencionesDto.java
│   │   ├── ProductoPrevioDto.java
│   │   ├── SesionDto.java
│   │   ├── SesionPreviaInfoDto.java
│   │   ├── ComentarioDto.java
│   │   └── ComentariosRequestDto.java
│   ├── security/                    # Seguridad y JWT
│   │   ├── JwtUtil.java            # Utilidades JWT
│   │   ├── JwtAuthenticationFilter.java  # Filtro de autenticación
│   │   └── SecurityConfig.java     # Configuración de seguridad y CORS
│   ├── event/                       # Sistema de eventos
│   │   ├── UserRegisteredEvent.java
│   │   └── UserRegistrationListener.java
│   └── globalexceptionhandler/      # Manejo de excepciones
│       └── ExecptionHandler.java
├── src/main/resources/
│   ├── application.properties       # Configuración de Spring Boot y BD
│   └── data.sql                     # Scripts SQL iniciales
├── pom.xml                          # Dependencias Maven
├── mvnw / mvnw.cmd                 # Scripts Maven Wrapper
└── .gitignore                       # Exclusiones de Git
```

---

## 🗄️ Modelo de Base de Datos

### Diagrama de Relaciones

```
┌─────────────────────────────────┐
│           usuarios              │
├─────────────────────────────────┤
│ PK │ usuario_id (INTEGER)       │
│    │ nombre (VARCHAR)           │
│    │ apellido (VARCHAR)         │
│ UQ │ correo (VARCHAR)           │
│    │ contraseña (VARCHAR HASH)  │
└──────────────┬──────────────────┘
               │ 1:N
               ├──────────────────────────────┐
               │                              │
               ▼                              ▼
┌─────────────────────────────────┐  ┌─────────────────────────────────┐
│          categoria              │  │           sesion                │
├─────────────────────────────────┤  ├─────────────────────────────────┤
│ PK │ categoria_id (INTEGER)     │  │ PK │ sesion_id (INTEGER)       │
│    │ nombre_categoria (VARCHAR) │  │    │ fecha (DATE)              │
│    │ descripcion (VARCHAR)      │  │    │ avg_score (DOUBLE)        │
│ FK │ usuario_id                 │  │    │ total (INTEGER)           │
└──────────────┬──────────────────┘  │    │ positivos (INTEGER)       │
               │ 1:N                  │    │ negativos (INTEGER)       │
               │                      │    │ neutrales (INTEGER)       │
               ▼                      │ FK │ usuario_id                │
┌─────────────────────────────────┐  │ FK │ producto_id (NULLABLE)    │
│           producto              │  └──────────────┬──────────────────┘
├─────────────────────────────────┤                 │ 1:N
│ PK │ producto_id (INTEGER)      │◄────────────────┤
│    │ nombre_producto (VARCHAR)  │                 │
│    │ total_menciones (INTEGER)  │                 ▼
│    │ positivos (INTEGER)        │  ┌─────────────────────────────────┐
│    │ negativos (INTEGER)        │  │         comentario              │
│    │ neutrales (INTEGER)        │  ├─────────────────────────────────┤
│    │ fecha_creacion (TIMESTAMP) │  │ PK │ comentario_id (INTEGER)   │
│    │ ultima_actualizacion (TS)  │  │    │ texto (TEXT)              │
│ FK │ categoria_id               │  │    │ sentimiento (VARCHAR)     │
│ FK │ usuario_id                 │  │    │ probabilidad (DOUBLE)     │
└──────────────┬──────────────────┘  │ FK │ sesion_id                 │
               │                      └─────────────────────────────────┘
               │ N:M
               ▼
┌─────────────────────────────────┐
│       sesion_producto           │
├─────────────────────────────────┤
│ PK │ sesion_producto_id         │
│ FK │ sesion_id                  │
│ FK │ producto_id                │
│    │ menciones_sesion (INTEGER) │
│    │ positivos_sesion (INTEGER) │
│    │ negativos_sesion (INTEGER) │
│    │ neutrales_sesion (INTEGER) │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│             rol                 │
├─────────────────────────────────┤
│ PK │ rol_id (INTEGER)           │
│    │ nombre_rol (VARCHAR)       │
└─────────────────────────────────┘
       ▲
       │ N:M (User_rol)
       │
┌──────┴──────────────────────────┐
```

### Entidades JPA

**User (usuarios)**
- `usuario_id`: Primary Key (auto-increment)
- `nombre`: Nombre del usuario (NOT NULL)
- `apellido`: Apellido del usuario (NOT NULL)
- `correo`: Email único (UNIQUE, NOT NULL)
- `contraseña`: Hash BCrypt de la contraseña (NOT NULL)
- `rol`: Relación N:M con Rol (ManyToMany)
- `sesiones`: Relación 1:N con Sesion

**Rol (rol)**
- `rol_id`: Primary Key (auto-increment)
- `nombre_rol`: Nombre del rol (ej: "ADMIN", "USER")

**Categoria (categoria)**
- `categoria_id`: Primary Key (auto-increment)
- `nombre_categoria`: Nombre de la categoría (NOT NULL, máx 100 chars)
- `descripcion`: Descripción de la categoría (máx 255 chars)
- `usuario_id`: Foreign Key a User (NOT NULL)
- `productos`: Relación 1:N con Producto

**Producto (producto)**
- `producto_id`: Primary Key (auto-increment)
- `nombre_producto`: Nombre del producto (NOT NULL, máx 200 chars)
- `total_menciones`: Total de menciones acumuladas (DEFAULT 0)
- `positivos`: Comentarios positivos acumulados (DEFAULT 0)
- `negativos`: Comentarios negativos acumulados (DEFAULT 0)
- `neutrales`: Comentarios neutrales acumulados (DEFAULT 0)
- `fecha_creacion`: Timestamp de creación (auto-generado)
- `ultima_actualizacion`: Timestamp de última actualización (auto-generado)
- `categoria_id`: Foreign Key a Categoria (NOT NULL)
- `usuario_id`: Foreign Key a User (NOT NULL)
- `sesiones`: Relación 1:N con Sesion

**Sesion (sesion)**
- `sesion_id`: Primary Key (auto-increment)
- `fecha`: Fecha de la sesión (NOT NULL)
- `avg_score`: Score promedio de probabilidades (NOT NULL)
- `total`: Total de comentarios analizados (NOT NULL)
- `positivos`: Cantidad de comentarios positivos (NOT NULL)
- `negativos`: Cantidad de comentarios negativos (NOT NULL)
- `neutrales`: Cantidad de comentarios neutrales (NOT NULL)
- `usuario_id`: Foreign Key a User (NOT NULL)
- `producto_id`: Foreign Key a Producto (NULLABLE)
- `comentarios`: Relación 1:N con Comentario

**Comentario (comentario)**
- `comentario_id`: Primary Key (auto-increment)
- `texto`: Texto del comentario (TEXT, NOT NULL)
- `sentimiento`: Sentimiento detectado ("Positivo", "Negativo", "Neutral")
- `probabilidad`: Confianza del modelo ML (DOUBLE, NOT NULL)
- `sesion_id`: Foreign Key a Sesion (NOT NULL)

**SesionProducto (sesion_producto)**
- `sesion_producto_id`: Primary Key (auto-increment)
- `sesion_id`: Foreign Key a Sesion (NOT NULL)
- `producto_id`: Foreign Key a Producto (NOT NULL)
- `menciones_sesion`: Menciones del producto en esta sesión (DEFAULT 0)
- `positivos_sesion`: Comentarios positivos en esta sesión (DEFAULT 0)
- `negativos_sesion`: Comentarios negativos en esta sesión (DEFAULT 0)
- `neutrales_sesion`: Comentarios neutrales en esta sesión (DEFAULT 0)

---

## 🚀 Guía de Uso

### Prerrequisitos

- ☕ **Java 17** o superior
- 🐘 **PostgreSQL 15+** instalado y ejecutándose
- 🐍 **Python API** ejecutándose en `http://localhost:8000`
- 📦 Maven (incluido como wrapper, no requiere instalación)

### Paso 1: Configurar PostgreSQL

**Crear la base de datos:**

```sql
-- Conectarse a PostgreSQL
psql -U postgres

-- Crear base de datos
CREATE DATABASE hackathonone;

-- Conectarse a la base de datos
\c hackathonone

-- Crear tabla de roles
CREATE TABLE rol (
    rol_id SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL
);

-- Insertar roles por defecto
INSERT INTO rol (nombre_rol) VALUES ('ADMIN');
INSERT INTO rol (nombre_rol) VALUES ('USER');
```

### Paso 2: Configurar application.properties

Edita `src/main/resources/application.properties`:

```properties
spring.application.name=sentimentapi
server.servlet.context-path=/project/api/v2

# Configuración API Python
config.url=http://localhost:8000

# Conexión PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/hackathonone
spring.datasource.username=postgres
spring.datasource.password=root

# Configuración JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

⚠️ **Importante:** Cambia `spring.datasource.password` por tu contraseña de PostgreSQL.

### Paso 3: Iniciar la API Python

```bash
cd api/
uvicorn main:app --reload --port 8000
```

### Paso 4: Ejecutar la API Spring Boot

**Linux/Mac:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

> 💡 El Maven Wrapper descargará automáticamente Maven si no está instalado.

---

## 📡 Endpoints Disponibles

### 🔐 Autenticación de Usuarios

#### 1. Registrar Usuario

**Endpoint:** `POST /project/api/v2/usuario`

Registra un nuevo usuario con contraseña encriptada (BCrypt) y crea automáticamente 12 categorías predeterminadas.

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan.perez@example.com",
  "contraseña": "miContraseñaSegura123"
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/usuario \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "correo": "juan.perez@example.com",
    "contraseña": "miContraseñaSegura123"
  }'
```

**Respuesta (200 OK):**
```json
{}
```

**Categorías creadas automáticamente:**
1. Electrónica
2. Ropa y Moda
3. Alimentos y Bebidas
4. Hogar y Decoración
5. Belleza y Cuidado Personal
6. Entretenimiento
7. Deportes y Fitness
8. Servicios
9. Automotriz
10. Educación
11. Salud y Bienestar
12. Niños y Bebés

---

#### 2. Login de Usuario

**Endpoint:** `POST /project/api/v2/usuario/login`

Autentica un usuario y retorna un token JWT válido por 24 horas.

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "correo": "juan.perez@example.com",
  "contraseña": "miContraseñaSegura123"
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/usuario/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan.perez@example.com",
    "contraseña": "miContraseñaSegura123"
  }'
```

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan.perez@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c3VhcmlvSWQiOjEsInN1YiI6Imp1YW4ucGVyZXpAZXhhbXBsZS5jb20iLCJpYXQiOjE3MzgwMDAwMDAsImV4cCI6MTczODA4NjQwMH0.XYZ..."
}
```

**Uso del Token:**
```bash
# Todas las peticiones protegidas deben incluir el token en el header Authorization
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### 📂 Gestión de Categorías

#### 3. Obtener Categorías del Usuario

**Endpoint:** `GET /project/api/v2/categoria`

Obtiene todas las categorías del usuario autenticado.

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/project/api/v2/categoria \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Respuesta (200 OK):**
```json
[
  {
    "categoriaId": 1,
    "nombreCategoria": "Electrónica",
    "descripcion": "Productos electrónicos, smartphones, computadoras y accesorios tecnológicos",
    "totalProductos": 5
  },
  {
    "categoriaId": 2,
    "nombreCategoria": "Ropa y Moda",
    "descripcion": "Vestimenta, calzado, accesorios y productos de moda",
    "totalProductos": 2
  }
]
```

---

#### 4. Obtener Categoría por ID

**Endpoint:** `GET /project/api/v2/categoria/{categoriaId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/project/api/v2/categoria/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Respuesta (200 OK):**
```json
{
  "categoriaId": 1,
  "nombreCategoria": "Electrónica",
  "descripcion": "Productos electrónicos, smartphones, computadoras y accesorios tecnológicos",
  "totalProductos": 5
}
```

---

#### 5. Crear Categoría

**Endpoint:** `POST /project/api/v2/categoria`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "comentarios": [
    "El iPhone 15 Pro tiene una cámara increíble",
    "La batería del iPhone 15 Pro dura todo el día",
    "El precio del iPhone 15 Pro es muy alto"
  ],
  "productoId": 1
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/sesion/analizar-con-producto \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "comentarios": [
      "El iPhone 15 Pro tiene una cámara increíble",
      "La batería del iPhone 15 Pro dura todo el día",
      "El precio del iPhone 15 Pro es muy alto"
    ],
    "productoId": 1
  }'
```

**Respuesta (200 OK):**
```json
{
  "sessionId": 2,
  "date": "2026-01-24",
  "avgScore": 0.8521,
  "total": 3,
  "positivos": 2,
  "negativos": 1,
  "neutrales": 0,
  "productoId": 1,
  "nombreProducto": "iPhone 15 Pro",
  "productoMenciones": {
    "nombreProducto": "iPhone 15 Pro",
    "totalMencionesEnSesion": 3,
    "positivosEnSesion": 2,
    "negativosEnSesion": 1,
    "neutralesEnSesion": 0,
    "porcentajeMenciones": 100.0
  },
  "comentarios": [
    {
      "texto": "El iPhone 15 Pro tiene una cámara increíble",
      "sentimiento": "positivo",
      "probabilidad": 0.9456
    },
    {
      "texto": "La batería del iPhone 15 Pro dura todo el día",
      "sentimiento": "positivo",
      "probabilidad": 0.8921
    },
    {
      "texto": "El precio del iPhone 15 Pro es muy alto",
      "sentimiento": "negativo",
      "probabilidad": 0.7186
    }
  ]
}
```

---

#### 14. Analizar con Múltiples Productos

**Endpoint:** `POST /project/api/v2/sesion/analizar-con-lista-productos`

Analiza comentarios y detecta automáticamente menciones de múltiples productos. Actualiza contadores de todos los productos mencionados.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "comentarios": [
    "El iPhone 15 Pro tiene mejor cámara que el Samsung Galaxy S24",
    "Prefiero el Samsung Galaxy S24 por su precio",
    "El iPhone 15 Pro es más caro pero vale la pena"
  ],
  "productosIds": [1, 2]
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/sesion/analizar-con-lista-productos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "comentarios": [
      "El iPhone 15 Pro tiene mejor cámara que el Samsung Galaxy S24",
      "Prefiero el Samsung Galaxy S24 por su precio",
      "El iPhone 15 Pro es más caro pero vale la pena"
    ],
    "productosIds": [1, 2]
  }'
```

**Respuesta (200 OK):**
```json
{
  "sessionId": 3,
  "date": "2026-01-24",
  "avgScore": 0.7892,
  "total": 3,
  "positivos": 2,
  "negativos": 0,
  "neutrales": 1,
  "productosDetectados": [
    {
      "nombreProducto": "iPhone 15 Pro",
      "totalMencionesEnSesion": 2,
      "positivosEnSesion": 2,
      "negativosEnSesion": 0,
      "neutralesEnSesion": 0,
      "porcentajeMenciones": 66.67
    },
    {
      "nombreProducto": "Samsung Galaxy S24",
      "totalMencionesEnSesion": 2,
      "positivosEnSesion": 1,
      "negativosEnSesion": 0,
      "neutralesEnSesion": 1,
      "porcentajeMenciones": 66.67
    }
  ],
  "comentarios": [...]
}
```

---

#### 15. Obtener Productos de la Última Sesión

**Endpoint:** `GET /project/api/v2/sesion/ultima-sesion-productos`

Retorna información sobre los productos analizados en la última sesión del usuario.

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/project/api/v2/sesion/ultima-sesion-productos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Respuesta (200 OK):**
```json
{
  "sesionId": 3,
  "fecha": "2026-01-24",
  "totalProductosAnalizados": 2,
  "productos": [
    {
      "productoId": 1,
      "nombreProducto": "iPhone 15 Pro",
      "nombreCategoria": "Electrónica",
      "mencionesEnUltimaSesion": 2,
      "positivosEnUltimaSesion": 2,
      "negativosEnUltimaSesion": 0
    },
    {
      "productoId": 2,
      "nombreProducto": "Samsung Galaxy S24",
      "nombreCategoria": "Electrónica",
      "mencionesEnUltimaSesion": 2,
      "positivosEnUltimaSesion": 1,
      "negativosEnUltimaSesion": 0
    }
  ]
}
```

---

#### 16. Analizar con Productos de Sesión Previa

**Endpoint:** `POST /project/api/v2/sesion/analizar-con-productos-previos`

Reutiliza los productos de una sesión anterior para analizar nuevos comentarios.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "comentarios": [
    "Ahora el iPhone 15 Pro tiene mejor batería",
    "El Samsung Galaxy S24 sigue siendo más económico"
  ],
  "sesionPreviaId": 3
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/sesion/analizar-con-productos-previos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "comentarios": [
      "Ahora el iPhone 15 Pro tiene mejor batería",
      "El Samsung Galaxy S24 sigue siendo más económico"
    ],
    "sesionPreviaId": 3
  }'
```

---

#### 17. Obtener Historial de Sesiones

**Endpoint:** `GET /project/api/v2/sesion/historial`

Retorna todas las sesiones del usuario con sus comentarios completos.

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/project/api/v2/sesion/historial \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Respuesta (200 OK):**
```json
[
  {
    "sessionId": 3,
    "date": "2026-01-24",
    "avgScore": 0.7892,
    "total": 3,
    "positivos": 2,
    "negativos": 0,
    "neutrales": 1,
    "comentarios": [
      {
        "texto": "El iPhone 15 Pro tiene mejor cámara que el Samsung Galaxy S24",
        "sentimiento": "Positivo",
        "probabilidad": 0.8456
      },
      {
        "texto": "Prefiero el Samsung Galaxy S24 por su precio",
        "sentimiento": "Neutral",
        "probabilidad": 0.6234
      },
      {
        "texto": "El iPhone 15 Pro es más caro pero vale la pena",
        "sentimiento": "Positivo",
        "probabilidad": 0.8987
      }
    ]
  },
  {
    "sessionId": 2,
    "date": "2026-01-24",
    "avgScore": 0.8521,
    "total": 3,
    "positivos": 2,
    "negativos": 1,
    "neutrales": 0,
    "comentarios": [...]
  }
]
```

---

### 🛠️ Utilidades de Debug

#### 18. Crear Categorías Manualmente (Debug)

**Endpoint:** `POST /project/api/v2/debug/crear-categorias`

⚠️ **ENDPOINT TEMPORAL:** Para usuarios que se registraron antes de la implementación del sistema de eventos y no tienen categorías automáticas.

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/debug/crear-categorias \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Respuesta (200 OK):**
```json
{
  "mensaje": "Categorías creadas exitosamente",
  "total": 12,
  "usuario": "Juan Pérez"
}
```

---

#### 19. Información del Usuario (Debug)

**Endpoint:** `GET /project/api/v2/debug/info`

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/project/api/v2/debug/info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Respuesta (200 OK):**
```json
{
  "usuarioId": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan.perez@example.com",
  "totalCategorias": 12
}
```

---

## 📊 Estructura de Respuestas

### UserDtoRegistro (Registro)

| Campo | Tipo | Descripción | Requerido |
|-------|------|-------------|-----------|
| `nombre` | String | Nombre del usuario | ✅ |
| `apellido` | String | Apellido del usuario | ✅ |
| `correo` | String | Email único | ✅ |
| `contraseña` | String | Contraseña (mín. 8 caracteres) | ✅ |

### LoginResponseDto (Login)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Integer | ID del usuario |
| `nombre` | String | Nombre del usuario |
| `apellido` | String | Apellido del usuario |
| `correo` | String | Email del usuario |
| `token` | String | Token JWT válido por 24 horas |

### CategoriaDto

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `categoriaId` | Integer | ID de la categoría |
| `nombreCategoria` | String | Nombre de la categoría |
| `descripcion` | String | Descripción de la categoría |
| `totalProductos` | Integer | Cantidad de productos en la categoría |

### ProductoDto

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `productoId` | Integer | ID del producto |
| `nombreProducto` | String | Nombre del producto |
| `categoriaId` | Integer | ID de la categoría |
| `nombreCategoria` | String | Nombre de la categoría |
| `totalMenciones` | Integer | Total de menciones acumuladas |
| `positivos` | Integer | Comentarios positivos acumulados |
| `negativos` | Integer | Comentarios negativos acumulados |
| `neutrales` | Integer | Comentarios neutrales acumulados |
| `porcentajePositivos` | Double | Porcentaje de menciones positivas |
| `porcentajeNegativos` | Double | Porcentaje de menciones negativas |
| `porcentajeNeutrales` | Double | Porcentaje de menciones neutrales |
| `fechaCreacion` | String | Fecha de creación (formato: yyyy-MM-dd HH:mm:ss) |
| `ultimaActualizacion` | String | Última actualización (formato: yyyy-MM-dd HH:mm:ss) |

### SesionDto

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `sessionId` | Integer | ID de la sesión |
| `date` | String | Fecha de la sesión (formato: yyyy-MM-dd) |
| `avgScore` | Double | Score promedio de confianza (0.0 - 1.0) |
| `total` | Integer | Total de comentarios analizados |
| `positivos` | Integer | Cantidad de comentarios positivos |
| `negativos` | Integer | Cantidad de comentarios negativos |
| `neutrales` | Integer | Cantidad de comentarios neutrales |
| `productoId` | Integer | ID del producto (si aplica) |
| `nombreProducto` | String | Nombre del producto (si aplica) |
| `productoMenciones` | ProductoMencionesDto | Estadísticas del producto en esta sesión |
| `productosDetectados` | List<ProductoMencionesDto> | Lista de productos detectados (análisis múltiple) |
| `comentarios` | List<ComentarioDto> | Lista de comentarios analizados |

### ComentarioDto

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `texto` | String | Texto del comentario |
| `sentimiento` | String | Sentimiento: "Positivo", "Negativo" o "Neutral" |
| `probabilidad` | Double | Nivel de confianza (0.0 - 1.0) |

### ResponseDto (Análisis Individual)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `prevision` | String | Sentimiento: "Positivo", "Negativo" o "Neutral" |
| `probabilidad` | Double | Nivel de confianza (0.0 - 1.0) |

### SentimentsResponseDto (Análisis por Lotes)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `results` | List\<ResponseDto\> | Lista de resultados individuales |
| `total` | Integer | Cantidad total de textos analizados |

---

## ⚠️ Respuestas de Error

### 400 Bad Request - Validación Fallida

**Caso 1: Texto vacío**
```json
{
  "Error": [
    "Se ha ingresado un mensaje vacio"
  ]
}
```

**Caso 2: Texto fuera de rango (análisis individual)**
```json
{
  "Error": [
    "El texto ingresado debe contener 5 o 2000 carácteres"
  ]
}
```

**Caso 3: Lote fuera de rango**
```json
{
  "Error": [
    "El texto ingresado debe contener 5 o 20000 carácteres"
  ]
}
```

**Caso 4: Producto duplicado**
```json
"Ya existe un producto con este nombre"
```

**Caso 5: Categoría duplicada**
```json
"Ya existe una categoría con este nombre"
```

### 401 Unauthorized - Token Inválido o Faltante

```json
"No autorizado - Token inválido o faltante"
```

o

```json
"Acceso no autorizado"
```

### 502 Bad Gateway - API Python no disponible

```
Hubo un error al comunicarse con otro servidor
```

---

## ✨ Características Principales

### 🔐 Sistema de Autenticación JWT
- ✅ **Registro de usuarios** con validación de datos
- ✅ **Encriptación BCrypt** con salt automático
- ✅ **Login con JWT** - tokens válidos por 24 horas
- ✅ **Filtro de autenticación** para rutas protegidas
- ✅ **CORS configurado** para desarrollo con frontend
- ✅ **Roles de usuario** (Admin, User)

### 📂 Sistema de Categorías
- ✅ **12 categorías predeterminadas** creadas automáticamente al registrarse
- ✅ **Creación de categorías personalizadas**
- ✅ **Validación de nombres únicos** por usuario
- ✅ **Conteo automático** de productos por categoría

### 📦 Gestión de Productos
- ✅ **CRUD completo** de productos
- ✅ **Asociación a categorías** del usuario
- ✅ **Contadores acumulativos** (menciones, positivos, negativos, neutrales)
- ✅ **Porcentajes automáticos** de sentimientos
- ✅ **Timestamps automáticos** de creación y actualización
- ✅ **Validación de permisos** (solo el dueño puede modificar)

### 📊 Análisis de Sentimientos con Productos
- ✅ **Análisis simple** sin producto asociado
- ✅ **Análisis con producto específico**
- ✅ **Análisis con múltiples productos** (detección automática)
- ✅ **Reutilización de productos** de sesiones previas
- ✅ **Actualización automática** de contadores de productos
- ✅ **Estadísticas por sesión** (menciones, sentimientos)

### 💾 Persistencia de Sesiones
- ✅ **Guardado completo** de sesiones con todos los comentarios
- ✅ **Historial de sesiones** ordenado por fecha
- ✅ **Relación sesión-productos** (tabla intermedia)
- ✅ **Recuperación de productos** de la última sesión
- ✅ **Métricas agregadas** (avg_score, totales por sentimiento)

### 🔄 Sistema de Eventos
- ✅ **Event-Driven Architecture** para creación de categorías
- ✅ **Transaccionalidad independiente** (REQUIRES_NEW)
- ✅ **Listener automático** al registro de usuarios
- ✅ **Separación de responsabilidades** entre registro y configuración inicial

### 💬 Análisis de Sentimientos Base
- ✅ **Análisis individual**: 5-2000 caracteres
- ✅ **Análisis por lotes**: 5-20000 caracteres
- ✅ Mensajes de error descriptivos
- ✅ Validación de campos no vacíos
- ✅ Sin autenticación requerida (endpoints públicos)

### ⚡ Comunicación Reactiva
- 🔄 **WebClient**: Cliente HTTP no bloqueante de Spring WebFlux
- ⚡ **Asíncrono**: Mejor rendimiento y escalabilidad
- 🛡️ **Resiliente**: Manejo robusto de errores de red

### 🛡️ Manejo de Errores
- 🛡️ **Global Exception Handler**: Captura centralizada de excepciones
- 📝 **Respuestas estructuradas**: JSON consistente para todos los errores
- 🔍 **Tipos de error**: Validación (400), Autenticación (401), Conectividad (502)

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                      Cliente (Frontend)                         │
│               (React, Vue, Angular, Postman, cURL)             │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP POST/GET + JWT Token
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│              Spring Boot API Gateway (v2)                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         SecurityConfig + JwtAuthenticationFilter         │  │
│  │  • CORS Filter (Highest Priority)                        │  │
│  │  • JWT Validation Filter                                 │  │
│  │  • Public routes: /usuario, /sentiment/analyze           │  │
│  │  • Protected routes: /categoria, /producto, /sesion      │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         UsuarioController                                │  │
│  │  • POST /usuario (registro)                              │  │
│  │  • POST /usuario/login (autenticación JWT)               │  │
│  └──────────────┬───────────────────────────────────────────┘  │
│                 │                                               │
│  ┌──────────────▼───────────────────────────────────────────┐  │
│  │      UserService + JwtUtil + Event Publisher            │  │
│  │  • Registro con hash BCrypt                              │  │
│  │  • Validación de login                                   │  │
│  │  • Generación de token JWT (24h)                         │  │
│  │  • Publicación de UserRegisteredEvent                    │  │
│  └──────────────┬───────────────────────────────────────────┘  │
│                 │                                               │
│                 ├──────────────────────────┐                    │
│                 │                          │                    │
│  ┌──────────────▼──────────────┐  ┌────────▼──────────────┐    │
│  │   UserRepository (JPA)      │  │ UserRegistrationListener│   │
│  └──────────────┬──────────────┘  │ • @EventListener        │   │
│                 │                  │ • Crea 12 categorías    │   │
│                 │                  └─────────────────────────┘   │
│                 ▼                                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │         PostgreSQL Database                             │   │
│  │  • usuarios (user data + hashed passwords)              │   │
│  │  • rol (user roles)                                     │   │
│  │  • categoria (12 default + custom)                      │   │
│  │  • producto (productos con contadores acumulativos)     │   │
│  │  • sesion (historial de análisis)                       │   │
│  │  • comentario (comentarios individuales)                │   │
│  │  • sesion_producto (relación N:M con estadísticas)      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │      CategoriaController + ProductoController            │  │
│  │  • GET/POST /categoria                                   │  │
│  │  • GET/POST /producto                                    │  │
│  │  • GET /producto/por-categoria?categoriaId=X             │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │      SesionController                                    │  │
│  │  • POST /sesion/analizar                                 │  │
│  │  • POST /sesion/analizar-con-producto                    │  │
│  │  • POST /sesion/analizar-con-lista-productos             │  │
│  │  • POST /sesion/analizar-con-productos-previos           │  │
│  │  • GET /sesion/historial                                 │  │
│  │  • GET /sesion/ultima-sesion-productos                   │  │
│  └──────────────┬───────────────────────────────────────────┘  │
│                 │                                               │
│  ┌──────────────▼───────────────────────────────────────────┐  │
│  │      SesionService + ProductoService                     │  │
│  │  • Análisis de comentarios                               │  │
│  │  • Detección de productos mencionados                    │  │
│  │  • Actualización de contadores de productos              │  │
│  │  • Guardado de sesiones con comentarios                  │  │
│  │  • Creación de relaciones sesion_producto                │  │
│  └──────────────┬───────────────────────────────────────────┘  │
│                 │                                               │
│  ┌──────────────▼───────────────────────────────────────────┐  │
│  │      SentimentApiController                              │  │
│  │  • POST /sentiment/analyze                               │  │
│  │  • POST /sentiment/analyze/batch                         │  │
│  └──────────────┬───────────────────────────────────────────┘  │
│                 │                                               │
│  ┌──────────────▼───────────────────────────────────────────┐  │
│  │      Jakarta Validation                                  │  │
│  │  • @NotBlank, @Size                                      │  │
│  └──────────────┬───────────────────────────────────────────┘  │
│                 │                                               │
│  ┌──────────────▼───────────────────────────────────────────┐  │
│  │      SentimentServiceImplement                           │  │
│  │  • consultarSentimiento()                                │  │
│  │  • consultarSentimientos()                               │  │
│  └──────────────┬───────────────────────────────────────────┘  │
│                 │                                               │
│  ┌──────────────▼───────────────────────────────────────────┐  │
│  │      WebClient (Spring WebFlux)                          │  │
│  │  • Comunicación HTTP reactiva                            │  │
│  └──────────────┬───────────────────────────────────────────┘  │
└─────────────────┼────────────────────────────────────────────────┘
                  │ HTTP POST (application/json)
                  ▼
         ┌────────────────┐
         │  Python API    │
         │   (FastAPI)    │
         │ /sentiment     │
         │ /sentiment/batch│
         └────────┬───────┘
                  │
                  ▼
         ┌────────────────┐
         │   Modelo ML    │
         │  (Sentimientos)│
         └────────────────┘
```

---

## ⚙️ Configuración

### application.properties

```properties
# Nombre de la aplicación
spring.application.name=sentimentapi

# Context path de la API (v2)
server.servlet.context-path=/project/api/v2

# URL de la API Python
config.url=http://localhost:8000

# Configuración PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/hackathonone
spring.datasource.username=postgres
spring.datasource.password=root

# Configuración JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Puerto del servidor (opcional)
server.port=8080
```

### Variables de Configuración

| Propiedad | Descripción | Valor por Defecto |
|-----------|-------------|-------------------|
| `config.url` | URL base de la API Python | `http://localhost:8000` |
| `server.servlet.context-path` | Prefijo de todos los endpoints | `/project/api/v2` |
| `server.port` | Puerto del servidor Spring Boot | `8080` |
| `spring.datasource.url` | URL de conexión PostgreSQL | `jdbc:postgresql://localhost:5432/hackathonone` |
| `spring.jpa.hibernate.ddl-auto` | Estrategia de generación de esquema | `update` |
| `spring.jpa.show-sql` | Mostrar SQL en consola | `true` |

---

## 🧪 Testing y Desarrollo

### Compilar el Proyecto

```bash
# Linux/Mac
./mvnw clean compile

# Windows
mvnw.cmd clean compile
```

### Ejecutar Tests

```bash
# Linux/Mac
./mvnw test

# Windows
mvnw.cmd test
```

### Empaquetar como JAR

```bash
# Linux/Mac
./mvnw clean package

# Windows
mvnw.cmd clean package

# Ejecutar JAR
java -jar target/sentimentapi-0.0.1-SNAPSHOT.jar
```

### Limpiar Build

```bash
# Linux/Mac
./mvnw clean

# Windows
mvnw.cmd clean
```

---

## 🛠️ Dependencias del Proyecto

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| spring-boot-starter-webmvc | 4.0.1 | Framework web MVC |
| spring-boot-starter-webflux | 4.0.1 | WebClient reactivo |
| spring-boot-starter-data-jpa | 4.0.1 | ORM con Hibernate |
| spring-boot-starter-validation | 4.0.1 | Validación de beans |
| postgresql | Latest | Driver JDBC PostgreSQL |
| jakarta.validation-api | 3.0.2 | API de validación Jakarta |
| jbcrypt | 0.4 | Encriptación de contraseñas |
| jjwt-api | 0.11.5 | API de JWT |
| jjwt-impl | 0.11.5 | Implementación de JWT |
| jjwt-jackson | 0.11.5 | Serialización JSON para JWT |
| lombok | Latest | Reducción de boilerplate |

---

## 🐛 Troubleshooting

### Error: "Hubo un error al comunicarse con otro servidor"

**Causa:** La API Python no está disponible o la URL está mal configurada.

**Solución:**
1. Verifica que la API Python esté ejecutándose:
   ```bash
   curl http://localhost:8000/docs
   ```
2. Revisa `application.properties` y confirma la URL correcta
3. Verifica conectividad de red

---

### Error: "Connection refused" a PostgreSQL

**Causa:** PostgreSQL no está ejecutándose o la configuración es incorrecta.

**Solución:**
1. Inicia PostgreSQL:
   ```bash
   # Linux
   sudo systemctl start postgresql
   
   # macOS
   brew services start postgresql
   
   # Windows
   net start postgresql-x64-15
   ```
2. Verifica que la base de datos `hackathonone` existe
3. Confirma usuario y contraseña en `application.properties`

---

### Error: "Permission denied" al ejecutar mvnw

**Causa:** El script no tiene permisos de ejecución (Linux/Mac).

**Solución:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

---

### Error: "Port 8080 already in use"

**Causa:** Otro proceso está usando el puerto 8080.

**Solución 1 - Cambiar puerto:**
```properties
# application.properties
server.port=8081
```

**Solución 2 - Liberar puerto:**
```bash
# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

### Error: Tabla "rol" vacía, no se puede registrar usuario

**Causa:** La tabla `rol` no tiene datos iniciales.

**Solución:**
```sql
-- Conectarse a PostgreSQL
psql -U postgres -d hackathonone

-- Insertar roles
INSERT INTO rol (nombre_rol) VALUES ('ADMIN');
INSERT INTO rol (nombre_rol) VALUES ('USER');
```

---

### Error: "No autorizado - Token inválido o faltante"

**Causa:** El token JWT no se está enviando correctamente o ha expirado.

**Solución:**
1. Verifica que el header Authorization esté correctamente formateado:
   ```
   Authorization: Bearer {tu_token_aqui}
   ```
2. El token expira después de 24 horas - haz login nuevamente
3. Asegúrate de que el token no tenga espacios extras

---

### Error: Usuario no tiene categorías después de registrarse

**Causa:** El sistema de eventos no funcionó correctamente.

**Solución temporal:**
```bash
# Usar el endpoint de debug para crear categorías manualmente
curl -X POST http://localhost:8080/project/api/v2/debug/crear-categorias \
  -H "Authorization: Bearer {token}"
```

---

### Error: CORS al conectar desde el frontend

**Causa:** El frontend está corriendo en un puerto no permitido.

**Solución:**
Verifica que tu origen esté en la lista de permitidos en `SecurityConfig.java`:
```java
config.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:5173",  // Vite default
    "http://127.0.0.1:5173",
    "http://localhost:*"      // Cualquier puerto en localhost
));
```

---

## 📋 Requisitos del Sistema

| Componente | Requisito |
|------------|-----------|
| **Java** | 17 o superior |
| **PostgreSQL** | 15 o superior |
| **Maven** | Incluido (Maven Wrapper) |
| **RAM** | 2 GB mínimo (recomendado 4 GB) |
| **Espacio en Disco** | 1 GB para dependencias y base de datos |
| **Sistema Operativo** | Linux, macOS, Windows |

---

## 📝 Ejemplos de Uso Completo

### Flujo Completo: Registro → Login → Crear Producto → Analizar

```bash
# 1. Registrar usuario
curl -X POST http://localhost:8080/project/api/v2/usuario \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María",
    "apellido": "González",
    "correo": "maria.gonzalez@example.com",
    "contraseña": "MiPassword2024!"
  }'

# 2. Login (guardar el token)
TOKEN=$(curl -X POST http://localhost:8080/project/api/v2/usuario/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "maria.gonzalez@example.com",
    "contraseña": "MiPassword2024!"
  }' | jq -r '.token')

# 3. Ver categorías automáticas
curl -X GET http://localhost:8080/project/api/v2/categoria \
  -H "Authorization: Bearer $TOKEN"

# 4. Crear un producto
curl -X POST http://localhost:8080/project/api/v2/producto \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreProducto": "MacBook Pro M3",
    "categoriaId": 1
  }'

# 5. Analizar comentarios sobre el producto
curl -X POST http://localhost:8080/project/api/v2/sesion/analizar-con-producto \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "comentarios": [
      "El MacBook Pro M3 es increíblemente rápido",
      "La pantalla del MacBook Pro M3 es excelente",
      "El precio del MacBook Pro M3 es muy alto"
    ],
    "productoId": 1
  }'

# 6. Ver historial de sesiones
curl -X GET http://localhost:8080/project/api/v2/sesion/historial \
  -H "Authorization: Bearer $TOKEN"

# 7. Ver estadísticas del producto
curl -X GET http://localhost:8080/project/api/v2/producto/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔒 Consideraciones de Seguridad

### ⚠️ Mejoras Recomendadas para Producción

1. **JWT:**
   - Mover SECRET_KEY a variables de entorno
   - Implementar refresh tokens
   - Agregar blacklist de tokens revocados
   - Reducir tiempo de expiración (ej: 2 horas)

2. **Validaciones:**
   - Agregar `@Email` en campo correo
   - Implementar validación de complejidad de contraseña
   - Agregar `@Size(min=8, max=100)` en contraseña
   - Validar formato de datos de entrada

3. **Base de Datos:**
   - Usar variables de entorno para credenciales
   - Implementar cifrado a nivel de columna para datos sensibles
   - Configurar SSL para conexión a PostgreSQL
   - Implementar backups automáticos

4. **API:**
   - Implementar rate limiting por usuario/IP
   - Agregar HTTPS en producción
   - Implementar auditoría de acciones de usuarios
   - Configurar logs estructurados

5. **CORS:**
   - Configurar orígenes específicos en producción
   - Evitar usar `allowedOriginPatterns` con wildcards
   - Implementar whitelist de dominios

---

## 🤝 Contribuciones

Este proyecto fue desarrollado como parte del **Hackathon ONE - No Country**.

### Equipo de Desarrollo

**Backend (Java/Spring Boot):**
- Sistema de autenticación JWT
- Gestión de productos y categorías
- Integración con API Python
- Persistencia de sesiones

**Frontend:**
- Interfaz de usuario (React/Vue/Angular)
- Integración con API REST
- Visualización de datos

**Data Science (Python/FastAPI):**
- Modelo de Machine Learning
- API de análisis de sentimientos

### Cómo Contribuir

1. Fork el repositorio
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m 'feat: agrega nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo licencia Apache 2.0 (heredada de Spring Boot).

---

## 🔄 Changelog

### v3.0.0 (Actual)
- ✅ Implementa sistema completo de gestión de productos
- ✅ Agrega análisis de sentimientos asociado a productos
- ✅ Implementa detección automática de múltiples productos
- ✅ Crea tabla intermedia sesion_producto con estadísticas
- ✅ Agrega persistencia de comentarios individuales
- ✅ Implementa reutilización de productos de sesiones previas
- ✅ Agrega contadores acumulativos en productos
- ✅ Implementa sistema de eventos para creación automática de categorías
- ✅ Agrega endpoints de debug y utilidades
- ✅ Mejora manejo de errores y validaciones

### v2.0.0
- ✅ Agrega autenticación JWT
- ✅ Implementa sistema de categorías
- ✅ Crea gestión básica de productos
- ✅ Implementa persistencia de sesiones
- ✅ Actualiza context path a `/project/api/v2`
- ✅ Agrega CORS configuration

### v1.0.0
- ✅ API Gateway para análisis de sentimientos
- ✅ Endpoints individual y batch
- ✅ Integración con API Python
- ✅ Validación de entrada
- ✅ Manejo global de excepciones

---

## 📚 Recursos Adicionales

### Documentación Oficial
- [Spring Boot 4.0.1 Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [JWT.io - JSON Web Tokens](https://jwt.io/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Tutoriales Recomendados
- Spring Security + JWT Authentication
- REST API Best Practices
- PostgreSQL Performance Tuning
- Docker Deployment Guide

---

## 🎯 Roadmap Futuro

### Próximas Características
- [ ] Implementar paginación en endpoints de listado
- [ ] Agregar búsqueda y filtrado avanzado de productos
- [ ] Implementar exportación de reportes (PDF, Excel)
- [ ] Agregar gráficos y visualizaciones de tendencias
- [ ] Implementar comparación entre productos
- [ ] Agregar notificaciones en tiempo real
- [ ] Implementar análisis de palabras clave
- [ ] Agregar soporte para análisis de imágenes
- [ ] Implementar API versioning (v3)
- [ ] Agregar documentación con Swagger/OpenAPI

### Mejoras Técnicas
- [ ] Migrar a Spring Boot 4.1
- [ ] Implementar caché con Redis
- [ ] Agregar tests unitarios y de integración
- [ ] Implementar CI/CD con GitHub Actions
- [ ] Dockerizar la aplicación
- [ ] Agregar monitoreo con Prometheus/Grafana
- [ ] Implementar logging centralizado
- [ ] Optimizar queries con índices en BD

---

## 📞 Soporte y Contacto

Para preguntas, sugerencias o reporte de bugs:

- **GitHub Issues:** [Reportar un problema](https://github.com/tu-repo/issues)
- **Email:** soporte@ejemplo.com
- **Discord:** [Unirse al servidor](https://discord.gg/ejemplo)

---

<div align="center">

**Desarrollado con ❤️ para Hackathon ONE - No Country**

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-Secured-purple?style=for-the-badge&logo=jsonwebtokens)

</div>

**Body (JSON):**
```json
{
  "nombreCategoria": "Mascotas",
  "descripcion": "Productos y accesorios para mascotas"
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/categoria \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCategoria": "Mascotas",
    "descripcion": "Productos y accesorios para mascotas"
  }'
```

**Respuesta (200 OK):**
```json
{
  "categoriaId": 13,
  "nombreCategoria": "Mascotas",
  "descripcion": "Productos y accesorios para mascotas",
  "totalProductos": 0
}
```

---

### 📦 Gestión de Productos

#### 6. Crear Producto

**Endpoint:** `POST /project/api/v2/producto`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nombreProducto": "iPhone 15 Pro",
  "categoriaId": 1
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/producto \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "nombreProducto": "iPhone 15 Pro",
    "categoriaId": 1
  }'
```

**Respuesta (200 OK):**
```json
{
  "productoId": 1,
  "nombreProducto": "iPhone 15 Pro",
  "categoriaId": 1,
  "nombreCategoria": "Electrónica",
  "totalMenciones": 0,
  "positivos": 0,
  "negativos": 0,
  "neutrales": 0,
  "porcentajePositivos": 0.0,
  "porcentajeNegativos": 0.0,
  "porcentajeNeutrales": 0.0,
  "fechaCreacion": "2026-01-24 15:30:45",
  "ultimaActualizacion": "2026-01-24 15:30:45"
}
```

---

#### 7. Obtener Todos los Productos

**Endpoint:** `GET /project/api/v2/producto`

Retorna todos los productos del usuario ordenados por última actualización.

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/project/api/v2/producto \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Respuesta (200 OK):**
```json
[
  {
    "productoId": 1,
    "nombreProducto": "iPhone 15 Pro",
    "categoriaId": 1,
    "nombreCategoria": "Electrónica",
    "totalMenciones": 150,
    "positivos": 120,
    "negativos": 20,
    "neutrales": 10,
    "porcentajePositivos": 80.0,
    "porcentajeNegativos": 13.33,
    "porcentajeNeutrales": 6.67,
    "fechaCreacion": "2026-01-24 15:30:45",
    "ultimaActualizacion": "2026-01-24 18:22:10"
  }
]
```

---

#### 8. Obtener Productos por Categoría

**Endpoint:** `GET /project/api/v2/producto/por-categoria?categoriaId={id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X GET "http://localhost:8080/project/api/v2/producto/por-categoria?categoriaId=1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Respuesta (200 OK):**
```json
[
  {
    "productoId": 1,
    "nombreProducto": "iPhone 15 Pro",
    "categoriaId": 1,
    "nombreCategoria": "Electrónica",
    "totalMenciones": 150,
    "positivos": 120,
    "negativos": 20,
    "neutrales": 10,
    "porcentajePositivos": 80.0,
    "porcentajeNegativos": 13.33,
    "porcentajeNeutrales": 6.67,
    "fechaCreacion": "2026-01-24 15:30:45",
    "ultimaActualizacion": "2026-01-24 18:22:10"
  }
]
```

---

#### 9. Obtener Producto por ID

**Endpoint:** `GET /project/api/v2/producto/{productoId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/project/api/v2/producto/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

### 💬 Análisis de Sentimientos

#### 10. Análisis Individual (Sin Autenticación)

**Endpoint:** `POST /project/api/v2/sentiment/analyze`

Analiza un único texto sin requerir autenticación.

**Headers:**
```
Content-Type: text/plain
```

**Body (raw text):**
```
El producto es excelente y llegó muy rápido
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/sentiment/analyze \
  -H "Content-Type: text/plain" \
  -d "El servicio al cliente fue excepcional"
```

**Respuesta (200 OK):**
```json
{
  "prevision": "Positivo",
  "probabilidad": 0.9456
}
```

---

#### 11. Análisis por Lotes (Sin Autenticación)

**Endpoint:** `POST /project/api/v2/sentiment/analyze/batch`

Analiza múltiples textos en una sola petición (separados por saltos de línea).

**Headers:**
```
Content-Type: text/plain
```

**Body (raw text, separado por `\n`):**
```
El producto es excelente
La calidad es mala
El servicio fue aceptable
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/sentiment/analyze/batch \
  -H "Content-Type: text/plain" \
  -d $'El producto es excelente\nLa calidad es mala\nEl servicio fue aceptable'
```

**Respuesta (200 OK):**
```json
{
  "results": [
    {
      "prevision": "Positivo",
      "probabilidad": 0.9456
    },
    {
      "prevision": "Negativo",
      "probabilidad": 0.8723
    },
    {
      "prevision": "Neutral",
      "probabilidad": 0.6891
    }
  ],
  "total": 3
}
```

---

### 📊 Gestión de Sesiones y Análisis con Productos

#### 12. Analizar Comentarios (Sin Producto Asociado)

**Endpoint:** `POST /project/api/v2/sesion/analizar`

Analiza una lista de comentarios y guarda la sesión con todos los comentarios individuales.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "comentarios": [
    "El producto llegó en perfectas condiciones",
    "La calidad no es la esperada",
    "El precio es adecuado"
  ]
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/sesion/analizar \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "comentarios": [
      "El producto llegó en perfectas condiciones",
      "La calidad no es la esperada",
      "El precio es adecuado"
    ]
  }'
```

**Respuesta (200 OK):**
```json
{
  "sessionId": 1,
  "date": "2026-01-24",
  "avgScore": 0.8357,
  "total": 3,
  "positivos": 1,
  "negativos": 1,
  "neutrales": 1,
  "comentarios": [
    {
      "texto": "El producto llegó en perfectas condiciones",
      "sentimiento": "Positivo",
      "probabilidad": 0.9234
    },
    {
      "texto": "La calidad no es la esperada",
      "sentimiento": "Negativo",
      "probabilidad": 0.8123
    },
    {
      "texto": "El precio es adecuado",
      "sentimiento": "Neutral",
      "probabilidad": 0.7714
    }
  ]
}
```

---

#### 13. Analizar Comentarios con Producto Específico

**Endpoint:** `POST /project/api/v2/sesion/analizar-con-producto`

Analiza comentarios y los asocia a un producto. Actualiza automáticamente los contadores del producto y guarda estadísticas específicas de la sesión.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```
---

## 🎓 Conclusiones

Este proyecto representa una **solución integral de análisis de sentimientos** que combina lo mejor de dos mundos: la robustez y escalabilidad de **Spring Boot** con la potencia del **Machine Learning en Python**. A través de su desarrollo, hemos logrado:

### Logros Técnicos Destacados

- ✅ **Arquitectura de Microservicios**: Implementación exitosa de un gateway que orquesta la comunicación entre el backend Java y el modelo de ML en Python, demostrando cómo integrar tecnologías heterogéneas de forma eficiente.

- ✅ **Seguridad Robusta**: Sistema de autenticación JWT completo con encriptación BCrypt, filtros de seguridad personalizados y manejo granular de permisos, garantizando la protección de datos sensibles de los usuarios.

- ✅ **Gestión Inteligente de Datos**: Modelo de base de datos relacional optimizado que permite no solo almacenar análisis puntuales, sino rastrear la evolución temporal de productos, categorías y sentimientos a lo largo del tiempo.

- ✅ **Event-Driven Architecture**: Implementación de un sistema de eventos desacoplado que automatiza la configuración inicial de usuarios, demostrando buenas prácticas de diseño de software empresarial.

- ✅ **Flexibilidad Analítica**: Múltiples modalidades de análisis (individual, por lotes, con productos únicos o múltiples, reutilización de sesiones previas) que se adaptan a diferentes casos de uso reales.

### Aprendizajes Clave

Durante el desarrollo de **SentimentAPI**, profundizamos en:

- **Comunicación Reactiva**: Implementación de WebClient de Spring WebFlux para llamadas HTTP no bloqueantes, mejorando el rendimiento y la escalabilidad.
- **Persistencia Avanzada**: Manejo de relaciones complejas en JPA (OneToMany, ManyToOne, ManyToMany) con optimización de queries y estrategias de carga.
- **Transaccionalidad**: Gestión correcta de transacciones en operaciones que involucran múltiples entidades y actualizaciones acumulativas.
- **Seguridad en APIs REST**: Implementación de JWT, filtros personalizados, CORS y protección de endpoints sensibles.

### Casos de Uso Reales

Esta API está diseñada para ser utilizada en:

- 📊 **Análisis de Reviews de E-commerce**: Monitoreo de opiniones de clientes sobre productos.
- 🎯 **Brand Monitoring**: Seguimiento de menciones y sentimientos hacia marcas en redes sociales.
- 📈 **Market Research**: Análisis de feedback de usuarios para investigación de mercado.
- 🛠️ **Product Management**: Identificación de fortalezas y debilidades de productos basándose en comentarios reales.
- 📱 **Customer Service**: Priorización de tickets de soporte según el sentimiento detectado.

### Escalabilidad y Futuro

El diseño modular y la separación de responsabilidades permiten que **SentimentAPI** pueda crecer fácilmente:

- **Horizontalmente**: Agregando más instancias del servidor Spring Boot detrás de un load balancer.
- **Funcionalmente**: Incorporando nuevos modelos de ML (detección de tópicos, análisis de emociones específicas).
- **Tecnológicamente**: Migrando a arquitecturas de microservicios con Kubernetes, implementando caché distribuido con Redis, o agregando mensajería asíncrona con RabbitMQ/Kafka.

---

## 🙏 Agradecimientos

Este proyecto fue posible gracias al esfuerzo colaborativo y el apoyo de múltiples actores:

### Al Programa Hackathon ONE - No Country

Agradecemos profundamente a **No Country** por:
- Proporcionar un espacio de aprendizaje colaborativo y desafiante
- Fomentar el trabajo en equipo interdisciplinario
- Crear oportunidades para desarrolladores de toda Latinoamérica
- Impulsar proyectos que resuelven problemas reales con tecnología

### Al Equipo No Data - No Code

Agradecimiento especial al equipo **No Data - No Code** por el extraordinario trabajo realizado durante la Hackathon:

- **Jonathan Tuppia** - Por su repositorio de referencia [SentimentAPI](https://github.com/Jona-9/SentimentAPI) que sirvió como base para este backend, por liderar el deploy de los tres frentes en local y la presentación exitosa en el Demo Day.
- **Alexandra Cleto** - Por su repositorio de referencia [sentimientos](https://github.com/Alexandracleto/sentimientos/tree/Ale-dev) que inspiró el diseño del frontend React.
- **Francisco Llendo** - Por su modelo de Machine Learning optimizado a la versión 4.0, disponible en [Sentimental_API_No_Data_No_Code_Semana_4](https://github.com/GustavoVasquezS/Sentimental_API_No_Data_No_Code_Semana_4).

### Al Equipo de Desarrollo

**Backend Team (Java/Spring Boot)**:
- Por la implementación robusta de la arquitectura REST
- Por el diseño cuidadoso del modelo de datos
- Por la integración fluida con el modelo de Machine Learning

**Frontend Team (React/Tailwind)**:
- Por crear una interfaz intuitiva que hace accesible la complejidad del análisis
- Por el feedback constante que mejoró los endpoints de la API

**Data Science Team (Python/FastAPI)**:
- Por desarrollar un modelo de ML preciso y eficiente con TF-IDF + Regresión Logística Calibrada
- Por documentar claramente los endpoints de análisis
- Por optimizar los tiempos de respuesta del modelo

### A la Comunidad Open Source

Especial reconocimiento a los mantenedores de:
- **Spring Framework** y **Spring Boot** - Por democratizar el desarrollo empresarial en Java
- **PostgreSQL** - Por proporcionar una base de datos robusta y gratuita
- **jjwt** - Por facilitar la implementación de JWT en Java
- **BCrypt** - Por hacer la seguridad de contraseñas accesible

### A los Futuros Usuarios y Contribuidores

Si este proyecto te resultó útil, considera:
- ⭐ **Dar una estrella** al repositorio en GitHub
- 🐛 **Reportar bugs** o sugerir mejoras a través de Issues
- 🔧 **Contribuir** con Pull Requests
- 📢 **Compartir** el proyecto con otros desarrolladores

---

**Desarrollado con dedicación y pasión por el aprendizaje continuo** ❤️

---

<div align="center">

**⭐ Si este proyecto te ayudó, considera darle una estrella ⭐**

**🚀 Happy Coding! 🚀**

---

*SentimentAPI v4.0 - Hackathon ONE 2026*

</div>
