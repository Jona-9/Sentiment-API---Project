package com.project.sentimentapi.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// UTILIDAD DE SEGURIDAD (capa infrastructure). Encapsula la creación y verificación
// de tokens JWT (JSON Web Token). Aísla la librería jjwt del resto del sistema: los
// use cases solo llaman generateToken/validateToken sin conocer los detalles.
@Component
public class JwtUtil {

    // Clave secreta para firmar/verificar (se lee de application.properties: nunca hardcodeada)
    @Value("${jwt.secret}")
    private String secretKey;

    // Tiempo de vida del token en milisegundos (también externalizado)
    @Value("${jwt.expiration}")
    private long expirationTime;

    // Construye la clave HMAC-SHA a partir del secreto; se usa tanto al firmar como al validar
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Genera un token firmado para un usuario tras un login correcto.
    public String generateToken(String correo, Integer usuarioId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuarioId);        // dato extra dentro del token (payload)
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(correo)                 // "subject" = identidad principal (el correo)
                .setIssuedAt(new Date(System.currentTimeMillis()))                    // emitido ahora
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // vence luego
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)                  // firma HMAC-SHA256
                .compact();                         // serializa a la cadena "xxx.yyy.zzz"
    }

    // Lee el correo (subject) contenido en el token
    public String extractCorreo(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Lee el usuarioId guardado como claim en el token
    public Integer extractUsuarioId(String token) {
        return extractAllClaims(token).get("usuarioId", Integer.class);
    }

    // Verifica la firma con la clave secreta y devuelve el payload; lanza excepción si fue alterado
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Válido si el correo del token coincide con el esperado y aún no ha expirado
    public Boolean validateToken(String token, String correo) {
        final String extractedCorreo = extractCorreo(token);
        return (extractedCorreo.equals(correo) && !isTokenExpired(token));
    }

    // true si la fecha de expiración del token ya pasó
    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
