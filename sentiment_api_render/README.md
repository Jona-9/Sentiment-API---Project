# 🧠 Sentiment Analysis API v4.0 (Español/Portugués)

<div align="center">

![Python](https://img.shields.io/badge/Python-3.11-blue?style=flat-square&logo=python)
![FastAPI](https://img.shields.io/badge/FastAPI-0.104-green?style=flat-square&logo=fastapi)
![scikit-learn](https://img.shields.io/badge/scikit--learn-1.3-orange?style=flat-square&logo=scikit-learn)
![Render](https://img.shields.io/badge/Deploy-Render-purple?style=flat-square&logo=render)

**API de Machine Learning para análisis de sentimientos en español y portugués**

[Demo en Producción](https://sentiment-api-render.onrender.com/docs) · [Dashboard](https://sentiment-dashboard-pi.vercel.app)

</div>

---

## 🌐 URLs de Producción

| Servicio | URL |
|----------|-----|
| **API ML (este repo)** | https://sentiment-api-render.onrender.com |
| **Documentación Swagger** | https://sentiment-api-render.onrender.com/docs |
| **Backend Java** | https://sentiment-backend-java-production.up.railway.app |
| **Frontend React** | https://sentiment-dashboard-pi.vercel.app |

---

## 📖 Descripción

API REST desarrollada en **FastAPI** que utiliza un modelo de Machine Learning para clasificar textos en tres categorías de sentimiento: **Positivo**, **Neutro** y **Negativo**.

### 🔬 Modelo ML
- **Pipeline:** Limpieza de texto → TF-IDF Vectorizer → Regresión Logística → Calibración de probabilidades
- **Entrenamiento:** Reseñas en español y portugués, etiquetas derivadas de estrellas (1-2 Negativo, 3 Neutro, 4-5 Positivo)
- **Artefacto:** `sentiment_bundle_es_pt_v2.joblib` contiene el pipeline calibrado, umbral de confianza, metadatos y términos explicativos

### 📊 Predicciones
- **Entrada:** Texto libre (string, 5-2000 caracteres)
- **Salida:**
  - `prevision`: Sentimiento predicho (`Negativo`, `Neutro`, `Positivo`)
  - `probabilidad`: Confianza de la predicción (0-1)
  - `review_required`: `true` si la confianza es baja y requiere revisión humana

---

## 🚀 Instalación Local

### Requisitos
- Python 3.11+
- pip

### Pasos

```bash
# Clonar el repositorio
git clone https://github.com/GustavoVasquezS/sentiment-api-render.git
cd sentiment-api-render

# Crear entorno virtual
python -m venv .venv

# Activar entorno virtual
# Windows:
.venv\Scripts\activate
# Linux/Mac:
source .venv/bin/activate

# Instalar dependencias
pip install -r requirements.txt

# Ejecutar la API
python main.py
```

La API estará disponible en `http://localhost:8000`

### 📚 Documentación Interactiva
- **Swagger UI:** http://localhost:8000/docs
- **ReDoc:** http://localhost:8000/redoc

---

## 📡 Endpoints

### `GET /health`
Estado y versión del modelo.

**Response:**
```json
{
  "status": "healthy",
  "model_version": "4.0",
  "languages": ["es", "pt"]
}
```

### `POST /predict`
Predicción de sentimiento para un texto individual.

**Request:**
```json
{
  "text": "Este producto es excelente, me encanta!"
}
```

**Response:**
```json
{
  "prevision": "Positivo",
  "probabilidad": 0.95,
  "review_required": false
}
```

### `POST /predict/batch`
Predicción múltiple (máximo 100 textos).

**Request:**
```json
{
  "texts": [
    "Excelente servicio",
    "Pésima atención",
    "Normal, nada especial"
  ]
}
```

**Response:**
```json
[
  {"prevision": "Positivo", "probabilidad": 0.92, "review_required": false},
  {"prevision": "Negativo", "probabilidad": 0.88, "review_required": false},
  {"prevision": "Neutro", "probabilidad": 0.65, "review_required": true}
]
```

---

## 📁 Estructura del Proyecto

```
sentiment_api_render/
├── main.py                          # Aplicación FastAPI
├── text_cleaner.py                  # Utilidades de limpieza de texto
├── sentiment_bundle_es_pt_v2.joblib # Modelo ML serializado
├── requirements.txt                 # Dependencias Python
├── render.yaml                      # Configuración de despliegue Render
├── test_api.py                      # Tests de la API
└── README.md
```

---

## ☁️ Despliegue en Render

### Configuración automática (render.yaml)

El archivo `render.yaml` ya está configurado:

```yaml
services:
  - type: web
    name: sentiment-api-render
    env: python
    buildCommand: pip install -r requirements.txt
    startCommand: uvicorn main:app --host 0.0.0.0 --port $PORT
```

### Pasos para desplegar

1. Crear cuenta en [Render](https://render.com)
2. Conectar repositorio de GitHub
3. Render detectará automáticamente `render.yaml`
4. El servicio se desplegará en ~2 minutos

---

## 🧪 Testing

```bash
# Ejecutar tests
python test_api.py

# Test manual con curl
curl -X POST "http://localhost:8000/predict" \
  -H "Content-Type: application/json" \
  -d '{"text": "Este producto es increíble"}'
```

---

## 📦 Dependencias Principales

| Paquete | Versión | Descripción |
|---------|---------|-------------|
| fastapi | 0.104+ | Framework web async |
| uvicorn | 0.24+ | Servidor ASGI |
| scikit-learn | 1.3+ | ML Pipeline |
| joblib | 1.3+ | Serialización del modelo |
| pydantic | 2.0+ | Validación de datos |

---

## 🔗 Repositorios Relacionados

| Componente | Repositorio | Descripción |
|------------|-------------|-------------|
| Backend Java | [sentiment-backend-java](https://github.com/GustavoVasquezS/sentiment-backend-java) | API Gateway con autenticación JWT |
| Frontend React | [sentiment-dashboard](https://github.com/GustavoVasquezS/sentiment-dashboard) | Dashboard interactivo |

---

## 📄 Licencia

MIT License

---

## 🙏 Agradecimientos

Este proyecto fue posible gracias al esfuerzo colaborativo y el apoyo de múltiples actores:

### Al Programa Hackathon ONE - No Country

Agradecemos profundamente a **No Country** por:
- Proporcionar un espacio de aprendizaje colaborativo y desafiante
- Fomentar el trabajo en equipo interdisciplinario
- Crear oportunidades para desarrolladores de toda Latinoamérica
- Impulsar proyectos que resuelven problemas reales con tecnología

### Al Equipo No Data - No Code

Agradecimiento especial al equipo **No Data - No Code** por el extraordinario trabajo realizado durante la Hackathon:

- **Francisco Llendo** - Por desarrollar y optimizar el modelo de Machine Learning a la versión 4.0, disponible en [Sentimental_API_No_Data_No_Code_Semana_4](https://github.com/GustavoVasquezS/Sentimental_API_No_Data_No_Code_Semana_4). El modelo final incluye:
  - Pipeline TF-IDF + Regresión Logística con calibración de probabilidades
  - Soporte multilingüe (español y portugués)
  - Sistema de revisión automática para predicciones de baja confianza
  - Optimización de umbrales para clasificación de 3 clases
  
- **Jonathan Tuppia** - Por su repositorio de referencia [SentimentAPI](https://github.com/Jona-9/SentimentAPI) y por liderar el deploy de los tres frentes en local y la presentación en el Demo Day.
- **Alexandra Cleto** - Por su repositorio de referencia [sentimientos](https://github.com/Alexandracleto/sentimientos/tree/Ale-dev) que inspiró el diseño del frontend.

### Al Equipo de Desarrollo

**Data Science Team (Python/FastAPI)**:
- Por desarrollar un modelo de ML preciso y eficiente
- Por documentar claramente los endpoints de análisis
- Por optimizar los tiempos de respuesta del modelo

**Backend Team (Java/Spring Boot)**:
- Por la integración fluida con esta API de ML
- Por el diseño del gateway de autenticación

**Frontend Team (React/Tailwind)**:
- Por crear una interfaz intuitiva para consumir los análisis

### A la Comunidad Open Source

Especial reconocimiento a los mantenedores de:
- **FastAPI** - Por el framework web moderno y de alto rendimiento
- **scikit-learn** - Por las herramientas de Machine Learning accesibles
- **Pydantic** - Por la validación de datos robusta

### A los Futuros Usuarios y Contribuidores

Si este proyecto te resultó útil, considera:
- ⭐ **Dar una estrella** al repositorio en GitHub
- 🐛 **Reportar bugs** o sugerir mejoras a través de Issues
- 🔧 **Contribuir** con Pull Requests
- 📢 **Compartir** el proyecto con otros desarrolladores

---

<div align="center">

**⭐ Si este proyecto te ayudó, considera darle una estrella ⭐**

**🚀 Happy Coding! 🚀**

---

*Sentiment Analysis API v4.0 - Hackathon ONE 2026*

</div>
