import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  FaUserTie,
  FaUsers,
  FaSearch,
  FaTachometerAlt,
  FaBolt,
  FaFileInvoiceDollar,
  FaBell
} from "react-icons/fa";

const OfficerDashboard = () => {
  const [officer, setOfficer] = useState(null);
  const [consumers, setConsumers] = useState([]);
  const [searchName, setSearchName] = useState("");

  useEffect(() => {
    // Fetch logged-in officer details
    axios.get("/api/officer/me").then((res) => setOfficer(res.data));

    // Fetch all consumers
    axios.get("/api/officer/consumers").then((res) => setConsumers(res.data.content));
  }, []);

  const handleSearch = () => {
    axios
      .get(`/api/officer/consumers/search?name=${searchName}`)
      .then((res) => setConsumers(res.data.content));
  };

  const assignMeter = (publicId) => {
    axios.post(`/api/officer/meters/${publicId}`, { meterType: "SMART" })
      .then(() => alert("Meter assigned successfully"));
  };

  const generateBill = (publicId) => {
    axios.post(`/api/officer/bills/${publicId}`)
      .then(() => alert("Bill generated successfully"));
  };

  const createAlert = (publicId) => {
    axios.post(`/api/officer/alerts/${publicId}`, { message: "Usage alert!" })
      .then(() => alert("Alert created successfully"));
  };

  return (
    <div className="dashboard-container">
      <header className="glass-card header">
        <FaTachometerAlt size={28} color="#6a0dad" />
        <h1>Officer Dashboard</h1>
      </header>

      <section className="glass-card officer-info">
        <FaUserTie size={24} color="#000" />
        {officer ? (
          <div>
            <h2>{officer.name}</h2>
            <p>Role: {officer.role}</p>
            <p>Email: {officer.email}</p>
          </div>
        ) : (
          <p>Loading officer details...</p>
        )}
      </section>

      <section className="glass-card consumer-section">
        <div className="search-bar">
          <FaSearch color="#6a0dad" />
          <input
            type="text"
            placeholder="Search consumers by name..."
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
          <button onClick={handleSearch}>Search</button>
        </div>

        <h2>
          <FaUsers color="#000" /> Consumers
        </h2>
        <ul>
          {consumers.map((c) => (
            <li key={c.publicId} className="consumer-card">
              <strong>{c.name}</strong> ({c.publicId})
              <div className="consumer-actions">
                <button onClick={() => assignMeter(c.publicId)}>
                  <FaBolt color="#6a0dad" /> Assign Meter
                </button>
                <button onClick={() => generateBill(c.publicId)}>
                  <FaFileInvoiceDollar color="#6a0dad" /> Generate Bill
                </button>
                <button onClick={() => createAlert(c.publicId)}>
                  <FaBell color="#6a0dad" /> Create Alert
                </button>
              </div>
            </li>
          ))}
        </ul>
      </section>

      <style jsx>{`
        .dashboard-container {
          font-family: "Segoe UI", sans-serif;
          background: linear-gradient(135deg, #f0f0f0, #dcdcdc);
          min-height: 100vh;
          padding: 2rem;
        }

        .glass-card {
          background: rgba(255, 255, 255, 0.25);
          border-radius: 16px;
          padding: 1.5rem;
          margin-bottom: 2rem;
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.18);
        }

        .header {
          display: flex;
          align-items: center;
          gap: 1rem;
        }

        .officer-info {
          display: flex;
          align-items: center;
          gap: 1rem;
        }

        .consumer-section .search-bar {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          margin-bottom: 1rem;
        }

        .consumer-card {
          background: rgba(255, 255, 255, 0.35);
          border-radius: 12px;
          padding: 1rem;
          margin: 0.5rem 0;
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .consumer-actions button {
          margin-left: 0.5rem;
          background: rgba(106, 13, 173, 0.2);
          border: none;
          border-radius: 8px;
          padding: 0.5rem 1rem;
          cursor: pointer;
          color: #000;
          font-weight: 500;
          transition: background 0.3s ease;
        }

        .consumer-actions button:hover {
          background: rgba(106, 13, 173, 0.4);
        }
      `}</style>
    </div>
  );
};

export default OfficerDashboard;
