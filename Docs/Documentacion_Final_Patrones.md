# PROYECTO FINAL — DISEÑO DE PATRONES
## Sentiment API: del monolito a Clean Architecture (Antes y Después)

**Curso:** Diseño de Patrones (100000SI47) — 2026, Ciclo 1
**Docente:** Jose Luis Milla Flores
**Modalidad de evaluación:** Proyecto Final (40%) — Exposición Semana 18

**Integrantes y participación:**

| Integrante | Rol principal en el refactoring | Participación |
|---|---|---|
| Jose Eduardo Diaz Fernandez | Capa `domain` + `application` (ports, use cases, Builder) | 33.3% |
| Jonathan Edilson Tuppia Lozano | Capa `infrastructure` (adapters, Singleton, seguridad) | 33.3% |
| Joaquin Sebastian Chaparro Villavicencio | Capa `presentation` (controllers, DTOs) + pruebas JUnit | 33.3% |

**Lima – Perú, 2026**

---

## Índice

1. Descripción del negocio y problemática
2. Análisis AS-IS — el código anterior (monolítico)
   - 2.1 Estructura del proyecto original
   - 2.2 Las 7 violaciones SOLID (con evidencia)
3. Arquitectura TO-BE — el código nuevo (Clean Architecture)
   - 3.1 Clean Architecture y la regla de dependencias
   - 3.2 Principios SOLID aplicados (Unidad 1)
   - 3.3 Patrones GOF aplicados — Antes vs. Después (Unidades 2–4)
   - 3.4 Patrones GRASP (Unidad 5)
4. Pruebas Unitarias (JUnit 5) — evidencia
5. Mapeo del sílabo → dónde se cumple en el proyecto
6. Conclusiones y repositorio

---

## 1. Descripción del negocio y problemática

Las empresas reciben cientos de reseñas de productos en **español y portugués**, y revisarlas manualmente es inviable. **Sentiment API** automatiza ese proceso: un backend **Spring Boot** gestiona usuarios, productos, categorías y sesiones de análisis, y se conecta a una **API de Machine Learning** propia (Python, Regresión Logística) que clasifica cada comentario como **Positivo, Negativo o Neutro** con su probabilidad. Un dashboard en React consume el backend y muestra estadísticas e historial.

El sistema **funcionaba** desde la primera versión: los endpoints respondían, los CSV se procesaban y las estadísticas se generaban. El problema **no era funcional sino de diseño**: el código concentraba responsabilidades, acoplaba el dominio a los frameworks (JPA, WebClient) y hacía costoso cualquier cambio futuro (cambiar el modelo de IA, migrar la base de datos, añadir un canal de notificación).

Este documento demuestra, **con el código antes y después**, cómo aplicamos los **principios SOLID**, **cinco patrones GOF**, **patrones GRASP** y **Clean Architecture** —todos temas del sílabo— para convertir un monolito rígido en un sistema extensible.

---

## 2. Análisis AS-IS — el código anterior (monolítico)

> Código de referencia: `ProyectoOriginal-before/sentiment-backend-java`.

### 2.1 Estructura del proyecto original

Los paquetes mezclaban niveles de abstracción sin barrera entre negocio e infraestructura:

```
sentimentapi/
├── config/          ← DataInitializer mezclado con configuración
├── configuration/   ← ConectarApi y EndPointConfg (duplica config/)
├── controller/      ← 6 controllers, algunos con lógica de negocio
├── dto/             ← 16 DTOs sin separar request/response
├── entity/          ← Entidades JPA usadas como modelo de dominio
├── event/           ← Observer fuera de su capa
├── repository/      ← Spring Data JPA
├── security/        ← JWT
└── service/         ← Interfaces e implementaciones mezcladas
```

**Problema central:** una entidad como `Producto` era a la vez el modelo de BD (`@Entity`, `@Column`) y el objeto de negocio que los `service` manipulaban. Cambiar de motor de base de datos obligaba a tocar clases de negocio.

### 2.2 Las 7 violaciones SOLID (con evidencia)

