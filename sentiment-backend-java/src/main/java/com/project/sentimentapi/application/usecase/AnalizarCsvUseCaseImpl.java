package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.application.builder.SesionBuilder;
import com.project.sentimentapi.domain.exception.SentimentApiException;
import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.domain.port.in.AnalizarCsvUseCase;
import com.project.sentimentapi.domain.model.Comentario;
import com.project.sentimentapi.domain.port.out.CategoriaRepositoryPort;
import com.project.sentimentapi.domain.port.out.ComentarioRepositoryPort;
import com.project.sentimentapi.domain.port.out.ProductoRepositoryPort;
import com.project.sentimentapi.domain.port.out.SentimentAnalysisPort;
import com.project.sentimentapi.domain.port.out.SesionRepositoryPort;
import com.project.sentimentapi.presentation.dto.response.CsvAnalysisResponseDto;
import com.project.sentimentapi.presentation.dto.response.CsvAnalysisResponseDto.CategoriaAnalisisDto;
import com.project.sentimentapi.presentation.dto.response.CsvAnalysisResponseDto.ProductoAnalisisDto;
import com.project.sentimentapi.presentation.dto.request.CsvEntradaDto;
import com.project.sentimentapi.presentation.dto.response.ComentarioDto;
import com.project.sentimentapi.presentation.dto.response.ResponseDto;
import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// Patrón FACADE: el controller llama un solo método;
// este use case coordina internamente sentiment API, productos, sesión y estadísticas.
// Principio SRP: cada responsabilidad está en su propio método privado.
// Principio DIP: todas las dependencias son interfaces (ports), no clases concretas.
@Service
public class AnalizarCsvUseCaseImpl implements AnalizarCsvUseCase {

    private final SentimentAnalysisPort sentimentPort;
    private final ProductoRepositoryPort productoPort;
    private final CategoriaRepositoryPort categoriaPort;
    private final SesionRepositoryPort sesionPort;
    private final ComentarioRepositoryPort comentarioPort;

    public AnalizarCsvUseCaseImpl(SentimentAnalysisPort sentimentPort,
                                  ProductoRepositoryPort productoPort,
                                  CategoriaRepositoryPort categoriaPort,
                                  SesionRepositoryPort sesionPort,
                                  ComentarioRepositoryPort comentarioPort) {
        this.sentimentPort = sentimentPort;
        this.productoPort = productoPort;
        this.categoriaPort = categoriaPort;
        this.sesionPort = sesionPort;
        this.comentarioPort = comentarioPort;
    }

    @Override
    @Transactional
    public CsvAnalysisResponseDto analizar(List<CsvEntradaDto> filas, Integer usuarioId) {
        if (filas == null || filas.isEmpty()) {
            throw new IllegalArgumentException("El CSV no contiene filas válidas");
        }

        // PASO 1: Extraer textos para enviar a la API de sentimientos
        List<String> textos = filas.stream()
                .map(CsvEntradaDto::getTexto)
                .filter(t -> t != null && !t.isBlank())
                .collect(Collectors.toList());

        // PASO 2: Llamar a la API de sentimientos (Facade delega al port — OCP aplicado)
        SentimentsResponseDto sentiments = sentimentPort.analizarLote(textos)
                .orElseThrow(() -> new SentimentApiException(
                        "El servicio de análisis de sentimientos no respondió"));

        List<ResponseDto> resultados = sentiments.getResults();

        // PASO 3: Calcular estadísticas generales
        int total = resultados.size();
        int positivos = 0, negativos = 0, neutrales = 0;
        double sumaScores = 0.0;

        // Contadores por producto y categoría
        Map<String, int[]> contadoresProd = new HashMap<>();  // [total, pos, neg, neu]
        Map<String, int[]> contadoresCat  = new HashMap<>();
        List<ComentarioDto> comentariosDto = new ArrayList<>();

        for (int i = 0; i < filas.size(); i++) {
            CsvEntradaDto fila = filas.get(i);
            ResponseDto resultado = resultados.get(i);
            String sentimiento = resultado.getPrevision();
            double prob = resultado.getProbabilidad() != null ? resultado.getProbabilidad() : 0.0;

            sumaScores += prob;
            if ("Positivo".equalsIgnoreCase(sentimiento)) positivos++;
            else if ("Negativo".equalsIgnoreCase(sentimiento)) negativos++;
            else neutrales++;

            // Acumular por producto
            String catKey  = fila.getCategoria() != null ? fila.getCategoria().trim().toLowerCase() : "sin_categoria";
            String prodKey = catKey + "|" + (fila.getProducto() != null ? fila.getProducto().trim().toLowerCase() : "sin_producto");

            acumular(contadoresProd, prodKey, sentimiento);
            acumular(contadoresCat,  catKey,  sentimiento);

            comentariosDto.add(new ComentarioDto(fila.getTexto(), sentimiento, prob, productoNombre(fila)));
        }

        double avgScore = total > 0 ? sumaScores / total : 0.0;

        // PASO 4: Construir y guardar la sesión (Patrón BUILDER)
        // Builder local (no compartido) → seguro ante análisis concurrentes de varios usuarios.
        Sesion sesion = new SesionBuilder()
                .conUsuario(usuarioId)
                .conFecha(LocalDateTime.now())
                .conTotalComentarios(total)
                .conEstadisticas(positivos, negativos, neutrales, avgScore)
                .build();

        Sesion sesionGuardada = sesionPort.guardar(sesion);

        // PASO 4.5: Guardar comentarios en BD vinculados a la sesión
        // (sin esto el historial no puede mostrar el detalle de cada análisis)
        List<Comentario> comentariosDominio = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            CsvEntradaDto fila = filas.get(i);
            ResponseDto resultado = resultados.get(i);
            comentariosDominio.add(new Comentario(
                    null,
                    fila.getTexto(),
                    resultado.getPrevision(),
                    resultado.getProbabilidad() != null ? resultado.getProbabilidad() : 0.0,
                    sesionGuardada.getId(),
                    productoNombre(fila)
            ));
        }
        comentarioPort.guardarTodos(comentariosDominio);

