import api from "./api"

export interface User {
  id: string
  email: string
  name: string
  role: string
  createdAt: string
}

export interface Role {
  id: string
  name: string
}

export interface AdminDashboard {
  totalUsers: number
  totalCompanies: number
  totalDocuments: number
  totalReviews: number
  recentActivity?: any[]
}

export const getAllUsers = async () => {
  const response = await api.get("/users")
  return response.data
}

export const getRoles = async () => {
  const response = await api.get("/roles")
  return response.data
}

export const updateUserRole = async (userId: string, roleId: string) => {
  const response = await api.put(`/users/${userId}/role`, { roleId })
  return response.data
}

export const getAdminDashboard = async () => {
  const response = await api.get("/dashboard/admin")
  return response.data
}

export const getAllReviews = async () => {
  const response = await api.get("/reviews")
  return response.data
}
