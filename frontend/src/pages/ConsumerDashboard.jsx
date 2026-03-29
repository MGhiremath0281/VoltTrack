import React, { useEffect, useState, useRef } from "react";
import axios from "axios";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import {
  ResponsiveContainer, CartesianGrid, XAxis, YAxis, Tooltip,
  AreaChart, Area
} from "recharts";

const API_CONFIG = {
  BASE_URL: "http://localhost:8080/api/dashboard",
  SOCKET_URL: "http://localhost:8080/ws",
};

// Helper to parse Java LocalDateTime arrays [YYYY, MM, DD, HH, mm, ss]
const parseJavaDate = (dateArray) => {
  if (!dateArray || !Array.isArray(dateArray)) return "N/A";
  const [year, month, day] = dateArray;
  return new Date(year, month - 1, day).toLocaleDateString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric'
  });
};

export default function VoltTrackDashboard() {
  const [profile, setProfile] = useState(null);
  const [activeMeter, setActiveMeter] = useState(null);
  const [readings, setReadings] = useState([]);
  const [latestBill, setLatestBill] = useState(null);
  const [latestStats, setLatestStats] = useState({ voltage: 230, current: 0, pulse: 0 });
  const [isLoading, setIsLoading] = useState(true);
  const [isMobile, setIsMobile] = useState(window.innerWidth < 768);
  
  const stompClient = useRef(null);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth < 768);
    window.addEventListener('resize', handleResize);
    const token = localStorage.getItem("jwtToken");

    const fetchData = async () => {
      try {
        const headers = { Authorization: `Bearer ${token}` };
        const [resProfile, resMeters, resLatestBill] = await Promise.all([
          axios.get(`${API_CONFIG.BASE_URL}/profile`, { headers }),
          axios.get(`${API_CONFIG.BASE_URL}/meters`, { headers }),
          axios.get(`${API_CONFIG.BASE_URL}/bills/latest`, { headers })
        ]);

        setProfile(resProfile.data);
        // Take the first meter from the array
        if (resMeters.data && resMeters.data.length > 0) {
          setActiveMeter(resMeters.data[0]);
        }
        setLatestBill(resLatestBill.data);
      } catch (err) {
        console.error("Dashboard Sync Error:", err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  useEffect(() => {
    const socket = new SockJS(API_CONFIG.SOCKET_URL);
    stompClient.current = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        stompClient.current.subscribe("/topic/meter-readings", (msg) => {
          const data = JSON.parse(msg.body);
          setLatestStats({
            voltage: data.voltage || 0,
            current: data.current || 0,
            pulse: data.pulseCount || 0
          });
          setReadings((prev) => [
            { 
              time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }), 
              val: data.unitsConsumed 
            }, 
            ...prev
          ].slice(0, 15));
        });
      }
    });
    stompClient.current.activate();
    return () => stompClient.current?.deactivate();
  }, []);

  if (isLoading) return <div style={styles.loader}>SYNCING SMART METER...</div>;

  return (
    <div style={styles.container}>
      {/* HEADER: Profile & Meter ID */}
      <header style={{...styles.header, flexDirection: isMobile ? 'column' : 'row', alignItems: isMobile ? 'flex-start' : 'center'}}>
        <div style={styles.profileCard}>
          <div style={styles.avatar}>{profile?.username?.charAt(0).toUpperCase()}</div>
          <div>
            <h2 style={styles.username}>{profile?.username}</h2>
            <p style={styles.email}>{profile?.email}</p>
          </div>
        </div>
        
        <div style={{...styles.meterBadge, marginTop: isMobile ? '10px' : '0'}}>
          <div style={{textAlign: isMobile ? 'left' : 'right'}}>
            <span style={styles.meterLabel}>ACTIVE METER</span>
            <div style={styles.meterId}>{activeMeter?.meterId || "N/A"}</div>
          </div>
          <div style={{...styles.statusDot, backgroundColor: activeMeter?.status === 'ONLINE' ? '#22C55E' : '#F43F5E'}}></div>
        </div>
      </header>

      {/* TOP STATS: Voltage, Amps, Location */}
      <div style={{...styles.statGrid, gridTemplateColumns: isMobile ? '1fr' : 'repeat(3, 1fr)'}}>
        <div style={styles.statBox}>
          <label style={styles.statLabel}>LINE VOLTAGE</label>
          <div style={styles.statValue}>{latestStats.voltage}<span>V</span></div>
        </div>
        <div style={styles.statBox}>
          <label style={styles.statLabel}>CURRENT LOAD</label>
          <div style={styles.statValue}>{latestStats.current}<span>A</span></div>
        </div>
        <div style={styles.statBox}>
          <label style={styles.statLabel}>LOCATION</label>
          <div style={{...styles.statValue, fontSize: '16px', color: '#64748B', marginTop: '12px'}}>
            {activeMeter?.location || "Not Set"}
          </div>
        </div>
      </div>

      {/* MAIN CONTENT AREA */}
      <div style={{...styles.mainGrid, gridTemplateColumns: isMobile ? '1fr' : '1.8fr 1.2fr'}}>
        
        {/* GRAPH SECTION */}
        <div style={styles.glassCard}>
          <h3 style={styles.cardTitle}>Live Consumption (kWh)</h3>
          <div style={{height: "280px", width: '100%'}}>
            <ResponsiveContainer>
              <AreaChart data={[...readings].reverse()}>
                <defs>
                  <linearGradient id="colorVal" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#6366F1" stopOpacity={0.2}/>
                    <stop offset="95%" stopColor="#6366F1" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#F1F5F9" />
                <XAxis dataKey="time" fontSize={10} tick={{fill: '#94A3B8'}} />
                <YAxis fontSize={10} tick={{fill: '#94A3B8'}} />
                <Tooltip />
                <Area type="monotone" dataKey="val" stroke="#6366F1" strokeWidth={3} fill="url(#colorVal)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* BILLING SECTION */}
        <div style={styles.glassCard}>
          <h3 style={styles.cardTitle}>Current Billing Cycle</h3>
          <div style={styles.billContent}>
            <div style={styles.billMain}>
              <span style={styles.billAmountLabel}>TOTAL PAYABLE</span>
              <div style={styles.totalAmount}>₹{latestBill?.totalAmount?.toFixed(2)}</div>
              <div style={{...styles.tag, backgroundColor: latestBill?.status === 'UNPAID' ? '#FEE2E2' : '#DCFCE7', color: latestBill?.status === 'UNPAID' ? '#EF4444' : '#16A34A'}}>
                {latestBill?.status}
              </div>
            </div>

            <div style={styles.billMeta}>
              <div style={styles.metaRow}>
                <span>Cycle End</span>
                <strong>{parseJavaDate(latestBill?.cycleEndDate)}</strong>
              </div>
              <div style={styles.metaRow}>
                <span>Units</span>
                <strong>{latestBill?.unitsConsumed?.toFixed(2)} kWh</strong>
              </div>
              <div style={styles.metaRow}>
                <span>Due Date</span>
                <span style={{color: '#EF4444', fontWeight: 'bold'}}>{parseJavaDate(latestBill?.dueDate)}</span>
              </div>
            </div>
            
            <button style={styles.payButton}>Pay Digital Bill</button>
          </div>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: { backgroundColor: "#F8FAFC", minHeight: "100vh", padding: "25px", fontFamily: "'Inter', sans-serif", color: "#1E293B" },
  header: { display: "flex", justifyContent: "space-between", marginBottom: "30px", gap: "20px" },
  profileCard: { display: "flex", alignItems: "center", gap: "15px" },
  avatar: { width: "50px", height: "50px", borderRadius: "12px", backgroundColor: "#1E293B", color: "white", display: "flex", alignItems: "center", justifyContent: "center", fontWeight: "800", fontSize: "20px" },
  username: { margin: 0, fontSize: "18px", fontWeight: "800" },
  email: { margin: 0, fontSize: "13px", color: "#64748B" },
  meterBadge: { display: "flex", alignItems: "center", gap: "15px", backgroundColor: "white", padding: "10px 20px", borderRadius: "16px", border: "1px solid #E2E8F0" },
  meterLabel: { fontSize: "10px", fontWeight: "800", color: "#94A3B8", display: "block" },
  meterId: { fontSize: "14px", fontWeight: "800", color: "#6366F1" },
  statusDot: { width: "10px", height: "10px", borderRadius: "50%" },
  statGrid: { display: "grid", gap: "20px", marginBottom: "30px" },
  statBox: { backgroundColor: "white", padding: "20px", borderRadius: "20px", border: "1px solid #E2E8F0", boxShadow: "0 2px 4px rgba(0,0,0,0.02)" },
  statLabel: { fontSize: "11px", fontWeight: "800", color: "#94A3B8", letterSpacing: "0.05em" },
  statValue: { fontSize: "32px", fontWeight: "800", marginTop: "8px" },
  mainGrid: { display: "grid", gap: "25px" },
  glassCard: { backgroundColor: "white", padding: "25px", borderRadius: "28px", border: "1px solid #E2E8F0" },
  cardTitle: { fontSize: "13px", fontWeight: "900", textTransform: "uppercase", marginBottom: "25px", color: "#64748B", borderLeft: "4px solid #6366F1", paddingLeft: "10px" },
  billContent: { display: "flex", flexDirection: "column", gap: "20px" },
  billMain: { textAlign: "center", padding: "20px", backgroundColor: "#F8FAFC", borderRadius: "20px" },
  billAmountLabel: { fontSize: "11px", fontWeight: "800", color: "#94A3B8" },
  totalAmount: { fontSize: "42px", fontWeight: "900", margin: "10px 0", color: "#1E293B" },
  billMeta: { display: "flex", flexDirection: "column", gap: "12px" },
  metaRow: { display: "flex", justifyContent: "space-between", fontSize: "14px" },
  tag: { display: "inline-block", padding: "4px 12px", borderRadius: "8px", fontSize: "11px", fontWeight: "900" },
  payButton: { width: "100%", padding: "16px", borderRadius: "16px", border: "none", backgroundColor: "#6366F1", color: "white", fontWeight: "800", cursor: "pointer", boxShadow: "0 10px 15px -3px rgba(99, 102, 241, 0.3)" },
  loader: { height: "100vh", display: "flex", alignItems: "center", justifyContent: "center", fontWeight: "900", color: "#6366F1", fontSize: "20px" }
};