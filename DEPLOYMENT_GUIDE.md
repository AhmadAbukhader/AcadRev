# 🚀 Deployment Guide - Internal & External Auditor Updates

## ✅ All Changes Complete!

All backend and frontend changes have been successfully implemented. Follow this guide to deploy and test the new features.

---

## 📋 Prerequisites Checklist

Before proceeding, ensure you have:
- [ ] PostgreSQL database running
- [ ] Backend Spring Boot application ready
- [ ] Frontend React application ready
- [ ] Database backup (recommended)

---

## 🗄️ Step 1: Run Database Migrations

**CRITICAL: Run these migrations in order before starting the backend!**

### Migration 1: Update Role Types
```bash
psql -U postgres -d acadrev -f Database/migration_update_roles.sql
```
**What it does:** Updates COMPANY_OWNER → INTERNAL_AUDITOR, AUDITOR → EXTERNAL_AUDITOR, adds COMPANY_MANAGER

### Migration 2: Add Company Link to Users
```bash
psql -U postgres -d acadrev -f Database/migration_user_company_link.sql
```
**What it does:** Adds `company_profile_id` column to users table for internal auditors

### Migration 3: Create Auditor Assignment Table
```bash
psql -U postgres -d acadrev -f Database/migration_auditor_assignment.sql
```
**What it does:** Creates `auditor_assignment` table to track which external auditors are assigned to which companies

### Verify Migrations
```sql
-- Check if columns exist
\d acadrev_schema.users
\d acadrev_schema.auditor_assignment

-- Check role types
SELECT * FROM acadrev_schema.role;
```

---

## 🔧 Step 2: Backend Deployment

### Build Backend
```bash
cd Backend
./mvnw clean install
```

### Start Backend
```bash
./mvnw spring-boot:run
```

### Verify Backend is Running
```bash
curl http://localhost:8089/api/v1/company-profile/list-for-signup
```
Should return list of companies (even if empty).

---

## 🎨 Step 3: Frontend Deployment

### Install Dependencies (if not already done)
```bash
cd Frontend
npm install
```

### Start Frontend Development Server
```bash
npm run dev
```

### Build for Production (optional)
```bash
npm run build
```

---

## 🧪 Step 4: Testing Guide

### Test 1: Manager Workflow ✅

1. **Sign Up as Manager**
   - Select "Company Manager" role
   - Fill in details
   - Should NOT require company selection
   - Should route to `/manager-dashboard`

2. **Create Company Profile**
   - Fill in company name, address, industry, phone
   - Click "Create Profile"
   - Should see company profile displayed

3. **Edit Company Profile**
   - Click "Edit Profile"
   - Modify any field
   - Click "Save Changes"
   - Should see updated information

4. **View Read-Only Sections**
   - Should see progress overview
   - Should see all requirements (read-only)
   - Should NOT see upload buttons
   - Should NOT see edit options

---

### Test 2: Internal Auditor Workflow ✅

1. **Sign Up as Internal Auditor**
   - Select "Internal Auditor" role
   - Fill in name and email
   - Should see dropdown to "Select Company"
   - Choose a company created by a manager
   - Complete signup
   - Should route to `/company-dashboard`

2. **View Company Profile (Read-Only)**
   - Should see company information
   - Should NOT see "Edit Profile" button
   - Cannot modify company details

3. **Assign External Auditor**
   - Scroll to "External Auditor Assignment" section
   - Select an external auditor from dropdown
   - Click "Assign Auditor"
   - Should see success message
   - Should see auditor in "Currently Assigned Auditors" list

4. **Remove External Auditor**
   - Click "Remove" on an assigned auditor
   - Confirm removal
   - Should see auditor removed from list

5. **Upload Documents**
   - Should see upload button
   - Upload a document for a requirement
   - Should see document listed

6. **Self-Evaluate Requirements**
   - Should see status dropdowns for requirements
   - Change status (No/TSE/Yes)
   - Should see progress update

7. **Add Responses**
   - Click "Add Response" on a requirement
   - Write response text
   - Submit
   - Should see response displayed

---

### Test 3: External Auditor Workflow ✅

1. **Sign Up as External Auditor**
   - Select "External Auditor" role
   - Fill in details
   - Complete signup
   - Should route to `/auditor-dashboard`

2. **No Companies Initially**
   - Should see message: "No Companies Assigned"
   - Should see: "Contact an internal auditor to get assigned to a company"

3. **After Internal Auditor Assigns You**
   - Refresh page or log out and log back in
   - Should see company in the list
   - Click on company to view details

4. **Review Documents**
   - Browse requirements
   - Click "Review" on a document
   - Select rating (ACCEPTED/REJECTED)
   - Add comments
   - Submit review

5. **Update Audit Status**
   - Should see audit status dropdowns
   - Change status (No/TSE/Yes)
   - Should see progress update

6. **Reply to Responses**
   - View internal auditor's responses
   - Click "Reply"
   - Add reply text
   - Submit

7. **Cannot Upload Documents**
   - Should NOT see upload buttons
   - External auditors only review, not upload

---

## 🔍 Step 5: Verification Checklist

### Database Verification
- [ ] All three migration scripts ran successfully
- [ ] Users table has `company_profile_id` column
- [ ] `auditor_assignment` table exists
- [ ] Role enum values updated

