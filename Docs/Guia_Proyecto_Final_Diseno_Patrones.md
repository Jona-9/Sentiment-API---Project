# MANUAL DETALLADO: PROYECTO FINAL DE DISEÑO DE PATRONES

**Curso:** Diseño de Patrones (1000005147)
**Enfoque:** Arquitectura Limpia, Pruebas Unitarias y Sustentación.

---

## PARTE 1: Desarrollo del Proyecto Paso a Paso (Caso: UTP-Express)

Para asegurar el éxito del proyecto, los estudiantes deben seguir este flujo de trabajo iterativo en NetBeans:

### Paso 1: Configuración y Modelado Inicial (Semanas 1-5)

1. **Crear el Proyecto:** File > New Project > Java with Maven.
2. **Definir el Problema (AS-IS):** Crear el código monolítico intencionalmente para entender el dolor. Por ejemplo, una clase `GestorLogistico` que hace cálculos, conexiones a BD y envíos de correo.
3. **Aplicar SOLID (TO-BE):**
   - **SRP & ISP:** Separar en `CalculadorTarifa`, `Notificador` y `Repositorio`.
   - **OCP:** Crear la interfaz `ITransporte` para que el sistema soporte camiones y barcos sin usar switch/case.

### Paso 2: Escalabilidad y Estructura (Semanas 6-11)

1. **Patrones Creacionales:**
   - Implementar **Singleton** para la clase `ConexionBaseDatos` (garantizando que solo haya una conexión activa en todo el sistema).
   - Implementar **Factory Method** para crear las instancias de `ITransporte` (`CamionFactory`, `BarcoFactory`) dependiendo de la zona de envío.
2. **Patrones Estructurales:**
   - Usar un **Facade** (`LogisticaFacade`) para que la interfaz de usuario (Consola o GUI) solo llame a un método `procesarEnvio()`, ocultando la complejidad de las fábricas y bases de datos.

### Paso 3: Interacción Dinámica (Semanas 12-15)

1. **Patrones de Comportamiento:**
   - Aplicar **State** al paquete: Estado `EnAlmacen`, `EnTransito`, `Entregado`. Cada estado tiene reglas diferentes (ej. no se puede cancelar si ya está en tránsito).
   - Aplicar **Observer**: Cuando el paquete cambia de estado, el `Notificador` (Observador) envía un correo automáticamente al cliente.

### Paso 4: Refinamiento y Cierre (Semanas 16-18)

1. **Aplicar GRASP:** Auditar el código. ¿Quién tiene la responsabilidad de crear el paquete? (Patrón Creador). ¿La lógica está altamente cohesionada?
2. **Integrar Pruebas Unitarias:** Validar que los patrones no rompan la lógica (Ver Parte 2).

---

## PARTE 2: Integración de Pruebas Unitarias (JUnit 5)

Las pruebas unitarias son obligatorias para demostrar que los patrones funcionan correctamente y que el código es confiable. En NetBeans, los alumnos deben agregar dependencias de JUnit en su archivo `pom.xml` y crear clases de prueba.

### Ejemplo 1: Probando el Patrón Singleton

El alumno debe demostrar matemáticamente que el patrón Singleton está retornando exactamente el mismo espacio en memoria.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConexionBaseDatosTest {

    @Test
    public void testSingletonInstanciaUnica() {
        // Act (Acción)
        ConexionBaseDatos conexion1 = ConexionBaseDatos.getInstance();
        ConexionBaseDatos conexion2 = ConexionBaseDatos.getInstance();

        // Assert (Verificación)
        assertSame(conexion1, conexion2, "Fallo: Singleton está creando múltiples instancias");
    }
}
```

### Ejemplo 2: Probando el Patrón Factory Method

Validar que la fábrica entregue la clase correcta aplicando polimorfismo.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransporteFactoryTest {

    @Test
    public void testCreacionTransporteTerrestre() {
        TransporteFactory fabrica = new CamionFactory();
        ITransporte transporte = fabrica.crearTransporte();

        // Verifica que la fábrica nos dio un Camión y no otra cosa
        assertTrue(transporte instanceof Camion, "La fábrica no generó un objeto Camion");
    }
}
```

---

## PARTE 3: Pautas para la Exposición y Presentación (PowerPoint)

La sustentación de la Semana 18 evalúa la capacidad de comunicación técnica. El equipo debe preparar una presentación (PPT) de máximo 10 minutos.

### Estructura Obligatoria de las Diapositivas (PPT):

- **PPT 1: Portada.** Título del proyecto, integrantes y porcentajes de participación.
- **PPT 2: El Problema (AS-IS).** Descripción breve de la empresa y cómo su software fallaba (rigidez, código espagueti).
- **PPT 3: La Arquitectura (TO-BE).** Mostrar el Diagrama de Clases UML final. No mostrar código aquí, solo las "cajas" conectadas.
- **PPT 4-5: Patrones Clave Aplicados.** Explicar 2 patrones específicos que salvaron el proyecto. Ejemplo: "Usamos Observer para automatizar correos y evitar acoplamiento".
- **PPT 6: Demostración Rápida.** Capturas de las pruebas unitarias en verde (JUnit) o un video corto del software funcionando.
- **PPT 7: Conclusiones.** Cómo el uso de GRASP y SOLID preparó el sistema para el futuro.

### Tips de Exposición para el Estudiante:

1. **Cero Código en Pantalla:** A nadie le gusta leer código fuente proyectado en un proyector. Expliquen la lógica con diagramas UML. El código solo se muestra si el docente pide abrir el IDE.
2. **Enfocarse en el "Por qué":** No digan "Creamos una interfaz". Digan "Creamos una interfaz aplicando OCP para poder escalar el sistema el próximo año sin bugs".
3. **Rol del Arquitecto:** Expongan como si estuvieran vendiendo la solución al gerente de sistemas de la empresa.

---

## PARTE 4: Formato del Documento Final (PDF)

El documento escrito que se sube a la plataforma debe mantener este orden:

1. Carátula Institucional.
2. Índice.
3. **Capítulo 1:** Descripción del Negocio y Problemática.
4. **Capítulo 2:** Análisis AS-IS (Código Malo). Incluye diagramas de por qué fallaba.
5. **Capítulo 3:** Arquitectura TO-BE.
   - Subsección 3.1: Principios SOLID implementados.
   - Subsección 3.2: Patrones Creacionales y Estructurales justificados.
   - Subsección 3.3: Patrones de Comportamiento justificados.
6. **Capítulo 4:** Pruebas Unitarias. Evidencia de la ejecución en JUnit.
7. Conclusiones y Link al Repositorio GitHub.
