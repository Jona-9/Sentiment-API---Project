# GUION DE PRESENTACIÓN (CANVA) — PROYECTO FINAL
## Sentiment API — Diseño de Patrones

**Equipo (3 expositores):**
- **E1 — Jose Eduardo Diaz Fernandez** → Portada, Problema (AS-IS), Arquitectura (TO-BE) y Conclusiones
- **E2 — Jonathan Edilson Tuppia Lozano** → Patrones clave aplicados (PPT 4 y 5) + narración de la demo
- **E3 — Joaquin Sebastian Chaparro Villavicencio** → Demostración en vivo + pruebas JUnit

**Formato:** **7 diapositivas** (estructura obligatoria de la guía) + demo en vivo, en **15 minutos en total**.
**Regla de oro:** *cero código en las diapositivas* — se explica con diagramas UML y con el "por qué". El código solo se abre en el IDE si el docente lo pide.

### Reparto de los 15 minutos

| PPT | Contenido | Tiempo | Expositor |
|---|---|---|---|
| 1 | Portada | ~0:30 | E1 |
| 2 | El Problema (AS-IS) | ~1:30 | E1 |
| 3 | La Arquitectura (TO-BE) — Diagrama UML | ~2:30 | E1 |
| 4 | Patrón clave 1 (Facade) | ~1:45 | E2 |
| 5 | Patrón clave 2 (Adapter) | ~1:45 | E2 |
| 6 | Demostración rápida (demo en vivo + JUnit) | ~6:00 | E3 (narra E2) |
| 7 | Conclusiones (SOLID + GRASP) | ~1:00 | E1 |
| | **TOTAL** | **~15:00** | |

### Hilo conductor didáctico (úsenlo en TODA la presentación)

Para que la exposición sea **dinámica**, sigan un mismo caso de principio a fin en vez de explicar cada patrón por separado:

> **Caso:** *María, dueña de la tienda "TecnoStore", sube un CSV con 500 reseñas de su producto estrella, la "Laptop X". Quiere saber, en segundos, cuántos clientes hablan bien o mal de ella.*

Ese CSV de María **atraviesa cada patrón**; en cada slide se muestra "qué le pasa a las reseñas de María aquí". Así el jurado ve el sistema **en movimiento**, no cajas sueltas. En la demo (PPT 6) se sube ese mismo CSV en vivo → la historia se cierra sola.

**Regla de oro didáctica:** en los patrones se explican **solo 2** (Facade y Adapter, los que salvaron el proyecto), y cada uno con **un ejemplo de negocio sobre nuestro sistema** (qué le pasa a María), **no** con definiciones de memoria ni código en pantalla.

---

## PARTE A — LAS 7 DIAPOSITIVAS (Canva)

> Cada PPT indica: **título**, **bullets** (poco texto), **notas del orador** (qué decir), **quién expone** y **tiempo**.

### PPT 1 — Portada · [E1] · ~0:30
- **Título:** Sentiment API — Del monolito a Clean Architecture
- **Bullets:**
  - Curso: Diseño de Patrones · Docente: Jose Luis Milla Flores
  - Integrantes y participación: Jose Eduardo Diaz Fernandez (33.3%) · Jonathan Edilson Tuppia Lozano (33.3%) · Joaquin Sebastian Chaparro Villavicencio (33.3%)
  - Lima – Perú, 2026
- **Notas:** "Buenas, somos el equipo de Sentiment API. Rediseñamos un sistema que ya funcionaba aplicando los principios y patrones del curso, para volverlo mantenible."

### PPT 2 — El Problema (AS-IS) · [E1] · ~1:30
- **Título:** La empresa y su software rígido (código espagueti)
- **La empresa (descripción breve):**
  - **Sentiment API** da servicio a negocios que venden productos y reciben **cientos de reseñas** de clientes en **español y portugués** (por ejemplo *TecnoStore*, la tienda de María).
  - Su necesidad: saber **rápido** si los clientes hablan bien o mal de cada producto, para tomar decisiones. Revisar las reseñas a mano es inviable.
