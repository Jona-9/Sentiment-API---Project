package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.domain.port.in.AutenticarUsuarioUseCase;
import com.project.sentimentapi.domain.port.out.UsuarioRepositoryPort;
import com.project.sentimentapi.presentation.dto.request.LoginRequestDto;
import com.project.sentimentapi.presentation.dto.response.LoginResponseDto;
import com.project.sentimentapi.infrastructure.security.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

// CASO DE USO de login (capa application).
// Principio SRP: solo autentica — no registra ni recupera contraseña.
// Principio DIP: depende de UsuarioRepositoryPort (abstracción), no de JPA.
// Seguridad: compara la contraseña con BCrypt.checkpw (nunca en texto plano) y,
// si coincide, emite un JWT firmado mediante JwtUtil.
@Service
public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioPort;
    private final JwtUtil jwtUtil;

    public AutenticarUsuarioUseCaseImpl(UsuarioRepositoryPort usuarioPort,
                                        JwtUtil jwtUtil) {
        this.usuarioPort = usuarioPort;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Optional<LoginResponseDto> autenticar(LoginRequestDto request) {
        // 1) Buscar el usuario por su correo a través del port
        Optional<Usuario> usuarioOpt = usuarioPort.buscarPorEmail(request.getCorreo());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // 2) Verificar la contraseña contra el hash BCrypt almacenado
            if (BCrypt.checkpw(request.getContrasena(), usuario.getPasswordHash())) {
                // 3) Credenciales correctas → generar token JWT con email e id
                String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getId());
                return Optional.of(new LoginResponseDto(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getEmail(),
                        token
                ));
            }
        }
        // Usuario inexistente o contraseña incorrecta → sin token (401 en el controller)
        return Optional.empty();
    }
}