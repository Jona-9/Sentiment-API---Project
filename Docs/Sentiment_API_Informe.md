# Sentiment API (Sistema de análisis de Sentimientos)

**Autor(es):**
Jose Eduardo Diaz Fernandez
Jonathan Edilson Tuppia Lozano
Joaquin Sebastian Chaparro Villavicencio

**Docente:** Jose Luis Milla Flores
**Curso:** Diseño de Patrones
**Lima - Perú, 2026**

---

## Índice

1. Introducción — 3
2. Diagnóstico del código actual — 3
   - 2.1 Estructura actual (problemática) — 3
   - 2.2 Resumen de violaciones SOLID — 4
3. Análisis de violaciones SOLID — 6
   - 3.1 SRP — CsvAnalysisServiceImplement.java — 6
   - 3.2 SRP — UserServiceImplement.java — 6
   - 3.3 DIP — SentimentServiceImplement.java y entidades JPA — 7
   - 3.4 SRP + DIP — SesionServiceImplement.java — 7
   - 3.5 ISP — SesionService con métodos mezclados — 8
4. Arquitectura objetivo — Clean Architecture — 9
   - 4.1 Nueva estructura de paquetes — 9
5. Patrones de diseño GOF aplicados — 11
   - 5.1 Facade — AnalizarCsvUseCaseImpl — 11
   - 5.2 Adapter — SentimentApiAdapter y adapters de repositorio — 11
   - 5.3 Builder — SesionBuilder — 11
   - 5.4 Observer — UserRegisteredEvent (ya existe, reubicar) — 12
   - 5.5 Singleton — WebClientConfig como @Bean — 12
6. Asignación de tareas y regla de dependencias — 13
   - Orden de implementación sugerido — 14
7. Conclusión — 14

---

## 1. Introducción

Este proyecto nació de una necesidad concreta: las empresas reciben cientos de reseñas de productos en español y portugués, y revisarlas manualmente es inviable. La solución que construimos es un sistema completo que automatiza ese proceso — un backend en Spring Boot que gestiona usuarios, productos y sesiones de análisis, conectado a una API de Machine Learning propia que clasifica cada comentario como positivo, neutro o negativo usando un pipeline de Regresión Logística entrenado con reseñas reales.

El sistema funciona. Los endpoints responden, los CSV se procesan y las estadísticas se generan correctamente. El problema está en cómo está construido por dentro: durante el análisis del código encontramos siete violaciones a los principios SOLID, la mayoría concentradas en los servicios. Hay clases que hacen demasiadas cosas a la vez, dependencias directas a frameworks que deberían estar escondidos, y lógica de negocio mezclada con detalles de infraestructura. Nada que impida que el sistema corra hoy, pero sí cosas que van a complicar cualquier cambio futuro — por ejemplo, cambiar el modelo de ML, añadir otro proveedor o migrar la base de datos.

Este informe documenta exactamente qué está mal y dónde, propone Clean Architecture como arquitectura objetivo y describe los cinco patrones GOF que vamos a aplicar para corregirlo.

---

## 2. Diagnóstico del código actual

### 2.1 Estructura actual (problemática)

La estructura de paquetes mezcla responsabilidades de distinto nivel de abstracción sin ninguna barrera conceptual entre el dominio del negocio y la infraestructura técnica:

```
sentimentapi/
├── config/          ← DataInitializer mezclado con configuración técnica
├── configuration/   ← ConectarApi y EndPointConfg (duplica la carpeta config/)
├── controller/      ← 6 controllers, algunos con lógica de negocio
├── dto/             ← 16 DTOs sin separar requests de responses
├── entity/          ← Entidades JPA usadas directamente como modelos de dominio
├── event/           ← Observer bien implementado pero ubicado fuera de su capa
├── repository/      ← Spring Data JPA
├── security/        ← JWT, SecurityConfig
└── service/         ← Interfaces e implementaciones mezcladas sin separación
```

El problema estructural central es que no existe ninguna barrera entre qué hace el sistema y cómo lo hace. Una entidad JPA como `Producto` es simultáneamente el modelo de base de datos (`@Entity`, `@Column`) y el objeto de negocio que los services manipulan. Si mañana se cambia de MySQL a MongoDB, hay que tocar clases de dominio.

### 2.2 Resumen de violaciones SOLID