- **Cómo fallaba su software (AS-IS):**
  - Funcionaba, pero por dentro **todo estaba pegado**: una sola parte hacía de todo (código espagueti).
  - Cambiar el motor de IA o la base de datos obligaba a **reprogramar medio sistema**.
  - Resultado: cada mejora era **cara y riesgosa** (rigidez, deuda técnica).
- **Notas (presentar a María aquí):** "Imaginen a María, dueña de TecnoStore, con 500 reseñas de su Laptop X. El sistema le daba la respuesta… pero por dentro todo estaba acoplado. Si ella quería otro modelo de IA, había que reescribir el negocio. Ese es el dolor que resolvimos."
- **Visual sugerido:** una caja gigante "TODO JUNTO" con 6 flechas saliendo (BD, IA, estadísticas, sesión, productos, respuesta) → imagen de "código espagueti".

### PPT 3 — La Arquitectura (TO-BE) · [E1] · ~2:30
- **Título:** Clean Architecture — estructura por capas y diagrama de clases
- **Regla:** *sin código, solo cajas.*
- **Aclaración para el equipo:** se muestran **dos vistas complementarias** — (A) la **estructura de paquetes** (cómo se organiza el código) y (B) el **diagrama de clases UML** (cómo se relacionan las clases, lo que pide la guía: "cajas conectadas"). En Canva conviene una animación/build: primero A, luego B.

**Vista A — Estructura de paquetes por capas** (imagen principal del slide; se puede simplificar mostrando solo las carpetas):

```
com.project.sentimentapi/
├── domain/            ← el núcleo: NO conoce Spring ni JPA
│   ├── model/         ← Usuario, Producto, Sesion, Comentario (POJOs, sin @Entity)
│   ├── port/in/       ← AnalizarCsvUseCase, RegistrarUsuarioUseCase…  (contratos de entrada)
│   ├── port/out/      ← SentimentAnalysisPort, ProductoRepositoryPort, EmailPort…  (contratos de salida)
│   ├── event/         ← UserRegisteredEvent (POJO puro → Observer)
│   └── exception/     ← UsuarioNoEncontradaException, SentimentApiException…
├── application/       ← orquesta el negocio; solo depende de domain
│   ├── usecase/       ← AnalizarCsvUseCaseImpl (Facade), RegistrarUsuarioUseCaseImpl…
│   ├── builder/       ← SesionBuilder (Patrón Builder)
│   ├── event/         ← UserRegistrationListener (Observer)
│   └── mapper/        ← UsuarioMapper, SesionMapper, ProductoMapper
├── infrastructure/    ← implementa los contratos del dominio
│   ├── persistence/{entity, repository, adapter}   ← JPA + *RepositoryAdapter (Adapter)
│   ├── external/      ← SentimentApiAdapter (implementa SentimentAnalysisPort)
│   ├── email/         ← EmailAdapter (implementa EmailPort)
│   ├── security/      ← JWT (JwtUtil, filtro, SecurityConfig)
│   └── config/        ← WebClientConfig @Bean (Singleton), EndPointConfg, DataInitializer
└── presentation/      ← la puerta HTTP; llama a los use cases
    ├── controller/    ← CsvAnalysisController, UsuarioController…  (Controller GRASP)
    ├── dto/request/   ← RegistroRequestDto, LoginRequestDto, CsvEntradaDto…
    ├── dto/response/  ← LoginResponseDto, SesionDto, CsvAnalysisResponseDto…
    └── exception/     ← GlobalExceptionHandler (excepciones → códigos HTTP)
```

**Vista B — Diagrama de clases UML** (cajas conectadas; es lo que exige la guía):

```
        ┌───────────────────────┐        «interface»
        │  CsvAnalysisController │ ─────▶ AnalizarCsvUseCase
        └───────────────────────┘               △
             (presentation)                     │ implements
                                                 │
                                   ┌─────────────────────────┐
                                   │  AnalizarCsvUseCaseImpl  │  (application)
                                   └─────────────────────────┘
                                     │ usa (depende de interfaces)
              ┌──────────────────────┼───────────────────────┐
              ▼                      ▼                        ▼
     «interface»            «interface»               ┌──────────────┐
  SentimentAnalysisPort  ProductoRepositoryPort       │ SesionBuilder│─build─▶ Sesion
        △                        △                     └──────────────┘
        │ implements             │ implements
 ┌───────────────────┐  ┌──────────────────────────┐
 │SentimentApiAdapter│  │ ProductoRepositoryAdapter │   (infrastructure)
 └───────────────────┘  └──────────────────────────┘
```

