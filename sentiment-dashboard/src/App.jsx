// src/App.jsx
import React, { useState, useEffect, useRef } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import Landing from './views/Landing';
import Auth from './views/Auth';
import DashboardView from './views/DashboardView';
import AnalysisView from './views/AnalysisView';
import HistoryView from './views/HistoryView';
import DemoSelectionView from './views/DemoSelectionView';
import CategorySelectionView from './views/CategorySelectionView';
import ProductSelectionView from './views/ProductSelectionView';
import ForgotPasswordView from './views/ForgotPasswordView';
import ResetPasswordView from './views/ResetPasswordView';
import { sentimentService } from './services/sentimentService';
import Footer from './components/Footer';

const STORAGE_KEY = 'sentimentapi_user';

const AppContent = () => {
  const navigate = useNavigate();
  const location = useLocation();
  
  const [user, setUser] = useState(() => {
    try {
      const savedUser = localStorage.getItem(STORAGE_KEY);
      return savedUser ? JSON.parse(savedUser) : null;
    } catch (error) {
      console.error('Error al cargar usuario desde localStorage:', error);
      return null;
    }
  });
  
  const [isDemo, setIsDemo] = useState(false);
  const [showMobileMenu, setShowMobileMenu] = useState(false);
  
  const [text, setText] = useState('');
  const [analyzing, setAnalyzing] = useState(false);
  // Guard SÍNCRONO contra doble-submit: setAnalyzing es asíncrono, así que un doble-clic rápido
  // pasaría el disabled={analyzing} y dispararía dos POST → dos sesiones duplicadas.
  const analyzingRef = useRef(false);
  const [results, setResults] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [pendingHistoricalSession, setPendingHistoricalSession] = useState(null);
  
  // Estados para Categorías y Productos
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedProducts, setSelectedProducts] = useState([]);

  useEffect(() => {
    if (user && !isDemo) {
      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
      } catch (error) {
        console.error('Error al guardar usuario en localStorage:', error);
      }
    } else if (!user) {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [user, isDemo]);

  useEffect(() => {
    // Si hay una sesión histórica pendiente, cargarla en vez de limpiar
    if (pendingHistoricalSession) {
      setResults(pendingHistoricalSession);
      setPendingHistoricalSession(null);
      setText('');
      setErrorMessage('');
      return;
    }
    setText('');
    setResults(null);
    setErrorMessage('');
  }, [location.pathname]);

  // Cargar una sesión histórica como resultados y navegar a la vista de análisis
  const loadSessionFromHistory = async (session) => {
    // La fila del listado (GET /sesiones) NO trae comentarios; hay que pedir el detalle
    // (GET /sesiones/{id}) que sí los incluye. Si falla, se cae a la fila del listado.
    let detalle = session;
    if (session?.sessionId && user?.token) {
      try {
        detalle = await sentimentService.getSessionById(session.sessionId, user.token);
      } catch (err) {
        console.error('No se pudo cargar el detalle de la sesión, usando resumen:', err);
        detalle = session;
      }
    }

    const items = (detalle.comentarios || []).map(c => ({
      text: c.texto,
      sentiment: c.sentimiento,
      score: c.probabilidad,
      productoAsociado: c.productoAsociado || null
    }));

    const restoredResults = {
      isBatch: true,
      totalAnalyzed: session.total || items.length,
      sessionSaved: true,
      sessionId: session.sessionId,
      isHistorical: true,
      items,
      stats: {
        avgScore: session.avgScore,
        positivos: session.positivos || 0,
        negativos: session.negativos || 0,
        neutrales: session.neutrales || 0,
      },
      productosDetectados: session.productosDetectados || []
    };

    setPendingHistoricalSession(restoredResults);
    navigate('/analysis-batch');
  };

  const analyzeSentiment = async (csvEntradas = null) => {
    if (analyzingRef.current) return; // ya hay un análisis en curso → ignorar re-entrada
    if (!text.trim() && !csvEntradas) return;

    analyzingRef.current = true;
    setAnalyzing(true);
    setErrorMessage('');
    
    const isBatchMode = location.pathname === '/analysis-batch' || location.pathname === '/demo-batch';
    
    try {
      if (isBatchMode) {
        // ✨ Nuevo flujo: si hay entradas CSV con producto/categoría, usar el endpoint CSV batch
        if (csvEntradas && csvEntradas.length > 0 && user && !isDemo && user.token) {
          console.log('📊 Analizando CSV batch con', csvEntradas.length, 'entradas');
          
          const result = await sentimentService.analyzeCsvBatch(csvEntradas, user.token);
          setResults(result);
        } else if (user && !isDemo && user.token && selectedProducts.length > 0) {
          // Flujo legacy con productos pre-seleccionados
          console.log('🔑 Analizando con productos:', selectedProducts.map(p => p.nombreProducto));
          
          const comentarios = text.split('\n').filter(t => t.trim());
          const productosIds = selectedProducts.map(p => p.productoId);
          
          const result = await sentimentService.analyzeWithMultipleProducts(
            comentarios, 
            user.token,
            productosIds
          );
          
          setResults({
            isBatch: true,
            totalAnalyzed: result.total,
            sessionSaved: true,
            sessionId: result.sessionId,
            items: result.comentarios || [],
            stats: {
              avgScore: result.avgScore,
              positivos: result.positivos,
              negativos: result.negativos,
              neutrales: result.neutrales,
            },
            productosDetectados: result.productosDetectados || []
          });
        } else {
          // Análisis batch simple (sin productos específicos)
          const comentarios = text.split('\n').filter(t => t.trim());
          if (comentarios.length === 0) {
            setErrorMessage('No hay textos válidos para analizar.');
            setAnalyzing(false);
            return;
          }

          if (user && !isDemo && user.token) {
            // Usuario logueado → reutilizar analyzeCsvBatch con entradas sin producto/categoría
            // (evita depender de un endpoint /sesiones/analizar que no existe en el backend)
            console.log('📝 Analizando texto manual con', comentarios.length, 'comentarios (guardando sesión)');
            const entradas = comentarios.map(texto => ({ texto, producto: '', categoria: '' }));
            const result = await sentimentService.analyzeCsvBatch(entradas, user.token);
            setResults(result);
          } else {
            // Modo demo o sin login → análisis sin guardar
            const result = await sentimentService.analyzeBatch(text);
            setResults(result);
          }
        }
      } else {
        // Análisis simple (un solo texto)
        const result = await sentimentService.analyzeSingle(text);
        setResults(result);
      }
    } catch (error) {
      console.error('❌ Error en análisis:', error);
      setErrorMessage(error.message || 'Error al analizar el texto');
      setResults(null);
    } finally {
      setAnalyzing(false);
      analyzingRef.current = false; // libera el guard (también cubre los early-return dentro del try)
    }
  };

  const getSentimentColor = (sentiment) => {
    const normalizedSentiment = sentiment?.toLowerCase().trim();
    switch(normalizedSentiment) {
      case 'positivo': return '#10b981';
      case 'negativo': return '#ef4444';
      case 'neutral': return '#f59e0b';
      default: return '#8b5cf6';
    }
  };

  const getStatistics = () => {
    if (!results?.isBatch) return null;
    
    if (results.stats) {
      const { positivos, negativos, neutrales } = results.stats;
      const total = positivos + negativos + neutrales;
      const safeTotal = total === 0 ? 1 : total;
      
      return [
        { name: 'Positivo', value: positivos, color: '#10b981', percentage: ((positivos / safeTotal) * 100).toFixed(1) },
        { name: 'Negativo', value: negativos, color: '#ef4444', percentage: ((negativos / safeTotal) * 100).toFixed(1) },
        { name: 'Neutral', value: neutrales, color: '#f59e0b', percentage: ((neutrales / safeTotal) * 100).toFixed(1) }
      ];
    }
    
    // Fallback si no hay stats precalculados
    const items = results.items || [];
    if (items.length === 0) return null;
    
    const counts = { positivo: 0, negativo: 0, neutral: 0 };
    
    items.forEach(item => {
      const sentiment = item.sentiment?.toLowerCase().trim();
      if (sentiment === 'positivo') counts.positivo++;
      else if (sentiment === 'negativo') counts.negativo++;
      else if (sentiment === 'neutral') counts.neutral++;
    });
    
    const total = items.length;
    const safeTotal = total === 0 ? 1 : total;
    
    return [
      { name: 'Positivo', value: counts.positivo, color: '#10b981', percentage: ((counts.positivo / safeTotal) * 100).toFixed(1) },
      { name: 'Negativo', value: counts.negativo, color: '#ef4444', percentage: ((counts.negativo / safeTotal) * 100).toFixed(1) },
      { name: 'Neutral', value: counts.neutral, color: '#f59e0b', percentage: ((counts.neutral / safeTotal) * 100).toFixed(1) }
    ];
  };

  const handleLogin = (e, userData) => {
    e.preventDefault();
    console.log('✅ Login exitoso, token recibido:', userData.token);
    
    const newUser = {
      id: userData.id,
      email: userData.correo,
      name: userData.nombreCompleto || `${userData.nombre} ${userData.apellido}`,
      token: userData.token,
    };
    
    setUser(newUser);
    setIsDemo(false);
    navigate('/dashboard');
  };

  const handleRegister = (e, userData) => {
    e.preventDefault();
    navigate('/login');
  };

  const handleLogout = () => {
    console.log('🚪 Cerrando sesión...');
    setUser(null);
    setIsDemo(false);
    localStorage.removeItem(STORAGE_KEY);
    navigate('/');
    setText('');
    setResults(null);
    setErrorMessage('');
    setSelectedCategory(null);
    setSelectedProducts([]);
  };

  const handleDemoStart = () => {
    setUser({ email: 'demo@sentimentapi.com', name: 'Demo' });
    setIsDemo(true);
    navigate('/demo-selection');
  };

  const handleBackToLanding = () => {
    setUser(null);
    setIsDemo(false);
    localStorage.removeItem(STORAGE_KEY);
    navigate('/');
    setText('');
    setResults(null);
    setErrorMessage('');
    setSelectedCategory(null);
    setSelectedProducts([]);
  };

  const setCurrentView = (view) => {
    navigate(`/${view}`);
  };

  // Handlers para el flujo de selección
  const handleCategorySelected = (category) => {
    setSelectedCategory(category);
    navigate('/product-selection');
  };

  const handleProductsSelected = (products) => {
    setSelectedProducts(products);
    navigate('/analysis-batch');
  };

  const analysisProps = {
    setCurrentView,
    user,
    isDemo,
    handleLogout,
    handleBackToLanding,
    text,
    setText,
    analyzing,
    analyzeSentiment,
    results,
    setResults,
    getStatistics,
    getSentimentColor,
    errorMessage,
    selectedProducts
  };

  return (
    <div className="flex flex-col min-h-screen">
    <div className="flex-1">
    <Routes>
      <Route 
        path="/" 
        element={
          <Landing 
            setCurrentView={setCurrentView}
            handleDemoStart={handleDemoStart}
            showMobileMenu={showMobileMenu} 
            setShowMobileMenu={setShowMobileMenu} 
          />
        } 
      />

      <Route 
        path="/login" 
        element={
          <Auth 
            type="login" 
            handleSubmit={handleLogin} 
            setCurrentView={setCurrentView} 
          />
        } 
      />

      <Route 
        path="/register" 
        element={
          <Auth 
            type="register" 
            handleSubmit={handleRegister} 
            setCurrentView={setCurrentView} 
          />
        } 
      />

      <Route
        path="/forgot-password"
        element={
          <ForgotPasswordView setCurrentView={setCurrentView} />
        }
      />

      <Route
        path="/reset-password"
        element={
          <ResetPasswordView setCurrentView={setCurrentView} />
        }
      />

      <Route 
        path="/demo-selection" 
        element={
          user && isDemo ? (
            <DemoSelectionView
              setCurrentView={setCurrentView}
              handleBackToLanding={handleBackToLanding}
            />
          ) : (
            <Navigate to="/" replace />
          )
        } 
      />

      <Route 
        path="/dashboard" 
        element={
          user && !isDemo ? (
            <DashboardView
              currentView="dashboard"
              setCurrentView={setCurrentView}
              user={user}
              isDemo={isDemo}
              handleLogout={handleLogout}
            />
          ) : (
            <Navigate to="/" replace />
          )
        } 
      />

      {/* Rutas del flujo de selección de productos */}
      <Route 
        path="/category-selection" 
        element={
          user && !isDemo ? (
            <CategorySelectionView
              user={user}
              token={user.token}
              onCategorySelected={handleCategorySelected}
              onBack={() => navigate('/dashboard')}
            />
          ) : (
            <Navigate to="/" replace />
          )
        } 
      />

      <Route 
        path="/product-selection" 
        element={
          user && !isDemo && selectedCategory ? (
            <ProductSelectionView
              user={user}
              token={user.token}
              categoria={selectedCategory}
              onProductsSelected={handleProductsSelected}
              onBack={() => navigate('/category-selection')}
            />
          ) : (
            <Navigate to="/category-selection" replace />
          )
        } 
      />

      {/* Rutas de Análisis */}
      <Route 
        path="/analysis-simple" 
        element={
          user && !isDemo ? (
            <AnalysisView
              currentView="analysis-simple"
              isBatchMode={false}
              {...analysisProps}
            />
          ) : (
            <Navigate to="/" replace />
          )
        } 
      />

      <Route 
        path="/analysis-batch" 
        element={
          user && !isDemo ? (
            <AnalysisView
              currentView="analysis-batch"
              isBatchMode={true}
              {...analysisProps}
            />
          ) : (
            <Navigate to="/" replace />
          )
        } 
      />

      <Route 
        path="/demo-simple" 
        element={
          user && isDemo ? (
            <AnalysisView
              currentView="demo-simple"
              isBatchMode={false}
              {...analysisProps}
            />
          ) : (
            <Navigate to="/" replace />
          )
        } 
      />

      <Route 
        path="/demo-batch" 
        element={
          user && isDemo ? (
            <AnalysisView
              currentView="demo-batch"
              isBatchMode={true}
              {...analysisProps}
            />
          ) : (
            <Navigate to="/" replace />
          )
        } 
      />

      <Route 
        path="/history" 
        element={
          user && !isDemo ? (
            <HistoryView 
              user={user}
              token={user.token}
              setCurrentView={setCurrentView} 
              handleLogout={handleLogout}
              onLoadSession={loadSessionFromHistory}
            />
          ) : (
            <Navigate to="/" replace />
          )
        } 
      />
      
      {/* Ruta Catch-all */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
    </div>
    <Footer />
    </div>
  );
};

const App = () => {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
};

export default App;