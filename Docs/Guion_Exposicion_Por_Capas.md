# GUION DE EXPOSICIÓN POR CAPAS (HÍBRIDO) — PROYECTO FINAL
## Sentiment API — Diseño de Patrones

**Curso:** Diseño de Patrones — Docente: Jose Luis Milla Flores
**Formato:** 7 bloques + demo en vivo · **15 minutos** en total.

**Reparto por capas (cada integrante expone la capa que domina y sus patrones):**

| Integrante | Capa(s) que expone | Patrones que explica |
|---|---|---|
| **Jonathan Edilson Tuppia Lozano** | `domain` + `application` (el núcleo) | **Facade**, **Builder**, **Observer** + concepto de **Puertos/DIP** |
| **Jose Eduardo Diaz Fernandez** | `infrastructure` | **Adapter**, **Singleton** + seguridad JWT (**TokenProviderPort**) |
| **Joaquin Sebastian Chaparro Villavicencio** | `presentation` + **demo en vivo** + **pruebas JUnit** | **Controller (GRASP)** |

> **Enfoque híbrido:** no es un "tour de carpetas". Seguimos el **viaje del CSV de María** a través de las capas; cada quien explica **su capa, qué contiene y qué patrón usó y PARA QUÉ**, anclado a lo que le pasa a ese CSV. El orden de los expositores sigue el **camino de la petición**: entra por presentation → la orquesta application → sobre el núcleo domain → y la infraestructura hace el trabajo real.

---

## Reglas de oro (de la guía)

1. **Cero código en las diapositivas.** Se explica con diagramas y con el "por qué". El código solo se abre en el IDE si el docente lo pide.
2. **Hablar del PARA QUÉ, no del QUÉ.** No digan "creamos una interfaz"; digan "creamos un puerto para poder cambiar la IA el próximo año sin tocar el negocio (OCP/DIP)".
3. **Un patrón = una escena de negocio** sobre nuestro sistema (qué le pasa a María), no una definición de memoria.

## Hilo conductor (úsenlo toda la presentación)

> **Caso:** *María, dueña de "TecnoStore", sube un CSV con 500 reseñas de su "Laptop X" y quiere saber en segundos cuántos clientes hablan bien o mal.*

Ese CSV **atraviesa cada capa**. En cada bloque se muestra "qué le pasa al CSV de María aquí". En la demo se sube ese mismo CSV en vivo y la historia se cierra sola.

---

## Reparto de los 15 minutos

| # | Bloque | Tiempo | Expositor |
|---|---|---|---|
| 1 | Portada | ~0:30 | Joaquin |
| 2 | El Problema (AS-IS) — presentar a María | ~1:30 | Joaquin |
| 3 | Mapa de la arquitectura: 4 capas + regla de dependencias | ~1:30 | Jonathan |
| 4 | Capa **Presentation** (Controller GRASP) | ~1:30 | Joaquin |
| 5 | Capas **Domain + Application** (Facade, Builder, Observer, DIP) | ~3:00 | Jonathan |
| 6 | Capa **Infrastructure** (Adapter, Singleton, JWT) | ~2:30 | Jose |
| 7 | Demo en vivo + JUnit | ~3:30 | Joaquin (narran Jonathan/Jose) |
| 8 | Conclusiones (SOLID + GRASP) | ~1:00 | Jonathan |
| | **TOTAL** | **~15:00** | |

---

## Tabla resumen — capa → contenido → patrón → para qué

| Capa | Qué contiene | Patrón(es) | Para qué sirvió |
|---|---|---|---|
| **presentation** (Joaquin) | Controllers REST, `GlobalExceptionHandler`, filtro JWT | **Controller (GRASP)** | Recibir HTTP y delegar al caso de uso, sin lógica de negocio |
| **application** (Jonathan) | Casos de uso, puertos de entrada (`port/in`), DTOs, mappers, `SesionBuilder`, listener | **Facade**, **Builder**, **Observer** | Orquestar el negocio; ocultar la complejidad tras una sola puerta |
| **domain** (Jonathan) | POJOs puros, puertos de salida (`port/out`), evento, excepciones | **Puertos / DIP** | El núcleo del negocio, sin conocer Spring ni JPA |
| **infrastructure** (Jose) | Adapters JPA, `SentimentApiAdapter`, `EmailAdapter`, seguridad, config | **Adapter**, **Singleton** | Implementar los contratos del dominio con la tecnología real |

---

## BLOQUE 1 — Portada · Joaquin · ~0:30
- **Título:** Sentiment API — Del monolito a Clean Architecture.
- **Notas:** "Somos el equipo de Sentiment API. Rediseñamos un sistema que ya funcionaba aplicando los principios y patrones del curso para volverlo mantenible. Hoy lo recorremos **capa por capa**, siguiendo un caso real."

