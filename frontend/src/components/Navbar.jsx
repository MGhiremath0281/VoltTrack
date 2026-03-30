import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { Shield, Activity, ChevronRight, LogOut } from "lucide-react";

const Navbar = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("jwtToken");
  const role = localStorage.getItem("userRole");

  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userRole");
    navigate("/"); // Redirect to home after logout
  };

  return (
    <nav className="navbar-custom">
      <div className="navbar-container">
        {/* Logo */}
        <Link to="/" className="nav-logo">
          <div className="logo-icon-bg">
            <Activity size={22} color="#2563eb" strokeWidth={3} />
          </div>
          <span className="logo-text">
            VoltTrack<span className="dot">.</span>
          </span>
        </Link>

        {/* Navigation Links */}
        <div className="nav-menu">
          <a href="#features" className="nav-link">Features</a>
          <a href="#tech" className="nav-link">Architecture</a>
          <a href="#overview" className="nav-link">Operational</a>
          {/* Show Dashboard link if logged in */}
          {token && (
            <Link to={role === "OFFICER" ? "/officer-portal" : "/dashboard"} className="nav-link font-bold text-blue-600">
              Go to Dashboard
            </Link>
          )}
        </div>

        {/* CTA Actions */}
        <div className="nav-actions">
          {!token ? (
            <>
              {/* Show Login Links if NOT logged in */}
              <Link to="/consumer-login" className="btn-nav-outline">
                Consumer Login
              </Link>

              <Link to="/officer-login" className="btn-nav-dark">
                <Shield size={16} />
                <span>Officer Dashboard</span>
                <ChevronRight size={14} className="icon-shift" />
              </Link>
            </>
          ) : (
            /* Show Logout Button if logged in */
            <button onClick={handleLogout} className="btn-nav-outline flex items-center gap-2 border-red-200 text-red-600 hover:bg-red-50">
              <LogOut size={16} />
              <span>Logout</span>
            </button>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;