# RBAC (Role-Based Access Control) Implementation Summary

## Security Configuration
- **Location**: `AppConfig.java`
- **Roles**: `USER` (Job Seeker) and `RECRUITER` (Employer)

## Endpoint Access by Role

### 🔓 Public Endpoints (No Authentication Required)
- `POST /auth/register` - User registration
- `POST /auth/login` - User login

### 👤 USER Role (Job Seeker) - `/api/user/**`
All endpoints require USER role:

#### Saved Jobs/Bookmarks
- `POST /api/user/save-job/{jobId}` - Save a job
- `DELETE /api/user/unsave-job/{jobId}` - Unsave a job
- `GET /api/user/saved-jobs` - Get all saved jobs
- `GET /api/user/is-saved/{jobId}` - Check if job is saved

#### Application Management
- `GET /api/user/application-dashboard` - Get application dashboard with statistics
- `POST /api/user/withdraw-application/{applicationId}` - Withdraw an application
- `POST /api/user/apply/{id}` - Apply for a job

#### Resume Management
- `POST /api/user/resume` - Create a resume
- `PUT /api/user/resume/{resumeId}` - Update a resume
- `DELETE /api/user/resume/{resumeId}` - Delete a resume
- `GET /api/user/resumes` - Get all user resumes
- `GET /api/user/resume/{resumeId}` - Get resume by ID
- `POST /api/user/resume/{resumeId}/set-default` - Set default resume
- `GET /api/user/resume/default` - Get default resume

#### Job Alerts
- `POST /api/user/job-alert` - Create a job alert
- `PUT /api/user/job-alert/{alertId}` - Update a job alert
- `DELETE /api/user/job-alert/{alertId}` - Delete a job alert
- `GET /api/user/job-alerts` - Get all user job alerts
- `GET /api/user/job-alert/{alertId}` - Get job alert by ID
- `POST /api/user/job-alert/{alertId}/toggle` - Toggle job alert
- `GET /api/user/job-alert/{alertId}/matching-jobs` - Get matching jobs for alert

#### Company Reviews
- `POST /api/user/company/{companyId}/review` - Add a company review

### 🏢 RECRUITER Role (Employer) - `/api/recruiter/**`
All endpoints require RECRUITER role:

#### Job Management
- `POST /api/recruiter/post` - Post a new job
- `POST /api/recruiter/changeAppStatus` - Change application status
- `GET /api/recruiter/job/{jobId}/applicants` - View applicants for a job
- `POST /api/recruiter/bulk-update-applications` - Bulk update application statuses

#### Company Management
- `POST /api/recruiter/company/register` - Register a company
- `GET /api/recruiter/company/{companyId}` - Get company by ID
- `PUT /api/recruiter/company/{companyId}` - Update company
- `GET /api/recruiter/company/dashboard/{companyId}` - Get company dashboard
- `GET /api/recruiter/companies/my-companies` - Get my companies
- `POST /api/recruiter/company/{companyId}/verify` - Verify company (admin function)
- `POST /api/recruiter/company/{companyId}/reject` - Reject company (admin function)
- `GET /api/recruiter/company/{companyId}/reviews` - Get company reviews
- `GET /api/recruiter/company/{companyId}/average-rating` - Get company average rating

### 🔄 COMMON Endpoints (Both USER and RECRUITER) - `/api/common/**`
These endpoints are accessible by both roles:

#### Job Search & Browse
- `GET /api/common/getall` - Get all jobs (with optional sorting)
- `GET /api/common/getJobById/{id}` - Get job by ID
- `GET /api/common/search` - Search jobs by keyword
- `GET /api/common/filter` - Filter jobs (salary, location, etc.)
- `GET /api/common/category/{category}` - Get jobs by category
- `GET /api/common/company/{company}` - Get jobs by company
- `GET /api/common/postedBy/{id}` - Get jobs posted by user

#### Profile Management
- `GET /api/common/get/{id}` - Get profile by ID
- `PUT /api/common/update` - Update profile (own profile only)
- `GET /api/common/getAll` - Get all profiles

## Key RBAC Features Implemented

1. ✅ **JWT-based Authentication**: All protected endpoints require valid JWT token
2. ✅ **Role-based Authorization**: Endpoints are secured by USER or RECRUITER role
3. ✅ **User Context**: User ID is extracted from JWT token for all operations
4. ✅ **Ownership Validation**: Users can only access/modify their own data
5. ✅ **Proper Separation**: Job seeker features under `/api/user/**`, recruiter features under `/api/recruiter/**`

## Security Notes

- All endpoints under `/api/user/**` require USER role
- All endpoints under `/api/recruiter/**` require RECRUITER role  
- All endpoints under `/api/common/**` require either USER or RECRUITER role
- User ID is extracted from JWT token to ensure users can only access their own resources
- Company verification/rejection should ideally be admin-only (currently accessible to RECRUITER)

