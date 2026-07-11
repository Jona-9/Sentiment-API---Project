package com.project.sentimentapi.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

// CONFIGURACIÓN DE SEGURIDAD (capa infrastructure). Registra dos filtros como @Bean:
//  1) CorsFilter — permite que el dashboard (otro origen/puerto) llame a la API sin ser
//     bloqueado por la política CORS del navegador.
//  2) JwtAuthenticationFilter — valida el token en cada request.
// El orden importa: CORS va primero (HIGHEST_PRECEDENCE) y el filtro JWT después.
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:*",
                "https://*.onrender.com",
                "https://*.vercel.app",
                "https://*.up.railway.app"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtAuthenticationFilter);

        // CORRECCIÓN: antes solo cubría "/api/usuarios/*", "/sentiment/*", "/sesion/*"
        // (sin 's' en sesion, y sin /csv/* ni /sesiones/*), por lo que el filtro
        // nunca procesaba esas rutas → el token no se validaba → usuarioId nunca
        // se seteaba en el request → los controllers devolvían 401 o 500.
        //
        // Solución: aplicar el filtro a TODAS las rutas ("/*") y dejar que
        // JwtAuthenticationFilter.isPublicRoute() decida internamente qué rutas
        // no requieren token. Así no hay que sincronizar esta lista cada vez que
        // se agrega un controller nuevo.
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registrationBean;
    }
}