// src/config/api.js
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/project/api/v2';

export const API_ENDPOINTS = {
  // ── Autenticación ─────────────────────────────────────────────────────────
  // Controller: UsuarioController → @RequestMapping("/api/usuarios")
  REGISTER:        `${API_BASE_URL}/api/usuarios/registro`,
  LOGIN:           `${API_BASE_URL}/api/usuarios/login`,
  FORGOT_PASSWORD: `${API_BASE_URL}/api/usuarios/forgot-password`,
  RESET_PASSWORD:  `${API_BASE_URL}/api/usuarios/reset-password`,

  // ── Análisis de texto (sin guardar sesión — Demo o análisis rápido) ───────
  // Controller: SentimentApiController → @RequestMapping("/sentiment/analyze")
  ANALYZE_SINGLE: `${API_BASE_URL}/sentiment/analyze`,
  ANALYZE_BATCH:  `${API_BASE_URL}/sentiment/analyze/batch`,

  // ── Análisis CSV con guardado de sesión ───────────────────────────────────
  // Controller: CsvAnalysisController → @RequestMapping("/csv")
  // CORRECCIÓN BUG 3: antes apuntaba a /sesion/analizar-csv-batch (no existe)
  ANALYZE_CSV_BATCH: `${API_BASE_URL}/csv/analizar`,

  // ── Sesiones ──────────────────────────────────────────────────────────────
  // Controller: SesionController → @RequestMapping("/sesiones")
  // CORRECCIÓN BUG 3: antes apuntaba a /sesion (sin 's') — no existía
  GET_SESSIONS: `${API_BASE_URL}/sesiones`,
  GET_HISTORY:  `${API_BASE_URL}/sesiones`,          // mismo endpoint GET /sesiones

  // ── Categorías y Productos ────────────────────────────────────────────────
  // Controller: CategoriaController / ProductoController
  CATEGORIAS:              `${API_BASE_URL}/categoria`,
  PRODUCTOS:               `${API_BASE_URL}/producto`,
  PRODUCTOS_POR_CATEGORIA: `${API_BASE_URL}/producto/por-categoria`,

  // ── Endpoints ELIMINADOS (no tienen controller en el backend) ────────────
  // ANALYZE_AND_SAVE: no existe → reemplazado por ANALYZE_CSV_BATCH
  // ANALYZE_MULTI_PRODUCTS: no existe → si se necesita, hay que crear el controller
};

export default API_BASE_URL;