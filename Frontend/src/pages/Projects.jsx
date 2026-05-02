import { useEffect, useState } from "react";
import API from "../api/axios";

export default function Projects() {
  const [projects, setProjects] = useState([]);
  const [form, setForm] = useState({
    name: "",
    description: "",
    startDate: "",
    endDate: ""
  });

  // 🔹 Load Projects
  const load = () => {
    API.get("/projects/my")
      .then(res => setProjects(res.data))
      .catch(console.error);
  };

  useEffect(() => {
    load();
  }, []);

  // 🔹 Create Project
  const createProject = async () => {
    try {
      await API.post("/projects", form);

      setForm({
        name: "",
        description: "",
        startDate: "",
        endDate: ""
      });

      load();
    } catch (err) {
      console.error(err);
      alert("Failed to create project");
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      
      {/* 🔹 Title */}
      <h2>Projects</h2>

      {/* 🔹 Create Form */}
      <div style={formContainer}>
        <input
          style={inputStyle}
          placeholder="Project Name"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
        />

        <input
          style={inputStyle}
          placeholder="Description"
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />

        <input
          style={inputStyle}
          type="date"
          value={form.startDate}
          onChange={(e) => setForm({ ...form, startDate: e.target.value })}
        />

        <input
          style={inputStyle}
          type="date"
          value={form.endDate}
          onChange={(e) => setForm({ ...form, endDate: e.target.value })}
        />

        <button style={buttonStyle} onClick={createProject}>
          Add Project
        </button>
      </div>

      {/* 🔹 Project List */}
      <div style={{ marginTop: "20px" }}>
        {projects.length === 0 ? (
          <p>No projects found</p>
        ) : (
          projects.map((p) => (
            <div key={p.id} style={projectCard}>
              <h3>{p.name}</h3>
              <p>{p.description}</p>

              <p>📅 {p.startDate} → {p.endDate}</p>
              <p>Status: {p.active ? "🟢 Active" : "🔴 Inactive"}</p>

              {/* 🔹 Progress Bar */}
              <div style={progressBar}>
                <div
                  style={{
                    ...progressFill,
                    width: `${p.progressPercentage}%`
                  }}
                >
                  {p.progressPercentage}%
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

//////////////////////////////////////////////////////
// 🎨 STYLES
//////////////////////////////////////////////////////

const formContainer = {
  display: "flex",
  gap: "10px",
  flexWrap: "wrap",
  marginBottom: "20px"
};

const inputStyle = {
  padding: "8px",
  borderRadius: "5px",
  border: "1px solid #ccc"
};

const buttonStyle = {
  padding: "8px 12px",
  background: "#2563eb",
  color: "white",
  border: "none",
  borderRadius: "5px",
  cursor: "pointer"
};

const projectCard = {
  border: "1px solid #e5e7eb",
  borderRadius: "10px",
  padding: "15px",
  marginBottom: "15px",
  boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
  background: "white"
};

const progressBar = {
  background: "#e5e7eb",
  borderRadius: "10px",
  overflow: "hidden",
  marginTop: "10px"
};

const progressFill = {
  background: "#22c55e",
  color: "white",
  padding: "5px",
  textAlign: "center"
};
