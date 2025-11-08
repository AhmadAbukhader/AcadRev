# Implementation Summary: Auditor Reply to Company Owner Response

## Overview
This implementation allows auditors to see company owner responses to requirements and reply to them, creating a conversation thread.

## Database Changes

### DDL Commands
Run the SQL file: `Database/requirement_response_update.sql`

The following columns were added to `requirement_response` table:
- `parent_response_id` (INTEGER, NULL) - References the parent response for replies
- `created_by_user_id` (INTEGER, NOT NULL) - Tracks who created the response
- `created_at` (TIMESTAMP) - When the response was created
- `updated_at` (TIMESTAMP) - When the response was last updated

### Foreign Keys Added:
- `fk_parent_response` - Self-referencing foreign key for replies
- `fk_created_by_user` - Foreign key to users table

## Backend Changes

### 1. Model (`RequirementResponse.java`)
- Added `parentResponse` field for threading
- Added `createdBy` field to track the user who created the response
- Added `createdAt` and `updatedAt` timestamps
- Added `@PrePersist` and `@PreUpdate` hooks for automatic timestamp management

### 2. DTO (`RequirementResponseDTO.java`)
- Added fields: `parentResponseId`, `createdByUserId`, `createdByUserName`, `createdByUserRole`, `createdAt`, `updatedAt`
- Added `replies` list to support nested conversation threads

### 3. Repository (`RequirementResponseRepository.java`)
- Added methods to find top-level responses and replies
- `findByCompanyProfileIdAndRequirementIdAndParentResponseIsNullOrderByCreatedAtAsc` - Gets top-level responses
- `findByParentResponseIdOrderByCreatedAtAsc` - Gets replies to a specific response

### 4. Service (`RequirementResponseService.java`)
- Updated `createResponse` to set `createdBy` and validate company ownership
- Added `createReply` method for auditors and company owners to reply
- Added `getAllResponses` method to get all responses with nested replies
- Added `buildResponseTree` helper to build conversation threads recursively
- Updated `updateResponse` to validate user ownership

### 5. Controller (`RequirementResponseController.java`)
- Added `POST /api/v1/requirement-responses/{parentResponseId}/reply` endpoint for creating replies
- Added `GET /api/v1/requirement-responses/{companyId}/{requirementId}/all` endpoint to get all responses with replies
- Existing endpoints remain for backward compatibility

## Frontend Changes

### 1. API Files
**`company-api.js`:**
- Added `getAllRequirementResponses` - Gets all responses with replies
- Added `createRequirementResponseReply` - Creates a reply

**`auditor-api.js`:**
- Added `getAllRequirementResponses` - Gets all responses with replies
- Added `createRequirementResponseReply` - Allows auditors to reply

### 2. Component (`RequirementsTabs.jsx`)
- Updated `loadRequirementResponse` to fetch all responses (array) instead of single response
- Updated `openResponseDialog` to support replying to existing responses
- Updated `handleSubmitResponse` to handle replies for both auditors and company owners
- Added recursive `renderResponse` function to display conversation threads
- Updated UI to show:
  - Company owner responses in indigo background
  - Auditor replies in purple background
  - Reply buttons for appropriate users
  - Update button for company owners on their own top-level responses
  - Timestamps and user names for each response

## Features

1. **Company Owner:**
   - Can create initial responses to requirements
   - Can update their own responses
   - Can reply to auditor responses
   - Can see all responses in a threaded conversation

2. **Auditor:**
   - Can see company owner responses
   - Can reply to company owner responses
   - Can see the full conversation thread
   - Cannot create initial responses (only replies)

## Usage

1. **Run Database Migration:**
   ```sql
   -- Execute Database/requirement_response_update.sql
   ```

2. **Backend:**
   - The backend automatically handles authentication and authorization
   - Company owners can only create/update responses for their own company
   - Users can only update their own responses

3. **Frontend:**
   - Company owners see "Add Response" button when no response exists
   - Both company owners and auditors see "Reply" buttons on existing responses
   - Conversation threads are displayed with indentation for replies
   - Each response shows the author name, role, and timestamp

## Testing Checklist

- [ ] Company owner can create initial response
- [ ] Company owner can update their own response
- [ ] Auditor can see company owner response
- [ ] Auditor can reply to company owner response
- [ ] Company owner can see auditor reply
- [ ] Company owner can reply to auditor reply
- [ ] Conversation thread displays correctly
- [ ] Timestamps and user names display correctly
- [ ] Authorization works (users can only update their own responses)