| # | Archivo (antes) | Principio | Problema |
|---|---|---|---|
| 1 | `service/CsvAnalysisServiceImplement.java` | **SRP** | Un método de ~200 líneas con 6 responsabilidades: crear entidades, llamar la API, calcular estadísticas, construir la sesión, actualizar productos y armar la respuesta. |
| 2 | `service/UserServiceImplement.java` | **SRP** | Mezcla registro, login, reset de contraseña, JWT y `System.out.println` de debug. |
| 3 | `service/SesionServiceImplement.java` | **SRP + DIP** | Un servicio de Sesión inyecta `CategoriaRepository` y `ProductoRepository` de otro dominio. |
| 4 | `service/SentimentServiceImplement.java` | **DIP** | Depende directamente de `ConectarApi` (clase concreta). |
| 5 | `configuration/ConectarApi.java` | **SRP / Singleton ausente** | `client()` crea un `WebClient` nuevo en cada llamada. |
| 6 | `entity/` (Producto, Sesion…) | **DIP** | Entidades JPA usadas como modelos de negocio. |
| 7 | `config/` vs `configuration/` | **SRP** | Dos paquetes de configuración sin separación clara. |

**Evidencia 1 — SRP roto (`CsvAnalysisServiceImplement`, un método hace de todo):**

```java
// ANTES — el método procesarYAnalizarCsv concentra 6 responsabilidades
@Override
@Transactional
public CsvAnalysisResponseDto procesarYAnalizarCsv(List<CsvRowDto> rows, Integer usuarioId) {
    User usuario = userRepository.findById(usuarioId)...        // (1) persistencia
    // ... crea categorías y productos con new + save ...        // (2) creación entidades
    Optional<SentimentsResponseDto> responseOpt =
            sentimentService.consultarSentimientos(textoCompleto); // (3) llamada IA (clase concreta → DIP roto)
    // ... 40 líneas de conteos positivos/negativos/neutrales ... // (4) estadísticas
    Sesion sesion = new Sesion();
    sesion.setFecha(...); sesion.setTotal(...); /* ~15 setters */ // (5) construcción de Sesion
    for (...) { prod.incrementarContadores(...); productoRepository.save(prod); } // (6) actualizar productos
    return new CsvAnalysisResponseDto(...);
}
```

**Evidencia 2 — Singleton ausente (`ConectarApi`):**

```java
// ANTES — se construye un WebClient NUEVO en cada invocación (costoso e innecesario)
public WebClient client() {
    String url = endPointConfg.getUrl();
    return WebClient.builder().baseUrl(url).build();
}
```

**Evidencia 3 — DIP roto (`SentimentServiceImplement`):**

```java
// ANTES — el servicio de negocio depende de una clase concreta de infraestructura
@Autowired
ConectarApi conectarApi; // no hay interfaz que actúe de barrera
```

**Evidencia 4 — SRP roto + trazas de debug (`UserServiceImplement`):**

```java
// ANTES — registro + login + reset + JWT en la misma clase, con prints de depuración
System.out.println("REGISTRANDO USUARIO: " + userDtoRegistro.getCorreo());
System.out.println("[LOGIN] Hash en DB (primeros 20): " + usuario.getContrasena()...);
```

---

## 3. Arquitectura TO-BE — el código nuevo (Clean Architecture)

> Código de referencia: `Sentiment-API---Project/sentiment-backend-java` (este repositorio).

### 3.1 Clean Architecture y la regla de dependencias

Adoptamos **Clean Architecture** (Robert C. Martin). La regla fundamental: **las dependencias siempre apuntan hacia adentro**; el dominio no conoce nada del exterior.

```
[ presentation ] ──► [ application ] ──► [ domain ]
                                              ▲
              [ infrastructure ] ─────────────┘
```

