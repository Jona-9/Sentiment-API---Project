package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.LoginResponseDto;
import com.project.sentimentapi.dto.UserDtoRegistro;
import com.project.sentimentapi.entity.Rol;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.event.UserRegisteredEvent;
import com.project.sentimentapi.repository.RolRepository;
import com.project.sentimentapi.repository.UserRepository;
import com.project.sentimentapi.security.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImplement implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RolRepository rolrepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public void registrarUsuario(UserDtoRegistro userDtoRegistro) {
        System.out.println("====================================");
        System.out.println("REGISTRANDO USUARIO: " + userDtoRegistro.getCorreo());

        if (userRepository.findByCorreo(userDtoRegistro.getCorreo()).isPresent()) {
            System.err.println("ERROR: El correo ya esta registrado");
            throw new RuntimeException("El correo ya esta registrado");
        }

        Optional<Rol> rol = rolrepository.findByNombreRol("USER");
        if (rol.isEmpty()) {
            // Fallback: intentar por ID
            rol = rolrepository.findById(2);
        }
        if (rol.isEmpty()) {
            System.err.println("ERROR: Rol USER no encontrado");
            throw new RuntimeException("Rol USER no encontrado. Verifique que DataInitializer haya ejecutado correctamente.");
        }

        List<Rol> roles = new ArrayList<>();
        roles.add(rol.get());

        String claveHasheada = BCrypt.hashpw(userDtoRegistro.getContrasena(), BCrypt.gensalt());

        User nuevoUsuario = new User(
                userDtoRegistro.getNombre(),
                userDtoRegistro.getApellido(),
                claveHasheada,
                userDtoRegistro.getCorreo(),
                roles
        );

        User usuarioGuardado = userRepository.save(nuevoUsuario);
        System.out.println("Usuario guardado con ID: " + usuarioGuardado.getUsuarioID());

        System.out.println("Publicando evento UserRegisteredEvent...");
        eventPublisher.publishEvent(new UserRegisteredEvent(this, usuarioGuardado));

        System.out.println("REGISTRO COMPLETADO");
        System.out.println("====================================");
    }

    @Override
    public Optional<LoginResponseDto> login(UserDtoRegistro userDtoRegistro) {
        System.out.println("[LOGIN] Intentando login para: " + userDtoRegistro.getCorreo());
        System.out.println("[LOGIN] Contrasena recibida length: " + (userDtoRegistro.getContrasena() != null ? userDtoRegistro.getContrasena().length() : "NULL"));

        Optional<User> usuarioOpt = userRepository.findByCorreo(userDtoRegistro.getCorreo());

        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();
            System.out.println("[LOGIN] Hash en DB (primeros 20): " + usuario.getContrasena().substring(0, Math.min(20, usuario.getContrasena().length())));

            if (BCrypt.checkpw(userDtoRegistro.getContrasena(), usuario.getContrasena())) {
                String token = jwtUtil.generateToken(usuario.getCorreo(), usuario.getUsuarioID());

                System.out.println("[LOGIN] ✅ Exitoso - Token generado para: " + usuario.getCorreo());

                return Optional.of(new LoginResponseDto(
                        usuario.getUsuarioID(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getCorreo(),
                        token
                ));
            } else {
                System.err.println("[LOGIN] ❌ Contrasena incorrecta para: " + userDtoRegistro.getCorreo());
            }
        } else {
            System.err.println("[LOGIN] ❌ Usuario no encontrado: " + userDtoRegistro.getCorreo());
        }

        return Optional.empty();
    }

    @Override
    public void forgotPassword(String email) {
        User usuario = userRepository.findByCorreo(email)
                .orElseThrow(() -> new RuntimeException("No existe una cuenta con ese correo"));

        // Generar token único y establecer expiración de 30 minutos
        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(usuario);

        System.out.println("🔑 Token de recuperación generado para: " + email);
        emailService.sendRecoveryEmail(email, token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String nuevaContrasena) {
        System.out.println("[RESET] Iniciando resetPassword con token: " + token.substring(0, 8) + "...");
        System.out.println("[RESET] nuevaContrasena length: " + (nuevaContrasena != null ? nuevaContrasena.length() : "NULL"));

        User usuario = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o no encontrado"));

        System.out.println("[RESET] Usuario encontrado: " + usuario.getCorreo() + " | ID: " + usuario.getUsuarioID());
        System.out.println("[RESET] Hash actual (primeros 20 chars): " + usuario.getContrasena().substring(0, Math.min(20, usuario.getContrasena().length())));

        if (usuario.getTokenExpiry() == null || usuario.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado. Solicita un nuevo correo de recuperación");
        }

        // Actualizar contraseña y limpiar token
        String nuevaClaveHasheada = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());
        System.out.println("[RESET] Nuevo hash generado (primeros 20 chars): " + nuevaClaveHasheada.substring(0, 20));

        usuario.setContrasena(nuevaClaveHasheada);
        usuario.setResetToken(null);
        usuario.setTokenExpiry(null);
        User saved = userRepository.save(usuario);

        System.out.println("[RESET] ✅ Save completado. Hash guardado (primeros 20): " + saved.getContrasena().substring(0, 20));
        System.out.println("[RESET] ✅ Contraseña restablecida para: " + usuario.getCorreo());
    }
}