## BLOQUE 2 — El Problema (AS-IS) · Joaquin · ~1:30
- **Presentar a María:** dueña de TecnoStore, 500 reseñas de su Laptop X, en español y portugués. Revisarlas a mano es inviable.
- **Cómo fallaba el software:** funcionaba, pero por dentro **todo estaba pegado** (código espagueti). Cambiar la IA o la base de datos obligaba a reprogramar medio sistema.
- **Frase de cierre / handoff:** "Ese dolor lo resolvimos separando el sistema en **4 capas**. Jonathan les muestra el mapa."

## BLOQUE 3 — Mapa de la arquitectura · Jonathan · ~1:30
- **Idea central:** Clean Architecture. **Las dependencias apuntan hacia adentro**: el dominio no conoce nada del exterior.
- **Diagrama (sin código):** `presentation → application → domain ← infrastructure`.
- **Regla que no se rompe:** si un `import` del dominio apunta a Spring o JPA, el diseño está mal.
- **Quién explica qué:** "Yo tomo el corazón —domain y application—; Jose, la infraestructura; Joaquin, la puerta de entrada y la demo."
- **Handoff:** "Empecemos por donde entra el CSV de María: la capa de presentación, con Joaquin."

## BLOQUE 4 — Capa PRESENTATION · Joaquin · ~1:30
- **Qué contiene:** los `@RestController` (`CsvAnalysisController`, `UsuarioController`…), el `GlobalExceptionHandler` (traduce excepciones → códigos HTTP) y el filtro JWT.
- **Patrón: Controller (GRASP).** El controller **solo recibe la petición HTTP y la delega** al caso de uso; no sabe cómo se llama la IA, ni cómo se calculan estadísticas, ni cómo se guarda en BD.
- **Para qué sirvió:** mantener la puerta HTTP **delgada y sin lógica de negocio**; si mañana cambia el negocio, el controller no se toca.
- **Qué le pasa al CSV de María aquí:** "María arrastra su archivo y presiona *Analizar*. Su CSV llega a `CsvAnalysisController`, que solo verifica quién es (JWT) y se lo pasa al caso de uso. El controller no hace el trabajo: **lo delega**."
- **Handoff:** "¿Y quién hace el trabajo? El corazón del sistema. Jonathan."

## BLOQUE 5 — Capas DOMAIN + APPLICATION · Jonathan · ~3:00

**5.a — Domain (el núcleo puro)**
- **Qué contiene:** POJOs de negocio (`Usuario`, `Sesion`, `ResultadoSentimiento`…) **sin `@Entity` ni anotaciones de framework**, los **puertos de salida** (`port/out`), el evento y las excepciones.
- **Concepto clave — Puertos / DIP:** el dominio **define contratos** (`SentimentAnalysisPort`, `TokenProviderPort`, repositorios) pero **no sabe quién los cumple**. Eso es Inversión de Dependencias: el núcleo no depende de la tecnología; la tecnología depende del núcleo.
- **Para qué sirvió:** poder cambiar de IA o de base de datos **sin tocar una sola línea de negocio**.

**5.b — Application (orquesta el negocio) — aquí viven 3 patrones**
- **Facade — `AnalizarCsvUseCaseImpl`:** María hace **una** acción; por dentro el caso de uso coordina **cinco** tareas (consultar la IA, contar sentimientos, guardar la sesión, actualizar productos, armar el reporte). *Para qué:* ocultar la complejidad tras una sola puerta; añadir un paso (ej. email de resumen) solo cambia el caso de uso.
- **Builder — `SesionBuilder`:** la `Sesion` se arma paso a paso y **validado** (antes eran ~15 setters sueltos). Es un `new` por análisis, no un `@Bean`, para que su estado no se comparta entre usuarios concurrentes. *Para qué:* construcción segura y legible.
- **Observer — registro → correo:** al registrarse un usuario se publica `UserRegisteredEvent`; un listener reacciona y envía el correo, **sin acoplar el registro al email**. *Para qué:* agregar reacciones (SMS, WhatsApp) sin tocar el registro.
- **Qué le pasa al CSV de María aquí:** "El caso de uso recibe las 500 reseñas, pide a la IA que las clasifique (a través de un **puerto**, sin saber qué IA es), cuenta positivos/negativos/neutrales, arma la sesión con el **Builder** y devuelve el reporte. Todo esto es el **Facade**."
- **Handoff (a Jose):** "Pero el dominio solo **define** los puertos. ¿Quién los **implementa** de verdad —quién llama a la IA y quién guarda en Postgres? La infraestructura. Jose."

