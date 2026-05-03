package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.*;
import com.project.sentimentapi.dto.CsvUploadRequestDto.CsvRowDto;
import com.project.sentimentapi.dto.CsvAnalysisResponseDto.*;
import com.project.sentimentapi.entity.*;
import com.project.sentimentapi.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CsvAnalysisServiceImplement implements CsvAnalysisService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private SentimentService sentimentService;

    @Override
    @Transactional
    public CsvAnalysisResponseDto procesarYAnalizarCsv(List<CsvRowDto> rows, Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Crear o encontrar categorias y productos
        Map<String, Categoria> categoriasMap = new HashMap<>();
        Map<String, Producto> productosMap = new HashMap<>();
        
        for (CsvRowDto row : rows) {
            String catKey = row.getCategoria().trim().toLowerCase();
            String prodKey = catKey + "|" + row.getProducto().trim().toLowerCase();

            // Crear categoria si no existe
            if (!categoriasMap.containsKey(catKey)) {
                Categoria cat = categoriaRepository
                        .findByNombreCategoriaIgnoreCaseAndUsuario(row.getCategoria().trim(), usuario)
                        .orElseGet(() -> {
                            Categoria nueva = new Categoria(row.getCategoria().trim(), "Importado desde CSV", usuario);
                            return categoriaRepository.save(nueva);
                        });
                categoriasMap.put(catKey, cat);
            }

            // Crear producto si no existe
            if (!productosMap.containsKey(prodKey)) {
                Categoria cat = categoriasMap.get(catKey);
                Producto prod = productoRepository
                        .findByNombreProductoIgnoreCaseAndCategoriaAndUsuario(row.getProducto().trim(), cat, usuario)
                        .orElseGet(() -> {
                            Producto nuevo = new Producto(row.getProducto().trim(), cat, usuario);
                            return productoRepository.save(nuevo);
                        });
                productosMap.put(prodKey, prod);
            }
        }

        // 2. Extraer comentarios y analizar
        List<String> comentarios = rows.stream()
                .map(CsvRowDto::getComentario)
                .collect(Collectors.toList());

        String textoCompleto = String.join("\n", comentarios);
        Optional<SentimentsResponseDto> responseOpt = sentimentService.consultarSentimientos(textoCompleto);

        if (responseOpt.isEmpty()) {
            throw new RuntimeException("Error al analizar comentarios");
        }

        List<ResponseDto> resultados = responseOpt.get().getResults();

        // 3. Calcular estadisticas generales y por producto
        int total = resultados.size();
        int positivos = 0, negativos = 0, neutrales = 0;
        double sumaScores = 0.0;

        // Contadores por producto
        Map<String, int[]> contadoresProducto = new HashMap<>(); // [total, pos, neg, neu]
        Map<String, int[]> contadoresCategoria = new HashMap<>();

        List<Comentario> comentariosEntidades = new ArrayList<>();
        List<ComentarioDto> comentariosDto = new ArrayList<>();

        // Crear sesion temporal
        Sesion sesion = new Sesion();
        sesion.setFecha(LocalDateTime.now());
        sesion.setUsuario(usuario);

        for (int i = 0; i < rows.size(); i++) {
            CsvRowDto row = rows.get(i);
            ResponseDto resultado = resultados.get(i);
            String sentimiento = resultado.getPrevision();
            double prob = resultado.getProbabilidad();

            sumaScores += prob;

            if (sentimiento.equalsIgnoreCase("Positivo")) positivos++;
            else if (sentimiento.equalsIgnoreCase("Negativo")) negativos++;
            else neutrales++;

            // Contar por producto
            String catKey = row.getCategoria().trim().toLowerCase();
            String prodKey = catKey + "|" + row.getProducto().trim().toLowerCase();

            contadoresProducto.computeIfAbsent(prodKey, k -> new int[4]);
            int[] cp = contadoresProducto.get(prodKey);
            cp[0]++;
            if (sentimiento.equalsIgnoreCase("Positivo")) cp[1]++;
            else if (sentimiento.equalsIgnoreCase("Negativo")) cp[2]++;
            else cp[3]++;

            // Contar por categoria
            contadoresCategoria.computeIfAbsent(catKey, k -> new int[4]);
            int[] cc = contadoresCategoria.get(catKey);
            cc[0]++;
            if (sentimiento.equalsIgnoreCase("Positivo")) cc[1]++;
            else if (sentimiento.equalsIgnoreCase("Negativo")) cc[2]++;
            else cc[3]++;

            // Crear comentario
            Comentario com = new Comentario(row.getComentario(), sentimiento, prob, sesion);
            comentariosEntidades.add(com);
            comentariosDto.add(new ComentarioDto(row.getComentario(), sentimiento, prob));
        }

        double avgScore = total > 0 ? sumaScores / total : 0.0;

        // 4. Guardar sesion
        sesion.setAvgScore(avgScore);
        sesion.setTotal(total);
        sesion.setPositivos(positivos);
        sesion.setNegativos(negativos);
        sesion.setNeutrales(neutrales);
        sesion.setComentarios(comentariosEntidades);
        Sesion sesionGuardada = sesionRepository.save(sesion);

        // 5. Actualizar contadores de productos en BD
        for (Map.Entry<String, int[]> entry : contadoresProducto.entrySet()) {
            Producto prod = productosMap.get(entry.getKey());
            int[] c = entry.getValue();
            prod.incrementarContadores(c[1], c[2], c[3]);
            productoRepository.save(prod);
        }

        // 6. Construir respuesta
        List<ProductoAnalisisDto> productosAnalisis = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : contadoresProducto.entrySet()) {
            Producto prod = productosMap.get(entry.getKey());
            int[] c = entry.getValue();
            double porcPos = c[0] > 0 ? (c[1] * 100.0) / c[0] : 0;
            
            productosAnalisis.add(new ProductoAnalisisDto(
                    prod.getProductoId(),
                    prod.getNombreProducto(),
                    prod.getCategoria().getNombreCategoria(),
                    c[0], c[1], c[2], c[3], porcPos
            ));
        }

        List<CategoriaAnalisisDto> categoriasAnalisis = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : contadoresCategoria.entrySet()) {
            Categoria cat = categoriasMap.get(entry.getKey());
            int[] c = entry.getValue();
            double porcPos = c[0] > 0 ? (c[1] * 100.0) / c[0] : 0;
            
            categoriasAnalisis.add(new CategoriaAnalisisDto(
                    cat.getCategoriaId(),
                    cat.getNombreCategoria(),
                    c[0], c[1], c[2], c[3], porcPos
            ));
        }

        return new CsvAnalysisResponseDto(
                sesionGuardada.getSesionId(),
                LocalDateTime.now().toString(),
                total, positivos, negativos, neutrales, avgScore,
                categoriasAnalisis, productosAnalisis, comentariosDto
        );
    }

    @Override
    public List<ProductoAnalisisDto> obtenerComparativaProductos(Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Producto> productos = productoRepository.findByUsuario(usuario);

        return productos.stream()
                .filter(p -> p.getTotalMenciones() > 0)
                .map(p -> new ProductoAnalisisDto(
                        p.getProductoId(),
                        p.getNombreProducto(),
                        p.getCategoria().getNombreCategoria(),
                        p.getTotalMenciones(),
                        p.getPositivos(),
                        p.getNegativos(),
                        p.getNeutrales(),
                        p.getPorcentajePositivos()
                ))
                .sorted((a, b) -> Double.compare(b.getPorcentajePositivo(), a.getPorcentajePositivo()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaAnalisisDto> obtenerComparativaCategorias(Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Categoria> categorias = categoriaRepository.findByUsuario(usuario);

        return categorias.stream()
                .map(cat -> {
                    int total = 0, pos = 0, neg = 0, neu = 0;
                    for (Producto p : cat.getProductos()) {
                        total += p.getTotalMenciones();
                        pos += p.getPositivos();
                        neg += p.getNegativos();
                        neu += p.getNeutrales();
                    }
                    double porcPos = total > 0 ? (pos * 100.0) / total : 0;
                    return new CategoriaAnalisisDto(
                            cat.getCategoriaId(),
                            cat.getNombreCategoria(),
                            total, pos, neg, neu, porcPos
                    );
                })
                .filter(c -> c.getTotalComentarios() > 0)
                .sorted((a, b) -> Double.compare(b.getPorcentajePositivo(), a.getPorcentajePositivo()))
                .collect(Collectors.toList());
    }
}
