import React from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const ProtectedRoute = ({ children, requiredRole }) => {
  const token = localStorage.getItem("jwtToken");
  
  // Dynamic redirect based on who is trying to access the route
  const loginRedirect = requiredRole === "OFFICER" ? "/officer-login" : "/consumer-login";

  if (!token) {
    return <Navigate to={loginRedirect} replace />;
  }

  try {
    const decoded = jwtDecode(token);
    
    // 1. EXTRACT ROLES: Handles roles, authorities, or a single role string
    const rawRoles = decoded.roles || decoded.authorities || decoded.role || [];
    const userRoles = Array.isArray(rawRoles) ? rawRoles : [rawRoles];

    // 2. SMART PERMISSION CHECK:
    // Matches "OFFICER" against "OFFICER", "officer", or "ROLE_OFFICER"
    const hasPermission = !requiredRole || userRoles.some(role => {
      const upperRole = role.toUpperCase();
      const upperTarget = requiredRole.toUpperCase();
      return upperRole === upperTarget || upperRole === `ROLE_${upperTarget}`;
    });

    if (!hasPermission) {
      console.warn(`[Security] Access Denied: Route requires ${requiredRole}. User has:`, userRoles);
      // Kick them to home if they are logged in but on the wrong portal
      return <Navigate to="/" replace />; 
    }

    // 3. SUCCESS: Render the requested Dashboard/Portal
    return children;

  } catch (error) {
    console.error("Critical: Session invalid or corrupted token.", error);
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userRole");
    return <Navigate to={loginRedirect} replace />;
  }
};

export default ProtectedRoute;