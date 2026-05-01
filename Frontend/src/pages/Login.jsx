// import { useState } from "react";
// import { useAuth } from "../context/AuthContext";
// import { useNavigate, Link } from "react-router-dom";

// export default function Login() {
//   const [form, setForm] = useState({ email: "", password: "" });
//   const { login } = useAuth();
//   const navigate = useNavigate();

//   const handleSubmit = async () => {
//     try {
//       await login(form);
//       navigate("/dashboard");
//     } catch {
//       alert("Login Failed");
//     }
//   };

//   return (
//     <div className="container">
//       <h2>Login</h2>

//       <input
//         type="email"
//         placeholder="Email"
//         onChange={(e)=>setForm({...form,email:e.target.value})}
//       />

//       <input
//         type="password"
//         placeholder="Password"
//         onChange={(e)=>setForm({...form,password:e.target.value})}
//       />

//       <button onClick={handleSubmit}>Login</button>

//       <p>
//         Don't have an account? <Link to="/register">Register</Link>
//       </p>
//     </div>
//   );
// }

import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async () => {
    if (!form.email || !form.password) {
      alert("Please fill all fields");
      return;
    }

    try {
      await login(form);
      navigate("/dashboard");
    } catch (err) {
      console.error(err);
      alert("Login Failed");
    }
  };

  return (
    <div className="container">
      <h2>Login</h2>

      <input
        type="email"
        placeholder="Email"
        value={form.email}
        onChange={(e) => setForm({ ...form, email: e.target.value })}
      />

      <input
        type="password"
        placeholder="Password"
        value={form.password}
        onChange={(e) => setForm({ ...form, password: e.target.value })}
      />

      <button onClick={handleSubmit}>Login</button>

      <p>
        Don't have an account? <Link to="/register">Register</Link>
      </p>
    </div>
  );
}
