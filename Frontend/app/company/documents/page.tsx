"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ProtectedRoute } from "@/components/protected-route"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useAuth } from "@/components/auth-provider"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { getCompanyProfile, getCompanyDocuments, downloadFile, type Document } from "@/lib/company-api"
import { FileText, Download, Upload, Eye } from "lucide-react"

export default function CompanyDocumentsPage() {
  const { user } = useAuth()
  const router = useRouter()
  const [documents, setDocuments] = useState<Document[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchDocuments = async () => {
      if (!user?.id) return

      try {
        const company = await getCompanyProfile(user.id)
        const docs = await getCompanyDocuments(company.id)
        setDocuments(docs)
      } catch (err: any) {
        console.error("Failed to load documents", err)
      } finally {
        setLoading(false)
      }
    }

    fetchDocuments()
  }, [user])

  const handleDownload = async (documentId: string, fileName: string) => {
    try {
      const blob = await downloadFile(documentId)
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement("a")
      a.href = url
      a.download = fileName
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
    } catch (err: any) {
      console.error("Failed to download file", err)
    }
  }

  if (loading) {
    return (
      <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
        <DashboardLayout title="Documents">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
      <DashboardLayout title="Documents">
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-2xl font-bold">Your Documents</h2>
              <p className="text-muted-foreground">Manage and view your uploaded ISO documents</p>
            </div>
            <Button onClick={() => router.push("/company/upload")}>
              <Upload className="h-4 w-4 mr-2" />
              Upload Document
            </Button>
          </div>

          {documents.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <FileText className="h-12 w-12 text-muted-foreground mb-4" />
                <p className="text-lg font-medium mb-2">No documents yet</p>
                <p className="text-muted-foreground mb-4">Upload your first ISO document to get started</p>
                <Button onClick={() => router.push("/company/upload")}>
                  <Upload className="h-4 w-4 mr-2" />
                  Upload Document
                </Button>
              </CardContent>
            </Card>
          ) : (
            <div className="grid gap-4">
              {documents.map((doc) => (
                <Card key={doc.id}>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex items-start gap-3">
                        <FileText className="h-5 w-5 mt-1 text-primary" />
                        <div>
                          <CardTitle className="text-lg">{doc.name}</CardTitle>
                          <CardDescription>
                            Type: {doc.type} • Uploaded: {new Date(doc.uploadDate).toLocaleDateString()}
                          </CardDescription>
                        </div>
                      </div>
                      {doc.status && <Badge>{doc.status}</Badge>}
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="flex gap-2">
                      <Button variant="outline" size="sm" onClick={() => handleDownload(doc.id, doc.name)}>
                        <Download className="h-4 w-4 mr-2" />
                        Download
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => router.push(`/company/documents/${doc.id}/review`)}
                      >
                        <Eye className="h-4 w-4 mr-2" />
                        View Review
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}
