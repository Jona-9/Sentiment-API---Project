// src/services/authService.js
import { API_ENDPOINTS } from '../config/api';
import { formatUserName } from '../utils/formatName';

export const authService = {
  /**
   * Registra un nuevo usuario
   * @param {Object} userData - { nombre, apellido, correo, contrasena }
   */
  async register(userData) {
    try {
      const response = await fetch(API_ENDPOINTS.REGISTER, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        // CORRECCIÓN: Los nombres aquí deben ser idénticos a los del DTO en Java
        body: JSON.stringify({
          nombre: userData.nombre,
          apellido: userData.apellido,
          correo: userData.correo,
          contrasena: userData.contrasena // Asegurado para coincidir con tu DTO
        }),
      });

      if (!response.ok) {
        // Intentar obtener el mensaje de error del servidor
        const errorData = await response.json().catch(() => ({}));
        
        // Si el correo ya existe, el backend suele devolver 409 o 400
        if (response.status === 409 || response.status === 400) {
           throw new Error(errorData.message || 'El correo ya está registrado o datos inválidos.');
        }
        
        throw new Error(errorData.message || 'Error al registrar usuario');
      }

      const data = await response.json().catch(() => null);

      return {
        success: true,
        message: data?.message || 'Registro exitoso',
        user: {
          nombre: userData.nombre,
          apellido: userData.apellido,
          correo: userData.correo,
        }
      };
    } catch (error) {
      console.error('Error en registro:', error);
      throw error;
    }
  },

  // ... resto de tus métodos (login, forgotPassword, etc.)
  async login(correo, contrasena) {
    try {
      const response = await fetch(API_ENDPOINTS.LOGIN, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          correo: correo,
          contrasena: contrasena
        }),
      });

      if (!response.ok) {
        throw new Error('Credenciales incorrectas');
      }

      const userData = await response.json();
      const nombreFormateado = formatUserName(userData.nombre, userData.apellido);
      
      return {
        success: true,
        user: {
          id: userData.id,
          correo: userData.correo,
          nombre: userData.nombre,
          apellido: userData.apellido,
          nombreCompleto: nombreFormateado,
          token: userData.token,
        }
      };
    } catch (error) {
      console.error('Error en login:', error);
      throw error;
    }
  },
};