import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage("");
    setLoading(true);

    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        username,
        password,
      });

      const token = response.data.token;
      // Get role from response, fallback to CONSUMER if not explicitly sent
      const role = response.data.role || "CONSUMER"; 

      if (!token) {
        setMessage("Login failed: no token received");
        setLoading(false);
        return;
      }

      // 1. Store Credentials
      localStorage.setItem("jwtToken", token);
      localStorage.setItem("userRole", role);

      setMessage("Verification successful! Redirecting to grid...");

      // 2. Perform Redirection based on Role
      setTimeout(() => {
        if (role === "OFFICER") {
          navigate("/officer-portal");
        } else {
          navigate("/dashboard");
        }
      }, 800);

    } catch (error) {
      console.error("Login error:", error);
      const errorMsg = error.response?.data?.message || "Invalid credentials or server offline";
      setMessage(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-wrapper">
      <style>{`
        .login-wrapper {
          min-height: 100vh;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: #0f172a;
          background-image: radial-gradient(circle at top right, #1e1b4b, #0f172a);
          font-family: 'Inter', system-ui, sans-serif;
          margin: 0;
        }

        .login-card {
          background: #ffffff;
          padding: 40px;
          border-radius: 20px;
          box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3);
          width: 100%;
          max-width: 380px;
          text-align: center;
        }

        .login-card h1 {
          color: #1e40af;
          font-size: 30px;
          margin-bottom: 5px;
          font-weight: 800;
          letter-spacing: -0.5px;
        }

        .login-card p {
          color: #64748b;
          margin-bottom: 30px;
          font-size: 14px;
          font-weight: 500;
        }

        .form-group {
          margin-bottom: 18px;
          text-align: left;
        }

        .form-group label {
          display: block;
          font-size: 12px;
          font-weight: 700;
          color: #475569;
          margin-bottom: 8px;
          text-transform: uppercase;
          letter-spacing: 0.5px;
        }

        .form-group input {
          width: 100%;
          padding: 14px;
          border: 2px solid #e2e8f0;
          border-radius: 10px;
          font-size: 15px;
          box-sizing: border-box;
          transition: all 0.2s ease;
        }

        .form-group input:focus {
          outline: none;
          border-color: #3b82f6;
          box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
        }

        .login-button {
          width: 100%;
          padding: 14px;
          background-color: #1d4ed8;
          color: white;
          border: none;
          border-radius: 10px;
          font-size: 16px;
          font-weight: 700;
          cursor: pointer;
          transition: transform 0.1s, background-color 0.2s;
          margin-top: 10px;
        }

        .login-button:hover {
          background-color: #1e40af;
        }

        .login-button:active {
          transform: scale(0.98);
        }

        .login-button:disabled {
          background-color: #94a3b8;
          cursor: wait;
        }

        .auth-status {
          margin-top: 20px;
          padding: 12px;
          border-radius: 8px;
          font-size: 13px;
          font-weight: 600;
          animation: fadeIn 0.3s ease;
        }

        .status-success {
          background-color: #ecfdf5;
          color: #065f46;
          border: 1px solid #a7f3d0;
        }

        .status-error {
          background-color: #fef2f2;
          color: #991b1b;
          border: 1px solid #fecaca;
        }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(-10px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>

      <div className="login-card">
        <h1>VoltTrack</h1>
        <p>Secure Grid Authentication</p>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label>Access Identity</label>
            <input
              type="text"
              placeholder="Username or ID"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Security Key</label>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? "Establishing Link..." : "Authenticate"}
          </button>
        </form>

        {message && (
          <div className={`auth-status ${message.includes("successful") ? "status-success" : "status-error"}`}>
            {message}
          </div>
        )}
      </div>
    </div>
  );
};

export default Login;