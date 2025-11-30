import api from "./api"

// Internal auditors and company managers need to know their company ID to use the APIs
// Since there's no backend endpoint for internal auditors and company managers to get their company profile,
// we'll need to store the company ID when the user logs in or create their profile

export const getMyCompanyProfile = async () => {
    // Internal auditors and company managers can't use /api/v1/company-profile/all (EXTERNAL_AUDITOR only)
    // Internal auditors and company managers can't use /api/v1/company-profile/{id} (EXTERNAL_AUDITOR only)
    // This is a backend limitation - internal auditors and company managers have no way to get their company profile
    throw new Error("Internal auditors and company managers cannot get their company profile - backend limitation")
}

export const getMyDocuments = async (companyId) => {
    // Internal auditors and company managers must provide their company ID
    if (!companyId) {
        throw new Error("Company ID is required for internal auditors and company managers")
    }
    const response = await api.get(`/api/v1/company-profile/${companyId}/documents`)
    return response.data
}

export const uploadFile = async (file, companyId, documentType, sectionId = null, requirementId = null) => {
    const formData = new FormData()
    formData.append("file", file)
    formData.append("companyId", companyId)
    formData.append("documentType", documentType)
    if (sectionId) formData.append("sectionId", sectionId)
    if (requirementId) formData.append("requirementId", requirementId)

    const response = await api.post("/api/v1/document/upload", formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    })
    return response.data
}

export const downloadFile = async (documentId) => {
    const response = await api.get(`/api/v1/document/download/${documentId}`, {
        responseType: "blob",
    })
    return response.data
}

export const getDocumentMetadata = async (documentId) => {
    const response = await api.get(`/api/v1/document/metadata/${documentId}`)
    return response.data
}

export const updateDocument = async (documentId, data) => {
    const response = await api.put(`/api/v1/document/${documentId}`, data)
    return response.data
}

export const deleteDocument = async (documentId) => {
    const response = await api.delete(`/api/v1/document/${documentId}`)
    return response.data
}

export const getCompanyReviews = async (companyId) => {
    const response = await api.get(`/api/v1/audit-review/documents/${companyId}`)
    return response.data
}

export const getDocumentReviews = async (documentId) => {
    try {
        const response = await api.get(`/api/v1/audit-review/document/${documentId}`)
        // Backend now returns a list of reviews, not a single review
        return response.data || []
    } catch (error) {
        // If no reviews, return empty array
        if (error.response?.status === 500 || error.response?.status === 404) {
            return []
        }
        throw error
    }
}

// Get current user's company
// For managers: the company they created
// For internal auditors: the company they're assigned to
export const getCurrentUserCompany = async () => {
    try {
        const response = await api.get("/api/v1/company-profile/my-company")
        return response.data
    } catch (error) {
        if (error.response?.status === 404) {
            throw new Error("No company profile found")
        }
        throw error
    }
}

export const createCompanyProfile = async (data) => {
    const response = await api.post("/api/v1/company-profile", data)
    return response.data
}

export const updateCompany = async (id, data) => {
    const response = await api.put(`/api/v1/company-profile/${id}`, data)
    return response.data
}

// Alias for consistency
export const updateCompanyProfile = updateCompany

export const deleteCompany = async (id) => {
    const response = await api.delete(`/api/v1/company-profile/${id}`)
    return response.data
}

// Get companies for signup (public endpoint - no auth required)
export const getCompaniesForSignup = async () => {
    const response = await api.get("/api/v1/company-profile/list-for-signup")
    return response.data
}

// External auditor functions (these work because external auditors can use /api/v1/company-profile/all)
export const getAllCompanies = async () => {
    const response = await api.get("/api/v1/company-profile/all")
    return response.data
}

export const getCompanyProfile = async (id) => {
    const response = await api.get(`/api/v1/company-profile/${id}`)
    return response.data
}

export const getCompanyDocuments = async (companyId) => {
    const response = await api.get(`/api/v1/company-profile/${companyId}/documents`)
    return response.data
}

// Sections API
export const getAllSections = async () => {
    const response = await api.get("/api/v1/sections")
    return response.data
}

export const getSectionRequirements = async (sectionId) => {
    const response = await api.get(`/api/v1/sections/${sectionId}/requirements`)
    return response.data
}

// Requirements API
export const getAllRequirements = async () => {
    const response = await api.get("/api/v1/requirements")
    return response.data
}

export const getRequirementDocuments = async (requirementId, companyId = null) => {
    const response = await api.get(`/api/v1/requirements/${requirementId}/documents`)
    let documents = response.data || []

    console.log(`[getRequirementDocuments] Requirement ${requirementId}: Received ${documents.length} documents from backend`, {
        documents: documents.map(d => ({
            id: d.id,
            fileName: d.fileName,
            companyId: d.company?.id,
            companyName: d.company?.name
        }))
    })

    // Backend now filters by user's company, so no need for frontend filtering
    // Just return what the backend gives us
    return documents
}

// Requirement Status API
export const getRequirementStatuses = async () => {
    const response = await api.get("/api/v1/requirements-status")
    return response.data || []
}

export const updateRequirementStatus = async (requirementId, status) => {
    const response = await api.put(`/api/v1/requirements-status/${requirementId}`, null, {
        params: { status }
    })
    return response.data
}

export const getRequirementStatusProgress = async () => {
    const response = await api.get("/api/v1/requirements-status/progress")
    return response.data || 0
}

// Requirement Response API
export const getRequirementResponse = async (companyId, requirementId) => {
    try {
        const response = await api.get(`/api/v1/requirement-responses/${companyId}/${requirementId}`)
        return response.data
    } catch (error) {
        // If response not found (404), return null
        if (error.response?.status === 404) {
            return null
        }
        throw error
    }
}

export const getAllRequirementResponses = async (companyId, requirementId) => {
    try {
        const response = await api.get(`/api/v1/requirement-responses/${companyId}/${requirementId}/all`)
        return response.data || []
    } catch (error) {
        // If response not found (404), return empty array
        if (error.response?.status === 404) {
            return []
        }
        throw error
    }
}

export const createRequirementResponse = async (requirementId, companyId, responseText) => {
    const response = await api.post("/api/v1/requirement-responses", null, {
        params: {
            requirementId,
            companyId,
            responseText
        }
    })
    return response.data
}

export const createRequirementResponseReply = async (parentResponseId, responseText) => {
    const response = await api.post(`/api/v1/requirement-responses/${parentResponseId}/reply`, null, {
        params: {
            responseText
        }
    })
    return response.data
}

export const updateRequirementResponse = async (responseId, responseText) => {
    const response = await api.put(`/api/v1/requirement-responses/${responseId}`, null, {
        params: {
            responseText
        }
    })
    return response.data
}

// Requirement Auditing API (for internal auditors and company managers to see external auditor progress and statuses)
export const getAuditProgress = async (companyId) => {
    const response = await api.get(`/api/v1/requirement-auditing/progress/${companyId}`)
    return response.data || 0
}

export const getRequirementsWithAuditStatus = async (companyId) => {
    const response = await api.get(`/api/v1/requirement-auditing/company/${companyId}/requirements`)
    return response.data || []
}

export const upsertAudit = async (requirementId, companyId, status) => {
    const response = await api.post("/api/v1/requirement-auditing/upsert", null, {
        params: {
            requirementId,
            companyId,
            status
        }
    })
    return response.data
}