// src/pages/Login.jsx
import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Login.css";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      // Send login request
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        username,
        password,
      });

      console.log("Login response:", response.data); // 🔍 Debug

      const token = response.data.token;
      if (!token) {
        setMessage("Login failed: no token received");
        return;
      }

      // Save JWT in localStorage
      localStorage.setItem("jwtToken", token);
      console.log("Stored token:", localStorage.getItem("jwtToken")); // 🔍 Debug

      // ✅ Test protected endpoint right after login
      const profileResponse = await axios.get(
        "http://localhost:8080/api/dashboard/profile",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      console.log("Profile response:", profileResponse.data);

      setMessage("Login successful! Profile loaded.");

      // Redirect to dashboard
      navigate("/dashboard");

    } catch (error) {
      console.error("Login error:", error);
      setMessage("Invalid username or password");
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1 className="text-2xl font-bold text-blue-700 mb-4">VoltTrack Portal</h1>
        <p className="text-gray-500 mb-6">Enter your credentials to access the dashboard</p>

        <form onSubmit={handleLogin} className="space-y-4">
          <div className="input-group">
            <label className="block text-sm font-semibold mb-1">Username</label>
            <input
              type="text"
              className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500 outline-none"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="input-group">
            <label className="block text-sm font-semibold mb-1">Password</label>
            <input
              type="password"
              className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500 outline-none"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-700 hover:bg-blue-800 text-white font-bold py-2 rounded transition-colors"
          >
            Login to Dashboard
          </button>
        </form>

        {message && (
          <p
            className={`mt-4 text-center text-sm font-medium ${
              message.includes("successful") ? "text-green-600" : "text-red-600"
            }`}
          >
            {message}
          </p>
        )}
      </div>
    </div>
  );
};

export default Login;
