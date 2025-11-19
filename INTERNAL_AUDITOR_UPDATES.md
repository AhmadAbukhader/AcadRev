# Internal Auditor & External Auditor Workflow Updates

## Summary of Changes

This document tracks the major workflow changes for Internal Auditors and External Auditors.

---

## ✅ Backend Changes Completed

### 1. **Auditor Assignment System**
- ✅ Created `AuditorAssignment` model
- ✅ Created `AuditorAssignmentRepository`
- ✅ Created `AuditorAssignmentDto`
- ✅ Created `AuditorAssignmentService`
- ✅ Created `AuditorAssignmentController`
- ✅ Database migration script: `migration_auditor_assignment.sql`

### 2. **User-Company Linking**
- ✅ Added `companyProfile` field to `User` model
- ✅ Updated `SignUpRequest` DTO to include `companyId`
- ✅ Updated `AuthService` to handle company selection for internal auditors
- ✅ Database migration script: `migration_user_company_link.sql`

### 3. **Permission Updates**
- ✅ Removed `INTERNAL_AUDITOR` from company profile creation (`@PostMapping`)
- ✅ Removed `INTERNAL_AUDITOR` from company profile editing (`@PutMapping`)
- ✅ Removed `INTERNAL_AUDITOR` from company profile deletion (`@DeleteMapping`)
- ✅ Removed `COMPANY_MANAGER` from document upload permissions
- ✅ Added public endpoint `/list-for-signup` for getting companies during signup

### 4. **New Endpoints**

#### Auditor Assignment Endpoints:
- `POST /api/v1/auditor-assignments` - Assign external auditor (INTERNAL_AUDITOR only)
- `GET /api/v1/auditor-assignments/external-auditor/{id}/companies` - Get assigned companies (EXTERNAL_AUDITOR only)
- `GET /api/v1/auditor-assignments/company/{id}/external-auditors` - Get assigned auditors (INTERNAL_AUDITOR only)
- `GET /api/v1/auditor-assignments/available-external-auditors` - List available external auditors (INTERNAL_AUDITOR only)
- `DELETE /api/v1/auditor-assignments/{id}` - Remove assignment (INTERNAL_AUDITOR only)
- `GET /api/v1/auditor-assignments/check-access` - Check if external auditor has access (EXTERNAL_AUDITOR only)

#### Company Profile Endpoint:
- `GET /api/v1/company-profile/list-for-signup` - Public endpoint for signup

---

## 🔄 Frontend Changes Needed

### 1. **Internal Auditor Signup Flow** ⏳
**File:** `Frontend/src/pages/Login.jsx`

#### Changes Needed:
1. When user selects `INTERNAL_AUDITOR` role during signup:
   - Fetch list of companies from `/api/v1/company-profile/list-for-signup`
   - Show dropdown to select a company
   - Send `companyId` in signup request

2. Remove company name input field for internal auditors
3. Keep company name field only for managers (already done)

---

### 2. **Internal Auditor Dashboard Updates** ⏳
**File:** `Frontend/src/pages/CompanyDashboard.jsx`

#### Changes Needed:
1. **Remove Company Profile Editing:**
   - Remove "Edit Profile" button
   - Make all company profile fields read-only
   - Show view-only company information

2. **Add External Auditor Assignment Section:**
   - Add new section "Assign External Auditor"
   - Fetch available external auditors from `/api/v1/auditor-assignments/available-external-auditors`
   - Show dropdown to select external auditor
   - Button to assign: POST to `/api/v1/auditor-assignments`
   - Show list of currently assigned external auditors
   - Button to remove assignment: DELETE `/api/v1/auditor-assignments/{id}`

3. **Keep Existing Functionality:**
   - ✅ Upload documents
   - ✅ Self-evaluate (requirement statuses)
   - ✅ Add responses to requirements
   - ✅ Download documents
   - ✅ View reviews

---

### 3. **External Auditor Dashboard Updates** ⏳
**File:** `Frontend/src/pages/AuditorDashboard.jsx`

#### Changes Needed:
1. **Filter Companies by Assignment:**
   - Remove `getAllCompanies()` call
   - Replace with: GET `/api/v1/auditor-assignments/external-auditor/{auditorId}/companies`
   - Only show companies that the external auditor has been assigned to
   - Show message if no companies assigned yet

2. **Keep Existing Functionality:**
   - ✅ Review documents
   - ✅ Rate documents (ACCEPTED/REJECTED)
   - ✅ Reply to responses
   - ✅ Update audit statuses

---

### 4. **Frontend API Updates** ⏳
**File:** `Frontend/src/lib/company-api.js`

#### New Functions Needed:
```javascript
// Get companies for signup (public)
export const getCompaniesForSignup = async () => {
    const response = await api.get("/api/v1/company-profile/list-for-signup")
    return response.data
}
```

**File:** `Frontend/src/lib/auditor-api.js`

#### New Functions Needed:
```javascript
// Get companies assigned to external auditor
export const getAssignedCompanies = async (auditorId) => {
    const response = await api.get(`/api/v1/auditor-assignments/external-auditor/${auditorId}/companies`)
    return response.data
}
```

**New File:** `Frontend/src/lib/auditor-assignment-api.js`

#### Functions Needed:
```javascript
// Assign external auditor to company
export const assignExternalAuditor = async (companyId, externalAuditorId) => {
    const response = await api.post("/api/v1/auditor-assignments", {
        companyId,
        externalAuditorId
    })
    return response.data
}

// Get available external auditors
export const getAvailableExternalAuditors = async () => {
    const response = await api.get("/api/v1/auditor-assignments/available-external-auditors")
    return response.data
}

// Get external auditors for a company
export const getExternalAuditorsForCompany = async (companyId) => {
    const response = await api.get(`/api/v1/auditor-assignments/company/${companyId}/external-auditors`)
    return response.data
}

// Remove external auditor assignment
export const removeExternalAuditor = async (assignmentId) => {
    const response = await api.delete(`/api/v1/auditor-assignments/${assignmentId}`)
    return response.data
}
```

---

## 📋 Database Migrations Required

Run these SQL scripts in order:

1. `Database/migration_update_roles.sql` (if not already run)
2. `Database/migration_user_company_link.sql` ⚠️ **NEW**
3. `Database/migration_auditor_assignment.sql` ⚠️ **NEW**

---

## 🎯 Workflow Summary

### **Internal Auditor Workflow:**
1. **Signup:** Choose an existing company created by a manager
2. **Dashboard:** 
   - View company profile (read-only)
   - Upload documents
   - Self-evaluate requirements
   - Add responses
   - **NEW:** Assign external auditors to the company
   - View external auditor progress and reviews

### **External Auditor Workflow:**
1. **Signup:** Normal signup (no changes)
2. **Dashboard:**
   - **BEFORE:** See all companies
   - **AFTER:** Only see companies assigned by internal auditors
   - Review documents
   - Rate documents
   - Reply to responses
   - Update audit statuses

### **Manager Workflow:**
1. **Signup:** Normal signup
2. **Dashboard:**
   - Create company profile
   - Edit company profile
   - View all auditing processes (read-only)
   - Cannot upload, edit, or interact with audit process

---

## Next Steps

1. Run database migrations
2. Update frontend signup flow
3. Update CompanyDashboard for internal auditors
4. Update AuditorDashboard for external auditors
5. Create auditor assignment API functions
6. Test complete workflow