- **Bullets:** Las flechas de dependencia apuntan **hacia adentro** (al dominio) · El dominio define interfaces (ports); la infraestructura las implementa · 4 capas: presentation → application → domain ← infrastructure.
- **🎬 Animación por clic (storyboard en Canva):**
  1. Aparece solo la caja central **`domain`** (el núcleo).
  2. Clic → aparecen `application`, `infrastructure` y `presentation` alrededor.
  3. Clic → se dibujan las **flechas de dependencia apuntando HACIA el centro** (una por una).
  4. Clic → se resalta en rojo una flecha "prohibida" (dominio → JPA) con una ✗ → "esto es lo que evitamos".
- **Notas:** "A la izquierda ven la organización por capas; a la derecha, el diagrama de clases con las relaciones. La idea es la misma: el dominio no conoce Spring ni JPA, y los adapters implementan los contratos que el dominio define. Si un import rompe esa regla, el diseño está mal."

### PPT 4 — Patrón Clave 1: Facade · [E2] · ~1:45
- **Título:** Facade — María hace **una sola cosa**, el sistema hace cinco
- **Ejemplo dinámico (contarlo como una escena, sobre nuestro software):**
  "María tiene **500 reseñas** de su Laptop X. Pregúntenle a un compañero: *¿cuánto tardarías tú en leerlas una por una, decidir cuáles son buenas o malas, contarlas, sacar porcentajes y hacer un gráfico?* → horas.
  En **nuestro sistema**, María solo hace **UNA** acción: arrastra su archivo y presiona **'Analizar'**. En segundos recibe el reporte completo: cuántas positivas, negativas y neutrales, por producto y por categoría.
  Ese botón es el **Facade**: María pide *'el resultado'* y por dentro el sistema hace **cinco tareas** —consultar la IA, contar los sentimientos, guardar el análisis, actualizar los productos y armar el reporte— **sin que ella vea ese trabajo**."
- **Por qué salvó el proyecto:** antes esa complejidad estaba regada; ahora está detrás de una sola 'puerta'. Si el negocio quiere agregar un paso (p. ej. **enviar el reporte por correo**), se añade por dentro y María sigue apretando el mismo botón.
- **Bullets (poco texto):** 1 acción del usuario = 5 tareas internas · La complejidad queda oculta · Fácil de crecer sin afectar a quien lo usa.
- **🎬 Animación por clic (en lenguaje de negocio):**
  1. Aparece María agobiada junto a una **montaña de 500 reseñas**.
  2. Clic → aparece nuestro sistema con **un botón grande: "Analizar"**.
  3. Clic → el botón se presiona y por dentro giran **5 engranajes** (IA → contar → guardar → actualizar productos → reporte).
  4. Clic → sale el **reporte con gráfico** (👍 60% · 👎 25% · 😐 15%) y cara de alivio de María.
- **Frase de cierre:** "Una sola acción para el usuario; cinco tareas por dentro. Eso salvó la simplicidad del sistema."

### PPT 5 — Patrón Clave 2: Adapter · [E2] · ~1:45
- **Título:** Adapter — cambiar el "cerebro de IA" sin apagar el sistema
- **Ejemplo dinámico (contarlo como una escena, sobre nuestro software):**
  "Nuestro sistema tiene un **cerebro de inteligencia artificial** que decide si cada reseña es buena, mala o neutral.
  Pregúntenle a la clase: *¿qué pasa si mañana sale una IA más precisa, o un cliente pide analizar reseñas también en portugués?*
  En **nuestro software**, ese cerebro está **aislado detrás de una conexión estándar**. Reemplazarlo es cambiar **solo esa pieza**: el registro de usuarios, la carga de reseñas, los reportes y el historial **siguen funcionando exactamente igual, sin enterarse del cambio**.
  Eso es el **Adapter**. Antes, ese cerebro estaba 'pegado' a todo el sistema, así que cambiar la IA obligaba a tocar —y arriesgar— el sistema entero."
