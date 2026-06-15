# Plan de Refactoring — Sentiment API
## De MVC en Capas a Clean Architecture
### Clean Architecture + Principios SOLID + Patrones de Diseño GOF
**Curso:** Diseño de Patrones (100000SI47) — UTP 2026 Ciclo 1  
**Proyecto:** `sentiment-backend-java`  
**Equipo:** 3 integrantes

---

## Índice

1. [¿Por qué refactorizar? Diagnóstico del código actual](#1-por-qué-refactorizar-diagnóstico-del-código-actual)
2. [¿Qué es Clean Architecture?](#2-qué-es-clean-architecture)
3. [Principios SOLID aplicados](#3-principios-solid-aplicados)
4. [Patrones de Diseño GOF aplicados](#4-patrones-de-diseño-gof-aplicados)
5. [Nueva estructura de paquetes completa](#5-nueva-estructura-de-paquetes-completa)
6. [Integrante 1 — domain/ + application/](#6-integrante-1--domain--application)
7. [Integrante 2 — infrastructure/](#7-integrante-2--infrastructure)
8. [Integrante 3 — presentation/ + integración final](#8-integrante-3--presentation--integración-final)
9. [Regla de dependencias — la ley más importante](#9-regla-de-dependencias--la-ley-más-importante)
10. [Orden de implementación por semanas](#10-orden-de-implementación-por-semanas)
11. [Cómo sustentar el proyecto ante el docente](#11-cómo-sustentar-el-proyecto-ante-el-docente)

---

## 1. ¿Por qué refactorizar? Diagnóstico del código actual

### 1.1 La arquitectura actual y su problema fundamental

El proyecto actualmente sigue una **arquitectura en capas clásica** (también llamada MVC en capas). La estructura es:

```
Controller → Service → Repository → Entity (JPA)
```

Esto funciona para proyectos pequeños, pero tiene un problema crítico: **las capas de alto nivel conocen los detalles de las capas de bajo nivel**. Es decir, si mañana se cambia la base de datos, o se cambia el proveedor de análisis de sentimientos por IA, hay que modificar múltiples clases en múltiples capas.

En Clean Architecture (la que vamos a implementar), la regla es la contraria: **las capas de alto nivel definen contratos (interfaces), y las capas de bajo nivel los implementan**. El dominio nunca sabe que existe Spring, ni JPA, ni ningún framework externo.

### 1.2 Problemas concretos encontrados en el código

| # | Archivo afectado | Problema concreto | Principio violado |
|---|---|---|---|
| 1 | `CsvAnalysisServiceImplement.java` | Tiene más de 150 líneas mezclando 6 responsabilidades distintas: crea entidades JPA, llama a la API de sentimientos, calcula estadísticas, construye la sesión, actualiza productos y arma la respuesta final. Si cambia el formato de la API externa, hay que tocar esta clase. Si cambia el cálculo de estadísticas, también. Una clase no debería tener más de una razón para cambiar. | **SRP** |
| 2 | `SesionServiceImplement.java` | Este servicio inyecta directamente `CategoriaRepository` y `ProductoRepository`. Un servicio de Sesión no debería acceder al repositorio de otro dominio directamente; eso viola el principio de que los módulos de alto nivel no deben depender de detalles de bajo nivel (los repositorios JPA son detalles). | **SRP + DIP** |
| 3 | `SentimentServiceImplement.java` | Depende directamente de `ConectarApi`, una clase concreta. Si el equipo decide cambiar de proveedor de IA (por ejemplo, de una API Python propia a OpenAI), hay que modificar esta clase. Lo correcto es que dependa de una interfaz, y que la implementación concreta sea intercambiable. | **DIP** |
| 4 | `ConectarApi.java` | El método `client()` crea un nuevo objeto `WebClient` en cada invocación. `WebClient` es un objeto costoso de crear (inicializa conexiones, configuración, etc.). Debería existir como un único bean singleton en el contexto de Spring. | **SRP** |
| 5 | `UserServiceImplement.java` | Mezcla cuatro responsabilidades completamente distintas en 200+ líneas: lógica de registro de usuario, lógica de autenticación y generación de JWT, envío de emails de recuperación de contraseña y debug con `System.out.println`. Cada una de estas cosas tiene su propia razón para cambiar. | **SRP** |
| 6 | `entity/` (Producto, Sesion, User, etc.) | Las entidades JPA (con anotaciones `@Entity`, `@Table`, `@Column`) están siendo usadas directamente como modelos de dominio en los servicios. Esto significa que el dominio del negocio depende de un framework externo (JPA/Hibernate). Si se migra a MongoDB, hay que reescribir las entidades y también todo lo que las usa. | **DIP** |
| 7 | `configuration/` vs `config/` | Existen dos paquetes de configuración sin separación clara de responsabilidades. `configuration/` tiene `ConectarApi` y `EndPointConfg`, mientras que `config/` tiene `DataInitializer`. No hay criterio claro de por qué están separados. | **SRP** |
| 8 | `event/UserRegisteredEvent.java` | El patrón Observer ya está implementado usando el `ApplicationEventPublisher` de Spring, lo cual es correcto. Sin embargo, está ubicado en un paquete raíz `event/` cuando debería estar organizado entre `domain/event/` (el evento como POJO puro) y `application/event/` (el listener que reacciona). | **Organización** |

### 1.3 Estructura actual problemática (visualización)

```
com.project.sentimentapi/                    ← paquete raíz
│
├── config/                                  ← ¿configuración de qué?
│   └── DataInitializer.java
│
├── configuration/                           ← ¿distinto a config/?
│   ├── ConectarApi.java                     ← crea WebClient nuevo en cada llamada
│   └── EndPointConfg.java
│
├── controller/                              ← algunos tienen lógica de negocio
│   ├── CategoriaController.java
│   ├── CsvAnalysisController.java
│   ├── DebugController.java
│   ├── ProductoController.java
│   ├── SentimentApiController.java
│   ├── SesionController.java
│   └── UsuarioController.java
│
├── dto/                                     ← 16 DTOs sin separar requests de responses
│   ├── CategoriaDto.java
│   ├── ComentarioDto.java
│   ├── ComentariosRequestDto.java
│   ├── CsvAnalysisResponseDto.java
│   ├── CsvBatchRequestDto.java
│   ├── CsvEntradaDto.java
│   ├── CsvUploadRequestDto.java
│   ├── LoginResponseDto.java
│   ├── ProductoDto.java
│   ├── ProductoMencionesDto.java
│   ├── ProductoPrevioDto.java
│   ├── ProductoRequestDto.java
│   ├── ResponseDto.java
│   ├── SentimentsResponseDto.java
│   ├── SesionDto.java
│   ├── SesionPreviaInfoDto.java
│   ├── UserDto.java
│   ├── UserDtoLogin.java
│   └── UserDtoRegistro.java
│
├── entity/                                  ← entidades JPA usadas como modelos de dominio
│   ├── Categoria.java
│   ├── Comentario.java
│   ├── Producto.java
│   ├── Rol.java
│   ├── Sesion.java
│   ├── SesionProducto.java
│   └── User.java
│
├── event/                                   ← Observer existente pero mal ubicado
│   ├── UserRegisteredEvent.java
│   └── UserRegistrationListener.java
│
├── globalexceptionhandler/
│   └── ExecptionHandler.java               ← incluso el nombre tiene typo
│
├── repository/                              ← Spring Data JPA interfaces
│   ├── CategoriaRepository.java
│   ├── ComentarioRepository.java
│   ├── ProductoRepository.java
│   ├── RolRepository.java
│   ├── SesionProductoRepository.java
│   ├── SesionRepository.java
│   └── UserRepository.java
│
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
│
└── service/                                 ← interfaces e implementaciones mezcladas
    ├── CategoriaService.java
    ├── CategoriaServiceImplement.java       ← 3122 bytes, manejable
    ├── CsvAnalysisService.java
    ├── CsvAnalysisServiceImplement.java     ← 10200 bytes, demasiado grande
    ├── EmailService.java
    ├── EmailServiceImplement.java
    ├── ProductoService.java
    ├── ProductoServiceImplement.java        ← 6276 bytes
    ├── SentimentService.java
    ├── SentimentServiceImplement.java
    ├── SesionService.java
    ├── SesionServiceImplement.java          ← 37516 bytes — señal de alarma
    ├── UserService.java
    └── UserServiceImplement.java            ← 6862 bytes
```

> **Nota crítica:** `SesionServiceImplement.java` tiene 37KB. Una clase de servicio de ese tamaño es una señal inequívoca de que está haciendo demasiadas cosas. En Clean Architecture, un use case típico tiene entre 30 y 80 líneas.

---

## 2. ¿Qué es Clean Architecture?

Clean Architecture es un enfoque de diseño de software propuesto por Robert C. Martin (Uncle Bob) que organiza el código en capas concéntricas donde **las dependencias siempre apuntan hacia adentro**, hacia el dominio del negocio.

### 2.1 Las cuatro capas

```
┌─────────────────────────────────────────────────────┐
│                    presentation/                    │  ← Capa más externa
│              (Controllers, DTOs, Mappers)           │
│  ┌───────────────────────────────────────────────┐  │
│  │               application/                    │  │
│  │         (Use Cases, Builders, Mappers)        │  │
│  │  ┌─────────────────────────────────────────┐  │  │
│  │  │              domain/                    │  │  │
│  │  │   (Models, Port/in, Port/out, Events)   │  │  │
│  │  └─────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────┘  │
│                   infrastructure/                   │
│       (JPA Entities, Adapters, WebClient, Email)    │
└─────────────────────────────────────────────────────┘
                Las flechas de dependencia
                   apuntan hacia adentro →
```

La capa `infrastructure/` aunque está "fuera" en la representación de Clean Architecture, **implementa contratos definidos por el dominio**. Por eso sus flechas también apuntan hacia adentro.

### 2.2 El flujo de una request (cómo fluye la información)

Cuando el frontend sube un CSV para análisis, el flujo es:

```
1. HTTP POST /api/csv/analizar
        ↓
2. CsvAnalysisController (presentation)
   - Recibe el request DTO
   - Llama a AnalizarCsvUseCase (interfaz del dominio)
        ↓
3. AnalizarCsvUseCaseImpl (application)
   - Orquesta el proceso completo (FACADE)
   - Llama a SentimentAnalysisPort para el análisis
   - Llama a ProductoRepositoryPort para cruzar datos
   - Usa SesionBuilder para construir la sesión (BUILDER)
   - Llama a SesionRepositoryPort para guardar
        ↓
4a. SentimentApiAdapter (infrastructure/external)
    - Implementa SentimentAnalysisPort
    - Usa WebClient para llamar la API Python
        ↓
4b. ProductoRepositoryAdapter (infrastructure/persistence)
    - Implementa ProductoRepositoryPort
    - Usa ProductoJpaRepository (Spring Data)
        ↓
5. La respuesta sube de vuelta por las mismas capas
   infrastructure → application → presentation → HTTP Response
```

**Lo fundamental:** el use case (`AnalizarCsvUseCaseImpl`) nunca sabe si los productos están en MySQL, MongoDB o en memoria. Solo conoce la interfaz `ProductoRepositoryPort`. Eso es exactamente la inversión de dependencias.

### 2.3 ¿Qué cambia respecto al MVC actual?

| Aspecto | MVC en capas (actual) | Clean Architecture (objetivo) |
|---|---|---|
| Modelo de dominio | Entidades JPA con `@Entity` | POJOs puros sin anotaciones de framework |
| Acceso a base de datos | Services usan Repository directamente | Use cases usan interfaces (ports), los adapters usan JPA |
| Cambiar proveedor de IA | Hay que modificar `SentimentServiceImplement` | Solo se crea un nuevo Adapter que implementa `SentimentAnalysisPort` |
| Cambiar de MySQL a MongoDB | Afecta entities, repositories y services | Solo afecta `infrastructure/persistence/` |
| Testear lógica de negocio | Necesitas un contexto Spring completo | El dominio se testea con POJOs puros (JUnit sin Spring) |

---

## 3. Principios SOLID aplicados

### 3.1 SRP — Single Responsibility Principle
> *"Una clase debe tener una sola razón para cambiar."*

**Problema actual:** `CsvAnalysisServiceImplement` tiene 6 razones para cambiar:
- Si cambia el formato del CSV de entrada
- Si cambia la API de sentimientos
- Si cambia la fórmula de estadísticas
- Si cambia la estructura de la sesión
- Si cambia la lógica de productos
- Si cambia el formato de la respuesta

**Solución aplicada:** dividir en clases con una sola responsabilidad:

```
CsvAnalysisServiceImplement (actual, hace TODO)
    ↓ se divide en:
AnalizarCsvUseCaseImpl    → solo orquesta el flujo
SentimentApiAdapter       → solo llama la API externa
EstadisticasCalculator    → solo calcula positivos/negativos/neutrales
SesionBuilder             → solo construye el objeto Sesion
SentimentAnalysisPort     → solo define el contrato con la API
```

De igual forma, `UserServiceImplement` se divide en:

```
UserServiceImplement (actual, hace TODO)
    ↓ se divide en:
RegistrarUsuarioUseCaseImpl     → solo registra un usuario nuevo
AutenticarUsuarioUseCaseImpl    → solo valida credenciales y genera JWT
RecuperarContrasenaUseCaseImpl  → solo gestiona reset de contraseña
```

### 3.2 OCP — Open/Closed Principle
> *"Una entidad de software debe estar abierta para extensión, cerrada para modificación."*

**Dónde se aplica:** la interfaz `SentimentAnalysisPort` define el contrato para analizar sentimientos. Si el equipo cambia de proveedor de IA, **no se modifica ninguna clase existente**, solo se añade una nueva:

```java
// Contrato en el dominio (no cambia nunca)
// domain/port/out/SentimentAnalysisPort.java
public interface SentimentAnalysisPort {
    Optional<SentimentsResponseDto> analizarLote(List<String> textos);
}

// Implementación actual (no se modifica)
// infrastructure/external/PythonSentimentAdapter.java
@Component
public class PythonSentimentAdapter implements SentimentAnalysisPort {
    // usa WebClient para llamar la API Python actual
}

// Futura extensión (se AÑADE sin tocar lo existente)
// infrastructure/external/OpenAiSentimentAdapter.java
@Component
public class OpenAiSentimentAdapter implements SentimentAnalysisPort {
    // usa la API de OpenAI
}
```

Para cambiar de proveedor, solo se comenta `@Component` en uno y se descomenta en el otro (o se usa `@Primary` / `@Qualifier`). **Cero modificaciones en el use case.**

### 3.3 LSP — Liskov Substitution Principle
> *"Los objetos de una subclase deben poder sustituir a los de la clase base sin alterar el comportamiento correcto del programa."*

**Dónde se aplica:** todos los adapters de repositorio (`UsuarioRepositoryAdapter`, `ProductoRepositoryAdapter`, etc.) implementan sus respectivos ports. Para que LSP se cumpla, cada adapter debe:
- Retornar el mismo tipo que declara la interfaz (no un subtipo distinto)
- No lanzar excepciones que el contrato no declara (si el port dice `Optional<Usuario>`, no puede lanzar `EntityNotFoundException` directamente; debe convertirla o retornar `Optional.empty()`)
- No cambiar la semántica del método (si `buscarPorEmail` retorna vacío cuando no existe, todos los adapters deben hacer lo mismo, sin retornar `null` o lanzar excepción)

### 3.4 ISP — Interface Segregation Principle
> *"Los clientes no deben depender de interfaces que no usan."*

**Problema actual:** `SesionService` tiene métodos para guardar sesiones, consultar historial, calcular estadísticas y otras operaciones. Todos los controllers que necesitan algo de sesiones deben inyectar toda esa interfaz aunque solo usen un método.

**Solución aplicada:** separar en interfaces específicas:

```java
// ANTES (una interfaz gorda)
// service/SesionService.java
public interface SesionService {
    SesionDto guardarSesion(Sesion sesion);
    List<SesionDto> obtenerSesionesPorUsuario(Long usuarioId);
    SesionDto obtenerSesionPorId(Long sesionId);
    void eliminarSesion(Long sesionId);
    EstadisticasDto calcularEstadisticas(Long sesionId);
    // ... más métodos
}

// DESPUÉS (interfaces pequeñas y específicas)
// domain/port/in/GuardarSesionUseCase.java
public interface GuardarSesionUseCase {
    SesionDto guardar(Sesion sesion);
}

// domain/port/in/ConsultarSesionesUseCase.java
public interface ConsultarSesionesUseCase {
    List<SesionDto> obtenerPorUsuario(Long usuarioId);
    SesionDto obtenerPorId(Long sesionId);
}
```

Ahora `CsvAnalysisController` solo inyecta `GuardarSesionUseCase`, y `SesionController` solo inyecta `ConsultarSesionesUseCase`. Cada controller depende exactamente de lo que necesita, ni más ni menos.

### 3.5 DIP — Dependency Inversion Principle
> *"Los módulos de alto nivel no deben depender de módulos de bajo nivel. Ambos deben depender de abstracciones."*

Este es el principio más importante del proyecto y es la base de Clean Architecture.

**Antes (violación del DIP):**
```java
// SentimentServiceImplement depende de ConectarApi (clase concreta)
@Service
public class SentimentServiceImplement {
    @Autowired
    private ConectarApi conectarApi;  // ← dependencia de clase concreta
    
    // Si ConectarApi cambia, esta clase se rompe
}
```

**Después (DIP aplicado):**
```java
// AnalizarCsvUseCaseImpl depende de SentimentAnalysisPort (interfaz)
@Service
public class AnalizarCsvUseCaseImpl implements AnalizarCsvUseCase {
    
    private final SentimentAnalysisPort sentimentPort;  // ← abstracción
    
    // No sabe nada sobre WebClient, ConectarApi, ni la URL de la API Python
    // Si cambia el proveedor, esta clase NO se modifica
}
```

| Antes (MAL) | Después (BIEN) |
|---|---|
| `SentimentServiceImplement` → `ConectarApi` | `AnalizarCsvUseCaseImpl` → `SentimentAnalysisPort` |
| `SesionServiceImplement` → `ProductoRepository` | Use cases → `ProductoRepositoryPort` |
| Services → Entidades JPA (`@Entity`) | Use cases → Modelos de dominio (POJOs) |
| Controllers → `*ServiceImplement` | Controllers → interfaces `port/in/` |

---

## 4. Patrones de Diseño GOF aplicados

### 4.1 Patrón Facade (Estructural)
**Clasificación GOF:** Estructural  
**Archivo principal:** `application/usecase/AnalizarCsvUseCaseImpl.java`

**¿Qué problema resuelve?** El patrón Facade proporciona una interfaz simplificada a un conjunto de interfaces de un subsistema. En este caso, el proceso de analizar un CSV involucra llamar la API de sentimientos, cruzar con productos, construir una sesión, calcular estadísticas y guardar todo. El controller no necesita saber nada de eso.

**Estructura antes del Facade (controller conoce demasiado):**
```
CsvAnalysisController
    ├── llama SentimentService.analizarLote()
    ├── llama ProductoService.obtenerProductos()
    ├── llama SesionService.construirSesion()
    ├── calcula estadísticas inline
    └── llama SesionService.guardar()
```

**Estructura con Facade:**
```
CsvAnalysisController
    └── llama AnalizarCsvUseCase.analizar()  ← UNA sola llamada
            │
            └── AnalizarCsvUseCaseImpl (FACADE)
                    ├── SentimentAnalysisPort.analizarLote()
                    ├── ProductoRepositoryPort.obtenerActivos()
                    ├── SesionBuilder.build()
                    └── SesionRepositoryPort.guardar()
```

**Código del Facade:**
```java
// application/usecase/AnalizarCsvUseCaseImpl.java
@Service
public class AnalizarCsvUseCaseImpl implements AnalizarCsvUseCase {

    private final SentimentAnalysisPort sentimentPort;
    private final ProductoRepositoryPort productoPort;
    private final SesionRepositoryPort sesionPort;
    private final SesionBuilder sesionBuilder;

    // Constructor con inyección de dependencias
    public AnalizarCsvUseCaseImpl(
            SentimentAnalysisPort sentimentPort,
            ProductoRepositoryPort productoPort,
            SesionRepositoryPort sesionPort,
            SesionBuilder sesionBuilder) {
        this.sentimentPort = sentimentPort;
        this.productoPort = productoPort;
        this.sesionPort = sesionPort;
        this.sesionBuilder = sesionBuilder;
    }

    @Override
    public CsvAnalysisResponseDto analizar(List<CsvEntradaDto> filas, Long usuarioId) {
        // El controller llama esto y no le importa lo que pasa adentro
        
        // Paso 1: extraer textos del CSV
        List<String> textos = filas.stream()
                .map(CsvEntradaDto::getTexto)
                .collect(Collectors.toList());

        // Paso 2: llamar a la API de sentimientos
        SentimentsResponseDto sentiments = sentimentPort.analizarLote(textos)
                .orElseThrow(() -> new SentimentApiException("API no disponible"));

        // Paso 3: obtener productos activos para cruzar datos
        List<Producto> productos = productoPort.obtenerActivos();

        // Paso 4: calcular estadísticas
        int positivos = contarPorSentimiento(sentiments, "positivo");
        int negativos = contarPorSentimiento(sentiments, "negativo");
        int neutrales = contarPorSentimiento(sentiments, "neutral");
        double avgScore = calcularPromedio(sentiments);

        // Paso 5: construir la sesión con el Builder
        Sesion sesion = sesionBuilder
                .conUsuario(usuarioId)
                .conFecha(LocalDateTime.now())
                .conEstadisticas(positivos, negativos, neutrales, avgScore)
                .build();

        // Paso 6: guardar y retornar respuesta
        Sesion sesionGuardada = sesionPort.guardar(sesion);
        return SesionMapper.toResponseDto(sesionGuardada, sentiments);
    }
    
    // métodos privados de apoyo...
}
```

### 4.2 Patrón Adapter (Estructural)
**Clasificación GOF:** Estructural  
**Archivos:** `infrastructure/persistence/adapter/*.java` y `infrastructure/external/SentimentApiAdapter.java`

**¿Qué problema resuelve?** El patrón Adapter convierte la interfaz de una clase en otra interfaz que los clientes esperan. Permite que clases con interfaces incompatibles trabajen juntas.

En este proyecto hay dos tipos de Adapters:

**Adapter de repositorio** — convierte Spring Data JPA al contrato del dominio:

```
UsuarioRepositoryPort (dominio, interfaz limpia)
        ▲
        │ implementa
UsuarioRepositoryAdapter (infrastructure)
        │ usa internamente
UsuarioJpaRepository (Spring Data JPA)
        │ accede a
Base de datos MySQL
```

```java
// infrastructure/persistence/adapter/UsuarioRepositoryAdapter.java
@Component  // Spring lo registra como bean
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        // Convierte de JpaEntity (infraestructura) a modelo de dominio (puro)
        return jpaRepository.findByEmail(email)
                .map(entity -> new Usuario(
                        entity.getId(),
                        entity.getNombre(),
                        entity.getEmail(),
                        entity.getPassword()
                ));
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // Convierte de modelo de dominio (puro) a JpaEntity (infraestructura)
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setNombre(usuario.getNombre());
        entity.setEmail(usuario.getEmail());
        entity.setPassword(usuario.getPasswordHash());
        
        UsuarioJpaEntity guardado = jpaRepository.save(entity);
        
        // Convierte de vuelta a modelo de dominio
        return new Usuario(guardado.getId(), guardado.getNombre(),
                           guardado.getEmail(), guardado.getPassword());
    }
}
```

**Adapter de API externa** — convierte el WebClient al contrato del dominio:

```java
// infrastructure/external/SentimentApiAdapter.java
@Component
public class SentimentApiAdapter implements SentimentAnalysisPort {

    private final WebClient webClient;  // inyectado como @Bean singleton

    public SentimentApiAdapter(WebClient sentimentWebClient) {
        this.webClient = sentimentWebClient;
    }

    @Override
    public Optional<SentimentsResponseDto> analizarLote(List<String> textos) {
        try {
            SentimentsResponseDto respuesta = webClient.post()
                    .uri("/analizar")
                    .bodyValue(Map.of("textos", textos))
                    .retrieve()
                    .bodyToMono(SentimentsResponseDto.class)
                    .block();
            return Optional.ofNullable(respuesta);
        } catch (Exception e) {
            throw new SentimentApiException("Error al llamar API: " + e.getMessage());
        }
    }
}
```

**¿Por qué es importante este patrón para OCP?** Si mañana se cambia de la API Python a OpenAI, solo se crea `OpenAiSentimentAdapter implements SentimentAnalysisPort`. El `AnalizarCsvUseCaseImpl` no se toca. Eso es exactamente lo que dice el principio de Abierto/Cerrado.

### 4.3 Patrón Builder (Creacional)
**Clasificación GOF:** Creacional  
**Archivo:** `application/builder/SesionBuilder.java`

**¿Qué problema resuelve?** El patrón Builder separa la construcción de un objeto complejo de su representación, permitiendo construir el mismo tipo de objeto paso a paso.

**Problema actual:** en `CsvAnalysisServiceImplement` hay aproximadamente 30 líneas que construyen un objeto `Sesion` de forma dispersa, difícil de leer y propensa a errores (por ejemplo, olvidar setear un campo):

```java
// ANTES — construcción dispersa y frágil
Sesion sesion = new Sesion();
sesion.setUsuario(usuario);
sesion.setFecha(LocalDateTime.now());
sesion.setTotalComentarios(comentarios.size());
sesion.setPositivos(positivos);
sesion.setNegativos(negativos);
sesion.setNeutrales(neutrales);
sesion.setAvgScore(avgScore);
sesion.setActiva(true);
// ¿Se olvidó algún campo? Difícil saberlo.
```

**Después con Builder:**

```java
// application/builder/SesionBuilder.java
public class SesionBuilder {
    
    private Long usuarioId;
    private LocalDateTime fecha;
    private int positivos;
    private int negativos;
    private int neutrales;
    private double avgScore;
    private boolean activa = true;  // valor por defecto

    // Métodos encadenados (fluent interface)
    public SesionBuilder conUsuario(Long usuarioId) {
        this.usuarioId = usuarioId;
        return this;  // retorna this para encadenar
    }

    public SesionBuilder conFecha(LocalDateTime fecha) {
        this.fecha = fecha;
        return this;
    }

    public SesionBuilder conEstadisticas(int positivos, int negativos,
                                          int neutrales, double avgScore) {
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutrales = neutrales;
        this.avgScore = avgScore;
        return this;
    }

    public SesionBuilder activa(boolean activa) {
        this.activa = activa;
        return this;
    }

    public Sesion build() {
        // Validaciones antes de construir
        if (usuarioId == null) throw new IllegalStateException("usuarioId es requerido");
        if (fecha == null) this.fecha = LocalDateTime.now();
        
        return new Sesion(usuarioId, fecha, positivos, negativos, neutrales, avgScore, activa);
    }
}

// USO — claro, legible y extensible
Sesion sesion = new SesionBuilder()
        .conUsuario(usuarioId)
        .conFecha(LocalDateTime.now())
        .conEstadisticas(positivos, negativos, neutrales, avgScore)
        .activa(true)
        .build();
```

Si en el futuro se agrega un campo nuevo a `Sesion`, solo se agrega un método al Builder. Nada más cambia.

### 4.4 Patrón Observer (Comportamiento)
**Clasificación GOF:** Comportamiento  
**Archivos:** `domain/event/UserRegisteredEvent.java` + `application/event/UserRegistrationListener.java`

**¿Qué problema resuelve?** El patrón Observer define una dependencia de uno a muchos entre objetos, de modo que cuando un objeto cambia de estado, todos sus dependientes son notificados y actualizados automáticamente.

**Este patrón YA EXISTE en el código**, usando el `ApplicationEventPublisher` de Spring. Solo necesita reubicarse correctamente y verificarse tras el refactoring.

**Flujo del Observer en el proyecto:**

```
1. RegistrarUsuarioUseCaseImpl.registrar(dto)
   └── guarda el usuario en BD
   └── eventPublisher.publishEvent(new UserRegisteredEvent(email, nombre))
                                    ↓ Spring notifica automáticamente
2. UserRegistrationListener.onUserRegistered(event)
   └── emailPort.enviarBienvenida(event.getEmail(), event.getNombre())
                    ↓
3. EmailAdapter.enviarBienvenida(email, nombre)
   └── envía el email de bienvenida usando JavaMailSender
```

**El evento como POJO puro (en domain/):**

```java
// domain/event/UserRegisteredEvent.java
// NO extiende ApplicationEvent — es un POJO puro del dominio
public class UserRegisteredEvent {
    
    private final String email;
    private final String nombre;
    private final LocalDateTime timestamp;

    public UserRegisteredEvent(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
        this.timestamp = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

**El listener (en application/ — puede usar Spring):**

```java
// application/event/UserRegistrationListener.java
@Component
public class UserRegistrationListener {

    private final EmailPort emailPort;

    public UserRegistrationListener(EmailPort emailPort) {
        this.emailPort = emailPort;
    }

    @EventListener  // Spring intercepta cuando se publica un UserRegisteredEvent
    public void onUserRegistered(UserRegisteredEvent event) {
        emailPort.enviarBienvenida(event.getEmail(), event.getNombre());
    }
}
```

### 4.5 Patrón Singleton (Creacional)
**Clasificación GOF:** Creacional  
**Archivo:** `infrastructure/config/WebClientConfig.java` (refactor de `ConectarApi.java`)

**¿Qué problema resuelve?** El patrón Singleton garantiza que una clase tenga solo una instancia y proporciona un punto de acceso global a ella.

**Problema actual en `ConectarApi.java`:**

```java
// ACTUAL — crea un nuevo WebClient en cada llamada (¡costoso!)
@Component
public class ConectarApi {
    @Autowired
    private EndPointConfg config;
    
    public WebClient client() {
        // PROBLEMA: WebClient.builder() crea una instancia nueva cada vez
        return WebClient.builder()
                .baseUrl(config.getUrl())
                .build();
    }
}
```

**Solución — `@Bean` de Spring (Singleton gestionado por el framework):**

```java
// infrastructure/config/WebClientConfig.java
@Configuration
public class WebClientConfig {

    // @Bean en Spring es Singleton por defecto.
    // Spring crea UNA sola instancia y la reutiliza en todo el contexto.
    @Bean
    public WebClient sentimentWebClient(EndPointConfg config) {
        return WebClient.builder()
                .baseUrl(config.getUrl())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB max
                .build();
    }
}
```

Ahora cualquier clase que necesite el `WebClient` lo recibe por inyección de constructor, y Spring garantiza que es siempre la misma instancia:

```java
// En SentimentApiAdapter
@Component
public class SentimentApiAdapter implements SentimentAnalysisPort {
    
    private final WebClient webClient;
    
    // Spring inyecta el @Bean "sentimentWebClient" definido arriba
    public SentimentApiAdapter(WebClient sentimentWebClient) {
        this.webClient = sentimentWebClient;
    }
}
```

---

## 5. Nueva estructura de paquetes completa

> **Regla de lectura de esta sección:**
> - `← MOVIDO desde X` significa: tomar el archivo existente, cambiar solo el `package` declarado al inicio, ajustar los `import` que referencian el paquete viejo, y copiarlo a la nueva ubicación.
> - `← NUEVO` significa: crear desde cero.
> - `← REFACTOR de X` significa: la lógica viene del archivo existente pero cambia de nombre de clase y de estructura interna.

```
com.project.sentimentapi/
│
├── domain/                                                    ← NUEVO paquete raíz
│   │
│   ├── model/                                                 ← POJOs puros (CERO Spring, CERO JPA)
│   │   ├── Usuario.java                                       ← NUEVO (equivalente limpio de entity/User.java)
│   │   ├── Producto.java                                      ← NUEVO (equivalente limpio de entity/Producto.java)
│   │   ├── Sesion.java                                        ← NUEVO (equivalente limpio de entity/Sesion.java)
│   │   ├── Comentario.java                                    ← NUEVO (equivalente limpio de entity/Comentario.java)
│   │   ├── Categoria.java                                     ← NUEVO (equivalente limpio de entity/Categoria.java)
│   │   ├── SesionProducto.java                                ← NUEVO (equivalente limpio de entity/SesionProducto.java)
│   │   └── Rol.java                                           ← NUEVO (equivalente limpio de entity/Rol.java)
│   │
│   ├── port/
│   │   ├── in/                                                ← Interfaces de casos de uso (lo que puede hacer el sistema)
│   │   │   ├── RegistrarUsuarioUseCase.java                   ← NUEVO (extraído de UserService.java)
│   │   │   ├── AutenticarUsuarioUseCase.java                  ← NUEVO (extraído de UserService.java)
│   │   │   ├── RecuperarContrasenaUseCase.java                ← NUEVO (extraído de UserService.java)
│   │   │   ├── AnalizarCsvUseCase.java                        ← NUEVO (extraído de CsvAnalysisService.java)
│   │   │   ├── GuardarSesionUseCase.java                      ← NUEVO (extraído de SesionService.java — ISP)
│   │   │   ├── ConsultarSesionesUseCase.java                  ← NUEVO (extraído de SesionService.java — ISP)
│   │   │   ├── GestionarProductoUseCase.java                  ← NUEVO (extraído de ProductoService.java)
│   │   │   └── GestionarCategoriaUseCase.java                 ← NUEVO (extraído de CategoriaService.java)
│   │   │
│   │   └── out/                                               ← Interfaces hacia infraestructura (contratos que infra implementa)
│   │       ├── UsuarioRepositoryPort.java                     ← NUEVO (contrato que UsuarioRepositoryAdapter implementará)
│   │       ├── ProductoRepositoryPort.java                    ← NUEVO (contrato que ProductoRepositoryAdapter implementará)
│   │       ├── CategoriaRepositoryPort.java                   ← NUEVO
│   │       ├── SesionRepositoryPort.java                      ← NUEVO
│   │       ├── ComentarioRepositoryPort.java                  ← NUEVO
│   │       ├── SesionProductoRepositoryPort.java              ← NUEVO
│   │       ├── SentimentAnalysisPort.java                     ← NUEVO (contrato que SentimentApiAdapter implementará)
│   │       └── EmailPort.java                                 ← NUEVO (contrato que EmailAdapter implementará)
│   │
│   ├── event/
│   │   └── UserRegisteredEvent.java                           ← MOVIDO desde event/UserRegisteredEvent.java
│   │                                                            (convertido a POJO puro, sin extends ApplicationEvent)
│   └── exception/
│       ├── UsuarioNoEncontradoException.java                  ← NUEVO
│       ├── SentimentApiException.java                         ← NUEVO
│       └── TokenExpiradoException.java                        ← NUEVO
│
├── application/                                               ← NUEVO paquete raíz
│   │
│   ├── usecase/                                               ← Implementaciones de los casos de uso
│   │   ├── AnalizarCsvUseCaseImpl.java                        ← REFACTOR de CsvAnalysisServiceImplement.java (Patrón FACADE)
│   │   ├── RegistrarUsuarioUseCaseImpl.java                   ← REFACTOR de UserServiceImplement.java (método registrar)
│   │   ├── AutenticarUsuarioUseCaseImpl.java                  ← REFACTOR de UserServiceImplement.java (método login)
│   │   ├── RecuperarContrasenaUseCaseImpl.java                ← REFACTOR de UserServiceImplement.java (métodos forgot/reset)
│   │   ├── GuardarSesionUseCaseImpl.java                      ← REFACTOR de SesionServiceImplement.java (método guardar)
│   │   ├── ConsultarSesionesUseCaseImpl.java                  ← REFACTOR de SesionServiceImplement.java (métodos consultar)
│   │   ├── GestionarProductoUseCaseImpl.java                  ← MOVIDO desde ProductoServiceImplement.java
│   │   └── GestionarCategoriaUseCaseImpl.java                 ← MOVIDO desde CategoriaServiceImplement.java
│   │
│   ├── builder/
│   │   └── SesionBuilder.java                                 ← NUEVO (Patrón BUILDER, lógica extraída de CsvAnalysisServiceImplement)
│   │
│   ├── event/
│   │   └── UserRegistrationListener.java                      ← MOVIDO desde event/UserRegistrationListener.java
│   │
│   └── mapper/                                                ← Convierten entre modelo de dominio y DTOs
│       ├── UsuarioMapper.java                                  ← NUEVO
│       ├── ProductoMapper.java                                 ← NUEVO
│       ├── SesionMapper.java                                   ← NUEVO
│       └── CategoriaMapper.java                               ← NUEVO
│
├── infrastructure/                                            ← REORGANIZADO
│   │
│   ├── persistence/
│   │   │
│   │   ├── entity/                                            ← MOVIDO desde entity/ (conservan @Entity, @Table, etc.)
│   │   │   ├── UsuarioJpaEntity.java                          ← era entity/User.java (renombrado)
│   │   │   ├── ProductoJpaEntity.java                         ← era entity/Producto.java (renombrado)
│   │   │   ├── SesionJpaEntity.java                           ← era entity/Sesion.java (renombrado)
│   │   │   ├── ComentarioJpaEntity.java                       ← era entity/Comentario.java (renombrado)
│   │   │   ├── CategoriaJpaEntity.java                        ← era entity/Categoria.java (renombrado)
│   │   │   ├── SesionProductoJpaEntity.java                   ← era entity/SesionProducto.java (renombrado)
│   │   │   └── RolJpaEntity.java                              ← era entity/Rol.java (renombrado)
│   │   │
│   │   ├── repository/                                        ← MOVIDO desde repository/ (interfaces Spring Data JPA)
│   │   │   ├── UsuarioJpaRepository.java                      ← era repository/UserRepository.java (renombrado)
│   │   │   ├── ProductoJpaRepository.java                     ← era repository/ProductoRepository.java (renombrado)
│   │   │   ├── SesionJpaRepository.java                       ← era repository/SesionRepository.java (renombrado)
│   │   │   ├── CategoriaJpaRepository.java                    ← era repository/CategoriaRepository.java (renombrado)
│   │   │   ├── ComentarioJpaRepository.java                   ← era repository/ComentarioRepository.java (renombrado)
│   │   │   ├── SesionProductoJpaRepository.java               ← era repository/SesionProductoRepository.java (renombrado)
│   │   │   └── RolJpaRepository.java                          ← era repository/RolRepository.java (renombrado)
│   │   │
│   │   └── adapter/                                           ← NUEVO (Patrón ADAPTER — implementan los ports del dominio)
│   │       ├── UsuarioRepositoryAdapter.java                  ← NUEVO (implementa UsuarioRepositoryPort)
│   │       ├── ProductoRepositoryAdapter.java                 ← NUEVO (implementa ProductoRepositoryPort)
│   │       ├── CategoriaRepositoryAdapter.java                ← NUEVO (implementa CategoriaRepositoryPort)
│   │       ├── SesionRepositoryAdapter.java                   ← NUEVO (implementa SesionRepositoryPort)
│   │       ├── ComentarioRepositoryAdapter.java               ← NUEVO (implementa ComentarioRepositoryPort)
│   │       └── SesionProductoRepositoryAdapter.java           ← NUEVO (implementa SesionProductoRepositoryPort)
│   │
│   ├── external/
│   │   └── SentimentApiAdapter.java                           ← REFACTOR de SentimentServiceImplement.java (Patrón ADAPTER)
│   │
│   ├── email/
│   │   └── EmailAdapter.java                                  ← REFACTOR de EmailServiceImplement.java
│   │
│   ├── security/                                              ← SIN CAMBIOS de lógica (solo ajustar package)
│   │   ├── JwtUtil.java                                       ← era security/JwtUtil.java
│   │   ├── JwtAuthenticationFilter.java                       ← era security/JwtAuthenticationFilter.java
│   │   └── SecurityConfig.java                                ← era security/SecurityConfig.java
│   │
│   └── config/
│       ├── WebClientConfig.java                               ← REFACTOR de configuration/ConectarApi.java (Patrón SINGLETON)
│       ├── EndPointConfg.java                                 ← MOVIDO desde configuration/EndPointConfg.java
│       └── DataInitializer.java                               ← MOVIDO desde config/DataInitializer.java
│
├── presentation/                                              ← REORGANIZADO
│   │
│   ├── controller/                                            ← MOVIDO desde controller/ (ajustar imports)
│   │   ├── UsuarioController.java                             ← era controller/UsuarioController.java
│   │   ├── SesionController.java                              ← era controller/SesionController.java
│   │   ├── ProductoController.java                            ← era controller/ProductoController.java
│   │   ├── CategoriaController.java                           ← era controller/CategoriaController.java
│   │   ├── CsvAnalysisController.java                         ← era controller/CsvAnalysisController.java
│   │   ├── SentimentApiController.java                        ← era controller/SentimentApiController.java
│   │   └── DebugController.java                               ← era controller/DebugController.java
│   │
│   ├── dto/
│   │   ├── request/                                           ← DTOs de ENTRADA (lo que el cliente envía)
│   │   │   ├── LoginRequestDto.java                           ← era dto/UserDtoLogin.java
│   │   │   ├── RegistroRequestDto.java                        ← era dto/UserDtoRegistro.java
│   │   │   ├── CsvUploadRequestDto.java                       ← mismo nombre
│   │   │   ├── CsvBatchRequestDto.java                        ← mismo nombre
│   │   │   ├── CsvEntradaDto.java                             ← mismo nombre
│   │   │   ├── ComentariosRequestDto.java                     ← mismo nombre
│   │   │   └── ProductoRequestDto.java                        ← mismo nombre
│   │   │
│   │   └── response/                                          ← DTOs de SALIDA (lo que el servidor retorna)
│   │       ├── LoginResponseDto.java                          ← mismo nombre
│   │       ├── UserDto.java                                   ← mismo nombre
│   │       ├── CategoriaDto.java                              ← mismo nombre
│   │       ├── ComentarioDto.java                             ← mismo nombre
│   │       ├── ProductoDto.java                               ← mismo nombre
│   │       ├── ProductoMencionesDto.java                      ← mismo nombre
│   │       ├── ProductoPrevioDto.java                         ← mismo nombre
│   │       ├── ResponseDto.java                               ← mismo nombre
│   │       ├── SentimentsResponseDto.java                     ← mismo nombre
│   │       ├── SesionDto.java                                 ← mismo nombre
│   │       ├── SesionPreviaInfoDto.java                       ← mismo nombre
│   │       └── CsvAnalysisResponseDto.java                    ← mismo nombre
│   │
│   └── exception/
│       └── GlobalExceptionHandler.java                        ← MOVIDO desde globalexceptionhandler/ExecptionHandler.java
│                                                                (+ corrección del typo en el nombre)
│
└── SentimentapiApplication.java                              ← SIN CAMBIOS (entry point)
```

---

## 6. Integrante 1 — `domain/` + `application/`

**Principios SOLID a defender:** SRP y DIP  
**Patrones GOF a defender:** Facade (AnalizarCsvUseCaseImpl) + Builder (SesionBuilder)  
**Regla de oro:** ningún archivo creado por este integrante puede tener `import org.springframework.*` en el paquete `domain/`. En `application/` sí se permite `@Service` y `@Component`.

---

### Paso 1 — Crear los modelos de dominio en `domain/model/`

Estos son POJOs (Plain Old Java Objects) completamente puros. No llevan `@Entity`, no llevan `@Id` de JPA, no llevan `@Data` de Lombok si viene de una dependencia externa. Son clases Java puras con sus atributos, constructor, getters y setters.

**`domain/model/Usuario.java`**
```java
package com.project.sentimentapi.domain.model;

public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String rol;

    // Constructor vacío (requerido por algunos frameworks internamente)
    public Usuario() {}

    // Constructor completo
    public Usuario(Long id, String nombre, String email,
                   String passwordHash, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    // Constructor sin id (para creación de nuevos usuarios)
    public Usuario(String nombre, String email, String passwordHash, String rol) {
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
```

> **¿Por qué hacemos esto?** La entidad JPA `User.java` tiene anotaciones de Hibernate que la atan al framework de persistencia. Si el dominio usa directamente esa clase, el dominio depende de JPA. Con este POJO, el dominio es completamente independiente. La conversión entre `Usuario` (dominio) y `UsuarioJpaEntity` (infraestructura) la hace el Adapter.

Crear de forma similar los siguientes modelos de dominio, extrayendo solo los campos relevantes para el negocio (sin las anotaciones JPA):

**`domain/model/Producto.java`** — campos: `id`, `nombre`, `descripcion`, `categoriaId`, `activo`, `menciones`, `totalSentimientoPositivo`, `totalSentimientoNegativo`, `totalSentimientoNeutral`.

**`domain/model/Sesion.java`** — campos: `id`, `usuarioId`, `fecha`, `totalComentarios`, `positivos`, `negativos`, `neutrales`, `avgScore`, `activa`.

**`domain/model/Comentario.java`** — campos: `id`, `texto`, `sentimiento`, `score`, `productoId`, `sesionId`.

**`domain/model/Categoria.java`** — campos: `id`, `nombre`.

**`domain/model/Rol.java`** — campos: `id`, `nombre`.

---

### Paso 2 — Crear las interfaces de entrada `domain/port/in/`

Estas interfaces definen el **contrato** de lo que el sistema puede hacer. Son las interfaces que los controllers van a inyectar. No tienen implementación, solo la firma del método.

**`domain/port/in/AnalizarCsvUseCase.java`**
```java
package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.request.CsvEntradaDto;
import com.project.sentimentapi.presentation.dto.response.CsvAnalysisResponseDto;
import java.util.List;

public interface AnalizarCsvUseCase {
    // El controller llama este método con la lista de filas del CSV y el ID del usuario autenticado
    CsvAnalysisResponseDto analizar(List<CsvEntradaDto> filas, Long usuarioId);
}
```

**`domain/port/in/RegistrarUsuarioUseCase.java`**
```java
package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.request.RegistroRequestDto;
import com.project.sentimentapi.presentation.dto.response.UserDto;

public interface RegistrarUsuarioUseCase {
    UserDto registrar(RegistroRequestDto request);
}
```

**`domain/port/in/AutenticarUsuarioUseCase.java`**
```java
package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.request.LoginRequestDto;
import com.project.sentimentapi.presentation.dto.response.LoginResponseDto;

public interface AutenticarUsuarioUseCase {
    LoginResponseDto autenticar(LoginRequestDto request);
}
```

**`domain/port/in/GuardarSesionUseCase.java`** — ISP: separado de ConsultarSesiones
```java
package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.presentation.dto.response.SesionDto;

public interface GuardarSesionUseCase {
    SesionDto guardar(Sesion sesion);
}
```

**`domain/port/in/ConsultarSesionesUseCase.java`**
```java
package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.response.SesionDto;
import java.util.List;

public interface ConsultarSesionesUseCase {
    List<SesionDto> obtenerPorUsuario(Long usuarioId);
    SesionDto obtenerPorId(Long sesionId);
}
```

Crear de forma similar: `RecuperarContrasenaUseCase`, `GestionarProductoUseCase`, `GestionarCategoriaUseCase`.

---

### Paso 3 — Crear las interfaces de salida `domain/port/out/`

Estas interfaces son los contratos que el dominio define para acceder a recursos externos. La infraestructura (adaptadores) las implementa.

**`domain/port/out/UsuarioRepositoryPort.java`**
```java
package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(Long id);
    boolean existePorEmail(String email);
    Usuario guardar(Usuario usuario);
}
```

**`domain/port/out/ProductoRepositoryPort.java`**
```java
package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {
    List<Producto> obtenerActivos();
    Optional<Producto> buscarPorId(Long id);
    Optional<Producto> buscarPorNombre(String nombre);
    Producto guardar(Producto producto);
    List<Producto> guardarTodos(List<Producto> productos);
}
```

**`domain/port/out/SentimentAnalysisPort.java`**
```java
package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;
import java.util.List;
import java.util.Optional;

public interface SentimentAnalysisPort {
    // Envía una lista de textos y recibe el análisis de sentimiento de cada uno
    Optional<SentimentsResponseDto> analizarLote(List<String> textos);
}
```

**`domain/port/out/EmailPort.java`**
```java
package com.project.sentimentapi.domain.port.out;

public interface EmailPort {
    void enviarBienvenida(String destinatario, String nombre);
    void enviarResetPassword(String destinatario, String token, String urlReset);
}
```

Crear de forma similar: `SesionRepositoryPort`, `CategoriaRepositoryPort`, `ComentarioRepositoryPort`, `SesionProductoRepositoryPort`.

---

### Paso 4 — Crear las excepciones de dominio en `domain/exception/`

Las excepciones de dominio representan situaciones de error propias del negocio (no errores técnicos). El `GlobalExceptionHandler` de presentación las captura y las convierte a respuestas HTTP.

**`domain/exception/UsuarioNoEncontradoException.java`**
```java
package com.project.sentimentapi.domain.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    private final String email;

    public UsuarioNoEncontradoException(String email) {
        super("No se encontró usuario con email: " + email);
        this.email = email;
    }

    public String getEmail() { return email; }
}
```

**`domain/exception/SentimentApiException.java`**
```java
package com.project.sentimentapi.domain.exception;

public class SentimentApiException extends RuntimeException {

    public SentimentApiException(String mensaje) {
        super(mensaje);
    }

    public SentimentApiException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
```

**`domain/exception/TokenExpiradoException.java`**
```java
package com.project.sentimentapi.domain.exception;

public class TokenExpiradoException extends RuntimeException {

    public TokenExpiradoException() {
        super("El token de recuperación ha expirado o es inválido");
    }
}
```

---

### Paso 5 — Mover `UserRegisteredEvent` a `domain/event/`

Tomar `event/UserRegisteredEvent.java` y convertirlo en un POJO puro. El evento de dominio no debería extender `ApplicationEvent` de Spring (eso lo ata a Spring). Spring puede publicar cualquier objeto como evento.

**`domain/event/UserRegisteredEvent.java`**
```java
package com.project.sentimentapi.domain.event;

import java.time.LocalDateTime;

// POJO puro — sin imports de Spring
public class UserRegisteredEvent {

    private final String email;
    private final String nombre;
    private final LocalDateTime ocurrioEn;

    public UserRegisteredEvent(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
        this.ocurrioEn = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public LocalDateTime getOcurrioEn() { return ocurrioEn; }
}
```

---

### Paso 6 — Implementar el Patrón Facade: `AnalizarCsvUseCaseImpl`

Este es el archivo más importante del Integrante 1. Extrae toda la lógica dispersa en `CsvAnalysisServiceImplement` y la organiza en un flujo claro. Este use case es el **Facade**: el controller llama un solo método y este coordina todo internamente.

**`application/usecase/AnalizarCsvUseCaseImpl.java`**
```java
package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.application.builder.SesionBuilder;
import com.project.sentimentapi.domain.exception.SentimentApiException;
import com.project.sentimentapi.domain.model.Producto;
import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.domain.port.in.AnalizarCsvUseCase;
import com.project.sentimentapi.domain.port.out.ProductoRepositoryPort;
import com.project.sentimentapi.domain.port.out.SentimentAnalysisPort;
import com.project.sentimentapi.domain.port.out.SesionRepositoryPort;
import com.project.sentimentapi.presentation.dto.request.CsvEntradaDto;
import com.project.sentimentapi.presentation.dto.response.CsvAnalysisResponseDto;
import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalizarCsvUseCaseImpl implements AnalizarCsvUseCase {

    // Todas las dependencias son INTERFACES (DIP aplicado)
    private final SentimentAnalysisPort sentimentPort;
    private final ProductoRepositoryPort productoPort;
    private final SesionRepositoryPort sesionPort;
    private final SesionBuilder sesionBuilder;

    // Inyección por constructor (mejor práctica — permite tests sin Spring)
    public AnalizarCsvUseCaseImpl(
            SentimentAnalysisPort sentimentPort,
            ProductoRepositoryPort productoPort,
            SesionRepositoryPort sesionPort,
            SesionBuilder sesionBuilder) {
        this.sentimentPort = sentimentPort;
        this.productoPort = productoPort;
        this.sesionPort = sesionPort;
        this.sesionBuilder = sesionBuilder;
    }

    @Override
    public CsvAnalysisResponseDto analizar(List<CsvEntradaDto> filas, Long usuarioId) {
        // --- PASO 1: Extraer los textos del CSV ---
        List<String> textos = filas.stream()
                .map(CsvEntradaDto::getTexto)
                .filter(texto -> texto != null && !texto.isBlank())
                .collect(Collectors.toList());

        if (textos.isEmpty()) {
            throw new IllegalArgumentException("El CSV no contiene textos válidos");
        }

        // --- PASO 2: Llamar a la API de sentimientos (FACADE llama al port) ---
        // El use case no sabe si es una API Python, OpenAI, o cualquier otra.
        // Solo conoce la interfaz SentimentAnalysisPort.
        SentimentsResponseDto sentiments = sentimentPort.analizarLote(textos)
                .orElseThrow(() -> new SentimentApiException(
                        "El servicio de análisis de sentimientos no respondió"));

        // --- PASO 3: Obtener productos para cruzar menciones ---
        List<Producto> productos = productoPort.obtenerActivos();

        // --- PASO 4: Calcular estadísticas (responsabilidad separada) ---
        int positivos = contarPorSentimiento(sentiments, "positivo");
        int negativos = contarPorSentimiento(sentiments, "negativo");
        int neutrales = contarPorSentimiento(sentiments, "neutral");
        double avgScore = calcularPromedio(sentiments);
        int total = textos.size();

        // --- PASO 5: Construir la sesión con el Builder (Patrón BUILDER) ---
        Sesion sesion = new SesionBuilder()
                .conUsuario(usuarioId)
                .conFecha(LocalDateTime.now())
                .conTotalComentarios(total)
                .conEstadisticas(positivos, negativos, neutrales, avgScore)
                .activa(true)
                .build();

        // --- PASO 6: Guardar la sesión ---
        Sesion sesionGuardada = sesionPort.guardar(sesion);

        // --- PASO 7: Armar y retornar la respuesta ---
        return construirRespuesta(sesionGuardada, sentiments, productos);
    }

    // Métodos privados de apoyo (una responsabilidad interna cada uno)
    
    private int contarPorSentimiento(SentimentsResponseDto sentiments, String tipo) {
        if (sentiments.getResultados() == null) return 0;
        return (int) sentiments.getResultados().stream()
                .filter(r -> tipo.equalsIgnoreCase(r.getSentimiento()))
                .count();
    }

    private double calcularPromedio(SentimentsResponseDto sentiments) {
        if (sentiments.getResultados() == null || sentiments.getResultados().isEmpty()) return 0.0;
        return sentiments.getResultados().stream()
                .mapToDouble(r -> r.getScore() != null ? r.getScore() : 0.0)
                .average()
                .orElse(0.0);
    }

    private CsvAnalysisResponseDto construirRespuesta(Sesion sesion,
                                                        SentimentsResponseDto sentiments,
                                                        List<Producto> productos) {
        CsvAnalysisResponseDto respuesta = new CsvAnalysisResponseDto();
        respuesta.setSesionId(sesion.getId());
        respuesta.setFecha(sesion.getFecha());
        respuesta.setTotalComentarios(sesion.getTotalComentarios());
        respuesta.setPositivos(sesion.getPositivos());
        respuesta.setNegativos(sesion.getNegativos());
        respuesta.setNeutrales(sesion.getNeutrales());
        respuesta.setAvgScore(sesion.getAvgScore());
        respuesta.setResultados(sentiments.getResultados());
        return respuesta;
    }
}
```

---

### Paso 7 — Implementar el Patrón Builder: `SesionBuilder`

**`application/builder/SesionBuilder.java`**
```java
package com.project.sentimentapi.application.builder;

import com.project.sentimentapi.domain.model.Sesion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component  // Spring lo gestiona para que se pueda inyectar
public class SesionBuilder {

    // Atributos del objeto que se está construyendo
    private Long usuarioId;
    private LocalDateTime fecha;
    private int totalComentarios;
    private int positivos;
    private int negativos;
    private int neutrales;
    private double avgScore;
    private boolean activa;

    // Método para reiniciar el builder (útil si se reutiliza la misma instancia)
    public SesionBuilder reset() {
        this.usuarioId = null;
        this.fecha = null;
        this.totalComentarios = 0;
        this.positivos = 0;
        this.negativos = 0;
        this.neutrales = 0;
        this.avgScore = 0.0;
        this.activa = true;
        return this;
    }

    // Métodos encadenados — cada uno retorna 'this' para permitir el fluent API
    public SesionBuilder conUsuario(Long usuarioId) {
        this.usuarioId = usuarioId;
        return this;
    }

    public SesionBuilder conFecha(LocalDateTime fecha) {
        this.fecha = fecha;
        return this;
    }

    public SesionBuilder conTotalComentarios(int total) {
        this.totalComentarios = total;
        return this;
    }

    public SesionBuilder conEstadisticas(int positivos, int negativos,
                                          int neutrales, double avgScore) {
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutrales = neutrales;
        this.avgScore = avgScore;
        return this;
    }

    public SesionBuilder activa(boolean activa) {
        this.activa = activa;
        return this;
    }

    // Método final — valida y construye el objeto
    public Sesion build() {
        // Validaciones de campos obligatorios
        if (usuarioId == null) {
            throw new IllegalStateException("SesionBuilder: usuarioId es obligatorio");
        }
        // Valor por defecto si no se especificó fecha
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }

        return new Sesion(usuarioId, fecha, totalComentarios,
                          positivos, negativos, neutrales, avgScore, activa);
    }
}
```

---

### Paso 8 — Implementar los demás use cases

**`application/usecase/RegistrarUsuarioUseCaseImpl.java`**
```java
package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.domain.exception.UsuarioNoEncontradoException;
import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.domain.port.in.RegistrarUsuarioUseCase;
import com.project.sentimentapi.domain.port.out.UsuarioRepositoryPort;
import com.project.sentimentapi.domain.event.UserRegisteredEvent;
import com.project.sentimentapi.presentation.dto.request.RegistroRequestDto;
import com.project.sentimentapi.presentation.dto.response.UserDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioPort;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;  // Observer: publica eventos

    public RegistrarUsuarioUseCaseImpl(UsuarioRepositoryPort usuarioPort,
                                        PasswordEncoder passwordEncoder,
                                        ApplicationEventPublisher eventPublisher) {
        this.usuarioPort = usuarioPort;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UserDto registrar(RegistroRequestDto request) {
        // Validar que el email no esté en uso
        if (usuarioPort.existePorEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con el email: " + request.getEmail());
        }

        // Crear el modelo de dominio (sin anotaciones JPA)
        Usuario nuevoUsuario = new Usuario(
                request.getNombre(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                "ROLE_USER"
        );

        // Guardar a través del port (no sabe qué base de datos hay detrás)
        Usuario guardado = usuarioPort.guardar(nuevoUsuario);

        // Publicar evento (Patrón Observer) — el listener enviará el email de bienvenida
        eventPublisher.publishEvent(new UserRegisteredEvent(guardado.getEmail(), guardado.getNombre()));

        // Retornar DTO de respuesta
        return new UserDto(guardado.getId(), guardado.getNombre(), guardado.getEmail());
    }
}
```

---

### Paso 9 — Crear los mappers en `application/mapper/`

Los mappers convierten entre modelos de dominio y DTOs. No tienen lógica de negocio.

**`application/mapper/UsuarioMapper.java`**
```java
package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.presentation.dto.response.UserDto;

public class UsuarioMapper {

    // Clase utilitaria — sin instanciar
    private UsuarioMapper() {}

    public static UserDto toDto(Usuario usuario) {
        if (usuario == null) return null;
        return new UserDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );
    }
}
```

**`application/mapper/SesionMapper.java`**
```java
package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.presentation.dto.response.SesionDto;

public class SesionMapper {

    private SesionMapper() {}

    public static SesionDto toDto(Sesion sesion) {
        if (sesion == null) return null;
        SesionDto dto = new SesionDto();
        dto.setId(sesion.getId());
        dto.setFecha(sesion.getFecha());
        dto.setTotalComentarios(sesion.getTotalComentarios());
        dto.setPositivos(sesion.getPositivos());
        dto.setNegativos(sesion.getNegativos());
        dto.setNeutrales(sesion.getNeutrales());
        dto.setAvgScore(sesion.getAvgScore());
        return dto;
    }
}
```

Crear de forma similar: `ProductoMapper.java`, `CategoriaMapper.java`.

---

### Paso 10 — Mover `UserRegistrationListener` a `application/event/`

**`application/event/UserRegistrationListener.java`**
```java
package com.project.sentimentapi.application.event;

import com.project.sentimentapi.domain.event.UserRegisteredEvent;
import com.project.sentimentapi.domain.port.out.EmailPort;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationListener {

    private final EmailPort emailPort;  // interfaz del dominio, no EmailServiceImplement

    public UserRegistrationListener(EmailPort emailPort) {
        this.emailPort = emailPort;
    }

    // Spring intercepta cuando se publica un UserRegisteredEvent (Patrón Observer)
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        emailPort.enviarBienvenida(event.getEmail(), event.getNombre());
    }
}
```

---

## 7. Integrante 2 — `infrastructure/`

**Principio SOLID a defender:** OCP  
**Patrones GOF a defender:** Adapter (repositorios + API externa) + Singleton (WebClient)  
**Regla de oro:** ningún archivo de `domain/` ni `application/` debe importar clases de `infrastructure/`. La dirección es siempre: infra depende de dominio, nunca al revés.

---

### Paso 1 — Renombrar y mover las entidades JPA a `infrastructure/persistence/entity/`

Tomar cada archivo de `entity/` y:
1. Moverlo a `infrastructure/persistence/entity/`
2. Renombrar la clase con sufijo `JpaEntity`
3. Cambiar la declaración `package` al inicio del archivo
4. El contenido interno (`@Entity`, `@Table`, `@Column`, `@OneToMany`, etc.) permanece exactamente igual

**`infrastructure/persistence/entity/UsuarioJpaEntity.java`**
```java
package com.project.sentimentapi.infrastructure.persistence.entity;

// Solo cambia el package — el resto es igual que entity/User.java
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<RolJpaEntity> roles;

    // Getters y setters (igual que antes)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<RolJpaEntity> getRoles() { return roles; }
    public void setRoles(Set<RolJpaEntity> roles) { this.roles = roles; }
}
```

Hacer lo mismo para:
- `entity/Producto.java` → `ProductoJpaEntity.java`
- `entity/Sesion.java` → `SesionJpaEntity.java`
- `entity/Comentario.java` → `ComentarioJpaEntity.java`
- `entity/Categoria.java` → `CategoriaJpaEntity.java`
- `entity/SesionProducto.java` → `SesionProductoJpaEntity.java`
- `entity/Rol.java` → `RolJpaEntity.java`

---

### Paso 2 — Mover repositorios Spring Data a `infrastructure/persistence/repository/`

Igual que el paso anterior: mover, renombrar, cambiar package. La interfaz que extiende `JpaRepository` y los métodos `findBy*` permanecen intactos. Solo cambiar las referencias a las entidades al nuevo nombre.

**`infrastructure/persistence/repository/UsuarioJpaRepository.java`**
```java
package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {
    // Mismo contenido que UserRepository.java, solo renombrando UsuarioJpaEntity
    Optional<UsuarioJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UsuarioJpaEntity> findByResetPasswordToken(String token);
}
```

---

### Paso 3 — Crear los Adapters de repositorio en `infrastructure/persistence/adapter/`

Este es el núcleo del trabajo de Integrante 2. Los adapters son el puente entre el dominio (que usa interfaces limpias) y la infraestructura (que usa JPA).

**`infrastructure/persistence/adapter/UsuarioRepositoryAdapter.java`**
```java
package com.project.sentimentapi.infrastructure.persistence.adapter;

import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.domain.port.out.UsuarioRepositoryPort;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component  // Spring registra este bean — cuando alguien pide UsuarioRepositoryPort, Spring inyecta esto
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::entityToDomain);  // convierte JpaEntity → modelo de dominio
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(this::entityToDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioJpaEntity entity = domainToEntity(usuario);
        UsuarioJpaEntity guardado = jpaRepository.save(entity);
        return entityToDomain(guardado);
    }

    // --- Métodos de conversión privados ---

    // De infraestructura (JPA) → dominio (POJO puro)
    private Usuario entityToDomain(UsuarioJpaEntity entity) {
        String rol = entity.getRoles() != null && !entity.getRoles().isEmpty()
                ? entity.getRoles().iterator().next().getNombre()
                : "ROLE_USER";
        return new Usuario(entity.getId(), entity.getNombre(),
                           entity.getEmail(), entity.getPassword(), rol);
    }

    // De dominio (POJO puro) → infraestructura (JPA)
    private UsuarioJpaEntity domainToEntity(Usuario usuario) {
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        if (usuario.getId() != null) entity.setId(usuario.getId());
        entity.setNombre(usuario.getNombre());
        entity.setEmail(usuario.getEmail());
        entity.setPassword(usuario.getPasswordHash());
        return entity;
    }
}
```

**`infrastructure/persistence/adapter/ProductoRepositoryAdapter.java`**
```java
package com.project.sentimentapi.infrastructure.persistence.adapter;

import com.project.sentimentapi.domain.model.Producto;
import com.project.sentimentapi.domain.port.out.ProductoRepositoryPort;
import com.project.sentimentapi.infrastructure.persistence.entity.ProductoJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.ProductoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository jpaRepository;

    public ProductoRepositoryAdapter(ProductoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Producto> obtenerActivos() {
        return jpaRepository.findByActivoTrue()
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::entityToDomain);
    }

    @Override
    public Optional<Producto> buscarPorNombre(String nombre) {
        return jpaRepository.findByNombreIgnoreCase(nombre).map(this::entityToDomain);
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoJpaEntity entity = domainToEntity(producto);
        return entityToDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Producto> guardarTodos(List<Producto> productos) {
        List<ProductoJpaEntity> entities = productos.stream()
                .map(this::domainToEntity)
                .collect(Collectors.toList());
        return jpaRepository.saveAll(entities)
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    private Producto entityToDomain(ProductoJpaEntity entity) {
        return new Producto(entity.getId(), entity.getNombre(),
                            entity.getDescripcion(), entity.getCategoriaId(),
                            entity.isActivo());
    }

    private ProductoJpaEntity domainToEntity(Producto producto) {
        ProductoJpaEntity entity = new ProductoJpaEntity();
        if (producto.getId() != null) entity.setId(producto.getId());
        entity.setNombre(producto.getNombre());
        entity.setDescripcion(producto.getDescripcion());
        entity.setActivo(producto.isActivo());
        return entity;
    }
}
```

Crear de forma similar:
- `SesionRepositoryAdapter.java` — implementa `SesionRepositoryPort`
- `CategoriaRepositoryAdapter.java` — implementa `CategoriaRepositoryPort`
- `ComentarioRepositoryAdapter.java` — implementa `ComentarioRepositoryPort`
- `SesionProductoRepositoryAdapter.java` — implementa `SesionProductoRepositoryPort`

---

### Paso 4 — Crear `SentimentApiAdapter` en `infrastructure/external/` (Patrón Adapter)

Extrae la lógica de `SentimentServiceImplement.java` y la convierte en un adapter.

**`infrastructure/external/SentimentApiAdapter.java`**
```java
package com.project.sentimentapi.infrastructure.external;

import com.project.sentimentapi.domain.exception.SentimentApiException;
import com.project.sentimentapi.domain.port.out.SentimentAnalysisPort;
import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SentimentApiAdapter implements SentimentAnalysisPort {

    // Se inyecta el @Bean singleton definido en WebClientConfig
    private final WebClient webClient;

    public SentimentApiAdapter(WebClient sentimentWebClient) {
        this.webClient = sentimentWebClient;
    }

    @Override
    public Optional<SentimentsResponseDto> analizarLote(List<String> textos) {
        try {
            SentimentsResponseDto respuesta = webClient.post()
                    .uri("/analizar-lote")
                    .bodyValue(Map.of("textos", textos))
                    .retrieve()
                    .bodyToMono(SentimentsResponseDto.class)
                    .block();  // bloqueante — en proyectos reactivos usar .subscribe()

            return Optional.ofNullable(respuesta);

        } catch (WebClientResponseException e) {
            // La API respondió con un error HTTP
            throw new SentimentApiException(
                    "La API de sentimientos respondió con error " + e.getStatusCode()
                    + ": " + e.getResponseBodyAsString(), e);

        } catch (Exception e) {
            // Error de red, timeout, etc.
            throw new SentimentApiException(
                    "No se pudo conectar con la API de sentimientos: " + e.getMessage(), e);
        }
    }
}
```

---

### Paso 5 — Crear `WebClientConfig` (Patrón Singleton)

Reemplaza completamente a `ConectarApi.java`. Eliminar `ConectarApi.java` después de esto.

**`infrastructure/config/WebClientConfig.java`**
```java
package com.project.sentimentapi.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // @Bean en Spring tiene scope Singleton por defecto.
    // Esta instancia se crea UNA sola vez al arrancar la aplicación
    // y se reutiliza en todas las inyecciones.
    @Bean
    public WebClient sentimentWebClient(EndPointConfg config) {
        return WebClient.builder()
                .baseUrl(config.getUrl())           // URL de la API Python
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer ->
                    configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) // 10MB
                )
                .build();
        // Esta instancia se inyectará en SentimentApiAdapter y donde más se necesite
    }
}
```

**`infrastructure/config/EndPointConfg.java`** — mover desde `configuration/EndPointConfg.java`, solo cambiar el package:
```java
package com.project.sentimentapi.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.sentiment")
public class EndPointConfg {
    private String url;
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
```

---

### Paso 6 — Crear `EmailAdapter` en `infrastructure/email/`

**`infrastructure/email/EmailAdapter.java`**
```java
package com.project.sentimentapi.infrastructure.email;

import com.project.sentimentapi.domain.port.out.EmailPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    public EmailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarBienvenida(String destinatario, String nombre) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido a Sentiment API!");
        mensaje.setText("Hola " + nombre + ",\n\nTu cuenta ha sido creada exitosamente.");
        mailSender.send(mensaje);
    }

    @Override
    public void enviarResetPassword(String destinatario, String token, String urlReset) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Recuperación de contraseña");
        mensaje.setText("Para resetear tu contraseña, haz clic en: " + urlReset + "?token=" + token);
        mailSender.send(mensaje);
    }
}
```

---

### Paso 7 — Mover `security/` a `infrastructure/security/`

Solo cambiar el package en los tres archivos. La lógica no cambia:
- `security/JwtUtil.java` → `infrastructure/security/JwtUtil.java`
- `security/JwtAuthenticationFilter.java` → `infrastructure/security/JwtAuthenticationFilter.java`
- `security/SecurityConfig.java` → `infrastructure/security/SecurityConfig.java`

---

### Paso 8 — Mover archivos de configuración a `infrastructure/config/`

- `config/DataInitializer.java` → `infrastructure/config/DataInitializer.java` (solo cambiar package)

---

## 8. Integrante 3 — `presentation/` + integración final

**Principio SOLID a defender:** ISP  
**Patrón GOF a defender:** Observer (formalizar el flujo existente)  
**Rol adicional:** integrador del equipo — asegura que todo compile y funcione junto.

---

### Paso 1 — Refactorizar los controllers para aplicar ISP

El cambio central en cada controller: reemplazar la inyección de `*ServiceImplement` (clases concretas que traen TODO) por las interfaces específicas de `domain/port/in/` (solo lo que ese controller necesita).

**`presentation/controller/UsuarioController.java`**
```java
package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.AutenticarUsuarioUseCase;
import com.project.sentimentapi.domain.port.in.RecuperarContrasenaUseCase;
import com.project.sentimentapi.domain.port.in.RegistrarUsuarioUseCase;
import com.project.sentimentapi.presentation.dto.request.LoginRequestDto;
import com.project.sentimentapi.presentation.dto.request.RegistroRequestDto;
import com.project.sentimentapi.presentation.dto.response.LoginResponseDto;
import com.project.sentimentapi.presentation.dto.response.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // ISP: el controller SOLO inyecta las interfaces que necesita
    // No inyecta UserServiceImplement (que tiene MUCHAS más cosas que este controller no usa)
    private final RegistrarUsuarioUseCase registrarUseCase;
    private final AutenticarUsuarioUseCase autenticarUseCase;
    private final RecuperarContrasenaUseCase recuperarUseCase;

    // Constructor injection (sin @Autowired — mejor práctica desde Spring 4.3+)
    public UsuarioController(
            RegistrarUsuarioUseCase registrarUseCase,
            AutenticarUsuarioUseCase autenticarUseCase,
            RecuperarContrasenaUseCase recuperarUseCase) {
        this.registrarUseCase = registrarUseCase;
        this.autenticarUseCase = autenticarUseCase;
        this.recuperarUseCase = recuperarUseCase;
    }

    @PostMapping("/registro")
    public ResponseEntity<UserDto> registrar(@RequestBody RegistroRequestDto dto) {
        // El controller NO hace lógica de negocio — solo llama al use case y retorna
        UserDto usuario = registrarUseCase.registrar(dto);
        return ResponseEntity.status(201).body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        LoginResponseDto respuesta = autenticarUseCase.autenticar(dto);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        recuperarUseCase.solicitarReset(email);
        return ResponseEntity.noContent().build();
    }
}
```

**`presentation/controller/CsvAnalysisController.java`**
```java
package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.AnalizarCsvUseCase;
import com.project.sentimentapi.presentation.dto.request.CsvEntradaDto;
import com.project.sentimentapi.presentation.dto.response.CsvAnalysisResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/csv")
public class CsvAnalysisController {

    // ISP: solo inyecta la interfaz que necesita este controller
    private final AnalizarCsvUseCase analizarUseCase;

    public CsvAnalysisController(AnalizarCsvUseCase analizarUseCase) {
        this.analizarUseCase = analizarUseCase;
    }

    @PostMapping("/analizar")
    public ResponseEntity<CsvAnalysisResponseDto> analizar(
            @RequestBody List<CsvEntradaDto> filas,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Obtener el ID del usuario autenticado desde el contexto de seguridad
        Long usuarioId = obtenerUsuarioId(userDetails);
        
        // El controller delega al use case — cero lógica de negocio aquí
        CsvAnalysisResponseDto respuesta = analizarUseCase.analizar(filas, usuarioId);
        return ResponseEntity.ok(respuesta);
    }

    private Long obtenerUsuarioId(UserDetails userDetails) {
        // Lógica mínima para extraer el ID del usuario autenticado
        // (puede variar según la implementación del JWT actual)
        return Long.parseLong(userDetails.getUsername());
    }
}
```

**`presentation/controller/SesionController.java`**
```java
package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.ConsultarSesionesUseCase;
import com.project.sentimentapi.presentation.dto.response.SesionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sesiones")
public class SesionController {

    // ISP: solo inyecta ConsultarSesionesUseCase — no GuardarSesionUseCase
    // (guardar lo hace CsvAnalysisController a través de AnalizarCsvUseCase)
    private final ConsultarSesionesUseCase consultarUseCase;

    public SesionController(ConsultarSesionesUseCase consultarUseCase) {
        this.consultarUseCase = consultarUseCase;
    }

    @GetMapping
    public ResponseEntity<List<SesionDto>> obtenerMisSesiones(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long usuarioId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(consultarUseCase.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SesionDto> obtenerSesion(@PathVariable Long id) {
        return ResponseEntity.ok(consultarUseCase.obtenerPorId(id));
    }
}
```

---

### Paso 2 — Reorganizar los DTOs en `presentation/dto/`

Crear las subcarpetas `request/` y `response/`. El contenido de los DTOs no cambia, solo el package.

**Regla:** los DTOs de `request/` son lo que el cliente HTTP envía al servidor. Los de `response/` son lo que el servidor retorna al cliente. Esta separación deja claro el propósito de cada DTO sin leer su contenido.

Renombraciones concretas:
- `dto/UserDtoLogin.java` → `presentation/dto/request/LoginRequestDto.java` (cambiar package y nombre de clase)
- `dto/UserDtoRegistro.java` → `presentation/dto/request/RegistroRequestDto.java` (cambiar package y nombre de clase)
- Todos los demás DTOs de salida (`SesionDto`, `ProductoDto`, `CsvAnalysisResponseDto`, etc.) → `presentation/dto/response/` (solo cambiar package)

---

### Paso 3 — Mover y mejorar `GlobalExceptionHandler` a `presentation/exception/`

Mover desde `globalexceptionhandler/ExecptionHandler.java` (con el typo en el nombre) y agregar el manejo de las nuevas excepciones de dominio que crea Integrante 1.

**`presentation/exception/GlobalExceptionHandler.java`**
```java
package com.project.sentimentapi.presentation.exception;

import com.project.sentimentapi.domain.exception.SentimentApiException;
import com.project.sentimentapi.domain.exception.TokenExpiradoException;
import com.project.sentimentapi.domain.exception.UsuarioNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Excepción de dominio → 404 Not Found
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(
            UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Usuario no encontrado",
                "message", ex.getMessage()
        ));
    }

    // Excepción de dominio → 503 Service Unavailable
    @ExceptionHandler(SentimentApiException.class)
    public ResponseEntity<Map<String, Object>> handleSentimentApi(
            SentimentApiException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Servicio de análisis no disponible",
                "message", ex.getMessage()
        ));
    }

    // Excepción de dominio → 401 Unauthorized
    @ExceptionHandler(TokenExpiradoException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpirado(
            TokenExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 401,
                "error", "Token expirado",
                "message", ex.getMessage()
        ));
    }

    // Validaciones fallidas → 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Argumento inválido",
                "message", ex.getMessage()
        ));
    }

    // Fallback — cualquier excepción no manejada → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 500,
                "error", "Error interno del servidor",
                "message", "Ocurrió un error inesperado. Contacte al soporte."
        ));
    }
}
```

---

### Paso 4 — Verificar el Patrón Observer

Tras el refactoring, verificar que el flujo completo del Observer funciona:

```
1. POST /api/usuarios/registro
   → UsuarioController.registrar(dto)
   → RegistrarUsuarioUseCaseImpl.registrar(dto)
     → usuarioPort.guardar(nuevoUsuario)                ← guarda en BD vía adapter
     → eventPublisher.publishEvent(UserRegisteredEvent) ← publica evento (OBSERVER)
                           ↓ Spring notifica automáticamente
