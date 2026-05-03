package com.project.sentimentapi.controller;

import com.project.sentimentapi.dto.UserDtoRegistro;
import com.project.sentimentapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody UserDtoRegistro userDtoRegistro) {
        try {
            userService.registrarUsuario(userDtoRegistro);
            return ResponseEntity.ok(java.util.Map.of("message", "Usuario registrado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT)
                    .body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody UserDtoRegistro userDtoRegistro) {
        return userService.login(userDtoRegistro)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    // Solicitar recuperación de contraseña → envía email con token
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.forgotPassword(email);
            return ResponseEntity.ok(java.util.Map.of("message", "Correo de recuperación enviado"));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
            if (msg.contains("No existe")) {
                return ResponseEntity.status(404).body(java.util.Map.of("message", msg));
            }
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Error al enviar el correo: " + msg));
        }
    }

    // Resetear contraseña con el token recibido por email
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String nuevaContrasena) {
        try {
            userService.resetPassword(token, nuevaContrasena);
            return ResponseEntity.ok(java.util.Map.of("message", "Contraseña actualizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(java.util.Map.of("message", e.getMessage()));
        }
    }
}