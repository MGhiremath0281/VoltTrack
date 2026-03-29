// src/components/ProtectedRoute.jsx
import React from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children }) => {
  // ✅ Match the key used in Login.jsx
  const token = localStorage.getItem("jwtToken");
  console.log("ProtectedRoute check token:", token); // 🔍 Debug

  const isAuthenticated = Boolean(token);

  return isAuthenticated ? children : <Navigate to="/consumer-login" replace />;
};

export default ProtectedRoute;