- **Por qué salvó el proyecto:** le da al negocio la **libertad de mejorar o cambiar la IA** cuando quiera, sin frenar la operación ni romper lo que ya funciona.
- **Bullets (poco texto):** La IA es una pieza reemplazable · Cambiarla no afecta al resto del sistema · El negocio evoluciona sin reprogramar todo.
- **🎬 Animación por clic (en lenguaje de negocio):**
  1. Aparece el sistema funcionando, con un bloque etiquetado **"IA v1"** conectado por un enchufe estándar.
  2. Clic → llega desde arriba un bloque nuevo: **"IA v2 (más precisa)"** / **"IA en portugués"**.
  3. Clic → se **reemplaza SOLO el bloque de IA**; los bloques **Registro · Reportes · Historial** se quedan quietos y **parpadean "sigue funcionando"** en verde.
  4. Clic → aparece un cronómetro: "cambio hecho, cero interrupciones".
- **Frase de cierre:** "El negocio cambia de IA cuando quiera; el resto del sistema ni se entera."

### PPT 6 — Demostración Rápida · [E3] · ~6:00
- **Título:** El caso de María, en vivo + pruebas en verde
- **Bullets:** Registro de María → correo de bienvenida automático · Sube el CSV de la Laptop X → análisis con **un botón** (Facade) · Historial y comparativa · **10 pruebas en verde ✅** (Adapter verificado)
- **Notas:** "Ahora cerramos la historia: hacemos en vivo lo que le pasa a las 500 reseñas de María, y terminamos mostrando las pruebas que demuestran que los patrones funcionan." → *ver PARTE B.*
- **Visual de respaldo (plan B):** captura del dashboard + captura de consola JUnit `Tests run: 10, Failures: 0`.

### PPT 7 — Conclusiones · [E1] · ~1:00
- **Título:** Cómo SOLID y GRASP prepararon el sistema para el futuro
- **El sistema está listo para lo que venga** (cada mejora futura ↔ el principio que la hace posible):
  - **Mejorar o cambiar la IA / agregar idiomas** → posible gracias a **DIP + OCP**: se **agrega** una pieza, no se reescribe el sistema.
  - **Cambiar la base de datos** → gracias a **bajo acoplamiento + fabricación pura**: el negocio ni se entera.
  - **Agregar funciones nuevas** (ej. notificaciones, reportes por correo) → gracias a **SRP + alta cohesión**: cada parte se cambia por separado, sin romper las demás.
  - **Crecer con confianza** → el diseño es **testeable** (10 pruebas en verde), gracias al **DIP**.
- **Idea final:** pasamos de **modificar** (riesgoso) a **extender** (seguro).
- **Notas (cerrar con María):** "Para María nada cambió por fuera: sigue subiendo su CSV y viendo resultados. Pero por dentro, gracias a SOLID y GRASP, si quiere otro modelo de IA, otra base de datos o notificaciones por WhatsApp, ahora es **agregar**, no reescribir. Eso es diseñar pensando en el futuro. Gracias."

---

## PARTE B — GUION DE LA DEMO EN VIVO (contenido del PPT 6, ~6 minutos)

> **Antes de empezar:** backend corriendo (`./mvnw spring-boot:run`), API de sentimientos Python activa, dashboard React arriba, CSV de prueba a la mano, IDE abierto en 2 clases clave. Tener el plan B (capturas/video) por si falla la red.

| Bloque | Tiempo | Quién | Qué mostrar | Qué se evidencia |
|---|---|---|---|---|
| 1. Registro + login | ~1:00 | E3 | María crea su cuenta y **recibe un correo de bienvenida automático** | Funcionalidad automática del sistema |
| 2. Subir CSV y analizar | ~2:30 | E3 (narra E2) | María sube el CSV; con **un botón** el sistema devuelve positivos/negativos/neutrales por producto y categoría | **Facade** (una acción = todo el análisis) |
| 3. Dashboard e historial | ~1:30 | E3 | Gráficas de sentimiento, comparativa de productos, "Ver análisis" de una sesión previa | El sistema guarda cada análisis |
| 4. IDE: pruebas en verde | ~1:00 | E2 | Correr `./mvnw test` → **10 en verde** (incluye la prueba de que se puede cambiar la IA sin romper nada) | **Adapter** verificado por las pruebas |
| **Subtotal demo** | **~6:00** | | | |