### Backend Verification
- [ ] Backend starts without errors
- [ ] All new endpoints accessible
- [ ] Swagger UI shows new endpoints (if enabled)
- [ ] Permissions working correctly

### Frontend Verification
- [ ] Login page shows 3 role options
- [ ] Internal auditor signup shows company dropdown
- [ ] Manager dashboard works
- [ ] Internal auditor dashboard shows assignment UI
- [ ] External auditor dashboard filters by assignments
- [ ] All components render without errors

### Workflow Verification
- [ ] Manager can create/edit company profile
- [ ] Internal auditor can choose company during signup
- [ ] Internal auditor can assign external auditors
- [ ] External auditor sees only assigned companies
- [ ] All role-based permissions working

---

## 🐛 Troubleshooting

### Issue: "Company ID is required for internal auditors"
**Solution:** Ensure company is selected in signup dropdown before submitting.

### Issue: "No companies available" during signup
**Solution:** A manager must create at least one company profile first.

### Issue: External auditor sees all companies
**Solution:** 
1. Check if `getAssignedCompanies()` is being called
2. Verify userId is stored in localStorage
3. Check browser console for errors

### Issue: Cannot assign external auditor
**Solution:**
1. Verify `auditor_assignment` table exists
2. Check backend logs for errors
3. Ensure you're logged in as internal auditor

### Issue: "Auditor not found" error
**Solution:**
1. Verify user has EXTERNAL_AUDITOR role in database
2. Check if user ID is correct
3. Verify role enum updated correctly

### Issue: Database connection error
**Solution:**
1. Check `application.properties` for correct DB password
2. Verify PostgreSQL is running
3. Check database name and schema

---

## 📊 Database Queries for Debugging

### Check User Roles
```sql
SELECT u.id, u.username, u.name, r.role_type, cp.name as company_name
FROM acadrev_schema.users u
JOIN acadrev_schema.role r ON u.role_id = r.id
LEFT JOIN acadrev_schema.company_profile cp ON u.company_profile_id = cp.id
ORDER BY r.role_type, u.id;
```

### Check Auditor Assignments
```sql
SELECT 
    aa.id,
    cp.name as company_name,
    ea.name as external_auditor,
    ia.name as assigned_by,
    aa.assigned_at,
    aa.is_active
FROM acadrev_schema.auditor_assignment aa
JOIN acadrev_schema.company_profile cp ON aa.company_profile_id = cp.id
JOIN acadrev_schema.users ea ON aa.external_auditor_id = ea.id
JOIN acadrev_schema.users ia ON aa.assigned_by_internal_auditor_id = ia.id
ORDER BY aa.assigned_at DESC;
```

### Check Company Profiles
```sql
SELECT 
    cp.id,
    cp.name,
    cp.industry,
    u.name as created_by,
    r.role_type as creator_role
FROM acadrev_schema.company_profile cp
JOIN acadrev_schema.users u ON cp.user_id = u.id
JOIN acadrev_schema.role r ON u.role_id = r.id;
```

---

## 🎯 Key Changes Summary

### Backend Changes
- ✅ New model: `AuditorAssignment`
- ✅ New repository: `AuditorAssignmentRepository`
- ✅ New service: `AuditorAssignmentService`
- ✅ New controller: `AuditorAssignmentController`
- ✅ Updated `User` model with `companyProfile` field
- ✅ Updated `AuthService` to handle company selection
- ✅ Updated permissions (removed internal auditor from company profile editing)

### Frontend Changes
- ✅ New API file: `auditor-assignment-api.js`
- ✅ Updated `Login.jsx`: Company dropdown for internal auditors
- ✅ Updated `CompanyDashboard.jsx`: External auditor assignment UI
- ✅ Updated `AuditorDashboard.jsx`: Filter by assigned companies only
- ✅ Updated all API files with new functions

### Database Changes
- ✅ Role enum updated
- ✅ `company_profile_id` added to users table
- ✅ `auditor_assignment` table created

---

## 🎉 Success Criteria

Your deployment is successful if:

1. ✅ Manager can create and edit company profiles
2. ✅ Internal auditor chooses company during signup
3. ✅ Internal auditor can upload, evaluate, and respond
4. ✅ Internal auditor can assign/remove external auditors
5. ✅ Internal auditor CANNOT edit company profile
6. ✅ External auditor sees only assigned companies
7. ✅ External auditor can review, rate, and reply
8. ✅ All three dashboards render correctly
9. ✅ No console errors
10. ✅ All permissions enforced correctly

---

## 📞 Support

If you encounter issues:
1. Check browser console for errors
2. Check backend logs
3. Verify database migrations ran successfully
4. Review the troubleshooting section above
5. Check `INTERNAL_AUDITOR_UPDATES.md` for detailed technical documentation

---

## 🔄 Rollback Plan

If you need to rollback:

1. **Stop backend and frontend**
2. **Restore database backup**
3. **Git revert changes** (if using version control)
4. **Restart with previous version**

---

## ✨ Next Steps

After successful deployment:
1. Create test accounts for all three roles
2. Test complete workflow end-to-end
3. Train users on new assignment process
4. Monitor backend logs for any issues
5. Gather user feedback

---

**Deployment completed!** 🎊

All features are now live and ready for testing.

