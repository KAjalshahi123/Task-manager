import { useEffect, useState } from "react";
import API from "../api/axios";

export default function Tasks() {
  const [tasks, setTasks] = useState([]);

  useEffect(() => {
    API.get("/tasks/my")   // ✅ FIXED
      .then(res => setTasks(res.data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div style={{ padding: "20px" }}>
      <h2>Tasks</h2>

      {tasks.length === 0 ? (
        <p>No tasks found</p>
      ) : (
        tasks.map(t => (
          <div key={t.id} style={taskCard}>
            <h4>{t.title}</h4>
            <p>Status: <b>{t.status}</b></p>
            <p>Priority: {t.priority}</p>
            <p>Due: {t.dueDate}</p>
            <p>Project: {t.projectName}</p>
          </div>
        ))
      )}
    </div>
  );
}

const taskCard = {
  padding: "15px",
  marginBottom: "10px",
  borderRadius: "8px",
  background: "#f8fafc",
  boxShadow: "0 2px 6px rgba(0,0,0,0.1)"
};