import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

// Components
import Navbar from "./components/Navbar.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";

// Pages
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";  
import ConsumerDashboard from "./pages/ConsumerDashboard.jsx";
import OfficerDashboard from "./pages/OfficerDashboard.jsx"; 
import OfficerLogin from "./pages/OfficerLogin.jsx";
import AdminLogin from "./pages/AdminLogin.jsx";       // NEW
import AdminDashboard from "./pages/AdminDashboard.jsx"; // NEW

import "./App.css";

// Helper to prevent logged-in users from hitting login pages
const PublicRoute = ({ children }) => {
  const token = localStorage.getItem("jwtToken");
  const role = localStorage.getItem("userRole");

  if (token) {
    if (role === "OFFICER") return <Navigate to="/officer-portal" replace />;
    if (role === "ADMIN") return <Navigate to="/admin-dashboard" replace />;
    return <Navigate to="/dashboard" replace />;
  }
  return children;
};

const App = () => {
  return (
    <Router>
      <div className="min-h-screen bg-slate-50 flex flex-col font-sans">
        <Navbar />
        
        <main className="flex-grow">
          <Routes>
            {/* --- Public Landing --- */}
            <Route path="/" element={<Home />} />

            {/* --- Authentication (Public Only) --- */}
            <Route 
              path="/consumer-login" 
              element={<PublicRoute><Login /></PublicRoute>} 
            /> 
            <Route 
              path="/officer-login" 
              element={<PublicRoute><OfficerLogin /></PublicRoute>} 
            />
            <Route 
              path="/admin-login" 
              element={<PublicRoute><AdminLogin /></PublicRoute>} 
            />

            {/* Redirect generic /login to consumer login */}
            <Route path="/login" element={<Navigate to="/consumer-login" replace />} />

            {/* --- Protected Officer Portal --- */}
            <Route 
              path="/officer-portal" 
              element={
                <ProtectedRoute requiredRole="OFFICER">
                  <OfficerDashboard />
                </ProtectedRoute>
              } 
            />

            {/* --- Protected Consumer Portal --- */}
            <Route 
              path="/dashboard" 
              element={
                <ProtectedRoute requiredRole="CONSUMER">
                  <ConsumerDashboard />
                </ProtectedRoute>
              } 
            />

            {/* --- Protected Admin Portal --- */}
            <Route 
              path="/admin-dashboard" 
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminDashboard />
                </ProtectedRoute>
              } 
            />

            {/* --- 404 / Catch-All --- */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
};

export default App;
