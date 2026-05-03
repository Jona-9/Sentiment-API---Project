// src/services/authService.js
import { API_ENDPOINTS } from '../config/api';
import { formatUserName } from '../utils/formatName'; // ✅ IMPORTAR

export const authService = {
  /**
   * Registra un nuevo usuario
   * @param {Object} userData - { nombre, apellido, correo, contraseña }
   * @returns {Promise<Object>} Usuario registrado
   */
  async register(userData) {
    try {
      const response = await fetch(API_ENDPOINTS.REGISTER, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const msg = errorData.message || errorData.error || 'Error al registrar usuario';
        throw new Error(msg);
      }

      // El backend puede devolver body vacío (200 sin JSON) o JSON
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

  /**
   * Inicia sesión
   * @param {string} correo 
   * @param {string} contraseña 
   * @returns {Promise<Object>} Datos del usuario + token JWT
   */
  /**
   * Solicita un email de recuperación de contraseña
   * @param {string} correo
   */
  async forgotPassword(correo) {
    try {
      const response = await fetch(`${API_ENDPOINTS.FORGOT_PASSWORD}?email=${encodeURIComponent(correo)}`, {
        method: 'POST',
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const msg = errorData.message || 'Error al enviar el correo de recuperación';
        throw new Error(msg);
      }

      const data = await response.json().catch(() => ({}));
      return { success: true, message: data.message || 'Correo enviado exitosamente' };
    } catch (error) {
      console.error('Error en forgotPassword:', error);
      throw error;
    }
  },

  /**
   * Restablece la contraseña usando el token del email
   * @param {string} token
   * @param {string} nuevaContrasena
   */
  async resetPassword(token, nuevaContrasena) {
    try {
      const response = await fetch(
        `${API_ENDPOINTS.RESET_PASSWORD}?token=${encodeURIComponent(token)}&nuevaContrasena=${encodeURIComponent(nuevaContrasena)}`,
        { method: 'POST' }
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const msg = errorData.message || 'Error al restablecer la contraseña';
        throw new Error(msg);
      }

      const data = await response.json().catch(() => ({}));
      return { success: true, message: data.message || 'Contraseña actualizada exitosamente' };
    } catch (error) {
      console.error('Error en resetPassword:', error);
      throw error;
    }
  },
    
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
        if (response.status === 401) {
          throw new Error('Credenciales incorrectas');
        }
        throw new Error('Error al iniciar sesión');
      }

      const userData = await response.json();
      
      // ✅ FORMATEAR NOMBRE: Solo primer nombre y apellido
      const nombreFormateado = formatUserName(userData.nombre, userData.apellido);
      
      return {
        success: true,
        user: {
          id: userData.id,
          correo: userData.correo,
          nombre: userData.nombre, // Nombre completo original
          apellido: userData.apellido, // Apellido completo original
          nombreCompleto: nombreFormateado, // ✅ NUEVO: Nombre formateado
          token: userData.token,
        }
      };
    } catch (error) {
      console.error('Error en login:', error);
      throw error;
    }
  },
};