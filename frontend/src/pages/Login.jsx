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
    setMessage(""); // Clear previous messages

    try {
      // 1. Send login request to your Spring Boot backend
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        username,
        password,
      });

      console.log("Login response:", response.data);

      const token = response.data.token;
      if (!token) {
        setMessage("Login failed: no token received");
        return;
      }

      // 2. Save JWT and explicit Role in localStorage
      // This allows App.jsx ProtectedRoutes to validate the user
      localStorage.setItem("jwtToken", token);
      localStorage.setItem("userRole", "CONSUMER"); 

      console.log("Stored token & role: CONSUMER");

      // 3. (Optional) Verify token by fetching profile immediately
      try {
        await axios.get(
          "http://localhost:8080/api/dashboard/profile",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setMessage("Login successful! Redirecting...");
      } catch (profileErr) {
        console.warn("Profile fetch failed, but token is saved.");
      }

      // 4. Redirect to the protected dashboard route
      // The App.jsx Route path="/dashboard" will now allow entry
      setTimeout(() => {
        navigate("/dashboard");
      }, 500);

    } catch (error) {
      console.error("Login error:", error);
      setMessage("Invalid username or password");
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1 className="text-2xl font-bold text-blue-700 mb-2">VoltTrack Portal</h1>
        <p className="text-gray-500 mb-6">Consumer Access</p>

        <form onSubmit={handleLogin} className="space-y-4">
          <div className="input-group">
            <label className="block text-sm font-semibold mb-1 text-gray-700">Username</label>
            <input
              type="text"
              className="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 outline-none transition-all"
              placeholder="Enter username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="input-group">
            <label className="block text-sm font-semibold mb-1 text-gray-700">Password</label>
            <input
              type="password"
              className="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 outline-none transition-all"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-700 hover:bg-blue-800 text-white font-bold py-2.5 rounded shadow-sm transition-colors mt-2"
          >
            Login to Dashboard
          </button>
        </form>

        {message && (
          <div
            className={`mt-4 p-2 text-center text-sm rounded font-medium ${
              message.includes("successful") 
                ? "bg-green-50 text-green-700 border border-green-200" 
                : "bg-red-50 text-red-700 border border-red-200"
            }`}
          >
            {message}
          </div>
        )}
      </div>
    </div>
  );
};

export default Login;