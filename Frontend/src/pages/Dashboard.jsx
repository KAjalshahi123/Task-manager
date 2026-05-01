import { useEffect, useState } from "react";
import API from "../api/axios";

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  // 🔹 Fetch Dashboard Data
  useEffect(() => {
    API.get("/dashboard/stats")
      .then((res) => {
        setStats(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  }, []);

  // 🔹 Loading State
  if (loading) {
    return <p style={{ padding: "20px" }}>Loading dashboard...</p>;
  }

  // 🔹 No Data Case
  if (!stats) {
    return <p style={{ padding: "20px" }}>Failed to load dashboard</p>;
  }

  return (
    <div style={{ padding: "20px" }}>
      <h2 style={{ marginBottom: "20px" }}>Dashboard</h2>

      {/* 🔹 Grid Cards */}
      <div style={gridContainer}>
        {[
          ["Total Projects", stats.totalProjects],
          ["Total Tasks", stats.totalTasks],
          ["Completed Tasks", stats.completedTasks],
          ["In Progress", stats.inProgressTasks],
          ["Todo Tasks", stats.todoTasks],
          ["Overdue Tasks", stats.overdueTasks],
          ["Completion Rate (%)", stats.completionRate],
        ].map(([title, value], i) => (
          <div key={i} style={cardStyle}>
            <h4 style={{ color: "#64748b" }}>{title}</h4>
            <h2 style={{ marginTop: "10px", color: "#0f172a" }}>
              {value}
            </h2>
          </div>
        ))}
      </div>
    </div>
  );
}

//////////////////////////////////////////////////////
// 🎨 STYLES
//////////////////////////////////////////////////////

const gridContainer = {
  display: "grid",
  gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
  gap: "20px",
};

const cardStyle = {
  background: "white",
  padding: "20px",
  borderRadius: "12px",
  boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
  textAlign: "center",
  transition: "transform 0.2s ease",
  cursor: "pointer",
};