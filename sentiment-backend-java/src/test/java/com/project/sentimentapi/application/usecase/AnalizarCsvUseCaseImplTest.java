package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.domain.model.Categoria;
import com.project.sentimentapi.domain.model.Comentario;
import com.project.sentimentapi.domain.model.Producto;
import com.project.sentimentapi.domain.model.ResultadoSentimiento;
import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.domain.port.out.*;
import com.project.sentimentapi.application.dto.request.CsvEntradaDto;
import com.project.sentimentapi.application.dto.response.CsvAnalysisResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// PRUEBA DEL PATRÓN FACADE + PRINCIPIO DIP (Unidad 3 — estructurales; Unidad 1 — SOLID).
// Se prueba el use case aislado usando DOBLES DE PRUEBA (fakes) que implementan los ports.
// Esto demuestra que el dominio/application NO depende de Spring, JPA ni de la API de ML:
// se puede probar toda la orquestación sin base de datos ni red. Justamente lo que el DIP
// habilita. El código anterior (CsvAnalysisServiceImplement) era imposible de probar así
// porque instanciaba WebClient y repositorios JPA concretos dentro del método.
class AnalizarCsvUseCaseImplTest {

    @Test
    @DisplayName("analizar() calcula correctamente los totales y delega en los ports")
    void calculaEstadisticasYOrquestaLosPorts() {
        // Arrange — el fake de sentimientos devuelve 1 positivo, 1 negativo, 1 neutro
        List<ResultadoSentimiento> respuestaFake = List.of(
                new ResultadoSentimiento("Positivo", 0.9),
                new ResultadoSentimiento("Negativo", 0.8),
                new ResultadoSentimiento("Neutro", 0.5)
        );
        FakeSentimentPort sentimentPort = new FakeSentimentPort(respuestaFake);
        FakeSesionPort sesionPort = new FakeSesionPort();
        FakeComentarioPort comentarioPort = new FakeComentarioPort();

        AnalizarCsvUseCaseImpl useCase = new AnalizarCsvUseCaseImpl(
                sentimentPort,
                new FakeProductoPort(),
                new FakeCategoriaPort(),
                sesionPort,
                comentarioPort
        );

        List<CsvEntradaDto> filas = List.of(
                new CsvEntradaDto("Excelente producto", "Laptop X", "Electronica"),
                new CsvEntradaDto("Muy malo, se rompio", "Laptop X", "Electronica"),
                new CsvEntradaDto("Es normal", "Laptop X", "Electronica")
        );

        // Act
        CsvAnalysisResponseDto resultado = useCase.analizar(filas, 42);

        // Assert — estadísticas globales
        assertEquals(3, resultado.getTotalComentarios());
        assertEquals(1, resultado.getTotalPositivos());
        assertEquals(1, resultado.getTotalNegativos());
        assertEquals(1, resultado.getTotalNeutrales());
        assertEquals(0.733, resultado.getAvgScore(), 0.01, "avgScore = (0.9+0.8+0.5)/3");

        // Assert — el Facade realmente delegó en los ports (DIP en acción)
        assertTrue(sentimentPort.fueLlamado, "Debió llamar al SentimentAnalysisPort");
        assertEquals(99, resultado.getSesionId(), "Debe devolver el id de la sesión guardada");
        assertEquals(3, comentarioPort.comentariosGuardados,
                "Debió persistir los 3 comentarios vía ComentarioRepositoryPort");
    }

    @Test
    @DisplayName("analizar() con lista vacía lanza IllegalArgumentException")
    void fallaConCsvVacio() {
        AnalizarCsvUseCaseImpl useCase = new AnalizarCsvUseCaseImpl(
                new FakeSentimentPort(List.of()),
                new FakeProductoPort(), new FakeCategoriaPort(),
                new FakeSesionPort(), new FakeComentarioPort());

        assertThrows(IllegalArgumentException.class,
                () -> useCase.analizar(new ArrayList<>(), 1));
    }

    // ───────────────────────── Dobles de prueba (fakes) ─────────────────────────
    // Cada uno implementa un port del dominio: son la prueba viviente de que el
    // use case programa contra ABSTRACCIONES (DIP), no contra JPA/WebClient.

    static class FakeSentimentPort implements SentimentAnalysisPort {
        private final List<ResultadoSentimiento> respuesta;
        boolean fueLlamado = false;
        FakeSentimentPort(List<ResultadoSentimiento> respuesta) { this.respuesta = respuesta; }
        @Override public Optional<List<ResultadoSentimiento>> analizarLote(List<String> textos) {
            fueLlamado = true;
            return Optional.of(respuesta);
        }
    }

    static class FakeSesionPort implements SesionRepositoryPort {
        @Override public Sesion guardar(Sesion sesion) { sesion.setId(99); return sesion; }
        @Override public Optional<Sesion> buscarPorId(Integer id) { return Optional.empty(); }
        @Override public List<Sesion> buscarPorUsuario(Integer usuarioId) { return List.of(); }
    }

    static class FakeComentarioPort implements ComentarioRepositoryPort {
        int comentariosGuardados = 0;
        @Override public Comentario guardar(Comentario comentario) { comentariosGuardados++; return comentario; }
        @Override public List<Comentario> guardarTodos(List<Comentario> comentarios) {
            comentariosGuardados += comentarios.size();
            return comentarios;
        }
        @Override public List<Comentario> buscarPorSesion(Integer sesionId) { return List.of(); }
    }

    static class FakeProductoPort implements ProductoRepositoryPort {
        @Override public List<Producto> obtenerActivos() { return List.of(); }
        @Override public Optional<Producto> buscarPorId(Integer id) { return Optional.empty(); }
        @Override public Optional<Producto> buscarPorNombre(String nombre) { return Optional.empty(); }
        @Override public Producto guardar(Producto producto) { return producto; }
        @Override public List<Producto> guardarTodos(List<Producto> productos) { return productos; }
    }

    static class FakeCategoriaPort implements CategoriaRepositoryPort {
        @Override public List<Categoria> obtenerPorUsuario(Integer usuarioId) { return List.of(); }
        @Override public Optional<Categoria> buscarPorNombreYUsuario(String nombre, Integer usuarioId) { return Optional.empty(); }
        @Override public Categoria guardar(Categoria categoria) { return categoria; }
    }
}
