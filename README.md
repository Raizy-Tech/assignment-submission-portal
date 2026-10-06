# AssignFlow — Assignment Submission Portal (Android)

AssignFlow is a modern Android application built with Kotlin and Jetpack Compose, rewritten from the original static web MVP. It serves as a comprehensive assignment submission and grading portal with dedicated experiences for students and professors.

## Features

### Dual Role Portal & Authentication
- **Role Switching**: Fast toggle between Student and Professor portals.
- **Account Management**: Support for login, registration, and 1-tap demo logins.
- **Top Bar User Hub**: Displays user avatar, name, role badge, quick user switcher, and logout.

### Professor Dashboard
- **Analytics & Key Metrics**: Real-time stats on total assignments, total submissions, late submissions, and enrolled student counts.
- **Assignment Publishing**: Post new assignments with title, instructions/rubric, deadline presets, and optional PDF specification attachment.
- **Coursework Management**: View all assignments with on-time, late, and missing status breakdowns.
- **Submissions & Grading Hub**:
  - Comprehensive student roster per assignment.
  - Automatic status detection: `ON TIME`, `LATE`, or `MISSING`.
  - Document inspection dialog for reviewing submitted student work.
  - Marks input (0–100) and feedback/remarks recording with instant save.

### Student Dashboard
- **Personal Metrics**: Counters for Available assignments, Submitted work, Graded tasks, and Late submissions.
- **Filter & Search**: Filter coursework by `All`, `To Do` (Pending), `Submitted`, and `Graded`, plus real-time keyword search.
- **PDF Submission**:
  - Upload/submit assignments with custom file names and report summaries.
  - Automatic timestamping and late-submission detection against deadlines.
  - In-app document preview for reviewing submitted papers.
  - Grade and professor feedback viewer once evaluated.

### Architecture & Persistence
- **UI**: 100% Jetpack Compose with Material Design 3 guidelines and responsive layouts.
- **State Management**: Android MVVM architecture with `StateFlow` and reactive lifecycle observation.
- **Offline Persistence**: Local JSON store in app internal storage for zero-backend reliability and persistence across restarts.

## Demo Accounts
- **Professor**: `prof@college.edu` (Dr. Sarah Adams)
- **Student**: `student@college.edu` (Alex Johnson)
- **Student**: `emma@college.edu` (Emma Watson)
- Custom accounts can also be created via the registration form.