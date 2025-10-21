"use client"

import type React from "react"

import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { logout } from "@/lib/auth"
import { useAuth } from "./auth-provider"
import { LogOut, Building2, FileCheck, Shield } from "lucide-react"

interface DashboardLayoutProps {
  children: React.ReactNode
  title: string
}

export function DashboardLayout({ children, title }: DashboardLayoutProps) {
  const router = useRouter()
  const { user } = useAuth()

  const handleLogout = () => {
    logout()
  }

  const getRoleIcon = () => {
    switch (user?.role) {
      case "COMPANY_OWNER":
        return <Building2 className="h-5 w-5" />
      case "AUDITOR":
        return <FileCheck className="h-5 w-5" />
      case "ADMIN":
        return <Shield className="h-5 w-5" />
      default:
        return null
    }
  }

  const getRoleLabel = () => {
    switch (user?.role) {
      case "COMPANY_OWNER":
        return "Company Owner"
      case "AUDITOR":
        return "Auditor"
      case "ADMIN":
        return "Admin"
      default:
        return ""
    }
  }

  return (
    <div className="min-h-screen bg-muted/30">
      <header className="border-b bg-background">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            {getRoleIcon()}
            <div>
              <h1 className="text-xl font-bold">{title}</h1>
              <p className="text-sm text-muted-foreground">{getRoleLabel()}</p>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <div className="text-sm text-right">
              <p className="font-medium">{user?.name}</p>
              <p className="text-muted-foreground">{user?.email}</p>
            </div>
            <Button variant="outline" size="sm" onClick={handleLogout}>
              <LogOut className="h-4 w-4 mr-2" />
              Logout
            </Button>
          </div>
        </div>
      </header>
      <main className="container mx-auto px-4 py-8">{children}</main>
    </div>
  )
}