| Capa | Contenido | Regla |
|---|---|---|
| `domain` | POJOs de negocio, interfaces de ports (in/out), excepciones, evento | No importa Spring ni JPA |
| `application` | Use cases, `SesionBuilder`, mappers, listener | Solo importa `domain` |
| `infrastructure` | Adapters JPA, `SentimentApiAdapter`, `EmailAdapter`, `WebClientConfig`, seguridad | Implementa los contratos de `domain` |
| `presentation` | Controllers, DTOs request/response, `GlobalExceptionHandler` | Llama a los use cases de `application` |

Estructura de paquetes real:

```
com.project.sentimentapi/
├── domain/{model, port/in, port/out, event, exception}
├── application/{usecase, builder, mapper, event}
├── infrastructure/{persistence/{entity,repository,adapter}, external, email, security, config}
└── presentation/{controller, dto/request, dto/response, exception}
```

**Beneficio inmediato:** cambiar el proveedor de IA = crear un nuevo adapter que implemente `SentimentAnalysisPort`, sin tocar ningún use case. Cambiar de base de datos = nuevos adapters de repositorio, sin tocar el dominio.

### 3.2 Principios SOLID aplicados (Unidad 1)

| Principio | Antes (problema) | Después (solución) | Archivo nuevo |
|---|---|---|---|
| **SRP** (Responsabilidad Única) | `CsvAnalysisServiceImplement` hacía 6 cosas | Cada responsabilidad en un método/clase; el use case orquesta | `application/usecase/AnalizarCsvUseCaseImpl.java` |
| **OCP** (Abierto/Cerrado) | Cambiar de IA obligaba a editar el servicio | Nuevo proveedor = nuevo adapter, sin tocar el use case | `infrastructure/external/SentimentApiAdapter.java` |
| **LSP** (Sustitución de Liskov) | — | Cualquier implementación de un port es intercambiable; el handler trabaja con el tipo más específico | `presentation/exception/GlobalExceptionHandler.java` |
| **DIP** (Inversión de Dependencia) | `@Autowired ConectarApi` (concreta) | El use case depende de `SentimentAnalysisPort` (interfaz) | `domain/port/out/SentimentAnalysisPort.java` |
| **ISP** (Segregación de Interfaces) | `SesionService` mezclaba lectura y escritura | `GuardarSesionUseCase` (escritura) y `ConsultarSesionesUseCase` (lectura) separadas | `domain/port/in/*` |

**DIP — Antes vs. Después:**

```java
// ANTES: el dominio conoce la implementación concreta
@Autowired
ConectarApi conectarApi;

// DESPUÉS: el use case depende de una abstracción (port); Spring inyecta el adapter
public AnalizarCsvUseCaseImpl(SentimentAnalysisPort sentimentPort, ...) {
    this.sentimentPort = sentimentPort;
}
```

### 3.3 Patrones GOF aplicados — Antes vs. Después (Unidades 2–4)

#### a) Singleton *(Unidad 2 — creacional)*

**Definición (curso):** garantizar una única instancia de una clase en todo el sistema.
**Problema que resolvía:** `ConectarApi.client()` creaba un `WebClient` nuevo por llamada.

```java
// ANTES
public WebClient client() {
    return WebClient.builder().baseUrl(endPointConfg.getUrl()).build(); // nueva instancia cada vez
}

// DESPUÉS — un @Bean de Spring tiene scope Singleton por defecto: una sola instancia reutilizada
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient sentimentWebClient(EndPointConfg config) {
        return WebClient.builder().baseUrl(config.getUrl()).build();
    }
}
```
**Beneficio:** se reutiliza el pool de conexiones; menos costo de creación. *(Verificado con `WebClientConfigTest`.)*

#### b) Builder *(Unidad 2 — creacional)*

**Definición (curso):** separar la construcción de un objeto complejo de su representación.
**Problema que resolvía:** la `Sesion` se armaba con ~15 setters dispersos, sin validar campos obligatorios.

