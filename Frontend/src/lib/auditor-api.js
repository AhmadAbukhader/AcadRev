import api from "./api"

// Get companies assigned to external auditor (replaces getAllCompanies for external auditors)
export const getAssignedCompanies = async (auditorId) => {
    const response = await api.get(`/api/v1/auditor-assignments/external-auditor/${auditorId}/companies`)
    return response.data
}

export const getAllCompanies = async () => {
    const response = await api.get("/api/v1/company-profile/all")
    return response.data
}

export const getCompanyProfile = async (companyId) => {
    const response = await api.get(`/api/v1/company-profile/${companyId}`)
    return response.data
}

export const getCompanyDocuments = async (companyId) => {
    const response = await api.get(`/api/v1/company-profile/${companyId}/documents`)
    return response.data
}

export const reviewDocument = async (documentId, rating, comment) => {
    // Build query string for @RequestParam (Spring expects query parameters)
    const queryParams = new URLSearchParams({
        rating: rating || "",
        comment: comment || ""
    }).toString()

    console.log("Reviewing document:", { documentId, rating, comment, url: `/api/v1/audit-review/document/${documentId}?${queryParams}` })

    const response = await api.post(
        `/api/v1/audit-review/document/${documentId}?${queryParams}`,
        null,
        {
            headers: {
                "Content-Type": "application/json"
            }
        }
    )
    console.log("Review response:", response.data)
    return response.data
}

export const getDocumentReview = async (documentId) => {
    try {
        const response = await api.get(`/api/v1/audit-review/document/${documentId}`)
        return response.data || []
    } catch (error) {
        // Backend throws exception if document hasn't been reviewed yet
        if (error.response?.status === 500 || error.response?.status === 404) {
            return []
        }
        throw error
    }
}

// Alias for consistency
export const getDocumentReviews = getDocumentReview

export const updateReview = async (reviewId, rating, comment) => {
    const response = await api.put(`/api/v1/audit-review/${reviewId}`, {
        rating,
        comments: comment
    })
    return response.data
}

export const getExternalAuditorReviews = async (auditorId) => {
    const response = await api.get(`/api/v1/audit-review/auditor/${auditorId}`)
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

export const hasAlreadyReviewed = async (documentId, auditorId) => {
    try {
        // Get all reviews by this external auditor
        const reviews = await getExternalAuditorReviews(auditorId)
        // Check if any of the reviews are for this document
        return reviews.some(review => review.document?.id === documentId)
    } catch {
        return false
    }
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

    // Filter by companyId if provided
    if (companyId) {
        const companyIdNum = typeof companyId === 'string' ? parseInt(companyId, 10) : companyId
        documents = documents.filter(doc => {
            // Check if document has company and company.id matches
            return doc.company && doc.company.id === companyIdNum
        })
    }

    return documents
}

// Requirement Response API (read-only for external auditors, but can reply)
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

export const createRequirementResponseReply = async (parentResponseId, responseText) => {
    const response = await api.post(`/api/v1/requirement-responses/${parentResponseId}/reply`, null, {
        params: {
            responseText
        }
    })
    return response.data
}

// Requirement Auditing API
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

export const getAuditProgress = async (companyId) => {
    const response = await api.get(`/api/v1/requirement-auditing/progress/${companyId}`)
    return response.data || 0
}

// Get requirement statuses for a company (for external auditors to view internal auditor statuses)
export const getCompanyRequirementStatuses = async (companyId) => {
    const response = await api.get(`/api/v1/requirements-status/company/${companyId}`)
    return response.data || []
}