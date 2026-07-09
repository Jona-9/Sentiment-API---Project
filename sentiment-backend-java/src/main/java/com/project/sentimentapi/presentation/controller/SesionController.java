package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.ConsultarSesionesUseCase;
import com.project.sentimentapi.presentation.dto.response.SesionDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ISP: solo inyecta ConsultarSesionesUseCase.
// El guardado de sesiones ocurre internamente vía AnalizarCsvUseCase,
// por lo que SesionController no necesita GuardarSesionUseCase.
@RestController
@RequestMapping("/sesiones")
@RequiredArgsConstructor
public class SesionController {

    private final ConsultarSesionesUseCase consultarUseCase;

    @GetMapping
    public ResponseEntity<List<SesionDto>> obtenerMisSesiones(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(consultarUseCase.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSesion(@PathVariable Integer id) {
        SesionDto sesion = consultarUseCase.obtenerPorId(id);
        if (sesion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sesion);
    }
}