```java
// ANTES — setters sueltos dentro del servicio monolítico
Sesion sesion = new Sesion();
sesion.setFecha(LocalDateTime.now());
sesion.setTotal(total); sesion.setPositivos(positivos); /* ... */

// DESPUÉS — construcción centralizada y validada
Sesion sesion = new SesionBuilder()
        .conUsuario(usuarioId)
        .conFecha(LocalDateTime.now())
        .conTotalComentarios(total)
        .conEstadisticas(positivos, negativos, neutrales, avgScore)
        .build(); // lanza IllegalStateException si falta usuarioId
```
**Decisión de diseño:** `SesionBuilder` **NO** es un `@Bean`; se instancia con `new` en cada análisis para que su estado mutable no se comparta entre hilos HTTP concurrentes. *(Verificado con `SesionBuilderTest`.)*

#### c) Adapter *(Unidad 3 — estructural)*

**Definición (curso):** convertir la interfaz de una clase en otra que el cliente espera.
**Problema que resolvía:** el dominio dependía de `WebClient` y de las entidades JPA.

```java
// DESPUÉS — el adapter traduce el port del dominio a llamadas reales de WebClient
@Component
public class SentimentApiAdapter implements SentimentAnalysisPort {
    private final WebClient webClient; // el @Bean Singleton
    @Override
    public Optional<SentimentsResponseDto> analizarLote(List<String> textos) {
        // ... llamada HTTP; el dominio nunca ve estas líneas ...
    }
}
```
El mismo patrón desacopla JPA: `ProductoRepositoryAdapter implements ProductoRepositoryPort` traduce entre `ProductoJpaEntity` y el modelo `Producto`. **Beneficio:** el dominio no importa ninguna clase de Spring ni de JPA (habilita OCP y DIP).

#### d) Facade *(Unidad 3 — estructural)*

**Definición (curso):** ofrecer una interfaz única y simple a un subsistema complejo.
**Problema que resolvía:** el controller conocía repositorios, servicio de IA y lógica de estadísticas.

```java
// DESPUÉS — el controller llama UN método; el use case coordina todo por dentro
CsvAnalysisResponseDto resultado = analizarUseCase.analizar(filas, usuarioId);
```
Dentro, `AnalizarCsvUseCaseImpl` coordina `SentimentAnalysisPort`, `ProductoRepositoryPort`, `SesionRepositoryPort`, `ComentarioRepositoryPort` y `SesionBuilder`. **Beneficio:** añadir un paso nuevo (p. ej. un email de resumen) solo cambia el use case. *(Verificado con `AnalizarCsvUseCaseImplTest`.)*

#### e) Observer *(Unidad 4 — comportamiento)*

**Definición (curso):** cuando un objeto cambia de estado, notifica automáticamente a sus dependientes.
**Uso:** al registrarse un usuario, se envía un correo de bienvenida sin acoplar el registro al email.

```java
// El use case publica el evento (sujeto)
eventPublisher.publishEvent(new UserRegisteredEvent(guardado.getEmail(), guardado.getNombre()));

// El listener (observador) reacciona; depende de EmailPort (DIP), no de la implementación
@EventListener
public void onUserRegistered(UserRegisteredEvent event) {
    emailPort.enviarBienvenida(event.getEmail(), event.getNombre());
}
```
**Mejora respecto al original:** el `UserRegisteredEvent` ahora es un **POJO puro** en `domain/event/` (no extiende `ApplicationEvent`), respetando la regla de dependencias.

### 3.4 Patrones GRASP (Unidad 5)

| Patrón GRASP | Dónde se cumple en el proyecto |
|---|---|
| **Controlador** | Los `@RestController` (`CsvAnalysisController`, `UsuarioController`…) reciben la petición y delegan al use case, sin lógica de negocio. |
| **Bajo acoplamiento** | El dominio depende de interfaces (ports), no de JPA/WebClient. |
| **Alta cohesión** | Cada use case tiene una sola responsabilidad; cada adapter, una sola integración. |
| **Experto en información** | El cálculo de porcentajes vive en quien tiene los datos (`ProductoMapper`, use cases). |
| **Fabricación pura** | Los adapters y mappers son clases "artificiales" que no existen en el dominio pero bajan el acoplamiento. |
| **Polimorfismo** | El use case usa `SentimentAnalysisPort`; la implementación concreta se resuelve en tiempo de ejecución (permite cambiar de proveedor). |

