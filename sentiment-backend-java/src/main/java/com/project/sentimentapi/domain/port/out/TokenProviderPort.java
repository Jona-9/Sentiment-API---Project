package com.project.sentimentapi.domain.port.out;

// PORT OUT (puerto de salida — capa domain). Contrato que el dominio NECESITA para
// emitir un token de sesión tras un login correcto, sin conocer la tecnología (JWT,
// jjwt, etc.). Lo implementa JwtUtil (infrastructure). Barrera DIP: el use case de
// autenticación depende de esta abstracción, no de la librería de tokens.
public interface TokenProviderPort {
    // Genera un token firmado que identifica al usuario (por su email e id).
    String generarToken(String email, Integer usuarioId);
}
