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
        Optional<Usuario> usuarioOpt = usuarioPort.buscarPorEmail(request.getCorreo());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (BCrypt.checkpw(request.getContrasena(), usuario.getPasswordHash())) {
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
        return Optional.empty();
    }
}