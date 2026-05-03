package com.project.sentimentapi.security;

import com.project.sentimentapi.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("🔍 JWT Filter - Path: " + requestPath + " | Method: " + method);

        // ✅ CORS ya manejó OPTIONS, aquí solo validamos lógica de negocio

        // ✅ RUTAS PÚBLICAS (sin token)
        boolean isPublicRoute =
                requestPath.contains("/usuario/login") ||
                        (requestPath.contains("/usuario") && "POST".equals(method) && !requestPath.contains("/login")) ||
                        requestPath.contains("/sentiment/analyze") ||
                        requestPath.contains("/debug/health");

        if (isPublicRoute) {
            System.out.println("✅ Ruta pública - Sin validación JWT");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ RUTAS PROTEGIDAS (requieren token)
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String correo = jwtUtil.extractCorreo(token);
                Integer usuarioId = jwtUtil.extractUsuarioId(token);

                if (userRepository.findByCorreo(correo).isPresent() &&
                        jwtUtil.validateToken(token, correo)) {

                    request.setAttribute("usuarioId", usuarioId);
                    request.setAttribute("correo", correo);

                    System.out.println("✅ Token válido - Usuario: " + correo);
                }  else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token inválido o expirado");
                    return;
                }
            } catch (Exception e) {
                System.err.println("❌ Error al validar token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido o expirado");
                return;
            }

            // ✅ Continuar con la cadena de filtros FUERA del try-catch de JWT
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Acceso no autorizado");
    }
}