---

## 4. Pruebas Unitarias (JUnit 5) — evidencia

Se crearon pruebas que **demuestran que los patrones funcionan** y que el diseño es testeable sin base de datos ni red (consecuencia directa del DIP). Estilo Arrange/Act/Assert, como pide la guía.

| Prueba | Patrón/Principio demostrado | Resultado |
|---|---|---|
| `SesionBuilderTest` (4 casos) | **Builder**: construcción válida, validación de obligatorios, estado aislado | ✅ 4/4 |
| `WebClientConfigTest` (1 caso) | **Singleton**: `assertSame` sobre el `@Bean` del `WebClient` | ✅ 1/1 |
| `AnalizarCsvUseCaseImplTest` (2 casos) | **Facade + DIP**: use case probado con *dobles de prueba* que implementan los ports | ✅ 2/2 |
| `UsuarioMapperTest` (2 casos) | Mapeo dominio→DTO sin filtrar `passwordHash` | ✅ 2/2 |
| `SentimentapiApplicationTests` (1 caso) | Carga del contexto Spring | ✅ 1/1 |

**Evidencia de ejecución (`./mvnw test`):**

```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 -- SesionBuilderTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- WebClientConfigTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- AnalizarCsvUseCaseImplTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- UsuarioMapperTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- SentimentapiApplicationTests
——————————————————————————————————————————————————————————
Total: 10 pruebas · 0 fallos · 0 errores  → BUILD SUCCESS
```

Ejemplo del test de Singleton (demuestra "matemáticamente" la instancia única):

```java
WebClient primera = ctx.getBean(WebClient.class);
WebClient segunda = ctx.getBean(WebClient.class);
assertSame(primera, segunda, "El WebClient no es Singleton"); // misma referencia en memoria
```

---

## 5. Mapeo del sílabo → dónde se cumple en el proyecto

| Unidad del sílabo | Tema | Evidencia en el proyecto |
|---|---|---|
| **U1** — Principios de diseño | SRP, LSP, OCP, DIP, ISP | Sección 3.2; separación en capas y ports |
| **U2** — Creacionales | Singleton, Builder | `WebClientConfig`, `SesionBuilder` (+ pruebas) |
| **U3** — Estructurales | Adapter, Facade | `SentimentApiAdapter`/`*RepositoryAdapter`, `AnalizarCsvUseCaseImpl` |
| **U4** — Comportamiento | Observer | `UserRegisteredEvent` + `UserRegistrationListener` |
| **U5** — GRASP | Controlador, Alta cohesión, Bajo acoplamiento, Experto, Fabricación pura, Polimorfismo | Sección 3.4 |
| Pruebas (guía, Cap. 4) | JUnit 5 | Sección 4 — 10 pruebas en verde |

---

## 6. Conclusiones y repositorio

El proyecto partía de una base **funcional pero rígida**: siete violaciones SOLID acoplaban el dominio a los frameworks. **Clean Architecture** rompió ese acoplamiento de forma estructurada —el dominio define contratos, la infraestructura los implementa y los use cases orquestan— y los **cinco patrones GOF** no fueron decisiones arbitrarias sino respuestas directas a problemas concretos:

- **Singleton** para gestionar el `WebClient` correctamente,
- **Builder** para centralizar y validar la construcción de `Sesion`,
- **Adapter** para desacoplar WebClient y JPA del dominio,
- **Facade** para simplificar la complejidad dispersa del análisis de CSV,
- **Observer** para la reactividad al registro sin acoplamiento.

Tras el refactoring, **agregar un nuevo proveedor de IA, cambiar la base de datos o añadir un canal de notificación es una operación de extensión, no de modificación** (OCP). Las pruebas unitarias confirman que el diseño es correcto y testeable de forma aislada.

**Repositorio GitHub:** _(añadir enlace del repositorio del equipo)_
