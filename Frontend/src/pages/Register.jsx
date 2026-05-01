import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    password: ""
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    // 🔹 Validation
    if (!form.firstName || !form.email || !form.password) {
      setError("First name, email and password are required");
      return;
    }

    setError("");
    setLoading(true);

    try {
      const res = await register(form);

      alert("Registration successful ✅");

      // 👉 go to login instead of dashboard (better flow)
      navigate("/");

    } catch (err) {
      console.log(err.response?.data || err);

      setError(
        err.response?.data?.message || "Registration failed ❌"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={container}>
      <h2>Register</h2>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <input
        name="firstName"
        value={form.firstName}
        placeholder="First Name"
        onChange={handleChange}
      />

      <input
        name="lastName"
        value={form.lastName}
        placeholder="Last Name"
        onChange={handleChange}
      />

      <input
        name="username"
        value={form.username}
        placeholder="Username"
        onChange={handleChange}
      />

      <input
        name="email"
        value={form.email}
        placeholder="Email"
        onChange={handleChange}
      />

      <input
        name="password"
        type="password"
        value={form.password}
        placeholder="Password"
        onChange={handleChange}
      />

      <button onClick={handleSubmit} disabled={loading}>
        {loading ? "Registering..." : "Register"}
      </button>
    </div>
  );
}

//////////////////////////////////////////////////////
// 🎨 STYLE
//////////////////////////////////////////////////////

const container = {
  display: "flex",
  flexDirection: "column",
  gap: "10px",
  padding: "20px",
  maxWidth: "300px",
  margin: "auto"
};