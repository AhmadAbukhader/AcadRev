"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { ProtectedRoute } from "@/components/protected-route"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useAuth } from "@/components/auth-provider"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { getCompanyProfile, uploadFile } from "@/lib/company-api"
import { AlertCircle, Upload, FileText } from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"

export default function UploadDocumentPage() {
  const { user } = useAuth()
  const router = useRouter()
  const [file, setFile] = useState<File | null>(null)
  const [documentType, setDocumentType] = useState("")
  const [companyId, setCompanyId] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)
  const [fetchLoading, setFetchLoading] = useState(true)

  useEffect(() => {
    const fetchCompany = async () => {
      if (!user?.id) return

      try {
        const data = await getCompanyProfile(user.id)
        setCompanyId(data.id)
      } catch (err: any) {
        setError("Failed to load company profile")
      } finally {
        setFetchLoading(false)
      }
    }

    fetchCompany()
  }, [user])

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0])
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!file) {
      setError("Please select a file")
      return
    }

    setError("")
    setLoading(true)

    try {
      await uploadFile(file, companyId, documentType)
      router.push("/company/documents")
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to upload document")
    } finally {
      setLoading(false)
    }
  }

  if (fetchLoading) {
    return (
      <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
        <DashboardLayout title="Upload Document">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
      <DashboardLayout title="Upload Document">
        <div className="max-w-2xl mx-auto">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Upload className="h-5 w-5" />
                Upload ISO Document
              </CardTitle>
              <CardDescription>Upload a document for auditor review</CardDescription>
            </CardHeader>
            <form onSubmit={handleSubmit}>
              <CardContent className="space-y-4">
                {error && (
                  <Alert variant="destructive">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{error}</AlertDescription>
                  </Alert>
                )}
                <div className="space-y-2">
                  <Label htmlFor="documentType">Document Type</Label>
                  <Input
                    id="documentType"
                    value={documentType}
                    onChange={(e) => setDocumentType(e.target.value)}
                    required
                    placeholder="e.g., ISO 9001 Quality Manual, Process Documentation"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="file">Select File</Label>
                  <div className="flex items-center gap-4">
                    <Input id="file" type="file" onChange={handleFileChange} required className="flex-1" />
                    {file && (
                      <div className="flex items-center gap-2 text-sm text-muted-foreground">
                        <FileText className="h-4 w-4" />
                        {file.name}
                      </div>
                    )}
                  </div>
                </div>
                <div className="flex gap-4 pt-4">
                  <Button type="submit" disabled={loading} className="flex-1">
                    {loading ? "Uploading..." : "Upload Document"}
                  </Button>
                  <Button type="button" variant="outline" onClick={() => router.back()}>
                    Cancel
                  </Button>
                </div>
              </CardContent>
            </form>
          </Card>
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}