| # | Archivo | Principio | Problema |
|---|---|---|---|
| 1 | `CsvAnalysisServiceImplement.java` | SRP | Hace 6 cosas: crea entidades, llama la API, calcula estadísticas, construye la sesión, actualiza productos y arma la respuesta. Más de 150 líneas mezcladas. |
| 2 | `UserServiceImplement.java` | SRP | Mezcla registro, login, reset de contraseña, manejo JWT y `System.out.println` de debug en una sola clase. |
| 3 | `SesionServiceImplement.java` | SRP + DIP | Un servicio de Sesion inyecta directamente `CategoriaRepository` y `ProductoRepository`, acoplándose a otro dominio. |
| 4 | `SentimentServiceImplement.java` | DIP | Depende directamente de `ConectarApi` (clase concreta). Cambiar el proveedor de IA exige modificar este servicio. |
| 5 | `ConectarApi.java` | SRP | El método `client()` crea un nuevo `WebClient` en cada invocación en vez de ser un `@Bean` singleton gestionado por Spring. |
| 6 | `entity/` (Producto, Sesion…) | DIP | Las entidades JPA (`@Entity`, `@Column`) son los mismos objetos que los services manipulan como modelos de negocio. |
| 7 | `configuration/` vs `config/` | SRP | Dos paquetes de configuración sin separación clara. `DataInitializer` vive en `config/` junto a beans que no le corresponden. |

---

## 3. Análisis de violaciones SOLID

### 3.1 SRP — CsvAnalysisServiceImplement.java

Este archivo es el punto de mayor riesgo del sistema. El método `analizarCsv()` supera las 150 líneas y concentra seis responsabilidades completamente distintas: crear entidades JPA con `new` y setters, invocar la API de inteligencia artificial vía WebClient, calcular estadísticas (positivos, negativos, neutrales, avgScore), construir el objeto `Sesion` con todos sus campos, actualizar el campo `cantidadMenciones` de cada Producto y armar el `CsvAnalysisResponseDto` de salida. Cada vez que cambia cualquiera de estos seis aspectos —el formato de la API, la lógica de estadísticas, la estructura de Sesion— este archivo debe modificarse. Además, instancia `SentimentService` directamente, lo que también viola DIP.

### 3.2 SRP — UserServiceImplement.java

Cuatro responsabilidades de naturaleza diferente conviven en la misma clase: registro de usuario (validar email, encodear contraseña, guardar), autenticación y generación de token JWT, reset de contraseña (generar token, persistir, enviar email) y debugging con `System.out.println` que nunca fue eliminado. Si se cambia el proveedor de email, el algoritmo JWT o se añade verificación por SMS, los tres cambios convergen al mismo punto.

### 3.3 DIP — SentimentServiceImplement.java y entidades JPA

`SentimentServiceImplement`, un módulo de alto nivel, depende directamente de `ConectarApi`, una clase concreta de infraestructura. No existe ninguna interfaz `SentimentAnalysisPort` que actúe como barrera. Si el equipo decide cambiar el proveedor de IA, debe modificar este servicio de negocio:

```java
// Mal: el dominio conoce la implementación HTTP
@Autowired
private ConectarApi conectarApi; // clase concreta, no interfaz
```

El mismo problema ocurre con las entidades JPA. Las clases en `entity/` están anotadas con `@Entity` y `@Column`, y son los mismos objetos que los services manipulan como modelos de negocio. El dominio tiene una dependencia directa de `jakarta.persistence.*`. Cualquier cambio de motor de base de datos obliga a modificar clases de negocio.

### 3.4 SRP + DIP — SesionServiceImplement.java

```java
@Autowired
private CategoriaRepository categoriaRepository; // un servicio de Sesion accede
@Autowired
private ProductoRepository productoRepository;   // al repositorio de otro dominio
```

Un servicio de la entidad Sesion no debería conocer la existencia de `ProductoRepository`. Si se refactoriza `ProductoRepository`, `SesionServiceImplement` se ve afectado sin ninguna razón de negocio.

### 3.5 ISP — SesionService con métodos mezclados

La interfaz `SesionService` agrupa en un único contrato métodos de dos responsabilidades distintas: guardar una sesión nueva y consultar el historial. Un controller de solo lectura —por ejemplo para un rol analista— debe inyectar una interfaz con métodos de escritura que nunca va a usar. Si en el futuro se añade un endpoint de lectura, el principio de segregación ya estará violado de origen.

---

## 4. Arquitectura objetivo — Clean Architecture

