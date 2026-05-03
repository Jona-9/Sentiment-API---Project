package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.*;
import com.project.sentimentapi.entity.*;
import com.project.sentimentapi.repository.CategoriaRepository;
import com.project.sentimentapi.repository.ProductoRepository;
import com.project.sentimentapi.repository.SesionProductoRepository;
import com.project.sentimentapi.repository.SesionRepository;
import com.project.sentimentapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.stream.Collectors;

@Service
    public class SesionServiceImplement implements SesionService {

        @Autowired
        SesionRepository sesionRepository;

    @Autowired
    SesionProductoRepository sesionProductoRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        SentimentService sentimentService;

        @Autowired
        ProductoRepository productoRepository;

        @Autowired
        ProductoService productoService;

        @Autowired
        CategoriaRepository categoriaRepository;


        @Override
        public void guardarSesion(SesionDto sesionDto, Integer usuarioId) {
            Optional<User> usuario = userRepository.findById(usuarioId);

            if (usuario.isPresent()) {
                Sesion sesion = new Sesion(
                        LocalDateTime.now(),
                        sesionDto.getAvgScore(),
                        sesionDto.getTotal(),
                        sesionDto.getPositivos(),
                        sesionDto.getNegativos(),
                        sesionDto.getNeutrales(),
                        usuario.get()
                );

                sesionRepository.save(sesion);
            }
        }

        @Override
        public List<SesionDto> obtenerSesionesPorUsuario(Integer usuarioId) {
            Optional<User> usuario = userRepository.findById(usuarioId);

            if (usuario.isPresent()) {
                List<Sesion> sesiones = sesionRepository.findByUsuarioOrderBySesionIdDesc(usuario.get());

                return sesiones.stream()
                        .map(sesion -> {
                            // Obtener productos asociados a esta sesión
                            List<SesionProducto> sesionProductos = sesionProductoRepository.findBySesion(sesion);

                            // Construir mapa producto_id → nombreProducto para asociar comentarios
                            Map<Integer, String> productoIdToName = new HashMap<>();
                            sesionProductos.forEach(sp ->
                                productoIdToName.put(sp.getProducto().getProductoId(), sp.getProducto().getNombreProducto())
                            );

                            // Mapear comentarios a DTOs — intentar asociar productoAsociado
                            List<ComentarioDto> comentariosDto = sesion.getComentarios().stream()
                                    .map(c -> {
                                        // Inferir producto del comentario comparando con los productos de la sesión
                                        String productoAsociado = null;
                                        if (!productoIdToName.isEmpty()) {
                                            String textoLower = c.getTexto().toLowerCase();
                                            for (Map.Entry<Integer, String> entry : productoIdToName.entrySet()) {
                                                if (textoLower.contains(entry.getValue().toLowerCase())) {
                                                    productoAsociado = entry.getValue();
                                                    break;
                                                }
                                            }
                                        }
                                        return new ComentarioDto(c.getTexto(), c.getSentimiento(), c.getProbabilidad(), productoAsociado);
                                    })
                                    .collect(Collectors.toList());

                            SesionDto dto = new SesionDto(
                                    sesion.getSesionId(),
                                    sesion.getFecha().toString(),
                                    sesion.getAvgScore(),
                                    sesion.getTotal(),
                                    sesion.getPositivos(),
                                    sesion.getNegativos(),
                                    sesion.getNeutrales(),
                                    comentariosDto
                            );

                            // Agregar productosDetectados con estadísticas por producto
                            if (!sesionProductos.isEmpty()) {
                                int totalSesion = sesion.getTotal() != null ? sesion.getTotal() : 1;
                                List<ProductoMencionesDto> productosDetectados = sesionProductos.stream()
                                        .map(sp -> new ProductoMencionesDto(
                                                sp.getProducto().getNombreProducto(),
                                                sp.getMencionesSesion(),
                                                sp.getPositivosSesion(),
                                                sp.getNegativosSesion(),
                                                sp.getNeutralesSesion(),
                                                ((double) sp.getMencionesSesion() / totalSesion) * 100
                                        ))
                                        .collect(Collectors.toList());
                                dto.setProductosDetectados(productosDetectados);
                            }

                            return dto;
                        })
                        .collect(Collectors.toList());
            }

            return List.of();
        }
    @Override
    @Transactional
    public SesionDto analizarYGuardarComentarios(List<String> comentarios, Integer usuarioId) {
        System.out.println("✅ Analizando comentarios para usuario ID: " + usuarioId);

        Optional<User> usuario = userRepository.findById(usuarioId);

        if (usuario.isEmpty()) {
            System.err.println("❌ Usuario no encontrado con ID: " + usuarioId);
            return null;
        }

        // Concatenar todos los comentarios con salto de línea
        String textoCompleto = String.join("\n", comentarios);

        // Llamar al servicio de análisis batch
        Optional<SentimentsResponseDto> responseOpt =
                sentimentService.consultarSentimientos(textoCompleto);

        if (responseOpt.isEmpty()) {
            System.err.println("❌ Servicio de análisis retornó vacío");
            return null;
        }

        List<ResponseDto> resultados = responseOpt.get().getResults();
        System.out.println("📊 Análisis completado. Resultados: " + resultados.size());

        // Calcular métricas
        int total = resultados.size();
        int positivos = 0;
        int negativos = 0;
        int neutrales = 0;
        double sumaScores = 0.0;

        for (ResponseDto resultado : resultados) {
            String sentimiento = resultado.getPrevision();
            double probabilidad = resultado.getProbabilidad();

            sumaScores += probabilidad;

            if (sentimiento.equalsIgnoreCase("Positivo")) {
                positivos++;
            } else if (sentimiento.equalsIgnoreCase("Negativo")) {
                negativos++;
            } else {
                neutrales++;
            }
        }

        double avgScore = total > 0 ? sumaScores / total : 0.0;

        System.out.println("📈 Estadísticas calculadas:");
        System.out.println("   Total: " + total);
        System.out.println("   Positivos: " + positivos);
        System.out.println("   Negativos: " + negativos);
        System.out.println("   Neutrales: " + neutrales);
        System.out.println("   Avg Score: " + avgScore);

        // ✅ CREAR SESIÓN
        Sesion sesion = new Sesion(
                LocalDateTime.now(),
                avgScore,
                total,
                positivos,
                negativos,
                neutrales,
                usuario.get()
        );

        // ✅ GUARDAR CADA COMENTARIO ANALIZADO
        List<Comentario> comentariosEntidades = new ArrayList<>();
        List<ComentarioDto> comentariosDto = new ArrayList<>();

        for (int i = 0; i < comentarios.size(); i++) {
            String textoComentario = comentarios.get(i);
            ResponseDto resultado = resultados.get(i);

            Comentario comentarioEntity = new Comentario(
                    textoComentario,
                    resultado.getPrevision(),
                    resultado.getProbabilidad(),
                    sesion
            );

            comentariosEntidades.add(comentarioEntity);

            // ✅ Para el DTO de respuesta
            comentariosDto.add(new ComentarioDto(
                    textoComentario,
                    resultado.getPrevision(),
                    resultado.getProbabilidad()
            ));
        }

        sesion.setComentarios(comentariosEntidades);

        // ✅ GUARDAR SESIÓN CON COMENTARIOS
        Sesion sesionGuardada = sesionRepository.save(sesion);

        System.out.println("✅ Sesión guardada exitosamente con ID: " + sesionGuardada.getSesionId());

        // ✅ RETORNAR DTO CON TODA LA INFORMACIÓN
        SesionDto sesionDto = new SesionDto();
        sesionDto.setSesionId(sesionGuardada.getSesionId());
        sesionDto.setFecha(LocalDateTime.now().toString());
        sesionDto.setAvgScore(avgScore);
        sesionDto.setTotal(total);
        sesionDto.setPositivos(positivos);
        sesionDto.setNegativos(negativos);
        sesionDto.setNeutrales(neutrales);
        sesionDto.setComentarios(comentariosDto);

        System.out.println("✅ SesionDto creado: " + sesionDto);

        return sesionDto;
    }
        // ✅ AGREGAR ESTE NUEVO MÉTODO A SesionServiceImplement

        @Transactional
        @Override
        public SesionDto analizarYGuardarConProducto(
                List<String> comentarios,
                Integer usuarioId,
                Integer productoId
        ) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // ✅ Validar permisos
            if (!producto.getUsuario().getUsuarioID().equals(usuarioId)) {
                throw new RuntimeException("No tienes permiso para usar este producto");
            }

            // 1️⃣ ANÁLISIS NORMAL (reutilizamos tu código)
            String textoCompleto = String.join("\n", comentarios);
            Optional<SentimentsResponseDto> responseOpt = sentimentService.consultarSentimientos(textoCompleto);

            if (responseOpt.isEmpty()) {
                throw new RuntimeException("Error al analizar comentarios");
            }

            List<ResponseDto> resultados = responseOpt.get().getResults();

            // 2️⃣ CALCULAR ESTADÍSTICAS GENERALES
            int total = resultados.size();
            int positivos = 0;
            int negativos = 0;
            int neutrales = 0;
            double sumaScores = 0.0;

            for (ResponseDto resultado : resultados) {
                String sentimiento = resultado.getPrevision();
                double probabilidad = resultado.getProbabilidad();

                sumaScores += probabilidad;

                if (sentimiento.equalsIgnoreCase("Positivo")) {
                    positivos++;
                } else if (sentimiento.equalsIgnoreCase("Negativo")) {
                    negativos++;
                } else {
                    neutrales++;
                }
            }

            double avgScore = total > 0 ? sumaScores / total : 0.0;

            // 3️⃣ ⚡ ANÁLISIS ESPECÍFICO DEL PRODUCTO (CONTEO CORRECTO)
            String nombreProductoLower = producto.getNombreProducto().toLowerCase();

            int productoPosi = 0;
            int productoNega = 0;
            int productoNeutr = 0;
            int totalMencionesProducto = 0;

            List<Comentario> comentariosEntidades = new ArrayList<>();
            List<ComentarioDto> comentariosDto = new ArrayList<>();

            for (int i = 0; i < comentarios.size(); i++) {
                String textoComentario = comentarios.get(i);
                ResponseDto resultado = resultados.get(i);

                // ✅ VERIFICAR SI EL COMENTARIO MENCIONA EL PRODUCTO
                boolean mencionaProducto = textoComentario.toLowerCase().contains(nombreProductoLower);

                if (mencionaProducto) {
                    totalMencionesProducto++;

                    // ✅ CONTAR POR SENTIMIENTO
                    String sentimiento = resultado.getPrevision();
                    if (sentimiento.equalsIgnoreCase("Positivo")) {
                        productoPosi++;
                    } else if (sentimiento.equalsIgnoreCase("Negativo")) {
                        productoNega++;
                    } else {
                        productoNeutr++;
                    }
                }

                // Guardar comentario en BD
                Sesion sesionTemp = new Sesion(); // Se asignará después
                Comentario comentarioEntity = new Comentario(
                        textoComentario,
                        resultado.getPrevision(),
                        resultado.getProbabilidad(),
                        sesionTemp
                );
                comentariosEntidades.add(comentarioEntity);

                comentariosDto.add(new ComentarioDto(
                        textoComentario,
                        resultado.getPrevision().toLowerCase(),
                        resultado.getProbabilidad()
                ));
            }

            // 4️⃣ CREAR SESIÓN
            Sesion sesion = new Sesion(
                    LocalDateTime.now(),
                    avgScore,
                    total,
                    positivos,
                    negativos,
                    neutrales,
                    usuario
            );

            // ✅ ASOCIAR PRODUCTO A LA SESIÓN
            sesion.setProducto(producto);

            // Asignar sesión a los comentarios
            for (Comentario com : comentariosEntidades) {
                com.setSesion(sesion);
            }
            sesion.setComentarios(comentariosEntidades);

            // 5️⃣ GUARDAR SESIÓN
            Sesion sesionGuardada = sesionRepository.save(sesion);

            // 6️⃣ ⚡ ACTUALIZAR CONTADORES DEL PRODUCTO (TRANSACCIONAL)
            productoService.actualizarContadoresProducto(
                    productoId,
                    productoPosi,
                    productoNega,
                    productoNeutr
            );

            // 7️⃣ PREPARAR RESPUESTA CON INFO DEL PRODUCTO
            ProductoMencionesDto productoMenciones = new ProductoMencionesDto(
                    producto.getNombreProducto(),
                    totalMencionesProducto,
                    productoPosi,
                    productoNega,
                    productoNeutr,
                    total > 0 ? (totalMencionesProducto * 100.0) / total : 0.0
            );

            SesionDto sesionDto = new SesionDto(
                    sesionGuardada.getSesionId(),
                    LocalDateTime.now().toString(),
                    avgScore,
                    total,
                    positivos,
                    negativos,
                    neutrales,
                    comentariosDto
            );

            // ✅ AGREGAR INFO DEL PRODUCTO
            sesionDto.setProductoId(producto.getProductoId());
            sesionDto.setNombreProducto(producto.getNombreProducto());
            sesionDto.setProductoMenciones(productoMenciones);

            return sesionDto;
        }

        @Override
        public SesionPreviaInfoDto obtenerProductosUltimaSesion(Integer usuarioId) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Sesion> sesiones = sesionRepository.findByUsuarioOrderBySesionIdDesc(usuario);

            if (sesiones.isEmpty()) {
                return null; // No hay sesiones previas
            }

            Sesion ultimaSesion = sesiones.get(0);
            List<SesionProducto> productosUsados = sesionProductoRepository.findBySesion(ultimaSesion);

            if (productosUsados.isEmpty()) {
                return null; // La última sesión no tenía productos
            }

            List<ProductoPrevioDto> productosDto = productosUsados.stream()
                    .map(sp -> new ProductoPrevioDto(
                            sp.getProducto().getProductoId(),
                            sp.getProducto().getNombreProducto(),
                            sp.getProducto().getCategoria().getNombreCategoria(),
                            sp.getMencionesSesion(),
                            sp.getPositivosSesion(),
                            sp.getNegativosSesion()
                    ))
                    .collect(Collectors.toList());

            return new SesionPreviaInfoDto(
                    ultimaSesion.getSesionId(),
                    ultimaSesion.getFecha().toString(),
                    productosDto.size(),
                    productosDto
            );
        }

        /**
         * ✨ NUEVO: Analizar con los mismos productos de una sesión previa
         */
        @Override
        @Transactional
        public SesionDto analizarConMismosProductos(
                List<String> comentarios,
                Integer usuarioId,
                Integer sesionPreviaId
        ) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Sesion sesionPrevia = sesionRepository.findById(sesionPreviaId)
                    .orElseThrow(() -> new RuntimeException("Sesión previa no encontrada"));

            // Verificar que la sesión pertenezca al usuario
            if (!sesionPrevia.getUsuario().getUsuarioID().equals(usuarioId)) {
                throw new RuntimeException("No tienes permiso para acceder a esta sesión");
            }

            // Obtener productos usados en esa sesión
            List<SesionProducto> productosUsados = sesionProductoRepository.findBySesion(sesionPrevia);

            if (productosUsados.isEmpty()) {
                throw new RuntimeException("La sesión no tiene productos asociados");
            }

            List<Integer> productosIds = productosUsados.stream()
                    .map(sp -> sp.getProducto().getProductoId())
                    .collect(Collectors.toList());

            // Reutilizar el método de análisis con múltiples productos
            return analizarConMultiplesProductos(comentarios, usuarioId, productosIds);
        }

        /**
         * ✨ NUEVO: Analizar con múltiples productos seleccionados
         */
        @Override
        @Transactional
        public SesionDto analizarConMultiplesProductos(
                List<String> comentarios,
                Integer usuarioId,
                List<Integer> productosIds
        ) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener productos
            List<Producto> productos = new ArrayList<>();
            for (Integer productoId : productosIds) {
                Producto producto = productoRepository.findById(productoId)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

                if (!producto.getUsuario().getUsuarioID().equals(usuarioId)) {
                    throw new RuntimeException("No tienes permiso para usar el producto: " + producto.getNombreProducto());
                }

                productos.add(producto);
            }

            System.out.println("📊 Analizando con " + productos.size() + " productos:");
            productos.forEach(p -> System.out.println("   - " + p.getNombreProducto()));

            // Análisis de sentimientos
            String textoCompleto = String.join("\n", comentarios);
            Optional<SentimentsResponseDto> responseOpt = sentimentService.consultarSentimientos(textoCompleto);

            if (responseOpt.isEmpty()) {
                throw new RuntimeException("Error al analizar comentarios");
            }

            List<ResponseDto> resultados = responseOpt.get().getResults();

            // Calcular estadísticas generales
            int total = resultados.size();
            int positivos = 0, negativos = 0, neutrales = 0;
            double sumaScores = 0.0;

            for (ResponseDto resultado : resultados) {
                sumaScores += resultado.getProbabilidad();
                String sentimiento = resultado.getPrevision();

                if (sentimiento.equalsIgnoreCase("Positivo")) positivos++;
                else if (sentimiento.equalsIgnoreCase("Negativo")) negativos++;
                else neutrales++;
            }

            double avgScore = total > 0 ? sumaScores / total : 0.0;

            // Detectar productos en comentarios
            HashMap<Integer, ContadorProducto> contadores = new HashMap<>();
            for (Producto p : productos) {
                contadores.put(p.getProductoId(), new ContadorProducto(p.getNombreProducto()));
            }

            List<Comentario> comentariosEntidades = new ArrayList<>();
            List<ComentarioDto> comentariosDto = new ArrayList<>();

            for (int i = 0; i < comentarios.size(); i++) {
                String textoComentario = comentarios.get(i);
                ResponseDto resultado = resultados.get(i);
                String textoLower = textoComentario.toLowerCase();

                // Buscar productos mencionados
                for (Producto producto : productos) {
                    if (textoLower.contains(producto.getNombreProducto().toLowerCase())) {
                        ContadorProducto contador = contadores.get(producto.getProductoId());
                        contador.incrementar(resultado.getPrevision());
                    }
                }

                Sesion sesionTemp = new Sesion();
                comentariosEntidades.add(new Comentario(
                        textoComentario,
                        resultado.getPrevision(),
                        resultado.getProbabilidad(),
                        sesionTemp
                ));

                comentariosDto.add(new ComentarioDto(
                        textoComentario,
                        resultado.getPrevision().toLowerCase(),
                        resultado.getProbabilidad()
                ));
            }

            // Crear sesión
            Sesion sesion = new Sesion(LocalDateTime.now(), avgScore, total, positivos, negativos, neutrales, usuario);
            for (Comentario com : comentariosEntidades) {
                com.setSesion(sesion);
            }
            sesion.setComentarios(comentariosEntidades);
            Sesion sesionGuardada = sesionRepository.save(sesion);

            // Guardar relación sesión-productos y actualizar contadores
            List<ProductoMencionesDto> productosDetectados = new ArrayList<>();

            for (Map.Entry<Integer, ContadorProducto> entry : contadores.entrySet()) {
                Integer productoId = entry.getKey();
                ContadorProducto contador = entry.getValue();

                if (contador.getTotal() > 0) {
                    // Guardar relación
                    SesionProducto sp = new SesionProducto(
                            sesionGuardada,
                            productoRepository.findById(productoId).get(),
                            contador.getTotal(),
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales()
                    );
                    sesionProductoRepository.save(sp);

                    // Actualizar producto
                    productoService.actualizarContadoresProducto(
                            productoId,
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales()
                    );

                    // Agregar a respuesta
                    productosDetectados.add(new ProductoMencionesDto(
                            contador.getNombreProducto(),
                            contador.getTotal(),
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales(),
                            total > 0 ? (contador.getTotal() * 100.0) / total : 0.0
                    ));
                }
            }

            SesionDto sesionDto = new SesionDto(
                    sesionGuardada.getSesionId(),
                    LocalDateTime.now().toString(),
                    avgScore,
                    total,
                    positivos,
                    negativos,
                    neutrales,
                    comentariosDto
            );

            sesionDto.setProductosDetectados(productosDetectados);

            return sesionDto;
        }

        /**
         * ✨ NUEVO: Analizar batch CSV con auto-creación de categorías y productos
         */
        @Override
        @Transactional
        public SesionDto analizarBatchConCsv(List<CsvEntradaDto> entradas, Integer usuarioId) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            System.out.println("📊 Analizando batch CSV con " + entradas.size() + " entradas");

            // 1️⃣ Auto-crear categorías y productos que no existan
            // Mapa de nombreCategoria -> Categoria entity
            Map<String, Categoria> categoriasMap = new HashMap<>();
            // Mapa de nombreProducto -> Producto entity
            Map<String, Producto> productosMap = new HashMap<>();

            for (CsvEntradaDto entrada : entradas) {
                String catNombre = entrada.getCategoria();
                String prodNombre = entrada.getProducto();

                if (catNombre != null && !catNombre.isBlank()) {
                    catNombre = catNombre.trim();
                    if (!categoriasMap.containsKey(catNombre)) {
                        // Buscar o crear categoría
                        String finalCatNombre = catNombre;
                        Categoria cat = categoriaRepository.findByNombreCategoriaAndUsuario(catNombre, usuario)
                                .orElseGet(() -> {
                                    Categoria nueva = new Categoria(finalCatNombre, "Auto-creada desde CSV", usuario);
                                    return categoriaRepository.save(nueva);
                                });
                        categoriasMap.put(catNombre, cat);
                    }
                }

                if (prodNombre != null && !prodNombre.isBlank()) {
                    prodNombre = prodNombre.trim();
                    if (!productosMap.containsKey(prodNombre)) {
                        // Buscar o crear producto
                        String finalProdNombre = prodNombre;
                        String finalCatNombreForProd = catNombre;
                        Producto prod = productoRepository.findByNombreProductoAndUsuario(prodNombre, usuario)
                                .orElseGet(() -> {
                                    // Determinar categoría para el producto
                                    Categoria catParaProducto = null;
                                    if (finalCatNombreForProd != null && !finalCatNombreForProd.isBlank()) {
                                        catParaProducto = categoriasMap.get(finalCatNombreForProd.trim());
                                    }
                                    if (catParaProducto == null) {
                                        // Crear/buscar categoría "General"
                                        catParaProducto = categoriaRepository.findByNombreCategoriaAndUsuario("General", usuario)
                                                .orElseGet(() -> {
                                                    Categoria general = new Categoria("General", "Categoría por defecto", usuario);
                                                    return categoriaRepository.save(general);
                                                });
                                        categoriasMap.put("General", catParaProducto);
                                    }
                                    Producto nuevo = new Producto(finalProdNombre, catParaProducto, usuario);
                                    return productoRepository.save(nuevo);
                                });
                        productosMap.put(prodNombre, prod);
                    }
                }
            }

            System.out.println("   Categorías: " + categoriasMap.size() + ", Productos: " + productosMap.size());

            // 2️⃣ Recopilar todos los textos y analizar
            List<String> textos = entradas.stream()
                    .map(CsvEntradaDto::getTexto)
                    .filter(t -> t != null && !t.isBlank())
                    .collect(Collectors.toList());

            if (textos.isEmpty()) {
                throw new RuntimeException("No hay textos válidos para analizar");
            }

            String textoCompleto = String.join("\n", textos);
            Optional<SentimentsResponseDto> responseOpt = sentimentService.consultarSentimientos(textoCompleto);

            if (responseOpt.isEmpty()) {
                throw new RuntimeException("Error al analizar comentarios");
            }

            List<ResponseDto> resultados = responseOpt.get().getResults();

            // 3️⃣ Calcular estadísticas generales
            int total = resultados.size();
            int positivos = 0, negativos = 0, neutrales = 0;
            double sumaScores = 0.0;

            for (ResponseDto resultado : resultados) {
                sumaScores += resultado.getProbabilidad();
                String sentimiento = resultado.getPrevision();
                if (sentimiento.equalsIgnoreCase("Positivo")) positivos++;
                else if (sentimiento.equalsIgnoreCase("Negativo")) negativos++;
                else neutrales++;
            }

            double avgScore = total > 0 ? sumaScores / total : 0.0;

            // 4️⃣ Crear sesión
            Sesion sesion = new Sesion(LocalDateTime.now(), avgScore, total, positivos, negativos, neutrales, usuario);

            // 5️⃣ Procesar cada entrada y asignar producto
            // Contadores por producto
            Map<Integer, ContadorProducto> contadores = new HashMap<>();
            for (Producto p : productosMap.values()) {
                contadores.put(p.getProductoId(), new ContadorProducto(p.getNombreProducto()));
            }

            List<Comentario> comentariosEntidades = new ArrayList<>();
            List<ComentarioDto> comentariosDto = new ArrayList<>();

            // Filtrar entradas que tienen texto válido (misma lista que textos)
            List<CsvEntradaDto> entradasValidas = entradas.stream()
                    .filter(e -> e.getTexto() != null && !e.getTexto().isBlank())
                    .collect(Collectors.toList());

            for (int i = 0; i < entradasValidas.size() && i < resultados.size(); i++) {
                CsvEntradaDto entrada = entradasValidas.get(i);
                ResponseDto resultado = resultados.get(i);
                String textoComentario = entrada.getTexto().trim();
                String sentimiento = resultado.getPrevision();

                // Si la entrada tiene producto, contar para ese producto
                if (entrada.getProducto() != null && !entrada.getProducto().isBlank()) {
                    Producto prod = productosMap.get(entrada.getProducto().trim());
                    if (prod != null) {
                        ContadorProducto contador = contadores.get(prod.getProductoId());
                        if (contador != null) {
                            contador.incrementar(sentimiento);
                        }
                    }
                }

                Comentario comentarioEntity = new Comentario(
                        textoComentario,
                        sentimiento,
                        resultado.getProbabilidad(),
                        sesion
                );
                comentariosEntidades.add(comentarioEntity);

                String productoAsociado = (entrada.getProducto() != null && !entrada.getProducto().isBlank())
                        ? entrada.getProducto().trim() : null;

                comentariosDto.add(new ComentarioDto(
                        textoComentario,
                        sentimiento.toLowerCase(),
                        resultado.getProbabilidad(),
                        productoAsociado
                ));
            }

            sesion.setComentarios(comentariosEntidades);
            Sesion sesionGuardada = sesionRepository.save(sesion);

            // 6️⃣ Guardar relación sesión-productos y actualizar contadores
            List<ProductoMencionesDto> productosDetectados = new ArrayList<>();

            for (Map.Entry<Integer, ContadorProducto> entry : contadores.entrySet()) {
                Integer productoId = entry.getKey();
                ContadorProducto contador = entry.getValue();

                if (contador.getTotal() > 0) {
                    SesionProducto sp = new SesionProducto(
                            sesionGuardada,
                            productoRepository.findById(productoId).get(),
                            contador.getTotal(),
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales()
                    );
                    sesionProductoRepository.save(sp);

                    productoService.actualizarContadoresProducto(
                            productoId,
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales()
                    );

                    productosDetectados.add(new ProductoMencionesDto(
                            contador.getNombreProducto(),
                            contador.getTotal(),
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales(),
                            total > 0 ? (contador.getTotal() * 100.0) / total : 0.0
                    ));
                }
            }

            // 7️⃣ Preparar respuesta
            SesionDto sesionDto = new SesionDto(
                    sesionGuardada.getSesionId(),
                    LocalDateTime.now().toString(),
                    avgScore,
                    total,
                    positivos,
                    negativos,
                    neutrales,
                    comentariosDto
            );

            sesionDto.setProductosDetectados(productosDetectados);

            System.out.println("✅ Batch CSV analizado: " + total + " textos, " + productosDetectados.size() + " productos detectados");

            return sesionDto;
        }

        // Clase auxiliar para conteo
        private static class ContadorProducto {
            private final String nombreProducto;
            private int total = 0, positivos = 0, negativos = 0, neutrales = 0;

            public ContadorProducto(String nombreProducto) {
                this.nombreProducto = nombreProducto;
            }

            public void incrementar(String sentimiento) {
                total++;
                if (sentimiento.equalsIgnoreCase("Positivo")) positivos++;
                else if (sentimiento.equalsIgnoreCase("Negativo")) negativos++;
                else neutrales++;
            }

            public String getNombreProducto() { return nombreProducto; }
            public int getTotal() { return total; }
            public int getPositivos() { return positivos; }
            public int getNegativos() { return negativos; }
            public int getNeutrales() { return neutrales; }
        }
    }
