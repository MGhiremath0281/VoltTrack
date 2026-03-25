import React from "react";

const Home = () => {
  return (
    <div className="text-slate-900 font-sans">
      {/* Hero Section */}
      <header className="px-8 py-24 text-center bg-gradient-to-b from-white to-slate-50">
        <h1 className="text-5xl md:text-6xl font-extrabold text-slate-900 mb-6 tracking-tight">
          VoltTrack: <span className="text-blue-600">Intelligent Energy</span>
        </h1>
        <p className="max-w-2xl mx-auto text-xl text-slate-600 leading-relaxed">
          A production-grade Spring Boot ecosystem for real-time telemetry, 
          automated billing, and grid-level monitoring using IoT smart meters.
        </p>
        <div className="mt-10 flex justify-center gap-4">
          <button className="px-8 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition shadow-lg shadow-blue-200">
            Get Started
          </button>
          <button className="px-8 py-3 bg-white border border-slate-300 rounded-lg font-semibold hover:bg-slate-50 transition">
            Technical Docs
          </button>
        </div>
      </header>

      {/* Main Feature Image Slot */}
      <section className="px-8 -mt-12 mb-20 max-w-5xl mx-auto">
        <div className="bg-white p-3 rounded-2xl shadow-2xl border border-slate-200">
          <div className="bg-slate-100 rounded-xl overflow-hidden border border-slate-200">
             {/* Replace the src with your actual dashboard screenshot path */}
             <img 
              src="/assets/dashboard-preview.png" 
              alt="VoltTrack Dashboard" 
              className="w-full h-auto min-h-[400px] object-cover"
              onError={(e) => e.target.style.display='none'} 
            />
            <div className="h-96 flex items-center justify-center text-slate-400 italic">
              [Drop Dashboard Mockup / System Architecture Image Here]
            </div>
          </div>
        </div>
      </section>

      {/* System Overview with Image Space */}
      <section id="overview" className="px-8 py-20 max-w-6xl mx-auto grid md:grid-cols-2 gap-16 items-center border-t border-slate-100">
        <div className="bg-white p-4 rounded-xl shadow-md border border-slate-100">
            <div className="bg-slate-50 rounded-lg h-80 flex items-center justify-center border-2 border-dashed border-slate-200">
                <span className="text-slate-400 text-sm">[Hardware Setup / IoT Meter Image]</span>
            </div>
        </div>
        <div>
          <h2 className="text-3xl font-bold mb-6 text-slate-800">Operational Overview</h2>
          <p className="text-lg text-slate-600 mb-6">
            VoltTrack processes electrical pulses in real-time. Our backend architecture is designed to handle thousands of concurrent meter connections, translating raw data into billing insights.
          </p>
          <div className="space-y-4">
            <div className="flex gap-4 p-4 bg-white rounded-lg border border-slate-100 shadow-sm">
                <div className="text-blue-600 font-bold text-xl">01</div>
                <p className="text-slate-700 font-medium">WebSocket-driven live updates for voltage & power tracking.</p>
            </div>
            <div className="flex gap-4 p-4 bg-white rounded-lg border border-slate-100 shadow-sm">
                <div className="text-blue-600 font-bold text-xl">02</div>
                <p className="text-slate-700 font-medium">Automated 15-day and monthly cycles for GESCOM billing.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section id="features" className="px-8 py-24 bg-slate-900 text-white rounded-[3rem] mx-4 my-10">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-20">
            <h2 className="text-4xl font-bold mb-4">Enterprise Capabilities</h2>
            <p className="text-slate-400">High-performance features for modern energy grids</p>
          </div>
          <div className="grid md:grid-cols-3 gap-8 text-left">
            {[
              { title: "Live Monitoring", desc: "Real-time tracking of voltage, current, and power spikes." },
              { title: "Intelligent Alerts", desc: "Security-first tamper detection and load anomaly spikes." },
              { title: "Heartbeat Logic", desc: "Instant offline detection for meters inactive for 10+ minutes." },
              { title: "Pulse Conversion", desc: "Accurate calculation using 1000 pulses per kWh standard." },
              { title: "Admin Analytics", desc: "Advanced area-wise usage heatmaps and grid load data." },
              { title: "Bill Prediction", desc: "AI-ready bill estimation based on historical usage." }
            ].map((feature, idx) => (
              <div key={idx} className="p-8 bg-slate-800 rounded-2xl border border-slate-700 hover:border-blue-500 transition-colors group">
                <h3 className="text-xl font-bold mb-3 text-blue-400 group-hover:text-blue-300">{feature.title}</h3>
                <p className="text-slate-400 leading-relaxed">{feature.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Tech Stack Bubbles */}
      <section id="tech" className="px-8 py-20 max-w-4xl mx-auto text-center">
        <h2 className="text-2xl font-bold mb-10 text-slate-800">Powered By</h2>
        <div className="flex flex-wrap justify-center gap-3">
          {["Spring Boot 3", "Java 17", "PostgreSQL", "Redis", "WebSocket", "JWT Security", "Docker"].map((tech) => (
            <span key={tech} className="px-6 py-2 bg-white text-slate-700 rounded-full text-sm font-bold border border-slate-200 shadow-sm">
              {tech}
            </span>
          ))}
        </div>
      </section>

      {/* Footer */}
      <footer className="px-8 py-12 border-t border-slate-200 bg-white text-center">
        <p className="text-slate-500 font-medium">VoltTrack Utility Management System</p>
        <p className="text-slate-400 text-sm mt-2 font-mono">2026 Engineering Edition</p>
      </footer>
    </div>
  );
};

export default Home;