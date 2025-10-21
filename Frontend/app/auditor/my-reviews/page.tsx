"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ProtectedRoute } from "@/components/protected-route"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useAuth } from "@/components/auth-provider"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { getAuditorReviews, type Review } from "@/lib/company-api"
import { FileText, Star, ArrowLeft, Edit } from "lucide-react"

export default function MyReviewsPage() {
  const { user } = useAuth()
  const router = useRouter()
  const [reviews, setReviews] = useState<Review[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchReviews = async () => {
      if (!user?.id) return

      try {
        const data = await getAuditorReviews(user.id)
        setReviews(data)
      } catch (err: any) {
        console.error("Failed to load reviews", err)
      } finally {
        setLoading(false)
      }
    }

    fetchReviews()
  }, [user])

  if (loading) {
    return (
      <ProtectedRoute allowedRoles={["AUDITOR"]}>
        <DashboardLayout title="My Reviews">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute allowedRoles={["AUDITOR"]}>
      <DashboardLayout title="My Reviews">
        <div className="space-y-6">
          <Button variant="outline" onClick={() => router.back()}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Dashboard
          </Button>

          <div>
            <h2 className="text-2xl font-bold mb-2">My Reviews</h2>
            <p className="text-muted-foreground">All reviews you have submitted</p>
          </div>

          {reviews.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <FileText className="h-12 w-12 text-muted-foreground mb-4" />
                <p className="text-lg font-medium mb-2">No reviews yet</p>
                <p className="text-muted-foreground">Start reviewing company documents to see them here</p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid gap-4">
              {reviews.map((review) => (
                <Card key={review.id}>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex items-start gap-3">
                        <FileText className="h-5 w-5 mt-1 text-primary" />
                        <div>
                          <CardTitle className="text-lg">Document Review</CardTitle>
                          <CardDescription>
                            Reviewed on {new Date(review.reviewDate).toLocaleDateString()}
                          </CardDescription>
                        </div>
                      </div>
                      <Badge variant="secondary" className="flex items-center gap-1">
                        <Star className="h-3 w-3 fill-current" />
                        {review.rating}/5
                      </Badge>
                    </div>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    <div>
                      <p className="text-sm font-medium mb-1">Comments:</p>
                      <p className="text-sm text-muted-foreground line-clamp-2">{review.comments}</p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => router.push(`/auditor/review/${review.documentId}`)}
                    >
                      <Edit className="h-4 w-4 mr-2" />
                      Edit Review
                    </Button>
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
