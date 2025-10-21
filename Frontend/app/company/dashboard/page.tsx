"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ProtectedRoute } from "@/components/protected-route"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useAuth } from "@/components/auth-provider"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { getCompanyProfile } from "@/lib/company-api"
import { Building2, FileText, Upload, Settings } from "lucide-react"

export default function CompanyDashboardPage() {
  const { user } = useAuth()
  const router = useRouter()
  const [company, setCompany] = useState<any>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchCompany = async () => {
      if (!user?.id) return

      try {
        const data = await getCompanyProfile(user.id)
        setCompany(data)
      } catch (error: any) {
        if (error.response?.status === 404) {
          // Company profile not found
          setCompany(null)
        }
      } finally {
        setLoading(false)
      }
    }

    fetchCompany()
  }, [user])

  if (loading) {
    return (
      <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
        <DashboardLayout title="Company Dashboard">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  if (!company) {
    return (
      <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
        <DashboardLayout title="Company Dashboard">
          <div className="max-w-2xl mx-auto">
            <Card>
              <CardHeader>
                <CardTitle>Welcome to ISO Certification Management</CardTitle>
                <CardDescription>You need to create a company profile to get started</CardDescription>
              </CardHeader>
              <CardContent>
                <Button onClick={() => router.push("/company/create-profile")}>
                  <Building2 className="h-4 w-4 mr-2" />
                  Create Company Profile
                </Button>
              </CardContent>
            </Card>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
      <DashboardLayout title="Company Dashboard">
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-3xl font-bold">{company.name}</h2>
              <p className="text-muted-foreground">{company.industry}</p>
            </div>
            <Button variant="outline" onClick={() => router.push("/company/profile")}>
              <Settings className="h-4 w-4 mr-2" />
              Edit Profile
            </Button>
          </div>

          <div className="grid gap-6 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="h-5 w-5" />
                  Documents
                </CardTitle>
                <CardDescription>Manage your ISO certification documents</CardDescription>
              </CardHeader>
              <CardContent>
                <Button onClick={() => router.push("/company/documents")} className="w-full">
                  View Documents
                </Button>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Upload className="h-5 w-5" />
                  Upload
                </CardTitle>
                <CardDescription>Upload new documents for review</CardDescription>
              </CardHeader>
              <CardContent>
                <Button onClick={() => router.push("/company/upload")} className="w-full">
                  Upload Document
                </Button>
              </CardContent>
            </Card>
          </div>

          <Card>
            <CardHeader>
              <CardTitle>Company Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-muted-foreground">Address</p>
                  <p className="font-medium">{company.address}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Industry</p>
                  <p className="font-medium">{company.industry}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Contact Email</p>
                  <p className="font-medium">{company.contactEmail}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Contact Phone</p>
                  <p className="font-medium">{company.contactPhone}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}
