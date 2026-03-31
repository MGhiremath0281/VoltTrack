import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Shield, Activity, LogOut, Menu, X } from "lucide-react";

const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const token = localStorage.getItem("jwtToken");
  const role = localStorage.getItem("userRole");

  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userRole");
    setIsOpen(false);
    navigate("/");
  };

  const toggleMenu = () => setIsOpen(!isOpen);

  return (
    <>
      <style>{`
        :root {
          --primary-purple: #7c3aed;
          --hover-purple: #6d28d9;
          --soft-purple: #f5f3ff;
          --deep-black: #0f172a;
          --text-gray: #475569;
          --white: #ffffff;
          --glass: rgba(255, 255, 255, 0.95);
        }

        .navbar-custom {
          position: sticky;
          top: 0;
          z-index: 1000;
          background: var(--glass);
          backdrop-filter: blur(12px);
          border-bottom: 1px solid rgba(124, 58, 237, 0.1);
          padding: 0.75rem 1.5rem;
          font-family: 'Inter', system-ui, -apple-system, sans-serif;
        }

        .navbar-container {
          max-width: 1200px;
          margin: 0 auto;
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .nav-logo {
          display: flex;
          align-items: center;
          gap: 10px;
          text-decoration: none;
          z-index: 1001;
        }

        .logo-text {
          font-size: 1.25rem;
          font-weight: 800;
          color: var(--deep-black);
          letter-spacing: -0.025em;
        }

        /* Desktop Menu */
        .nav-wrapper {
          display: flex;
          align-items: center;
          gap: 2rem;
        }

        .nav-menu {
          display: flex;
          gap: 2rem;
          align-items: center;
        }

        .nav-link {
          text-decoration: none;
          color: var(--text-gray);
          font-weight: 500;
          font-size: 0.95rem;
          transition: all 0.2s ease;
        }

        .nav-link:hover { color: var(--primary-purple); }

        .nav-actions {
          display: flex;
          gap: 1rem;
          align-items: center;
        }

        .btn-nav-purple {
          text-decoration: none;
          background: var(--deep-black);
          color: var(--white);
          padding: 8px 18px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 600;
          font-size: 0.85rem;
          transition: all 0.3s ease;
        }

        .btn-logout {
          background: #fff1f2;
          border: 1px solid #ffe4e6;
          color: #e11d48;
          padding: 8px 16px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 600;
          cursor: pointer;
        }

        /* Mobile Styles */
        .mobile-toggle {
          display: none;
          background: none;
          border: none;
          color: var(--deep-black);
          cursor: pointer;
          z-index: 1001;
        }

        @media (max-width: 992px) {
          .mobile-toggle { display: block; }

          .nav-wrapper {
            display: none;
          }

          .nav-wrapper.active {
            display: flex;
            flex-direction: column;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: white;
            padding: 5rem 2rem;
            gap: 2rem;
            align-items: center;
            text-align: center;
          }

          .nav-menu {
            flex-direction: column;
            gap: 1.5rem;
          }

          .nav-link { font-size: 1.2rem; width: 100%; }
          .btn-nav-purple, .btn-logout { width: 100%; justify-content: center; }
        }
      `}</style>

      <nav className="navbar-custom">
        <div className="navbar-container">
          {/* Logo */}
          <Link to="/" className="nav-logo">
            <div className="logo-icon-bg" style={{background: 'var(--soft-purple)', padding: '6px', borderRadius: '10px'}}>
              <Activity size={20} color="#7c3aed" strokeWidth={3} />
            </div>
            <span className="logo-text">VoltTrack<span style={{color: 'var(--primary-purple)'}}>.</span></span>
          </Link>

          {/* Hamburger Toggle */}
          <button className="mobile-toggle" onClick={toggleMenu}>
            {isOpen ? <X size={28} /> : <Menu size={28} />}
          </button>

          {/* Navigation Items Wrapper */}
          <div className={`nav-wrapper ${isOpen ? "active" : ""}`}>
            <div className="nav-menu">
              <a href="#features" className="nav-link" onClick={() => setIsOpen(false)}>Features</a>
              <a href="#tech" className="nav-link" onClick={() => setIsOpen(false)}>Architecture</a>
              <a href="#overview" className="nav-link" onClick={() => setIsOpen(false)}>Operational</a>
              {token && (
                <Link 
                  to={role === "OFFICER" ? "/officer-portal" : "/dashboard"} 
                  className="nav-link"
                  style={{color: 'var(--primary-purple)', fontWeight: '700'}}
                  onClick={() => setIsOpen(false)}
                >
                  Dashboard
                </Link>
              )}
            </div>

            <div className="nav-actions">
              {!token ? (
                <>
                  <Link to="/consumer-login" className="nav-link" onClick={() => setIsOpen(false)}>Login</Link>
                  <Link to="/officer-login" className="btn-nav-purple" onClick={() => setIsOpen(false)}>
                    <Shield size={16} />
                    <span>Officer Dashboard</span>
                  </Link>
                </>
              ) : (
                <button onClick={handleLogout} className="btn-logout">
                  <LogOut size={16} />
                  <span>Logout</span>
                </button>
              )}
            </div>
          </div>
        </div>
      </nav>
    </>
  );
};

export default Navbar;
