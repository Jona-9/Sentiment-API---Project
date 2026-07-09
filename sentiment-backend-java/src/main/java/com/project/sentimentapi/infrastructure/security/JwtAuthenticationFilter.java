package com.project.sentimentapi.infrastructure.security;

import com.project.sentimentapi.infrastructure.persistence.repository.UsuarioJpaRepository;
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
    private UsuarioJpaRepository usuarioJpaRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("JWT Filter - Path: " + requestPath + " | Method: " + method);

        // CORRECCIÓN: antes usaba "/usuario/login" (singular, sin prefijo /api)
        // pero el controller está en @RequestMapping("/api/usuarios") → plural con /api/
        // Resultado: el filtro nunca reconocía login ni registro como rutas públicas → 401.
        boolean isPublicRoute =
                requestPath.contains("/api/usuarios/login") ||
                        requestPath.contains("/api/usuarios/registro") ||
                        requestPath.contains("/api/usuarios/forgot-password") ||
                        requestPath.contains("/api/usuarios/reset-password") ||
                        requestPath.contains("/sentiment/analyze") ||
                        requestPath.contains("/debug/health");

        if (isPublicRoute) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String correo = jwtUtil.extractCorreo(token);
                Integer usuarioId = jwtUtil.extractUsuarioId(token);

                if (usuarioJpaRepository.findByCorreo(correo).isPresent() &&
                        jwtUtil.validateToken(token, correo)) {
                    request.setAttribute("usuarioId", usuarioId);
                    request.setAttribute("correo", correo);
                    System.out.println("Token válido - Usuario: " + correo);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token inválido o expirado");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error al validar token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido o expirado");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Acceso no autorizado");
    }
}