// C:\Users\HP\OneDrive\Desktop\GESCOM\VoltTrack\frontend\src\App.jsx

import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Navbar from "./components/Navbar.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";  
import ConsumerDashboard from "./pages/ConsumerDashboard.jsx";
import OfficerDashboard from "./pages/OfficerDashboard.jsx"; // ✅ Import the new dashboard
import "./App.css";

const App = () => {
  return (
    <Router>
      <div className="min-h-screen bg-slate-50 flex flex-col">
        <Navbar />
        
        <main className="flex-grow">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/consumer-login" element={<Login />} />  

            {/* Officer Portal now points to the real dashboard */}
            <Route 
              path="/officer-portal" 
              element={
                <ProtectedRoute>
                  <OfficerDashboard />
                </ProtectedRoute>
              } 
            />

            {/* Consumer Dashboard */}
            <Route 
              path="/dashboard" 
              element={
                <ProtectedRoute>
                  <ConsumerDashboard />
                </ProtectedRoute>
              } 
            />
          </Routes>
        </main>
      </div>
    </Router>
  );
};

export default App;
