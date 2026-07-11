package com.project.sentimentapi.application.builder;

import com.project.sentimentapi.domain.model.Sesion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// PRUEBA DEL PATRÓN BUILDER (Unidad 2 del sílabo — patrones creacionales).
// Verifica que SesionBuilder construye correctamente la Sesion, valida los campos
// obligatorios y que cada builder mantiene su estado aislado (seguridad ante concurrencia).
class SesionBuilderTest {

    @Test
    @DisplayName("build() construye una Sesion con todos los campos asignados")
    void construyeSesionConDatosCompletos() {
        // Arrange (Preparar)
        LocalDateTime fecha = LocalDateTime.now();

        // Act (Actuar) — API fluida encadenada del patrón Builder
        Sesion sesion = new SesionBuilder()
                .conUsuario(7)
                .conFecha(fecha)
                .conTotalComentarios(10)
                .conEstadisticas(6, 3, 1, 0.75)
                .build();

        // Assert (Verificar)
        assertNotNull(sesion, "El builder no debe devolver null");
        assertEquals(7, sesion.getUsuarioId());
        assertEquals(fecha, sesion.getFecha());
        assertEquals(10, sesion.getTotalComentarios());
        assertEquals(6, sesion.getPositivos());
        assertEquals(3, sesion.getNegativos());
        assertEquals(1, sesion.getNeutrales());
        assertEquals(0.75, sesion.getAvgScore());
    }

    @Test
    @DisplayName("build() sin usuarioId lanza IllegalStateException (validación de obligatorios)")
    void fallaSiFaltaUsuarioObligatorio() {
        // El Builder centraliza la validación: sin usuarioId no se puede construir la Sesion
        SesionBuilder builder = new SesionBuilder()
                .conTotalComentarios(5)
                .conEstadisticas(2, 2, 1, 0.5);

        assertThrows(IllegalStateException.class, builder::build,
                "Debe fallar cuando falta el campo obligatorio usuarioId");
    }

    @Test
    @DisplayName("build() asigna la fecha actual si no se especifica")
    void asignaFechaPorDefectoSiNoSeIndica() {
        Sesion sesion = new SesionBuilder()
                .conUsuario(1)
                .conTotalComentarios(1)
                .conEstadisticas(1, 0, 0, 1.0)
                .build();

        assertNotNull(sesion.getFecha(), "El builder debe poner una fecha por defecto");
    }

    @Test
    @DisplayName("Dos builders no comparten estado (seguro ante análisis concurrentes)")
    void buildersIndependientesNoCompartenEstado() {
        // Esta es la razón por la que SesionBuilder se instancia con `new` y NO es un @Bean:
        // el estado mutable de un usuario no debe filtrarse al análisis de otro.
        SesionBuilder builderA = new SesionBuilder().conUsuario(100).conTotalComentarios(50);
        SesionBuilder builderB = new SesionBuilder().conUsuario(200).conTotalComentarios(5);

        Sesion sesionA = builderA.conEstadisticas(50, 0, 0, 1.0).build();
        Sesion sesionB = builderB.conEstadisticas(1, 4, 0, 0.2).build();

        assertEquals(100, sesionA.getUsuarioId());
        assertEquals(200, sesionB.getUsuarioId());
        assertEquals(50, sesionA.getTotalComentarios());
        assertEquals(5, sesionB.getTotalComentarios());
    }
}
