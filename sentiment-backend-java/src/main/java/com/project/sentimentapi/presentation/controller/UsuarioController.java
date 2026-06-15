package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.AutenticarUsuarioUseCase;
import com.project.sentimentapi.domain.port.in.RecuperarContrasenaUseCase;
import com.project.sentimentapi.domain.port.in.RegistrarUsuarioUseCase;
import com.project.sentimentapi.dto.LoginResponseDto;
import com.project.sentimentapi.dto.UserDtoRegistro;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

// ISP: cada use case inyectado es una interfaz de responsabilidad única
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final RegistrarUsuarioUseCase registrarUseCase;
    private final AutenticarUsuarioUseCase autenticarUseCase;
    private final RecuperarContrasenaUseCase recuperarUseCase;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody UserDtoRegistro dto) {
        try {
            registrarUseCase.registrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario registrado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDtoRegistro dto) {
        Optional<LoginResponseDto> resultado = autenticarUseCase.autenticar(dto);
        return resultado
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            recuperarUseCase.forgotPassword(email);
            return ResponseEntity.ok(Map.of("message", "Correo de recuperación enviado"));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
            if (msg.contains("No existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", msg));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al enviar el correo: " + msg));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String nuevaContrasena) {
        try {
            recuperarUseCase.resetPassword(token, nuevaContrasena);
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
