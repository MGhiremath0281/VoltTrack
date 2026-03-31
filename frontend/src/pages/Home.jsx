import React, { useState } from "react";
import { Link } from "react-router-dom";
import BlockDiagram from "../assets/Block.jpeg";
import Meter from "../assets/meter.jpeg";
import LaptopGateway from "../assets/gateway.jpeg";

import {
  Zap, ShieldCheck, Activity, BarChart3, Timer, Layers,
  ArrowRight, Cpu, Wifi, Database, Server, Radio,
  Code2, Lock
} from "lucide-react";
import "../App.css";

/* ─────────────────────────────────────────
    DATA
───────────────────────────────────────── */
const FEATURES = [
  { tag: "core.monitor", title: "Live Monitoring",     desc: "Real-time tracking of voltage, current, and power spikes with sub-10ms latency.",           icon: <Activity size={20} /> },
  { tag: "core.security", title: "Intelligent Alerts", desc: "Security-first tamper detection and load anomaly identification across the grid.",            icon: <ShieldCheck size={20} /> },
  { tag: "core.heartbeat",title: "Heartbeat Logic",     desc: "Instant offline detection for meters inactive for 10+ minutes via keep-alive signals.",      icon: <Timer size={20} /> },
  { tag: "core.pulse",    title: "Pulse Conversion",   desc: "Accurate energy calculation using the 1000 pulses per kWh industry standard.",                icon: <Zap size={20} /> },
  { tag: "admin.analytics",title: "Admin Analytics",  desc: "Area-wise usage heatmaps, peak-load graphs, and grid load distribution dashboards.",          icon: <BarChart3 size={20} /> },
  { tag: "ai.billing",    title: "Bill Prediction",    desc: "AI-ready estimation engine using historical consumption patterns and time-series trends.",    icon: <Layers size={20} /> },
];