### Guion hablado de la demo (frases sugeridas — cierran la historia de María)
1. **(E3, registro):** "María crea su cuenta y, sin que nadie haga nada más, **le llega un correo de bienvenida**. El sistema reacciona solo a cada registro nuevo."
2. **(E3, CSV / E2 narra):** "María sube su CSV de la Laptop X y aprieta **un solo botón**. En segundos tiene el reporte: cuántas reseñas positivas, negativas y neutrales. Eso es el **Facade** en acción — una acción de ella, cinco tareas por dentro."
3. **(E3, historial):** "Aquí quedó guardado su análisis: puede volver cuando quiera, ver el detalle de cada comentario y comparar productos."
4. **(E2, pruebas):** "Y para demostrar que está bien hecho, corremos las pruebas: **10 en verde**. Una de ellas comprueba que **podemos cambiar el motor de IA sin romper el resto del sistema** — ese es el **Adapter** que salvó la flexibilidad del proyecto."

---

## PARTE C — TIPS DE EXPOSICIÓN (de la guía)
1. **Cero código en pantalla:** a nadie le gusta leer código proyectado. Expliquen la lógica con los **diagramas UML** (PPT 3). El código solo se muestra si el docente pide abrir el IDE (bloque 4 de la demo).
2. **Enfocarse en el "por qué":** no digan "creamos una interfaz"; digan "creamos una interfaz aplicando **OCP/DIP** para escalar el sistema el próximo año sin bugs".
3. **Rol del arquitecto:** expongan como si le vendieran la solución al **gerente de sistemas** de la empresa.
4. **Ensayar los tiempos:** son 15 minutos exactos; si algo se cae en la demo, pasar de inmediato al plan B (capturas) sin perder el hilo.
5. **Cierre fuerte:** repetir la idea central — *de modificar a extender* (OCP).

### Cómo hacerlo DINÁMICO en Canva (no leer diapositivas)
- **Un patrón = una escena del negocio, animada:** cada patrón se muestra como algo que le pasa a María y a su tienda (cajas con nombres del negocio: **"Analizar" · "IA" · "Reportes" · "Registro" · "Historial"**), no con nombres de clases. El texto va en su boca, no en la slide.
- **Animaciones por clic (build):** que las **500 reseñas de María avancen** paso a paso mientras hablan (llega el archivo → el sistema trabaja → aparece el reporte). Evita el muro de texto y muestra el sistema en movimiento.
- **Hilo conductor de María:** un pequeño avatar/ícono de María que reaparece en cada slide siguiendo su CSV.
- **Transición hacia la demo:** la última animación del PPT 5 (la caja `analizar()`) enlaza con el PPT 6, donde ese mismo método se ejecuta **de verdad** en vivo con el CSV de la Laptop X.
- **Regla 1 idea / 1 slide / 1 respiro:** si una diapositiva necesita más de 20 segundos de lectura, sobra texto.

### Mecánica concreta en Canva (para lograr el "por clic")
- Selecciona un elemento → botón **Animar** → efecto de entrada (**Aparecer / Desvanecer / Deslizar**). Repite en cada caja/flecha para que salgan una por una.
- Ordena la salida con **temporización**: si usas "al hacer clic" no existe como en PowerPoint, usa **retrasos escalonados** (0.0s, 0.3s, 0.6s…) o **una diapositiva por paso** (duplicar la slide y agregar un elemento en cada copia) para simular el avance por clic — es lo más fiable en Canva.
- El **"dato" de María** (un rectángulo/ícono de CSV) se anima con **Deslizar** de una caja a la siguiente para dar la sensación de flujo.
- Usa **Transiciones** entre diapositivas (Desvanecer/Deslizar) suaves; evita efectos bruscos.
- Ensaya con **Presentar → Modo presentador** para calzar la narración con cada aparición.
