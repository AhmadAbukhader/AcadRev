import api from "./api"

export interface Company {
  id: string
  name: string
  address: string
  industry: string
  contactEmail: string
  contactPhone: string
  ownerId: string
  createdAt: string
}

export interface Document {
  id: string
  name: string
  type: string
  uploadDate: string
  companyId: string
  fileUrl: string
  status?: string
}

export interface Review {
  id: string
  documentId: string
  auditorId: string
  auditorName?: string
  rating: number
  comments: string
  reviewDate: string
}

export const createCompanyProfile = async (data: Omit<Company, "id" | "ownerId" | "createdAt">) => {
  const response = await api.post("/createCompanyProfile", data)
  return response.data
}

export const getAllCompanies = async () => {
  const response = await api.get("/getAllCompanyProfile")
  return response.data
}

export const getCompanyProfile = async (id: string) => {
  const response = await api.get(`/getCompanyProfile/${id}`)
  return response.data
}

export const updateCompany = async (id: string, data: Partial<Company>) => {
  const response = await api.put(`/company/${id}`, data)
  return response.data
}

export const deleteCompany = async (id: string) => {
  const response = await api.delete(`/company/${id}`)
  return response.data
}

export const getCompanyDocuments = async (companyId: string) => {
  const response = await api.get(`/getCompanyDocuments/${companyId}`)
  return response.data
}

export const uploadFile = async (file: File, companyId: string, documentType: string) => {
  const formData = new FormData()
  formData.append("file", file)
  formData.append("companyId", companyId)
  formData.append("type", documentType)

  const response = await api.post("/uploadFile", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  })
  return response.data
}

export const downloadFile = async (documentId: string) => {
  const response = await api.get(`/downloadFile/${documentId}`, {
    responseType: "blob",
  })
  return response.data
}

export const deleteDocument = async (id: string) => {
  const response = await api.delete(`/documents/${id}`)
  return response.data
}

export const getDocumentReview = async (documentId: string) => {
  const response = await api.get(`/getDocumentReview/${documentId}`)
  return response.data
}

export const getAllCompanyDocumentsReview = async (companyId: string) => {
  const response = await api.get(`/getAllCompanyDocumentsReview/${companyId}`)
  return response.data
}

export const getAuditorReviews = async (auditorId: string) => {
  const response = await api.get(`/reviews/auditor/${auditorId}`)
  return response.data
}