2. UserRegistrationListener.onUserRegistered(event)
   → emailPort.enviarBienvenida(email, nombre)           ← llama al port (OBSERVER reacciona)
                           ↓
3. EmailAdapter.enviarBienvenida(email, nombre)
   → mailSender.send(mensaje)                            ← envía el email real
```

Para verificar que funciona, ejecutar la aplicación y registrar un usuario nuevo. Si llega el email de bienvenida, el Observer está funcionando correctamente.

---

### Paso 5 — Integración y verificación final

Esta es la tarea más importante del Integrante 3 como integrador del equipo.

**5.1 — Verificar la resolución de dependencias de Spring**

Cuando un use case pide `UsuarioRepositoryPort`, Spring debe encontrar automáticamente `UsuarioRepositoryAdapter` (que está anotado con `@Component` e implementa esa interfaz). Verificar esto para cada port:

| Use case pide... | Spring debe inyectar... |
|---|---|
| `UsuarioRepositoryPort` | `UsuarioRepositoryAdapter` |
| `ProductoRepositoryPort` | `ProductoRepositoryAdapter` |
| `SesionRepositoryPort` | `SesionRepositoryAdapter` |
| `SentimentAnalysisPort` | `SentimentApiAdapter` |
| `EmailPort` | `EmailAdapter` |

Si Spring no encuentra un adapter para algún port, lanzará `NoSuchBeanDefinitionException` al arrancar. El error indicará qué interface está sin implementación.

**5.2 — Verificar el scan de componentes**

Si `SentimentapiApplication.java` tiene `@SpringBootApplication`, Spring hace component scan de todo el paquete raíz `com.project.sentimentapi` y sus subpaquetes. Si la clase principal no está en el paquete raíz, puede ser necesario añadir:

```java
// SentimentapiApplication.java — verificar que el @SpringBootApplication
// está en el paquete raíz correcto
@SpringBootApplication
public class SentimentapiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SentimentapiApplication.class, args);
    }
}
```

**5.3 — Verificar que no hay imports cruzados incorrectos**

Ejecutar estas búsquedas en el IDE (usando "Find in Files" / `Ctrl+Shift+F`):

```
En domain/**:   buscar "import org.springframework" — debe dar 0 resultados
En domain/**:   buscar "import jakarta.persistence" — debe dar 0 resultados
En application/**:  buscar "import jakarta.persistence" — debe dar 0 resultados
En application/**:  buscar "infrastructure" — debe dar 0 resultados
```

Si alguna búsqueda da resultados, hay una violación de la regla de dependencias que hay que corregir.

**5.4 — Ejecutar la aplicación y probar endpoints**

```bash
# Compilar
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run

# Probar que los endpoints responden igual que antes del refactoring
curl -X POST http://localhost:8080/api/usuarios/registro \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","email":"test@test.com","password":"123456"}'

curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"123456"}'
```

---

## 9. Regla de dependencias — la ley más importante

Esta es la regla fundamental de Clean Architecture. **Si se viola, el refactoring está mal hecho.**

```
✅ presentation  puede importar  application, domain, DTOs
✅ application   puede importar  domain
✅ infrastructure puede importar domain, frameworks externos (Spring, JPA, WebClient)
❌ domain        NO puede importar  NINGUNA otra capa ni framework externo
❌ application   NO puede importar  infrastructure ni presentation
❌ infrastructure NO puede importar presentation
```

### Visualización del flujo correcto

```
HTTP Request
     ↓
[presentation/controller]
     ↓ llama a
[domain/port/in] (interfaz)
     ↓ implementada por
[application/usecase]
     ↓ llama a
[domain/port/out] (interfaz)
     ↓ implementada por
[infrastructure/adapter]
     ↓ usa
[Spring Data JPA / WebClient / JavaMailSender]
     ↓
Base de datos / API externa / Servidor de email
```

### Checklists rápidos por capa

**Checklist `domain/`**
- [ ] Ningún `import org.springframework.*`
- [ ] Ningún `import jakarta.persistence.*`
- [ ] Ningún `import com.project.sentimentapi.infrastructure.*`
- [ ] Ningún `import com.project.sentimentapi.presentation.*`
- [ ] Ningún `import com.project.sentimentapi.application.*`

**Checklist `application/`**
- [ ] Ningún `import jakarta.persistence.*`
- [ ] Ningún `import com.project.sentimentapi.infrastructure.*`
- [ ] Sí puede importar `org.springframework.stereotype.Service` y similares
- [ ] Sí puede importar `com.project.sentimentapi.domain.*`

**Checklist `infrastructure/`**
- [ ] Ningún `import com.project.sentimentapi.presentation.*`
- [ ] Todos los adapters tienen `@Component`
- [ ] Sí puede importar `com.project.sentimentapi.domain.*` (implementa sus interfaces)

**Checklist `presentation/`**
- [ ] Los controllers no inyectan `*ServiceImplement` ni `*RepositoryAdapter`
- [ ] Los controllers solo inyectan interfaces de `domain/port/in/`
- [ ] No hay lógica de negocio en los controllers

---

## 10. Orden de implementación por semanas

Para que el equipo pueda trabajar en paralelo sin bloquearse, el orden de entrega es:

```
SEMANA 1 (todos dependen de esto — prioridad máxima)
├── Integrante 1: domain/model/*.java (los 5 modelos de dominio)
└── Integrante 1: domain/port/ (todas las interfaces in/ y out/)
    └── ⚠️ PUNTO DE SINCRONIZACIÓN: sin esto, nadie más puede avanzar

SEMANA 2 (una vez que domain/ está disponible)
├── Integrante 2: infrastructure/persistence/entity/ (renombrar JpaEntity)
├── Integrante 2: infrastructure/persistence/repository/ (renombrar JpaRepository)
├── Integrante 1: application/builder/SesionBuilder.java
├── Integrante 1: application/mapper/*.java
└── Integrante 3: presentation/dto/request/ y response/ (solo mover y renombrar)

SEMANA 3 (una vez que entity/ y repository/ están disponibles)
├── Integrante 1: application/usecase/*.java (todos los use cases)
├── Integrante 2: infrastructure/persistence/adapter/*.java
├── Integrante 2: infrastructure/external/SentimentApiAdapter.java
├── Integrante 2: infrastructure/config/WebClientConfig.java
└── Integrante 2: infrastructure/email/EmailAdapter.java

SEMANA 4 (integración final)
├── Integrante 3: presentation/controller/*.java (refactor de imports)
├── Integrante 3: presentation/exception/GlobalExceptionHandler.java
├── Integrante 3: verificar Observer (UserRegisteredEvent + Listener)
└── Integrante 3: integración final — compilar, testear endpoints, resolver conflictos
```

---

## 11. Cómo sustentar el proyecto ante el docente

### Preguntas que puede hacer el docente y cómo responder

**¿Por qué separaron el `SesionService` en dos interfaces?**
> Aplicamos el Principio de Segregación de Interfaces (ISP). `SesionController` solo necesita consultar sesiones, no guardarlas. `CsvAnalysisController` solo necesita guardar, no consultar. Al tener una interfaz gorda (`SesionService` original), ambos controllers dependían de métodos que no usaban. Con `GuardarSesionUseCase` y `ConsultarSesionesUseCase` separados, cada controller depende exactamente de lo que necesita.

**¿Dónde exactamente está el Patrón Facade?**
> En `AnalizarCsvUseCaseImpl`. El controller llama `analizarUseCase.analizar(filas, usuarioId)` — una sola llamada. Internamente, ese use case coordina: la API de sentimientos, los repositorios de productos, el Builder de sesión y el repositorio de sesiones. Para el controller, toda esa complejidad es invisible.

**¿Por qué el evento `UserRegisteredEvent` es un POJO y no extiende `ApplicationEvent`?**
> Porque el dominio no debe depender de Spring. Si `UserRegisteredEvent` extiende `ApplicationEvent`, el paquete `domain/` necesitaría `import org.springframework.*`, violando el DIP. Spring puede publicar cualquier objeto como evento desde Spring 4.2+, no solo subclases de `ApplicationEvent`.

**¿Qué diferencia hay entre `UsuarioRepositoryPort` (en domain) y `UsuarioJpaRepository` (en infrastructure)?**
> `UsuarioRepositoryPort` es el contrato del dominio: define qué operaciones sobre usuarios necesita el negocio (`buscarPorEmail`, `guardar`, etc.) en términos de modelos de dominio puros (`Usuario`). `UsuarioJpaRepository` es la implementación técnica de Spring Data JPA: trabaja con `UsuarioJpaEntity` y sus anotaciones JPA. El `UsuarioRepositoryAdapter` conecta ambos mundos: recibe una llamada en términos de dominio y la traduce a JPA.

**¿Cómo demuestran que cumple el OCP?**
> Si el equipo quisiera cambiar el proveedor de análisis de sentimientos de la API Python actual a OpenAI, solo se crea una nueva clase `OpenAiSentimentAdapter implements SentimentAnalysisPort`. `AnalizarCsvUseCaseImpl` no se modifica en absoluto. Eso es estar cerrado para modificación y abierto para extensión.

**¿Por qué mueven las entidades JPA a infrastructure?**
> Porque las entidades JPA son un detalle de infraestructura, no del dominio. La anotación `@Entity` ata la clase al framework Hibernate/JPA. El dominio de negocio (la lógica de si un usuario puede hacer determinada acción) no debería depender de eso. Al separar `UsuarioJpaEntity` (con `@Entity`) de `Usuario` (POJO puro), el dominio puede existir y testearse completamente independiente de la base de datos.

---

*Documento generado para el curso Diseño de Patrones (100000SI47) — UTP 2026 Ciclo 1*  
*Proyecto: `sentiment-backend-java` — Refactoring de MVC a Clean Architecture*
