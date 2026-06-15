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

    // Usa UsuarioJpaRepository (infrastructure) en lugar de UserRepository (legacy)
    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("JWT Filter - Path: " + requestPath + " | Method: " + method);

        boolean isPublicRoute =
                requestPath.contains("/usuario/login") ||
                        (requestPath.contains("/usuario") && "POST".equals(method) &&
                                !requestPath.contains("/login")) ||
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
