import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Navbar from "./components/Navbar.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx"; // ✅ Import it
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";  
import ConsumerDashboard from "./pages/ConsumerDashboard.jsx";
import "./App.css";

// Temporary placeholder for Officer Portal (until you build it)
const OfficerPortal = () => (
  <div className="p-20 text-center text-2xl">
    Officer Secure Portal (Coming Soon)
  </div>
);

const App = () => {
  return (
    <Router>
      <div className="min-h-screen bg-slate-50 flex flex-col">
        <Navbar />
        
        <main className="flex-grow">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/consumer-login" element={<Login />} />  
            <Route path="/officer-portal" element={<OfficerPortal />} />

            {/* Protected Dashboard Route */}
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