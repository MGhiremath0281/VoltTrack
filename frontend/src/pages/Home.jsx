import React from "react";
import { Link } from "react-router-dom";
import BlockDiagram from "../assets/Block.jpeg";
import Meter from "../assets/meter.jpeg";
import LaptopGateway from "../assets/gateway.jpeg";

import { Zap, ShieldCheck, Activity, Cpu, BarChart3, Timer, Layers, Laptop } from "lucide-react";
import "../App.css";

const Home = () => {
  const features = [
    { title: "Live Monitoring", desc: "Real-time tracking of voltage, current, and power spikes.", icon: <Activity size={28} /> },
    { title: "Intelligent Alerts", desc: "Security-first tamper detection and load anomaly spikes.", icon: <ShieldCheck size={28} /> },
    { title: "Heartbeat Logic", desc: "Instant offline detection for meters inactive for 10+ minutes.", icon: <Timer size={28} /> },
    { title: "Pulse Conversion", desc: "Accurate calculation using 1000 pulses per kWh standard.", icon: <Zap size={28} /> },
    { title: "Admin Analytics", desc: "Advanced area-wise usage heatmaps and grid load data.", icon: <BarChart3 size={28} /> },
    { title: "Bill Prediction", desc: "AI-ready bill estimation based on historical usage.", icon: <Layers size={28} /> }
  ];

  const techStack = [
    { name: "Spring Boot", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" },
    { name: "Java 17", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" },
    { name: "PostgreSQL", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" },
    { name: "Redis", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/redis/redis-original.svg" },
    { name: "Docker", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg" },
    { name: "React", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg" }
  ];

  return (
    <div className="home-wrapper">
      {/* Hero Section */}
      <header className="hero">
        <div className="container">
          <div className="badge">Smart Grid Initiative 2026</div>
          <h1 className="hero-title">
            VoltTrack: <span className="text-gradient">Intelligent Energy</span>
          </h1>
          <p className="hero-subtitle">
            A production-grade Spring Boot ecosystem for real-time telemetry, automated billing, and grid-level monitoring using IoT smart meters.
          </p>
          <div className="btn-group">
            <Link to="/consumer-login" className="btn btn-primary">Get Started</Link>
            <button className="btn btn-outline">Technical Docs</button>
          </div>
        </div>
      </header>

      {/* System Overview */}
      <section className="container dashboard-preview-container">
        <div className="image-placeholder-modern">
          <img src={BlockDiagram} alt="VoltTrack System Overview" className="dashboard-img" />
        </div>
      </section>

      {/* Features */}
      <section id="features" className="dark-section">
        <div className="container">
          <div className="section-header">
            <h2 className="text-white">Enterprise Capabilities</h2>
            <p className="text-muted">High-performance features for modern energy grids</p>
          </div>
          <div className="feature-grid">
            {features.map((f, idx) => (
              <div key={idx} className="feature-card">
                <div className="feature-icon">{f.icon}</div>
                <h3>{f.title}</h3>
                <p>{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Tech Stack */}
      <section id="tech" className="tech-section">
        <div className="container center-text">
          <h2 className="section-title">Powered By</h2>
          <div className="tech-grid">
            {techStack.map((tech) => (
              <div key={tech.name} className="tech-item">
                <img src={tech.icon} alt={tech.name} className="tech-logo" />
                <span className="tech-name">{tech.name}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* IoT Hardware */}
      <section className="iot-connection-section">
        <div className="container">
          <div className="section-header">
            <span className="badge">Hardware Integration</span>
            <h2 className="section-title">IoT Connectivity & Architecture</h2>
          </div>
          <div className="iot-image-grid">
            <div className="iot-card">
              <div className="iot-image-wrapper">
                <img src={Meter} alt="Meter Connection Hardware" className="iot-img" />
              </div>
              <div className="iot-info">
                <div className="iot-status-tag">
                  <span className="pulse-dot"></span> Active Node
                </div>
                <h3><Cpu size={20} /> Meter-to-Node Interface</h3>
                <p>Direct ESP32 integration with the energy meter to capture pulse data and voltage telemetry.</p>
              </div>
            </div>
            <div className="iot-card">
              <div className="iot-image-wrapper">
                <img src={LaptopGateway} alt="Laptop Gateway Connection" className="iot-img" />
              </div>
              <div className="iot-info">
                <div className="iot-status-tag">
                  <span className="pulse-dot"></span> Uplink Active
                </div>
                <h3><Laptop size={20} /> Laptop & Gateway Uplink</h3>
                <p>LoRa gateway communication routing local meter data to the Spring Boot backend via serial-to-cloud bridge.</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="footer">
        <div className="container">
          <div className="footer-content">
            <div className="footer-brand">
              <strong>VoltTrack</strong>
              <p>Advanced Utility Management</p>
            </div>
            <p className="footer-copy">© 2026 GESCOM Project — Engineering Edition</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Home;
