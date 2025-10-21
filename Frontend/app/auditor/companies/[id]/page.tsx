"use client"

import { useEffect, useState } from "react"
import { useRouter, useParams } from "next/navigation"
import { ProtectedRoute } from "@/components/protected-route"
import { DashboardLayout } from "@/components/dashboard-layout"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { getCompanyProfile, getCompanyDocuments, downloadFile, type Company, type Document } from "@/lib/company-api"
import { FileText, Download, ArrowLeft, Edit } from "lucide-react"

export default function CompanyDocumentsPage() {
  const router = useRouter()
  const params = useParams()
  const companyId = params.id as string
  const [company, setCompany] = useState<Company | null>(null)
  const [documents, setDocuments] = useState<Document[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [companyData, docsData] = await Promise.all([
          getCompanyProfile(companyId),
          getCompanyDocuments(companyId),
        ])
        setCompany(companyData)
        setDocuments(docsData)
      } catch (err: any) {
        console.error("Failed to load data", err)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [companyId])

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
      <ProtectedRoute allowedRoles={["AUDITOR"]}>
        <DashboardLayout title="Company Documents">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute allowedRoles={["AUDITOR"]}>
      <DashboardLayout title="Company Documents">
        <div className="space-y-6">
          <Button variant="outline" onClick={() => router.back()}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Companies
          </Button>

          {company && (
            <Card>
              <CardHeader>
                <CardTitle>{company.name}</CardTitle>
                <CardDescription>
                  {company.industry} • {company.address}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <p className="text-muted-foreground">Contact Email</p>
                    <p className="font-medium">{company.contactEmail}</p>
                  </div>
                  <div>
                    <p className="text-muted-foreground">Contact Phone</p>
                    <p className="font-medium">{company.contactPhone}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          <div>
            <h3 className="text-xl font-bold mb-4">Documents</h3>
            {documents.length === 0 ? (
              <Card>
                <CardContent className="flex flex-col items-center justify-center py-12">
                  <FileText className="h-12 w-12 text-muted-foreground mb-4" />
                  <p className="text-lg font-medium mb-2">No documents available</p>
                  <p className="text-muted-foreground">This company has not uploaded any documents yet</p>
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
                        <Button size="sm" onClick={() => router.push(`/auditor/review/${doc.id}`)}>
                          <Edit className="h-4 w-4 mr-2" />
                          Review Document
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}
