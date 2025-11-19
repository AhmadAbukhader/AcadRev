import api from "./api"

// Assign external auditor to company (internal auditor only)
export const assignExternalAuditor = async (companyId, externalAuditorId) => {
    const response = await api.post("/api/v1/auditor-assignments", {
        companyId,
        externalAuditorId
    })
    return response.data
}

// Get available external auditors for selection (internal auditor only)
export const getAvailableExternalAuditors = async () => {
    const response = await api.get("/api/v1/auditor-assignments/available-external-auditors")
    return response.data
}

// Get external auditors assigned to a company (internal auditor only)
export const getExternalAuditorsForCompany = async (companyId) => {
    const response = await api.get(`/api/v1/auditor-assignments/company/${companyId}/external-auditors`)
    return response.data
}

// Get companies assigned to an external auditor (external auditor only)
export const getAssignedCompanies = async (auditorId) => {
    const response = await api.get(`/api/v1/auditor-assignments/external-auditor/${auditorId}/companies`)
    return response.data
}

// Remove external auditor assignment (internal auditor only)
export const removeExternalAuditor = async (assignmentId) => {
    const response = await api.delete(`/api/v1/auditor-assignments/${assignmentId}`)
    return response.data
}

// Check if external auditor has access to a company
export const checkAuditorAccess = async (externalAuditorId, companyId) => {
    const response = await api.get("/api/v1/auditor-assignments/check-access", {
        params: { externalAuditorId, companyId }
    })
    return response.data
}