Se adoptará Clean Architecture (Robert C. Martin) adaptada a Spring Boot. La regla fundamental es que las dependencias siempre apuntan hacia adentro: el dominio no conoce nada del exterior.

```
[ presentation ] ──► [ application ] ──► [ domain ]
                                              ▲
              [ infrastructure ] ─────────────┘
```

- **domain** — POJOs puros, interfaces de ports, excepciones. Sin imports de Spring ni JPA.
- **application** — implementaciones de use cases. Solo importa domain.
- **infrastructure** — JPA adapters, WebClient, email. Implementa los contratos de domain.
- **presentation** — controllers y DTOs. Llama a los use cases de application.

Con esta estructura, cambiar el proveedor de IA implica crear una nueva clase `OpenAiSentimentAdapter` que implementa `SentimentAnalysisPort`, sin tocar ningún use case. Cambiar de MySQL a MongoDB implica crear nuevos adapters de repositorio, sin tocar el dominio. Los use cases no dependen de Spring ni JPA, por lo que se pueden probar con Mockito sin levantar contexto de aplicación.

### 4.1 Nueva estructura de paquetes

```
com.project.sentimentapi/
├── domain/
│   ├── model/       ← Usuario, Producto, Sesion, Comentario (POJOs, sin @Entity)
│   ├── port/in/     ← RegistrarUsuarioUseCase, AnalizarCsvUseCase, GuardarSesionUseCase…
│   ├── port/out/    ← UsuarioRepositoryPort, SentimentAnalysisPort, EmailPort…
│   ├── event/       ← UserRegisteredEvent (POJO puro)
│   └── exception/   ← UsuarioNoEncontradoException, SentimentApiException…
├── application/
│   ├── usecase/     ← AnalizarCsvUseCaseImpl, RegistrarUsuarioUseCaseImpl…
│   ├── builder/     ← SesionBuilder.java (Patrón Builder)
│   ├── event/       ← UserRegistrationListener (puede usar Spring aquí)
│   └── mapper/      ← UsuarioMapper, SesionMapper, ProductoMapper
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/      ← UsuarioJpaEntity, ProductoJpaEntity… (sufijo JpaEntity)
│   │   ├── repository/  ← Spring Data JPA (sufijo JpaRepository)
│   │   └── adapter/     ← UsuarioRepositoryAdapter, ProductoRepositoryAdapter…
│   ├── external/    ← SentimentApiAdapter (implementa SentimentAnalysisPort)
│   ├── email/       ← EmailAdapter (implementa EmailPort)
│   ├── security/    ← sin cambios, solo ajustar package
│   └── config/      ← WebClientConfig @Bean, DataInitializer, EndPointConfg
└── presentation/
    ├── controller/      ← ajustar imports para usar use cases
    ├── dto/request/     ← RegistroRequestDto, LoginRequestDto, CsvUploadRequestDto…
    ├── dto/response/    ← LoginResponseDto, SesionDto, CsvAnalysisResponseDto…
    └── exception/       ← GlobalExceptionHandler (mapeado a códigos HTTP)
```

---

## 5. Patrones de diseño GOF aplicados

### 5.1 Facade — AnalizarCsvUseCaseImpl

El controller llama un único método `analizarCsv(rows, usuarioId)`. Por detrás, el use case coordina `SentimentAnalysisPort`, `ProductoRepositoryPort`, `SesionRepositoryPort`, `SesionBuilder` y `EstadisticasCalculator` sin que el controller conozca ninguna de esas dependencias. Si se añade un paso nuevo al flujo (por ejemplo, enviar un email de resumen), solo cambia el use case.

### 5.2 Adapter — SentimentApiAdapter y adapters de repositorio

Los adapters en `infrastructure/` traducen la interfaz del framework externo a la interfaz que el dominio necesita. `SentimentApiAdapter` implementa `SentimentAnalysisPort` usando WebClient internamente; `ProductoRepositoryAdapter` implementa `ProductoRepositoryPort` usando `ProductoJpaRepository` y mapea entre JpaEntity y modelo de dominio. El dominio nunca importa una clase de Spring ni de JPA.

```java
@Component
public class SentimentApiAdapter implements SentimentAnalysisPort {
    private final WebClient client; // inyectado como @Bean

    @Override
    public Optional<SentimentsResponseDto> analizarLote(List<String> textos) {
        // lógica de WebClient — el dominio nunca ve estas líneas
    }
}
```

