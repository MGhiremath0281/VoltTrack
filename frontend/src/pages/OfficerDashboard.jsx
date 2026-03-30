import React, { useEffect, useState } from "react";
import axios from "axios";
import { 
  FaBolt, FaPlus, FaSearch, FaFileInvoiceDollar, 
  FaCheckCircle, FaTimes, FaUserCircle, FaChargingStation, 
  FaSpinner, FaIdCard, FaMapMarkerAlt, FaEnvelope, FaShieldAlt 
} from "react-icons/fa";

const OfficerDashboard = () => {
  const [officer, setOfficer] = useState(null);
  const [consumers, setConsumers] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showReceiptModal, setShowReceiptModal] = useState(false);
  const [generatedBill, setGeneratedBill] = useState(null);
  const [loading, setLoading] = useState(false);

  const token = localStorage.getItem("jwtToken");
  const config = { headers: { Authorization: `Bearer ${token}` } };
  const BASE_URL = "http://localhost:8080";

  useEffect(() => {
    if (token) fetchInitialData();
  }, [token]);

  const fetchInitialData = async () => {
    try {
      const [profile, list] = await Promise.all([
        axios.get(`${BASE_URL}/api/officer/me`, config),
        axios.get(`${BASE_URL}/api/officer/consumers`, config)
      ]);
      setOfficer(profile.data);
      setConsumers(list.data?.content || list.data || []);
    } catch (err) {
      console.error("Fetch Error:", err);
    }
  };

  const formatDateArray = (arr) => {
    if (!arr || !Array.isArray(arr)) return "N/A";
    const [year, month, day] = arr;
    return new Date(year, month - 1, day).toLocaleDateString('en-IN', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  };

  const handleCreateConsumer = async (e) => {
    e.preventDefault();
    setLoading(true);
    const formData = new FormData(e.target);

    const payload = {
      username: formData.get("username"),
      email: formData.get("email"),
      password: formData.get("password"),
      role: "CONSUMER",
      active: true
    };

    try {
      await axios.post(`${BASE_URL}/api/officer/consumers`, payload, config);
      setShowCreateModal(false);
      fetchInitialData();
    } catch (err) {
      alert("Registration Error: " + (err.response?.data?.message || "Check Officer Permissions"));
    } finally {
      setLoading(false);
    }
  };

  const handleAssignMeter = async (consumerPublicId) => {
    const meterPayload = {
      location: "Kalaburagi Grid Sector-1",
      userPublicId: consumerPublicId,
      status: "ONLINE",
      billing: "MONTHLY"
    };

    try {
      setLoading(true);
      const res = await axios.post(`${BASE_URL}/api/officer/meters/${consumerPublicId}`, meterPayload, config);
      
      // ✅ LOGIC UPDATE: Accessing the publicId from res.data.content[0]
      const meterData = res.data?.content ? res.data.content[0] : res.data;

      setConsumers(prev => prev.map(c => 
        c.publicId === consumerPublicId ? { ...c, meter: meterData } : c
      ));
    } catch (err) {
      alert("Meter Assignment Failed: " + (err.response?.data?.message || "Server Error"));
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateBill = async (consumerPublicId) => {
    try {
      setLoading(true);
      const res = await axios.post(`${BASE_URL}/api/officer/bills/${consumerPublicId}`, {}, config);
      setGeneratedBill(res.data);
      setShowReceiptModal(true);
    } catch (err) {
      alert("Billing Error: Ensure the consumer has a meter assigned.");
    } finally {
      setLoading(false);
    }
  };

  const filteredConsumers = consumers.filter(c => 
    c.username.toLowerCase().includes(searchQuery.toLowerCase()) || 
    c.publicId?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="glass-dashboard">
      <nav className="glass-nav">
        <div className="nav-content">
          <div className="brand">
            <div className="logo-box"><FaBolt /></div>
            <span>VoltTrack <small>Officer Pro</small></span>
          </div>
          <div className="status-pill">
            <span className="pulse-dot"></span>
            System Live
          </div>
        </div>
      </nav>

      <div className="dashboard-body">
        {officer && (
          <div className="officer-hero animate-in">
            <div className="officer-glass-card">
              <div className="officer-info">
                <div className="avatar-circle">
                   <FaUserCircle size={50} color="var(--primary)" />
                </div>
                <div className="text-group">
                  <h2>{officer.username}</h2>
                  <p><FaShieldAlt /> Authorized {officer.role}</p>
                </div>
              </div>
              <div className="officer-meta-grid">
                <div className="meta-box">
                  <small>Subdivision</small>
                  <span>Kalaburagi South</span>
                </div>
                <div className="meta-box">
                  <small>Manage</small>
                  <span>{consumers.length} Consumers</span>
                </div>
              </div>
            </div>
          </div>
        )}

        <div className="action-bar">
          <div className="search-wrapper">
            <FaSearch />
            <input 
              placeholder="Search by name or ID..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <button className="create-btn" onClick={() => setShowCreateModal(true)}>
            <FaPlus /> Add New Consumer
          </button>
        </div>

        <div className="consumer-grid">
          {filteredConsumers.map((c) => (
            <div key={c.publicId} className="consumer-card animate-in">
              <div className="card-header">
                <div>
                  <h4>{c.username}</h4>
                  {/* ✅ UPDATED: Fetching Public ID from meter if it exists */}
                  <span className="public-id-tag">
                    {c.meter?.publicId || c.publicId}
                  </span>
                </div>
                <div className={`connection-status ${c.meter ? 'active' : 'inactive'}`}></div>
              </div>

              <div className="card-body">
                 <p><FaEnvelope /> {c.email}</p>
              </div>

              <div className="card-actions">
                {c.meter ? (
                  <div className="meter-badge">
                    <FaChargingStation />
                    <div className="meter-info">
                      <small>PUBLIC ID</small>
                      {/* ✅ DISPLAYING THE publicId: "MTR-c02781b8" */}
                      <span>{c.meter.publicId}</span>
                    </div>
                  </div>
                ) : (
                  <button className="btn-assign" onClick={() => handleAssignMeter(c.publicId)}>
                    {loading ? <FaSpinner className="spin" /> : <><FaBolt /> Provision Meter</>}
                  </button>
                )}
                
                <button 
                  className={`btn-bill ${!c.meter ? 'disabled' : ''}`}
                  disabled={!c.meter || loading}
                  onClick={() => handleGenerateBill(c.publicId)}
                >
                    {loading ? <FaSpinner className="spin" /> : <FaFileInvoiceDollar />}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* --- MODALS --- */}
      {showCreateModal && (
        <div className="modal-overlay">
          <div className="glass-modal animate-in">
            <div className="modal-top">
              <h3>New Onboarding</h3>
              <FaTimes onClick={() => setShowCreateModal(false)} className="close-icon" />
            </div>
            <form onSubmit={handleCreateConsumer} className="glass-form">
              <div className="input-field">
                <label>Username</label>
                <input name="username" placeholder="Full Name" required />
              </div>
              <div className="input-field">
                <label>Email Address</label>
                <input name="email" type="email" placeholder="consumer@example.com" required />
              </div>
              <div className="input-field">
                <label>Temporary Password</label>
                <input name="password" type="password" placeholder="••••••••" required />
              </div>
              <button type="submit" className="form-submit" disabled={loading}>
                {loading ? "Syncing with Grid..." : "Initialize Profile"}
              </button>
            </form>
          </div>
        </div>
      )}

      {showReceiptModal && generatedBill && (
        <div className="modal-overlay">
          <div className="glass-modal receipt-card animate-in">
            <div className="receipt-head">
              <FaCheckCircle className="check-icon" />
              <h3>Billing Summary</h3>
              <code>{generatedBill.publicId}</code>
            </div>
            
            <div className="receipt-content">
              <div className="r-line"><span>Consumer ID</span> <strong>{generatedBill.consumerPublicId}</strong></div>
              <div className="r-line"><span>Due Date</span> <strong>{formatDateArray(generatedBill.dueDate)}</strong></div>
              
              <div className="billing-breakdown">
                <div className="b-item">
                  <small>Units (kWh)</small>
                  <span>{generatedBill.unitsConsumed?.toFixed(2)}</span>
                </div>
                <div className="b-item">
                  <small>Total Amount</small>
                  <span>₹{generatedBill.totalAmount?.toFixed(2)}</span>
                </div>
              </div>

              <hr className="dashed-divider" />
              <div className="r-line total">
                <span>Payable Amount</span>
                <strong className="price">₹{generatedBill.totalAmount?.toFixed(2)}</strong>
              </div>
            </div>
            
            <button className="form-submit" onClick={() => setShowReceiptModal(false)}>Close Invoice</button>
          </div>
        </div>
      )}

      <style>{`
        :root { --primary: #6366f1; --glass: rgba(255, 255, 255, 0.8); }
        .glass-dashboard { background: linear-gradient(135deg, #f8fafc 0%, #cbd5e1 100%); min-height: 100vh; font-family: 'Inter', sans-serif; }
        .glass-nav { background: rgba(255, 255, 255, 0.9); backdrop-filter: blur(10px); border-bottom: 1px solid rgba(0,0,0,0.05); padding: 1rem 2rem; position: sticky; top: 0; z-index: 100; }
        .nav-content { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
        .brand { display: flex; align-items: center; gap: 10px; font-weight: 900; font-size: 1.4rem; }
        .logo-box { background: var(--primary); color: white; padding: 8px; border-radius: 12px; }
        .status-pill { background: #f0fdf4; color: #166534; padding: 6px 12px; border-radius: 20px; font-size: 0.7rem; font-weight: 800; display: flex; align-items: center; gap: 8px; }
        .pulse-dot { width: 8px; height: 8px; background: #10b981; border-radius: 50%; animation: pulse 2s infinite; }
        .dashboard-body { max-width: 1100px; margin: 0 auto; padding: 3rem 1.5rem; }
        .officer-glass-card { background: var(--glass); backdrop-filter: blur(12px); border: 1px solid white; border-radius: 28px; padding: 2rem; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 10px 30px rgba(0,0,0,0.05); margin-bottom: 3rem; }
        .action-bar { display: flex; gap: 1.5rem; margin-bottom: 2rem; }
        .search-wrapper { flex: 1; background: white; border-radius: 16px; border: 1px solid #cbd5e1; display: flex; align-items: center; padding: 0 1.2rem; gap: 10px; }
        .search-wrapper input { border: none; padding: 1rem 0; width: 100%; outline: none; font-weight: 500; }
        .create-btn { background: #000; color: white; padding: 0 2rem; border-radius: 16px; border: none; font-weight: 700; cursor: pointer; transition: 0.3s; }
        .consumer-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 1.5rem; }
        .consumer-card { background: var(--glass); border-radius: 24px; padding: 1.8rem; border: 1px solid rgba(255,255,255,0.4); backdrop-filter: blur(10px); transition: 0.3s; }
        .public-id-tag { background: #e2e8f0; color: #475569; padding: 3px 8px; border-radius: 6px; font-size: 0.65rem; font-weight: 800; margin-top: 5px; display: inline-block; }
        .connection-status { width: 10px; height: 10px; border-radius: 50%; }
        .connection-status.active { background: #10b981; box-shadow: 0 0 10px #10b981; }
        .card-actions { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid rgba(0,0,0,0.05); padding-top: 1.5rem; }
        .meter-badge { display: flex; align-items: center; gap: 10px; background: #f0fdf4; color: #166534; padding: 8px 14px; border-radius: 12px; border: 1px solid #10b98133; }
        .meter-info { display: flex; flex-direction: column; }
        .meter-info small { font-size: 0.55rem; font-weight: 800; opacity: 0.7; }
        .meter-info span { font-size: 0.85rem; font-weight: 700; }
        .btn-assign { background: #f5f3ff; color: var(--primary); border: 1px solid #6366f133; padding: 10px 16px; border-radius: 12px; font-weight: 800; cursor: pointer; font-size: 0.8rem; }
        .btn-bill { background: #fff; border: 1px solid #e2e8f0; padding: 10px; border-radius: 12px; color: #64748b; cursor: pointer; }
        .modal-overlay { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.4); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 200; }
        .glass-modal { background: #fff; width: 400px; padding: 2.5rem; border-radius: 32px; box-shadow: 0 30px 60px rgba(0,0,0,0.2); }
        .input-field { margin-bottom: 1rem; text-align: left; }
        .input-field label { display: block; font-size: 0.7rem; font-weight: 800; color: #475569; margin-bottom: 6px; text-transform: uppercase; }
        .input-field input { width: 100%; padding: 1rem; border: 1px solid #e2e8f0; border-radius: 14px; outline: none; background: #f8fafc; }
        .form-submit { background: #000; color: #fff; padding: 1rem; border: none; border-radius: 16px; font-weight: 800; cursor: pointer; width: 100%; margin-top: 1rem; }
        .spin { animation: spin 1s linear infinite; }
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
        @keyframes pulse { 0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); } 70% { box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); } 100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); } }
      `}</style>
    </div>
  );
};

export default OfficerDashboard;