# 🚀 Future Feature Roadmap

This document tracks planned/suggested features for the EduFlow LMS. These features are categorized by their impact and complexity.

## 🏆 Gamification & Engagement
- **Student Leaderboard**: Top 5 students based on marks/consistency.
  - **Recommended Model:** `Gemini 1.5 Flash` (Simple SQL query + UI table).
- **User Online Status**: Green dot indicators for active users.
  - **Recommended Model:** `Gemini 1.5 Flash` (Basic WebSocket or polling logic).
- **Basic Course Rating**: 1-5 star ratings and 1-sentence reviews for courses.
  - **Recommended Model:** `Gemini 1.5 Flash` (Standard CRUD operations).
- **Private Study Notes**: A scratchpad on course pages that auto-saves for students.
  - **Recommended Model:** `Gemini 1.5 Flash` (Simple text-area persistence).

## 📊 Analytics & Visibility
- **Attendance Analytics (Pie Charts)**: Visual representation of student attendance.
  - **Recommended Model:** `Gemini 1.5 Flash` (Chart.js or CSS-based visualization).
- **Grade Heatmap**: Color-coded view of marks (Green >75, Yellow 50-75, Red <50).
  - **Recommended Model:** `Gemini 1.5 Flash` (Dynamic CSS class mapping).
- **Course Completion Progress Tracker**: Progress bar (0–100%) as students check off materials.
  - **Recommended Model:** `Gemini 1.5 Pro` (Requires coordinating multiple database entities and state logic).

## 🛠️ Productivity & Tools
- **Interactive Study Calendar**: Dashboard widget highlighting upcoming deadlines.
  - **Recommended Model:** `Gemini 1.5 Pro` (Tricky date logic and UI event handling).
- **Resource Bookmarking**: "Save for later" functionality for course materials.
  - **Recommended Model:** `Gemini 1.5 Flash` (Simple toggle/bookmark table).
- **Downloadable "Report Card" (PDF)**: Formatted summary of current grades across all subjects.
  - **Recommended Model:** `Gemini 1.5 Pro` (Precise formatting and PDF library integration).
- **Smart Search & Filter**: Global search bar on the dashboard for instant filtering.
  - **Recommended Model:** `Gemini 1.5 Flash` (Client-side JavaScript search).

## 🛡️ Security & Audit
- **Login History Log**: List of last 5 logins (Date, Time, IP placeholder).
  - **Recommended Model:** `Gemini 1.5 Flash` (Basic audit logging query).
- **Teacher Portfolio Page**: Public profile for teachers showing bio and courses.
  - **Recommended Model:** `Gemini 1.5 Flash` (Straightforward new page + controller).
- **Teacher "Activity Feed"**: Log of recent student submissions/interactions for instructors.
  - **Recommended Model:** `Gemini 1.5 Flash` (Simple activity stream query).

## 🎨 Aesthetics
- **Dark Mode / Theme Toggle**: Toggle switch for light/dark mode using CSS variables.
  - **Recommended Model:** `Gemini 1.5 Flash` (CSS-driven, low logic complexity).