### 5.3 Builder — SesionBuilder

La construcción de `Sesion` está actualmente dispersa en ~30 líneas de setters dentro de `CsvAnalysisServiceImplement`, sin validación de campos obligatorios. Con Builder, la construcción queda centralizada y cualquier campo nuevo obligatorio provoca un error de compilación en todos los puntos de uso:

```java
Sesion sesion = new SesionBuilder()
    .conUsuario(usuario)
    .conFecha(LocalDateTime.now())
    .conComentarios(comentarios)
    .conEstadisticas(total, positivos, negativos, neutrales, avgScore)
    .build(); // valida campos obligatorios
```

### 5.4 Observer — UserRegisteredEvent (ya existe, reubicar)

El patrón ya está implementado con `ApplicationEventPublisher` de Spring: cuando un usuario se registra, `RegistrarUsuarioUseCaseImpl` publica un `UserRegisteredEvent` y `UserRegistrationListener` reacciona enviando el correo de bienvenida. El único trabajo es reubicar `UserRegisteredEvent` en `domain/event/` como POJO puro y el listener en `application/event/`, respetando la regla de dependencias.

### 5.5 Singleton — WebClientConfig como @Bean

`ConectarApi.java` crea un nuevo `WebClient` en cada invocación. Se reemplaza por un `@Bean` de Spring, que por defecto garantiza una sola instancia en todo el contexto:

```java
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient sentimentWebClient(EndPointConfg config) {
        return WebClient.builder().baseUrl(config.getUrl()).build();
    }
}
```

---

## 6. Asignación de tareas y regla de dependencias

| Integrante | Capa | Principio | Responsabilidad |
|---|---|---|---|
| Integrante 1 | `domain/` | DIP | Modelos de dominio (POJOs), interfaces de ports in/out, excepciones de dominio. |
| Integrante 2 | `application/` | SRP | Use cases (AnalizarCsvUseCaseImpl, RegistrarUsuario…), SesionBuilder, mappers. |
| Integrante 3 | `infrastructure/` | OCP | JPA adapters, SentimentApiAdapter, EmailAdapter, WebClientConfig como `@Bean`. |
| Integrante 4 | `presentation/` | ISP | Controllers, DTOs request/response, GlobalExceptionHandler, integración final. |

La regla más importante del proyecto: **si algún import la viola, el refactoring está mal hecho.**

- `domain/` no puede importar `org.springframework.*`, `jakarta.persistence.*`, ni ninguna otra capa.
- `application/` no puede importar `infrastructure/` ni `presentation/`.
- `infrastructure/` no puede importar `presentation/`.
- `presentation/` puede importar `application/` y `domain/`, pero no `infrastructure/`.

### Orden de implementación sugerido

- **Semana 1** — Integrante 1: `domain/` completo (models, ports, exceptions, event). Nada más puede empezar sin esto.
- **Semana 2** — Integrante 3: entidades y repositorios JPA renombrados. Integrante 2: SesionBuilder y mappers.
- **Semana 3** — Integrante 2: use cases completos. Integrante 3: adapters de repositorio y SentimentApiAdapter.
- **Semana 4** — Integrante 4: controllers refactorizados, DTOs organizados, GlobalExceptionHandler y verificación de que todos los endpoints respondan igual que antes.

---

## 7. Conclusión

El proyecto tiene una base funcional sólida: los endpoints responden, la integración con la API de sentimientos opera y el sistema de autenticación JWT funciona. El problema no es que el código no funcione, sino que su arquitectura acumula deuda técnica que hace cada cambio futuro más costoso.

Las siete violaciones SOLID no son errores de lógica sino decisiones de diseño que acoplaron el dominio a los frameworks. Clean Architecture rompe ese acoplamiento de forma estructurada: el dominio define los contratos, la infraestructura los implementa y los use cases orquestan el flujo sin conocer detalles de persistencia ni de HTTP. Los cinco patrones GOF no son decisiones arbitrarias sino respuestas directas a los problemas encontrados: Facade para la complejidad dispersa en `CsvAnalysisServiceImplement`, Adapter para desacoplar WebClient y JPA del dominio, Builder para centralizar la construcción de Sesion, Observer para la reactividad al registro de usuarios y Singleton para gestionar el WebClient correctamente.

Al finalizar el refactoring, agregar un nuevo proveedor de IA, cambiar la base de datos o incorporar un nuevo canal de notificación será una operación de extensión y no de modificación.
