import React from "react";
import { Link } from "react-router-dom";
import { Shield, Activity, ChevronRight } from "lucide-react";

const Navbar = () => {
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
        </div>

        {/* CTA Actions */}
        <div className="nav-actions">
          {/* Consumer Login now routes to /consumer-login */}
          <Link to="/consumer-login" className="btn-nav-outline">
            Consumer Login
          </Link>

          {/* Officer Login placeholder */}
          <Link to="/officer-portal" className="btn-nav-dark">
            <Shield size={16} />
            <span>Officer Login</span>
            <ChevronRight size={14} className="icon-shift" />
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