const TECH_STACK = [
  { name: "Spring Boot", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" },
  { name: "Java 17",     icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" },
  { name: "PostgreSQL",  icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" },
  { name: "Redis",       icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/redis/redis-original.svg" },
  { name: "Docker",      icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg" },
  { name: "React",       icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg" },
];

const STATS = [
  { number: "10ms",  label: "Telemetry Latency" },
  { number: "1000",  label: "Pulses / kWh" },
  { number: "99.9%", label: "Uptime SLA" },
  { number: "10min", label: "Heartbeat Window" },
];

const PIPELINE = [
  { icon: <Radio size={24} />, title: "Smart Meter", desc: "Pulse output, tamper detection, DIN mounted" },
  { icon: <Wifi size={24} />,  title: "IoT Gateway",  desc: "MQTT broker, edge preprocessing, TLS" },
  { icon: <Server size={24} />, title: "Spring Boot", desc: "REST API, event processing, Redis pub/sub" },
  { icon: <Database size={24} />, title: "PostgreSQL", desc: "Time-series storage, billing records, audit log" },
];

const API_ENDPOINTS = [
  {
    method: "GET",  path: "/api/v1/meters/{id}/live",    desc: "Live reading",
    response: `{
  <span class="code-key">"meterId"</span>: <span class="code-str">"MTR-002-KA"</span>,
  <span class="code-key">"timestamp"</span>: <span class="code-str">"2026-03-31T10:24:01Z"</span>,
  <span class="code-key">"voltage"</span>: <span class="code-num">231.4</span>,
  <span class="code-key">"current"</span>: <span class="code-num">8.72</span>,
  <span class="code-key">"activePower"</span>: <span class="code-num">2017.0</span>,
  <span class="code-key">"status"</span>: <span class="code-str">"ONLINE"</span>,
  <span class="code-key">"tamper"</span>: <span class="code-bool">false</span>
}`,
  },
  {
    method: "GET",  path: "/api/v1/meters/{id}/usage",   desc: "Usage history",
    response: `{
  <span class="code-key">"period"</span>: <span class="code-str">"2026-03"</span>,
  <span class="code-key">"totalKwh"</span>: <span class="code-num">148.36</span>,
  <span class="code-key">"peakDemand"</span>: <span class="code-num">3.82</span>,
  <span class="code-key">"readings"</span>: [<span class="code-num">...</span>]
}`,
  },
  {
    method: "POST", path: "/api/v1/meters/{id}/pulse",   desc: "Ingest pulse",
    response: `{
  <span class="code-key">"accepted"</span>: <span class="code-bool">true</span>,
  <span class="code-key">"pulseCount"</span>: <span class="code-num">1</span>,
  <span class="code-key">"kwhDelta"</span>: <span class="code-num">0.001</span>,
  <span class="code-key">"ackId"</span>: <span class="code-str">"ACK-8f3a"</span>
}`,
  },
  {
    method: "GET",  path: "/api/v1/admin/grid/heatmap",  desc: "Grid heatmap",
    response: `{
  <span class="code-key">"zones"</span>: [
    { <span class="code-key">"area"</span>: <span class="code-str">"Zone-A"</span>, <span class="code-key">"load"</span>: <span class="code-num">82.4</span> },
    { <span class="code-key">"area"</span>: <span class="code-str">"Zone-B"</span>, <span class="code-key">"load"</span>: <span class="code-num">61.7</span> }
  ]
}`,
  },
];

const ARCH_NODES = [
  { icon: <Radio size={18} />,    label: "Smart Meter", sub: "Pulse / DLMS" },
  { icon: <Wifi size={18} />,     label: "Gateway",     sub: "MQTT / TLS"   },
  { icon: <Code2 size={18} />,    label: "API Layer",   sub: "REST / WS"    },
  { icon: <Database size={18} />, label: "PostgreSQL",  sub: "TimescaleDB"  },
  { icon: <BarChart3 size={18} />,label: "Dashboard",   sub: "React / SSE"  },
];

const IOT_DEVICES = [
  {
    image: Meter, title: "Smart Energy Meter", icon: <Zap size={16} />,
    desc: "Industrial-grade pulse-output energy meter with tamper detection and DIN rail mounting.",
    specs: ["1000 pulse/kWh", "DLMS/COSEM", "Class 1 Accuracy", "RS-485 Port"],
  },
  {
    image: LaptopGateway, title: "IoT Gateway Unit", icon: <Cpu size={16} />,
    desc: "Edge compute gateway bridging meter pulse data to the cloud via MQTT over TLS.",
    specs: ["MQTT Broker", "4G / Ethernet", "AES-256 TLS", "OTA Updates"],
  },
];

/* ─────────────────────────────────────────
    COMPONENT
───────────────────────────────────────── */
const Home = () => {
  const [activeEndpoint, setActiveEndpoint] = useState(0);
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  };

  return (
    <div className="home-wrapper">
      {/* SIMPLE LOGO HEADER (No Nav Links) */}
      <nav className="navbar-custom">
        <div className="navbar-container">
          <a href="/" className="nav-logo">
            <span className="logo-text">Volt<span className="dot">Track</span></span>
          </a>
        </div>
      </nav>

      {/* HERO SECTION */}
      <header className="hero">
        <div className="hero-bg">
          <div className="hero-orb hero-orb-1" />
          <div className="hero-orb hero-orb-2" />
          <div className="hero-orb hero-orb-3" />
          <div className="hero-grid" />
        </div>

        <div className="container">
          <div className="badge">
            <span className="badge-pip" />
            Smart Grid Initiative 2026
          </div>

          <h1 className="hero-title">
            VoltTrack<br />
            <span className="text-gradient">Intelligent Energy</span>
          </h1>

          <p className="hero-subtitle">
            A production-grade Spring Boot ecosystem for real-time telemetry,
            automated billing, and grid-level monitoring using IoT smart meters.
          </p>

          <div className="btn-group">
            <Link to="/consumer-login" className="btn btn-primary">
              Get Started <ArrowRight size={16} />
            </Link>
            <a href="#api" className="btn btn-outline">
              <Code2 size={16} /> Explore API
            </a>
          </div>

          <div className="hero-terminal-strip">
            <span className="terminal-chip"><span className="t-prompt">$</span> <span className="t-cmd">status</span> <span style={{color:"#86efac"}}>● ONLINE</span></span>
            <span className="terminal-chip"><span className="t-prompt">v</span> <span className="t-cmd">2.4.1-RELEASE</span></span>
            <span className="terminal-chip"><Lock size={10} style={{color:"rgba(255,255,255,0.3)"}}/> <span className="t-cmd">TLS 1.3</span></span>
          </div>
        </div>
      </header>

      {/* SYSTEM DIAGRAM */}
      <section className="container dashboard-preview-container">
        <div className="image-placeholder-modern">
          <div className="preview-toolbar">
            <div className="toolbar-dot" /><div className="toolbar-dot" /><div className="toolbar-dot" />
            <div className="toolbar-bar"><span className="toolbar-url">volttrack.gescom.in/admin/dashboard</span></div>
          </div>
          <img src={BlockDiagram} alt="VoltTrack System Architecture" className="dashboard-img" />
        </div>
      </section>

      {/* STATS */}
      <div className="stats-strip">
        <div className="container">
          <div className="stats-grid">
            {STATS.map((s) => (
              <div className="stat-item" key={s.label}>
                <span className="stat-number">{s.number}</span>
                <span className="stat-label">{s.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* HOW IT WORKS */}
      <section className="how-section">
        <div className="container">
          <div className="section-header">
            <span className="section-overline">Data Pipeline</span>
            <h2 className="section-title">How It Works</h2>
          </div>
          <div className="pipeline-grid">
            {PIPELINE.map((step, i) => (
              <div className="pipeline-step" key={step.title}>
                <div className="pipeline-icon">
                  <span className="pipeline-num">{i + 1}</span>
                  {step.icon}
                </div>
                <h4>{step.title}</h4>
                <p>{step.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* FEATURES */}
      <section className="dark-section">
        <div className="container">
          <div className="section-header">
            <span className="section-overline">Platform Capabilities</span>
            <h2 className="section-title light">Enterprise-Grade Features</h2>
          </div>
          <div className="feature-grid">
            {FEATURES.map((f) => (
              <div className="feature-card" key={f.title}>
                <div className="feature-tag">{f.tag}</div>
                <div className="feature-icon">{f.icon}</div>
                <h3>{f.title}</h3>
                <p>{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* API SHOWCASE */}
      <section id="api" className="api-section">
        <div className="container">
          <div className="section-header">
            <span className="section-overline">Developer Docs</span>
            <h2 className="section-title">REST API Reference</h2>
          </div>
          <div className="api-showcase">
            <div className="api-list">
              {API_ENDPOINTS.map((ep, i) => (
                <button key={i} className={`api-endpoint ${activeEndpoint === i ? "active" : ""}`} onClick={() => setActiveEndpoint(i)}>
                  <span className={`method-badge method-${ep.method.toLowerCase()}`}>{ep.method}</span>
                  <span className="endpoint-path">{ep.path}</span>
                  <span className="endpoint-desc">{ep.desc}</span>
                </button>
              ))}
            </div>
            <div className="api-code-block">
              <div className="code-toolbar">
                <span className="code-label">RESPONSE · application/json</span>
                <button className="code-copy" onClick={handleCopy}>{copied ? "✓ Copied" : "Copy"}</button>
              </div>
              <div className="code-body" dangerouslySetInnerHTML={{ __html: API_ENDPOINTS[activeEndpoint].response }} />
            </div>
          </div>
        </div>
      </section>

      {/* FOOTER */}
      <footer className="footer">
        <div className="container">
          <div className="footer-grid">
            <div className="footer-brand-col">
              <span className="footer-logo-text">VoltTrack</span>
              <p className="footer-brand-desc">Advanced utility management platform for GESCOM smart grid infrastructure.</p>
            </div>
            <div className="footer-links">
               <Link to="/consumer-login">Consumer Portal</Link>
               <Link to="/admin-login">Admin Dashboard</Link>
            </div>
          </div>
          <div className="footer-bottom">
            <span className="footer-copy">© 2026 GESCOM Project — Engineering Edition</span>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Home;