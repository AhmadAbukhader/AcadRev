"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ProtectedRoute } from "@/components/protected-route"
import { DashboardLayout } from "@/components/dashboard-layout"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { getAllCompanies, getCompanyDocuments, type Document } from "@/lib/company-api"
import { FileText, Search, ArrowLeft } from "lucide-react"

export default function AdminDocumentsPage() {
  const router = useRouter()
  const [allDocuments, setAllDocuments] = useState<(Document & { companyName?: string })[]>([])
  const [filteredDocuments, setFilteredDocuments] = useState<(Document & { companyName?: string })[]>([])
  const [searchQuery, setSearchQuery] = useState("")
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchAllDocuments = async () => {
      try {
        const companies = await getAllCompanies()
        const documentsPromises = companies.map(async (company: any) => {
          try {
            const docs = await getCompanyDocuments(company.id)
            return docs.map((doc: Document) => ({ ...doc, companyName: company.name }))
          } catch {
            return []
          }
        })
        const documentsArrays = await Promise.all(documentsPromises)
        const documents = documentsArrays.flat()
        setAllDocuments(documents)
        setFilteredDocuments(documents)
      } catch (err: any) {
        console.error("Failed to load documents", err)
      } finally {
        setLoading(false)
      }
    }

    fetchAllDocuments()
  }, [])

  useEffect(() => {
    if (searchQuery) {
      const filtered = allDocuments.filter(
        (doc) =>
          doc.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
          doc.type.toLowerCase().includes(searchQuery.toLowerCase()) ||
          doc.companyName?.toLowerCase().includes(searchQuery.toLowerCase()),
      )
      setFilteredDocuments(filtered)
    } else {
      setFilteredDocuments(allDocuments)
    }
  }, [searchQuery, allDocuments])

  if (loading) {
    return (
      <ProtectedRoute allowedRoles={["ADMIN"]}>
        <DashboardLayout title="Document Management">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute allowedRoles={["ADMIN"]}>
      <DashboardLayout title="Document Management">
        <div className="space-y-6">
          <Button variant="outline" onClick={() => router.back()}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Dashboard
          </Button>

          <div>
            <h2 className="text-2xl font-bold mb-2">All Documents</h2>
            <p className="text-muted-foreground">View all uploaded documents across all companies</p>
          </div>

          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search documents by name, type, or company..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>

          {filteredDocuments.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <FileText className="h-12 w-12 text-muted-foreground mb-4" />
                <p className="text-lg font-medium mb-2">No documents found</p>
                <p className="text-muted-foreground">
                  {searchQuery ? "Try adjusting your search" : "No documents uploaded yet"}
                </p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid gap-4">
              {filteredDocuments.map((doc) => (
                <Card key={doc.id}>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex items-start gap-3">
                        <FileText className="h-5 w-5 mt-1 text-primary" />
                        <div>
                          <CardTitle className="text-lg">{doc.name}</CardTitle>
                          <CardDescription>
                            Company: {doc.companyName} • Type: {doc.type} • Uploaded:{" "}
                            {new Date(doc.uploadDate).toLocaleDateString()}
                          </CardDescription>
                        </div>
                      </div>
                      {doc.status && <Badge>{doc.status}</Badge>}
                    </div>
                  </CardHeader>
                </Card>
              ))}
            </div>
          )}
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}
