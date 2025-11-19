"use client"

import { useState, useEffect, useRef } from "react"
import { useNavigate } from "react-router-dom"
import {
  LogOut,
  Download,
  FileText,
  Building2,
  Mail,
  Phone,
  MapPin,
  Calendar,
  X,
  AlertCircle,
  Star,
  Eye,
  Edit2,
  Save,
  MessageSquare,
  CheckCircle,
} from "lucide-react"
import {
  getCurrentUserCompany,
  createCompanyProfile,
  updateCompanyProfile,
  getMyDocuments,
  downloadFile,
  getCompanyReviews,
  getAllSections,
  getAllRequirements,
  getRequirementDocuments,
  getRequirementStatuses,
  getRequirementStatusProgress,
  getAuditProgress,
  getRequirementsWithAuditStatus,
  getDocumentReviews,
} from "../lib/company-api"
import RequirementsTabs from "../components/RequirementsTabs"
import PDFPreviewModal from "../components/PDFPreviewModal"
import isoPdf from "../assets/ISO-9001-2015-1.pdf"

export default function ManagerDashboard() {
  const navigate = useNavigate()
  const [profile, setProfile] = useState(null)
  const [documents, setDocuments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [companyId, setCompanyId] = useState(null)
  const [showCreateProfile, setShowCreateProfile] = useState(false)
  const [creatingProfile, setCreatingProfile] = useState(false)
  const [editingProfile, setEditingProfile] = useState(false)
  const [toast, setToast] = useState(null)

  const [profileForm, setProfileForm] = useState({
    name: "",
    address: "",
    industry: "",
    phone: "",
  })

  const [selectedISO, setSelectedISO] = useState("ISO 9001")
  const [sections, setSections] = useState([])
  const [requirements, setRequirements] = useState([])
  const [requirementStatuses, setRequirementStatuses] = useState([])
  const [statusProgress, setStatusProgress] = useState(0)
  const [auditProgress, setAuditProgress] = useState(0)
  const [auditStatuses, setAuditStatuses] = useState([])
  const [showISOPdfModal, setShowISOPdfModal] = useState(false)
  const [reviewsModalOpen, setReviewsModalOpen] = useState(false)
  const [selectedDocumentForReviews, setSelectedDocumentForReviews] = useState(null)
  const [documentReviews, setDocumentReviews] = useState([])
  
  // Ref for scrolling to requirements section
  const requirementsSectionRef = useRef(null)

  useEffect(() => {
    const checkCompanyAndLoad = async () => {
      const token = localStorage.getItem("token")
      const userRole = localStorage.getItem("userRole")

      if (!token || userRole !== "COMPANY_MANAGER") {
        navigate("/")
        return
      }

      // First try to get the current user's company
      try {
        const userCompany = await getCurrentUserCompany()

        // User has a company - load it
        const companyId = userCompany.id.toString()
        setCompanyId(companyId)
        setProfile(userCompany)
        localStorage.setItem("companyId", companyId)
        await fetchCompanyData(companyId)
      } catch {
        // User doesn't have a company - show create form
        setShowCreateProfile(true)
        setLoading(false)
      }
    }

    checkCompanyAndLoad()
  }, [navigate])

  const fetchCompanyData = async (compId) => {
    try {
      setLoading(true)

      // Fetch all data in parallel
      const [
        docsData,
        sectionsData,
        requirementsData,
        statusesData,
        progressData,
        auditProgressData,
        auditStatusesData,
      ] = await Promise.all([
        getMyDocuments(compId),
        getAllSections(),
        getAllRequirements(),
        getRequirementStatuses(),
        getRequirementStatusProgress(),
        getAuditProgress(compId),
        getRequirementsWithAuditStatus(compId),
      ])

      setDocuments(docsData || [])
      setSections(sectionsData || [])
      setRequirements(requirementsData || [])
      setRequirementStatuses(statusesData || [])
      setStatusProgress(progressData || 0)
      setAuditProgress(auditProgressData || 0)
      setAuditStatuses(auditStatusesData || [])
    } catch (err) {
      console.error("Error fetching company data:", err)
      showToast("Failed to load company data", "error")
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    localStorage.clear()
    navigate("/")
  }

  const showToast = (message, type = "success") => {
    setToast({ message, type })
    setTimeout(() => setToast(null), 3000)
  }

  const handleCreateProfile = async (e) => {
    e.preventDefault()
    setCreatingProfile(true)
    setError("")

    try {
      const createdProfile = await createCompanyProfile(profileForm)
      const newCompanyId = createdProfile.id.toString()

      setProfile(createdProfile)
      setCompanyId(newCompanyId)
      localStorage.setItem("companyId", newCompanyId)
      setShowCreateProfile(false)
      showToast("Company profile created successfully!")

      await fetchCompanyData(newCompanyId)
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Failed to create profile")
    } finally {
      setCreatingProfile(false)
    }
  }

  const handleEditProfile = () => {
    setProfileForm({
      name: profile.name || "",
      address: profile.address || "",
      industry: profile.industry || "",
      phone: profile.phone || "",
    })
    setEditingProfile(true)
  }

  const handleUpdateProfile = async (e) => {
    e.preventDefault()
    setCreatingProfile(true)
    setError("")

    try {
      const updatedProfile = await updateCompanyProfile(companyId, profileForm)
      setProfile(updatedProfile)
      setEditingProfile(false)
      showToast("Company profile updated successfully!")
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Failed to update profile")
    } finally {
      setCreatingProfile(false)
    }
  }

  const handleCancelEdit = () => {
    setEditingProfile(false)
    setError("")
  }

  const handleDownload = async (documentId, fileName) => {
    try {
      await downloadFile(documentId, fileName)
      showToast("Document downloaded successfully!")
    } catch (err) {
      showToast("Failed to download document", "error")
    }
  }

  const handleViewReviews = async (document) => {
    try {
      setSelectedDocumentForReviews(document)
      const reviews = await getDocumentReviews(document.id)
      setDocumentReviews(reviews || [])
      setReviewsModalOpen(true)
    } catch (err) {
      console.error("Error fetching reviews:", err)
      showToast("Failed to load reviews", "error")
    }
  }

  const getRequirementDocumentsForView = async (requirementId) => {
    return await getRequirementDocuments(companyId, requirementId)
  }

  if (showCreateProfile) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 via-blue-50 to-indigo-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-3xl shadow-2xl p-8 md:p-12 max-w-2xl w-full">
          <div className="flex items-center gap-4 mb-8">
            <div className="bg-gradient-to-br from-green-600 to-blue-600 p-3 rounded-xl">
              <Building2 className="w-8 h-8 text-white" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Create Company Profile</h1>
              <p className="text-gray-600">Let's set up your company profile to get started</p>
            </div>
          </div>

          <form onSubmit={handleCreateProfile} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Company Name *
              </label>
              <input
                type="text"
                required
                value={profileForm.name}
                onChange={(e) => setProfileForm({ ...profileForm, name: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter company name"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Address *</label>
              <input
                type="text"
                required
                value={profileForm.address}
                onChange={(e) => setProfileForm({ ...profileForm, address: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter company address"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Industry *</label>
              <input
                type="text"
                required
                value={profileForm.industry}
                onChange={(e) => setProfileForm({ ...profileForm, industry: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="e.g., Technology, Manufacturing"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Phone *</label>
              <input
                type="tel"
                required
                value={profileForm.phone}
                onChange={(e) => setProfileForm({ ...profileForm, phone: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="+1 (555) 000-0000"
              />
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl flex items-center gap-2">
                <AlertCircle className="w-5 h-5" />
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={creatingProfile}
              className="w-full bg-gradient-to-r from-green-600 to-blue-600 text-white py-3 px-4 rounded-xl font-medium hover:from-green-700 hover:to-blue-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 transition-all duration-300 transform hover:scale-[1.02] disabled:opacity-50 disabled:cursor-not-allowed shadow-lg"
            >
              {creatingProfile ? "Creating..." : "Create Profile"}
            </button>
          </form>
        </div>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 via-blue-50 to-indigo-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-green-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading dashboard...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 via-blue-50 to-indigo-50">
      {/* Toast Notification */}
      {toast && (
        <div
          className={`fixed top-4 right-4 z-50 px-6 py-3 rounded-xl shadow-lg flex items-center gap-2 animate-fade-in ${
            toast.type === "success"
              ? "bg-green-600 text-white"
              : "bg-red-600 text-white"
          }`}
        >
          {toast.type === "success" ? (
            <CheckCircle className="w-5 h-5" />
          ) : (
            <AlertCircle className="w-5 h-5" />
          )}
          {toast.message}
        </div>
      )}

      {/* Header */}
      <div className="bg-white border-b border-gray-200 shadow-sm sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="bg-gradient-to-br from-green-600 to-blue-600 p-2 rounded-xl">
                <Building2 className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-xl font-bold text-gray-900">Manager Dashboard</h1>
                <p className="text-sm text-gray-600">
                  {profile ? profile.name : "Viewing company overview"}
                </p>
              </div>
            </div>

            <button
              onClick={handleLogout}
              className="flex items-center gap-2 px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-xl transition-colors"
            >
              <LogOut className="w-4 h-4" />
              Logout
            </button>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Company Profile Section */}
        {profile && (
          <div className="bg-white rounded-2xl shadow-lg p-6 mb-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                <Building2 className="w-6 h-6 text-green-600" />
                Company Profile
              </h2>
              {!editingProfile && (
                <button
                  onClick={handleEditProfile}
                  className="flex items-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-xl transition-colors"
                >
                  <Edit2 className="w-4 h-4" />
                  Edit Profile
                </button>
              )}
            </div>

            {editingProfile ? (
              <form onSubmit={handleUpdateProfile} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Company Name
                    </label>
                    <input
                      type="text"
                      required
                      value={profileForm.name}
                      onChange={(e) =>
                        setProfileForm({ ...profileForm, name: e.target.value })
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Phone
                    </label>
                    <input
                      type="tel"
                      required
                      value={profileForm.phone}
                      onChange={(e) =>
                        setProfileForm({ ...profileForm, phone: e.target.value })
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Industry
                    </label>
                    <input
                      type="text"
                      required
                      value={profileForm.industry}
                      onChange={(e) =>
                        setProfileForm({ ...profileForm, industry: e.target.value })
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Address
                    </label>
                    <input
                      type="text"
                      required
                      value={profileForm.address}
                      onChange={(e) =>
                        setProfileForm({ ...profileForm, address: e.target.value })
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    />
                  </div>
                </div>

                {error && (
                  <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl flex items-center gap-2">
                    <AlertCircle className="w-5 h-5" />
                    {error}
                  </div>
                )}

                <div className="flex gap-3">
                  <button
                    type="submit"
                    disabled={creatingProfile}
                    className="flex items-center gap-2 px-6 py-2 bg-green-600 hover:bg-green-700 text-white rounded-xl transition-colors disabled:opacity-50"
                  >
                    <Save className="w-4 h-4" />
                    {creatingProfile ? "Saving..." : "Save Changes"}
                  </button>
                  <button
                    type="button"
                    onClick={handleCancelEdit}
                    className="px-6 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-xl transition-colors"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="flex items-start gap-3">
                  <Building2 className="w-5 h-5 text-green-600 mt-1" />
                  <div>
                    <p className="text-sm text-gray-600">Company Name</p>
                    <p className="font-semibold text-gray-900">{profile.name}</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <Phone className="w-5 h-5 text-green-600 mt-1" />
                  <div>
                    <p className="text-sm text-gray-600">Phone</p>
                    <p className="font-semibold text-gray-900">{profile.phone}</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <FileText className="w-5 h-5 text-green-600 mt-1" />
                  <div>
                    <p className="text-sm text-gray-600">Industry</p>
                    <p className="font-semibold text-gray-900">{profile.industry}</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <MapPin className="w-5 h-5 text-green-600 mt-1" />
                  <div>
                    <p className="text-sm text-gray-600">Address</p>
                    <p className="font-semibold text-gray-900">{profile.address}</p>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Progress Overview */}
        {companyId && (
          <div className="bg-white rounded-2xl shadow-lg p-6 mb-8">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Progress Overview</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-center gap-2 px-4 py-3 bg-indigo-50 rounded-lg border border-indigo-200">
                <div className="w-2 h-2 bg-indigo-600 rounded-full"></div>
                <span className="text-sm font-medium text-indigo-700">
                  Internal Auditor Progress: {Math.round(statusProgress)}%
                </span>
              </div>
              <div className="flex items-center gap-2 px-4 py-3 bg-purple-50 rounded-lg border border-purple-200">
                <div className="w-2 h-2 bg-purple-600 rounded-full"></div>
                <span className="text-sm font-medium text-purple-700">
                  External Auditor Progress: {Math.round(auditProgress)}%
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Requirements and Documents View (Read-only) */}
        {companyId && (
          <div ref={requirementsSectionRef} className="bg-white rounded-2xl shadow-lg p-6 mb-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold text-gray-900">
                ISO {selectedISO} Requirements & Documents
              </h2>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => setShowISOPdfModal(true)}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-xl transition-colors text-sm flex items-center gap-2"
                >
                  <Eye className="w-4 h-4" />
                  View ISO Standard
                </button>
              </div>
            </div>

            <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-4 mb-6">
              <div className="flex items-start gap-3">
                <Eye className="w-5 h-5 text-yellow-600 mt-0.5" />
                <div>
                  <p className="font-semibold text-yellow-900">View-Only Mode</p>
                  <p className="text-sm text-yellow-700 mt-1">
                    You can view all auditing processes, documents, reviews, and responses.
                    Editing is restricted to company profile only.
                  </p>
                </div>
              </div>
            </div>

            <RequirementsTabs
              sections={sections}
              requirements={requirements}
              companyId={companyId}
              isAuditor={false}
              onDownload={handleDownload}
              getRequirementDocuments={getRequirementDocumentsForView}
              requirementStatuses={requirementStatuses}
              auditStatuses={auditStatuses}
              onUpload={null}
              onUpdateStatus={null}
              onUpdateAuditStatus={null}
              onReview={null}
              onSectionClick={() => {
                // Scroll to the top of requirements section when a section is clicked
                // Small delay to ensure content has rendered
                setTimeout(() => {
                  if (requirementsSectionRef.current) {
                    requirementsSectionRef.current.scrollIntoView({ 
                      behavior: 'smooth', 
                      block: 'start' 
                    })
                  }
                }, 100)
              }}
            />
          </div>
        )}

        {/* Documents List with Reviews */}
        {companyId && documents.length > 0 && (
          <div className="bg-white rounded-2xl shadow-lg p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-6">All Documents & Reviews</h2>
            <div className="space-y-4">
              {documents.map((doc) => (
                <div
                  key={doc.id}
                  className="border border-gray-200 rounded-xl p-4 hover:border-green-300 transition-colors"
                >
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3">
                        <FileText className="w-5 h-5 text-green-600" />
                        <div>
                          <p className="font-semibold text-gray-900">{doc.fileName}</p>
                          <p className="text-sm text-gray-600">
                            Type: {doc.documentType} • Uploaded:{" "}
                            {new Date(doc.uploadedAt).toLocaleDateString()}
                          </p>
                        </div>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleViewReviews(doc)}
                        className="px-4 py-2 bg-purple-100 hover:bg-purple-200 text-purple-700 rounded-lg transition-colors text-sm flex items-center gap-2"
                      >
                        <Star className="w-4 h-4" />
                        View Reviews
                      </button>
                      <button
                        onClick={() => handleDownload(doc.id, doc.fileName)}
                        className="px-4 py-2 bg-green-100 hover:bg-green-200 text-green-700 rounded-lg transition-colors"
                      >
                        <Download className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Reviews Modal */}
      {reviewsModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-3xl shadow-2xl max-w-4xl w-full max-h-[90vh] flex flex-col">
            <div className="flex items-center justify-between p-6 border-b border-gray-200">
              <div>
                <h2 className="text-2xl font-bold text-gray-900">Document Reviews</h2>
                <p className="text-sm text-gray-600 mt-1">
                  {selectedDocumentForReviews?.fileName}
                </p>
              </div>
              <button
                onClick={() => setReviewsModalOpen(false)}
                className="p-2 hover:bg-gray-100 rounded-xl transition-colors"
              >
                <X className="w-6 h-6 text-gray-500" />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto p-6">
              {documentReviews.length === 0 ? (
                <div className="text-center py-12">
                  <Star className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500 text-lg">No reviews yet</p>
                  <p className="text-gray-400 text-sm">
                    This document hasn't been reviewed by any external auditors yet.
                  </p>
                </div>
              ) : (
                <div className="space-y-4">
                  {documentReviews.map((review, index) => (
                    <div
                      key={review.id || index}
                      className="bg-purple-50 border border-purple-200 rounded-xl p-4"
                    >
                      <div className="flex items-start justify-between mb-3">
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-2">
                            <span className="font-semibold text-gray-900">
                              {review.auditor?.name ||
                                review.auditor?.username ||
                                "Anonymous External Auditor"}
                            </span>
                            <span className="text-xs text-gray-500">
                              • {new Date(review.reviewedAt).toLocaleDateString()}
                            </span>
                          </div>
                          {review.rating && (
                            <span
                              className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${
                                review.rating === "ACCEPTED"
                                  ? "bg-green-100 text-green-700"
                                  : "bg-red-100 text-red-700"
                              }`}
                            >
                              {review.rating}
                            </span>
                          )}
                        </div>
                      </div>
                      <p className="text-gray-700 whitespace-pre-wrap">{review.comments}</p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* ISO PDF Modal */}
      {showISOPdfModal && (
        <PDFPreviewModal
          pdfUrl={isoPdf}
          onClose={() => setShowISOPdfModal(false)}
          title={`ISO ${selectedISO} Standard`}
        />
      )}
    </div>
  )
}

