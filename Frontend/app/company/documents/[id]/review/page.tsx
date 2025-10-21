"use client"

import { useEffect, useState } from "react"
import { useRouter, useParams } from "next/navigation"
import { ProtectedRoute } from "@/components/protected-route"
import { DashboardLayout } from "@/components/dashboard-layout"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { getDocumentReview, type Review } from "@/lib/company-api"
import { Star, ArrowLeft, FileText } from "lucide-react"

export default function DocumentReviewPage() {
  const router = useRouter()
  const params = useParams()
  const documentId = params.id as string
  const [review, setReview] = useState<Review | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const fetchReview = async () => {
      try {
        const data = await getDocumentReview(documentId)
        setReview(data)
      } catch (err: any) {
        if (err.response?.status === 404) {
          setError("No review available yet")
        } else {
          setError("Failed to load review")
        }
      } finally {
        setLoading(false)
      }
    }

    fetchReview()
  }, [documentId])

  if (loading) {
    return (
      <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
        <DashboardLayout title="Document Review">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute allowedRoles={["COMPANY_OWNER"]}>
      <DashboardLayout title="Document Review">
        <div className="max-w-3xl mx-auto space-y-6">
          <Button variant="outline" onClick={() => router.back()}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Documents
          </Button>

          {error || !review ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <FileText className="h-12 w-12 text-muted-foreground mb-4" />
                <p className="text-lg font-medium mb-2">{error || "No review available"}</p>
                <p className="text-muted-foreground">This document has not been reviewed by an auditor yet</p>
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div>
                    <CardTitle>Audit Review</CardTitle>
                    <CardDescription>
                      Reviewed by {review.auditorName || "Auditor"} on{" "}
                      {new Date(review.reviewDate).toLocaleDateString()}
                    </CardDescription>
                  </div>
                  <Badge variant="secondary" className="flex items-center gap-1">
                    <Star className="h-3 w-3 fill-current" />
                    {review.rating}/5
                  </Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <h3 className="font-semibold mb-2">Rating</h3>
                  <div className="flex gap-1">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <Star
                        key={star}
                        className={`h-5 w-5 ${star <= review.rating ? "fill-primary text-primary" : "text-muted-foreground"}`}
                      />
                    ))}
                  </div>
                </div>
                <div>
                  <h3 className="font-semibold mb-2">Comments</h3>
                  <p className="text-muted-foreground whitespace-pre-wrap">{review.comments}</p>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}
