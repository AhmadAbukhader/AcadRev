"use client"

import type React from "react"

import { createContext, useContext, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { type User, getCurrentUser, isAuthenticated } from "@/lib/auth"

interface AuthContextType {
  user: User | null
  loading: boolean
  checkAuth: () => void
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  loading: true,
  checkAuth: () => {},
})

export const useAuth = () => useContext(AuthContext)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const router = useRouter()

  const checkAuth = () => {
    if (isAuthenticated()) {
      const currentUser = getCurrentUser()
      setUser(currentUser)
    } else {
      setUser(null)
    }
    setLoading(false)
  }

  useEffect(() => {
    checkAuth()
  }, [])

  return <AuthContext.Provider value={{ user, loading, checkAuth }}>{children}</AuthContext.Provider>
}
