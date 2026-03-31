import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Container, Typography, Tabs, Tab, Box, Table, TableHead, 
  TableRow, TableCell, TableBody, Button, CircularProgress, 
  Chip, Paper, IconButton, Tooltip, Card
} from "@mui/material";
// Icons for a professional look
import { 
  Trash2, CheckCircle, ShieldCheck, Zap, 
  FileText, AlertTriangle, Users, Search 
} from "lucide-react";

const AdminDashboard = () => {
  const [tab, setTab] = useState(0);
  const [loading, setLoading] = useState(false);
  const [dataList, setDataList] = useState([]);

  const endpoints = ["users", "meters", "bills", "alerts"];

  const fetchData = async () => {
    setLoading(true);
    try {
      const type = endpoints[tab];
      const res = await axios.get(`/api/admin/dashboard/${type}`);
      setDataList(res.data.content || []);
    } catch (err) {
      console.error("Fetch error:", err);
      setDataList([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, [tab]);

  const handleDelete = async (publicId) => {
    if (window.confirm("Permanent Action: Delete this record?")) {
      try {
        await axios.delete(`/api/admin/dashboard/users/${publicId}`);
        fetchData();
      } catch (err) { alert("Error: " + err.message); }
    }
  };

  const handleApprove = async (publicId) => {
    try {
      await axios.put(`/api/admin/dashboard/users/${publicId}/approve`);
      fetchData();
    } catch (err) { alert("Approval failed"); }
  };

  const getTabIcon = (index) => {
    const icons = [
      <Users size={18} />, <Zap size={18} />, 
      <FileText size={18} />, <AlertTriangle size={18} />
    ];
    return icons[index];
  };

  const renderTable = (type) => (
    <Table sx={{ minWidth: 650 }}>
      <TableHead sx={{ bgcolor: "#f8f9fa" }}>
        <TableRow>
          <TableCell sx={{ fontWeight: 700, color: "#444" }}>IDENTIFIER</TableCell>
          <TableCell sx={{ fontWeight: 700, color: "#444" }}>DETAILS & STATUS</TableCell>
          <TableCell align="right" sx={{ fontWeight: 700, color: "#444" }}>ACTIONS</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {dataList.map((item) => (
          <TableRow 
            key={item.publicId} 
            sx={{ '&:hover': { bgcolor: '#fcfaff', transition: '0.3s' } }}
          >
            <TableCell>
              <Typography variant="caption" sx={{ fontFamily: 'Monospace', color: '#666', bgcolor: '#eee', p: 0.5, borderRadius: 1 }}>
                {item.publicId}
              </Typography>
            </TableCell>
            <TableCell>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Typography variant="body2" sx={{ fontWeight: 500 }}>
                  {item.name || item.username || item.meterNumber || item.message}
                </Typography>
                {item.role && (
                  <Chip 
                    label={item.role} 
                    size="small" 
                    sx={{ 
                      height: 20, fontSize: '0.65rem', fontWeight: 700,
                      bgcolor: item.role === 'OFFICER' ? '#f3e5f5' : '#f5f5f5',
                      color: item.role === 'OFFICER' ? '#7b1fa2' : '#666',
                      border: `1px solid ${item.role === 'OFFICER' ? '#ce93d8' : '#ddd'}`
                    }} 
                  />
                )}
              </Box>
            </TableCell>
            <TableCell align="right">
              {type === "users" ? (
                <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                  {/* DELETE: ANY USER */}
                  <Tooltip title="Delete User">
                    <IconButton 
                      size="small" 
                      onClick={() => handleDelete(item.publicId)}
                      sx={{ color: '#d32f2f', '&:hover': { bgcolor: '#ffebee' } }}
                    >
                      <Trash2 size={18} />
                    </IconButton>
                  </Tooltip>

                  {/* APPROVE: ONLY OFFICERS */}
                  {item.role === "OFFICER" && (
                    <Button
                      variant={item.active ? "text" : "contained"}
                      size="small"
                      startIcon={item.active ? <ShieldCheck size={16}/> : <CheckCircle size={16}/>}
                      disabled={item.active}
                      onClick={() => handleApprove(item.publicId)}
                      sx={{ 
                        borderRadius: 2, 
                        textTransform: 'none',
                        boxShadow: item.active ? 'none' : '0 4px 12px rgba(156, 39, 176, 0.2)',
                        bgcolor: item.active ? 'transparent' : '#9c27b0',
                        '&:hover': { bgcolor: item.active ? 'transparent' : '#7b1fa2' }
                      }}
                    >
                      {item.active ? "Verified" : "Approve"}
                    </Button>
                  )}
                </Box>
              ) : (
                <Chip label="Read Only" variant="outlined" size="small" sx={{ color: '#999', borderColor: '#eee' }} />
              )}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );

  return (
    <Box sx={{ bgcolor: "#f4f6f8", minHeight: "100vh", py: 6 }}>
      <Container maxWidth="lg">
        <Box sx={{ mb: 4, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 800, color: "#1a1a1a", display: 'flex', alignItems: 'center', gap: 1 }}>
              <Zap color="#9c27b0" fill="#9c27b0" size={32} /> VoltTrack <span style={{ fontWeight: 300, color: '#9c27b0' }}>Admin</span>
            </Typography>
            <Typography variant="body2" color="textSecondary">Manage system infrastructure and personnel</Typography>
          </Box>
        </Box>
        
        <Card sx={{ borderRadius: 4, boxShadow: "0 10px 40px rgba(0,0,0,0.04)", border: "1px solid #eef0f2" }}>
          <Tabs 
            value={tab} 
            onChange={(e, newVal) => setTab(newVal)}
            sx={{ 
              px: 2, pt: 1, borderBottom: '1px solid #f0f0f0',
              '& .MuiTab-root': { textTransform: 'none', fontWeight: 600, minHeight: 60, gap: 1 }
            }}
            TabIndicatorProps={{ sx: { height: 3, borderRadius: '3px 3px 0 0' } }}
          >
            <Tab icon={getTabIcon(0)} iconPosition="start" label="Personnel" />
            <Tab icon={getTabIcon(1)} iconPosition="start" label="Meters" />
            <Tab icon={getTabIcon(2)} iconPosition="start" label="Billing" />
            <Tab icon={getTabIcon(3)} iconPosition="start" label="Alerts" />
          </Tabs>

          <Box p={1}>
            {loading ? (
              <Box display="flex" justifyContent="center" py={10}><CircularProgress thickness={5} size={50} sx={{ color: '#9c27b0' }} /></Box>
            ) : (
              renderTable(endpoints[tab])
            )}
          </Box>
        </Card>
      </Container>
    </Box>
  );
};

export default AdminDashboard;