## BLOQUE 6 — Capa INFRASTRUCTURE · Jose · ~2:30
- **Qué contiene:** los adapters JPA (`*RepositoryAdapter`), el `SentimentApiAdapter`, el `EmailAdapter`, la seguridad (JWT) y la configuración.
- **Patrón: Adapter.** `SentimentApiAdapter implements SentimentAnalysisPort` traduce el **puerto del dominio** a llamadas reales con `WebClient` a la API Python; `ProductoRepositoryAdapter` traduce entre la entidad JPA y el modelo de dominio. *Para qué:* el dominio **nunca importa** una clase de Spring ni de JPA → habilita OCP y DIP. **Cambiar la IA = crear otro adapter**, sin tocar el caso de uso.
- **Detalle fino (buen punto):** el adapter recibe el JSON de la IA y lo **traduce a `ResultadoSentimiento`** (modelo de dominio), para que ni la forma del JSON externo se filtre al núcleo.
- **Patrón: Singleton.** El `WebClient` se define como `@Bean` en `WebClientConfig` → **una sola instancia** reutilizada en todo el sistema (antes se creaba uno nuevo por llamada). *Verificado con `WebClientConfigTest` (`assertSame`).*
- **Seguridad / DIP en acción:** `JwtUtil` **implementa `TokenProviderPort`** (un puerto del dominio). Por eso el caso de uso de login **no depende de la librería de JWT**, solo del contrato. Es el cierre de la historia de los puertos que abrió Jonathan.
- **Qué le pasa al CSV de María aquí:** "Cuando el caso de uso pide clasificar, mi `SentimentApiAdapter` es quien realmente llama a la IA en Python, recibe la respuesta y la convierte al modelo del dominio. Y mis adapters de repositorio guardan la sesión y los comentarios en PostgreSQL."
- **Handoff:** "Veámoslo todo funcionando de verdad. Joaquin, la demo."

## BLOQUE 7 — Demo en vivo + JUnit · Joaquin (narran Jonathan/Jose) · ~3:30
> **Preparación:** backend corriendo, API Python activa, dashboard arriba, CSV a la mano, IDE abierto en 2 clases. Tener plan B (capturas) por si falla la red.

| Paso | Qué mostrar | Qué evidencia |
|---|---|---|
| 1. Registro + login | María crea cuenta y **recibe correo de bienvenida** | **Observer** (application) |
| 2. Subir CSV y analizar | Con **un botón**, positivos/negativos/neutrales por producto | **Facade** (application) + **Adapter** (infra, llama la IA) |
| 3. Dashboard e historial | Gráficas, comparativa, "Ver análisis" de una sesión previa | El sistema guarda cada análisis |
| 4. IDE: `./mvnw test` | **10 pruebas en verde** | **Adapter/Builder/Singleton** verificados |

- **Frase de cierre:** "10 en verde. Una de ellas comprueba que **podemos cambiar la IA sin romper nada** — ese es el Adapter."

## BLOQUE 8 — Conclusiones · Jonathan · ~1:00
- **De modificar a extender (OCP):** cambiar la IA / agregar idiomas → **DIP + OCP**; cambiar la base de datos → bajo acoplamiento; agregar funciones → SRP + alta cohesión.
- **Testeable:** 10 pruebas en verde, gracias al DIP.
- **Cierre con María:** "Para María nada cambió por fuera: sigue subiendo su CSV. Pero por dentro, ahora mejorar el sistema es **agregar**, no reescribir. Eso es diseñar pensando en el futuro. Gracias."

---

## Cómo NO caer en el "tour de carpetas" (clave del híbrido)

- Cada capa se explica **con el CSV de María pasando por ella**, no listando clases.
- Usen los **handoffs** entre expositores (están en cada bloque): dan continuidad y demuestran que entienden cómo se conectan las capas, no solo la suya.
- El **puente Jonathan→Jose** (el dominio *define* el puerto, la infraestructura lo *implementa*) es el momento más fuerte: muéstrenlo como una sola idea contada por dos personas.
- Máximo **1 idea por diapositiva**; si una slide necesita más de 20 s de lectura, sobra texto.

## Anexo — mapeo al sílabo (por si el docente lo pide)

| Unidad | Tema | Capa / dónde |
|---|---|---|
| U1 | SOLID (SRP, OCP, LSP, DIP, ISP) | Transversal; DIP en los puertos (domain/application/infra) |
| U2 | Creacionales: Singleton, Builder | `WebClientConfig` (infra), `SesionBuilder` (application) |
| U3 | Estructurales: Adapter, Facade | `SentimentApiAdapter` (infra), caso de uso CSV (application) |
| U4 | Comportamiento: Observer | `UserRegisteredEvent` + listener (domain/application) |
| U5 | GRASP | Controller (presentation), Alta cohesión / Bajo acoplamiento (transversal) |
