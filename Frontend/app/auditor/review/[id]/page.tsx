"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter, useParams } from "next/navigation"
import { ProtectedRoute } from "@/components/protected-route"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useAuth } from "@/components/auth-provider"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { reviewDocument, getDocumentReview } from "@/lib/auditor-api"
import { downloadFile } from "@/lib/company-api"
import { AlertCircle, ArrowLeft, Star, Download } from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"

export default function ReviewDocumentPage() {
  const { user } = useAuth()
  const router = useRouter()
  const params = useParams()
  const documentId = params.id as string
  const [rating, setRating] = useState(0)
  const [comments, setComments] = useState("")
  const [error, setError] = useState("")
  const [success, setSuccess] = useState("")
  const [loading, setLoading] = useState(false)
  const [existingReview, setExistingReview] = useState<any>(null)
  const [checkingReview, setCheckingReview] = useState(true)

  useEffect(() => {
    const checkExistingReview = async () => {
      try {
        const review = await getDocumentReview(documentId)
        if (review && review.auditorId === user?.id) {
          setExistingReview(review)
          setRating(review.rating)
          setComments(review.comments)
        }
      } catch (err: any) {
        // No existing review, that's fine
      } finally {
        setCheckingReview(false)
      }
    }

    checkExistingReview()
  }, [documentId, user])

  const handleDownload = async () => {
    try {
      const blob = await downloadFile(documentId)
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement("a")
      a.href = url
      a.download = `document-${documentId}`
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
    } catch (err: any) {
      setError("Failed to download document")
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (rating === 0) {
      setError("Please select a rating")
      return
    }

    setError("")
    setSuccess("")
    setLoading(true)

    try {
      await reviewDocument({
        documentId,
        rating,
        comments,
      })
      setSuccess("Review submitted successfully")
      setTimeout(() => {
        router.back()
      }, 1500)
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to submit review")
    } finally {
      setLoading(false)
    }
  }

  if (checkingReview) {
    return (
      <ProtectedRoute allowedRoles={["AUDITOR"]}>
        <DashboardLayout title="Review Document">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute allowedRoles={["AUDITOR"]}>
      <DashboardLayout title="Review Document">
        <div className="max-w-2xl mx-auto space-y-6">
          <Button variant="outline" onClick={() => router.back()}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back
          </Button>

          <Card>
            <CardHeader>
              <CardTitle>{existingReview ? "Update Review" : "Submit Review"}</CardTitle>
              <CardDescription>
                {existingReview
                  ? "You have already reviewed this document. You can update your review below."
                  : "Provide your rating and comments for this document"}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button variant="outline" onClick={handleDownload} className="w-full mb-4 bg-transparent">
                <Download className="h-4 w-4 mr-2" />
                Download Document
              </Button>
            </CardContent>
          </Card>

          <Card>
            <form onSubmit={handleSubmit}>
              <CardContent className="space-y-4 pt-6">
                {error && (
                  <Alert variant="destructive">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{error}</AlertDescription>
                  </Alert>
                )}
                {success && (
                  <Alert>
                    <AlertDescription>{success}</AlertDescription>
                  </Alert>
                )}
                <div className="space-y-2">
                  <Label>Rating</Label>
                  <div className="flex gap-2">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <button
                        key={star}
                        type="button"
                        onClick={() => setRating(star)}
                        className="focus:outline-none focus:ring-2 focus:ring-ring rounded"
                      >
                        <Star
                          className={`h-8 w-8 transition-colors ${
                            star <= rating ? "fill-primary text-primary" : "text-muted-foreground hover:text-primary"
                          }`}
                        />
                      </button>
                    ))}
                  </div>
                  {rating > 0 && <p className="text-sm text-muted-foreground">Rating: {rating} out of 5</p>}
                </div>
                <div className="space-y-2">
                  <Label htmlFor="comments">Comments</Label>
                  <Textarea
                    id="comments"
                    value={comments}
                    onChange={(e) => setComments(e.target.value)}
                    required
                    rows={6}
                    placeholder="Provide detailed feedback about the document..."
                  />
                </div>
                <div className="flex gap-4 pt-4">
                  <Button type="submit" disabled={loading} className="flex-1">
                    {loading ? "Submitting..." : existingReview ? "Update Review" : "Submit Review"}
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
