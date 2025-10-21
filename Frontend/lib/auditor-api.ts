import api from "./api"

export const getAllCompanies = async () => {
  const response = await api.get("/getAllCompanyProfile")
  return response.data
}

export const reviewDocument = async (data: {
  documentId: string
  rating: number
  comments: string
}) => {
  const response = await api.post("/reviewDocument", data)
  return response.data
}

export const getAuditorReviews = async (auditorId: string) => {
  const response = await api.get(`/reviews/auditor/${auditorId}`)
  return response.data
}

export const updateReview = async (id: string, data: { rating: number; comments: string }) => {
  const response = await api.put(`/reviews/${id}`, data)
  return response.data
}

export const getDocumentReview = async (documentId: string) => {
  const response = await api.get(`/getDocumentReview/${documentId}`)
  return response.data
}
