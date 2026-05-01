import { createContext, useContext } from "react";
import API from "../api/axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {

  // ✅ REGISTER
  const register = async (data) => {
    const res = await API.post("/auth/register", data);
    return res.data;
  };

  // ✅ LOGIN
  const login = async (data) => {
    const res = await API.post("/auth/login", data);

    // store token
    localStorage.setItem("token", res.data.token);
  };

  // ✅ LOGOUT
  const logout = () => {
    localStorage.removeItem("token");
    window.location.href = "/";
  };

  return (
    <AuthContext.Provider value={{ register, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);