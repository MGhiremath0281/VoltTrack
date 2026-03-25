import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar.jsx";
import Home from "./pages/Home.jsx";
import "./App.css";

// Temporary placeholder components for testing
const ConsumerLogin = () => <div className="p-20 text-center text-2xl">Consumer Login Page (Coming Soon)</div>;
const OfficerPortal = () => <div className="p-20 text-center text-2xl">Officer Secure Portal (Coming Soon)</div>;

const App = () => {
  return (
    <Router>
      <div className="min-h-screen bg-slate-50 flex flex-col">
        <Navbar />
        
        <main className="flex-grow">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/consumer-login" element={<ConsumerLogin />} />
            <Route path="/officer-portal" element={<OfficerPortal />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
};

export default App;