import api from "./api"

export interface User {
  id: string
  email: string
  role: "COMPANY_OWNER" | "AUDITOR" | "ADMIN"
  name?: string
}

export interface SignupData {
  email: string
  password: string
  name: string
  role: "COMPANY_OWNER" | "AUDITOR"
}

export interface LoginData {
  email: string
  password: string
}

export const signup = async (data: SignupData) => {
  const response = await api.post("/api/v1/auth/signup", {
    username: data.email,
    password: data.password,
    name: data.name,
    role: data.role
  })
  return response.data
}

export const login = async (data: LoginData) => {
  const response = await api.post("/api/v1/auth/login", {
    username: data.email,
    password: data.password
  })
  if (response.data.token) {
    localStorage.setItem("token", response.data.token)
    localStorage.setItem("user", JSON.stringify(response.data.user))
  }
  return response.data
}

export const logout = () => {
  localStorage.removeItem("token")
  localStorage.removeItem("user")
  window.location.href = "/login"
}

export const getCurrentUser = (): User | null => {
  const userStr = localStorage.getItem("user")
  return userStr ? JSON.parse(userStr) : null
}

export const isAuthenticated = (): boolean => {
  return !!localStorage.getItem("token")
}
