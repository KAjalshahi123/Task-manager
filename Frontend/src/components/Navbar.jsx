import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { logout } = useAuth();

  return (
    <nav style={{
      display: "flex",
      alignItems: "center",
      padding: "12px 20px",
      background: "#1e293b",
      color: "white",
      boxShadow: "0 2px 5px rgba(0,0,0,0.2)"
    }}>
      <h3 style={{ marginRight: "30px" }}>Task Manager</h3>

      <Link style={linkStyle} to="/dashboard">Dashboard</Link>
      <Link style={linkStyle} to="/projects">Projects</Link>
      <Link style={linkStyle} to="/tasks">Tasks</Link>

      <button onClick={logout} style={logoutBtn}>
        Logout
      </button>
    </nav>
  );
}

const linkStyle = {
  color: "white",
  marginRight: "20px",
  textDecoration: "none",
  fontWeight: "500"
};

const logoutBtn = {
  marginLeft: "auto",
  padding: "6px 12px",
  background: "#ef4444",
  border: "none",
  color: "white",
  borderRadius: "5px",
  cursor: "pointer"
};