        // PASO 5: Actualizar contadores de productos en BD
        actualizarProductos(filas, contadoresProd, usuarioId);

        // PASO 6: Armar respuesta
        List<ProductoAnalisisDto> productosAnalisis = buildProductosAnalisis(filas, contadoresProd);
        List<CategoriaAnalisisDto> categoriasAnalisis = buildCategoriasAnalisis(filas, contadoresCat);

        return new CsvAnalysisResponseDto(
                sesionGuardada.getId(),
                LocalDateTime.now().toString(),
                total, positivos, negativos, neutrales, avgScore,
                categoriasAnalisis, productosAnalisis, comentariosDto
        );
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    // Normaliza el nombre del producto de una fila: trim, y blank/null → null
    private String productoNombre(CsvEntradaDto fila) {
        return fila.getProducto() != null && !fila.getProducto().isBlank()
                ? fila.getProducto().trim()
                : null;
    }

    private void acumular(Map<String, int[]> mapa, String key, String sentimiento) {
        int[] c = mapa.computeIfAbsent(key, k -> new int[4]);
        c[0]++;
        if ("Positivo".equalsIgnoreCase(sentimiento))  c[1]++;
        else if ("Negativo".equalsIgnoreCase(sentimiento)) c[2]++;
        else c[3]++;
    }

    private void actualizarProductos(List<CsvEntradaDto> filas,
                                     Map<String, int[]> contadoresProd,
                                     Integer usuarioId) {
        // Construir mapa de nombre-clave → producto de dominio
        Map<String, com.project.sentimentapi.domain.model.Producto> prodMap = new HashMap<>();
        for (CsvEntradaDto fila : filas) {
            String catKey  = fila.getCategoria() != null ? fila.getCategoria().trim().toLowerCase() : "sin_categoria";
            String prodKey = catKey + "|" + (fila.getProducto() != null ? fila.getProducto().trim().toLowerCase() : "sin_producto");
            if (!prodMap.containsKey(prodKey)) {
                productoPort.buscarPorNombre(fila.getProducto() != null ? fila.getProducto().trim() : "")
                        .ifPresent(p -> prodMap.put(prodKey, p));
            }
        }
        // Actualizar contadores
        for (Map.Entry<String, int[]> entry : contadoresProd.entrySet()) {
            com.project.sentimentapi.domain.model.Producto prod = prodMap.get(entry.getKey());
            if (prod != null) {
                int[] c = entry.getValue();
                prod.setTotalMenciones((prod.getTotalMenciones() != null ? prod.getTotalMenciones() : 0) + c[0]);
                prod.setPositivos((prod.getPositivos() != null ? prod.getPositivos() : 0) + c[1]);
                prod.setNegativos((prod.getNegativos() != null ? prod.getNegativos() : 0) + c[2]);
                prod.setNeutrales((prod.getNeutrales() != null ? prod.getNeutrales() : 0) + c[3]);
                productoPort.guardar(prod);
            }
        }
    }

    private List<ProductoAnalisisDto> buildProductosAnalisis(List<CsvEntradaDto> filas,
                                                             Map<String, int[]> contadoresProd) {
        Map<String, String> prodKeyToNombre = new HashMap<>();
        Map<String, String> prodKeyToCategoria = new HashMap<>();
        for (CsvEntradaDto fila : filas) {
            String catKey  = fila.getCategoria() != null ? fila.getCategoria().trim().toLowerCase() : "sin_categoria";
            String prodKey = catKey + "|" + (fila.getProducto() != null ? fila.getProducto().trim().toLowerCase() : "sin_producto");
            prodKeyToNombre.put(prodKey, fila.getProducto());
            prodKeyToCategoria.put(prodKey, fila.getCategoria());
        }

        List<ProductoAnalisisDto> lista = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : contadoresProd.entrySet()) {
            int[] c = entry.getValue();
            double porcPos = c[0] > 0 ? (c[1] * 100.0) / c[0] : 0;
            lista.add(new ProductoAnalisisDto(
                    null,
                    prodKeyToNombre.getOrDefault(entry.getKey(), entry.getKey()),
                    prodKeyToCategoria.getOrDefault(entry.getKey(), ""),
                    c[0], c[1], c[2], c[3], porcPos
            ));
        }
        return lista;
    }

    private List<CategoriaAnalisisDto> buildCategoriasAnalisis(List<CsvEntradaDto> filas,
                                                               Map<String, int[]> contadoresCat) {
        Map<String, String> catKeyToNombre = new HashMap<>();
        for (CsvEntradaDto fila : filas) {
            String catKey = fila.getCategoria() != null ? fila.getCategoria().trim().toLowerCase() : "sin_categoria";
            catKeyToNombre.put(catKey, fila.getCategoria());
        }

        List<CategoriaAnalisisDto> lista = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : contadoresCat.entrySet()) {
            int[] c = entry.getValue();
            double porcPos = c[0] > 0 ? (c[1] * 100.0) / c[0] : 0;
            lista.add(new CategoriaAnalisisDto(
                    null,
                    catKeyToNombre.getOrDefault(entry.getKey(), entry.getKey()),
                    c[0], c[1], c[2], c[3], porcPos
            ));
        }
        return lista;
    }
}