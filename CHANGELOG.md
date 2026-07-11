# Changelog

All notable changes to SafePoint are documented here.

This project now uses [CalVer](https://calver.org/) (`YYYY.MM.MICRO`) rather than Semantic Versioning — see the note at the bottom of this file for why. Prior to `2026.07.0`, releases were tagged with pre-1.0 SemVer (`v0.2.x-beta`); those are kept below for history but won't continue.

## [2026.07.0] - 2026-07-12

First CalVer release. Bundles everything since `v0.2.5-beta`: the student feedback system, PostgreSQL support for deployment, and a handful of fixes found while rolling both out to production.

### Added
- Student feedback rating widget: a 5-star rating plus optional comment, reachable from the About page CTA and an automatic one-time prompt after a student's first minute on the site. Each student can submit once; the prompt and CTA never resurface afterward, enforced server-side rather than via browser storage.
- PostgreSQL as a supported production database, selected via Spring profile (`dev` = H2, `prod` = Postgres) rather than hardcoded. No entity/repository changes were needed — Hibernate handles the dialect differences.
- Systemd + Nginx deployment on `almond` now runs against PostgreSQL in production.

### Changed
- Student logout flow refactored to allow logging out cleanly as a student user.
- Aggregate report generation now fetches by year instead of the previous approach, and dropped the printing-specific / detailed-list output paths that weren't needed.
- The feedback rating input was redesigned twice during rollout: first from labeled buttons to five face-emoji options, then from emoji to a 5-star widget for a cleaner, more standard rating pattern. Accent color and spacing were also tuned to match the rest of the UI.
- Dev database reverted from in-memory H2 back to file-based (`data/safepoint.mv.db`), so local data survives restarts again.
- Bumped `spring-boot-devtools` 3.5.3 → 3.5.14.

### Fixed
- Feedback submission now updates the on-page CTA immediately (e.g. the About page button switching to "already submitted") instead of requiring a page reload to reflect the new state.
- `/favicon.svg` and `/icons/**` (referenced by the PWA manifest) were being blocked with a 403 by Spring Security's catch-all deny rule — added to the permitted static asset paths.

## [v0.2.5-beta] - 2026-01-20

UI overhaul for authentication flows, a database-agnostic report generation backend, and a terms & privacy consent modal for student signup.

### Added
- Modern split-screen design for all authentication pages (login, signup, password reset), replacing the previous single-card layout.
- Terms and privacy consent modal displayed during student signup.
- SEO meta tags and updated auth theme colors for improved shareability and visual consistency.
- Database-agnostic report generation implementation that works across H2 and PostgreSQL without repository changes.
- Enhanced report generation interface with improved layout, consistent styling, and properly positioned action buttons.

### Changed
- Consolidated styles across auth variants using CSS custom properties and Bootstrap utility classes, reducing duplication.
- Modernized student login template and extracted CSS into an external stylesheet.
- Improved status pill alignment and responsive behavior across all views.

### Fixed
- Role-based access control now correctly prevents students from accessing admin routes.
- Admin welcome text centered and layout standardized across auth pages.
- Button text overflow and responsive design issues on auth pages resolved.
- Report generation layout issues and month selection bug fixed.
- Content clipping in narrow viewports resolved with proper bottom margin.

## [v0.2.4-beta] - 2025-12-26

Email template consistency pass and a handful of fixes found during production use over the holiday break.

### Changed
- Reworked email templates to maintain visual and structural consistency across all transactional emails (submission confirmations, appointment notifications, verification links).

### Fixed
- Confirm button on appointment reschedule modal is now disabled when the selected date is past due, preventing scheduling in the past.
- Removed incorrect image reference that was causing a broken asset on the reports page.

## [v0.2.3-beta] - 2025-12-20

Replaced the admin accordion-based lists with paginated card layouts, added real-time search, and restored student access to their own submission details.

### Added
- Pagination support for admin reports and appointments lists, replacing the previous accordion-based approach. **Breaking:** any integrations relying on the accordion DOM structure will need to be updated.
- Real-time search on the student dashboard and admin list views, built with reusable search components.
- Re-exposed submission detail views to students, allowing them to review their own reports and appointment history after submission.
- Status history accordion in submission detail views for full audit trail visibility.

### Changed
- Redesigned both reports and appointments admin lists with a card-based layout for improved readability and mobile experience.
- Improved modal layout and spacing across admin views.

### Fixed
- Modal sizing corrected and mobile spacing improved across management views.
- Report and appointment text headers no longer overflow on mobile viewports in admin detail pages.

## [v0.2.2-beta] - 2025-12-17

Admin interface polish: responsive status badges, mobile UX improvements, and centralized admin account creation.

### Added
- Admin account creation now exposed on the frontend through a centered modal in the admin views, replacing the previous backdoor initialization flow.
- Responsive status pill badges with consistent styling across all admin templates, replacing the previous text-based status display.
- Cancelled appointment status added to the status enum.
- Mobile UX improvements: button reordering in report detail views and date truncation on narrow viewports.

### Changed
- Standardized report and appointment URLs to singular form (`/report` instead of `/reports`) and fixed corresponding navigation active states.
- Redesigned error page with improved styling and contextual navigation links.
- Simplified the report status enum for cleaner internal handling.
- Admin contextual buttons rearranged and resized for better visual hierarchy.
- Interactive modals centered across all management views.
- Reschedule modal reorganized for proper element hierarchy and mobile support.

### Fixed
- Date field in reschedule modal is now readonly, preventing manual input that could bypass validation.
- Collapsed status indicator repositioned on mobile viewport to avoid overlapping content.
- Common status styles now applied consistently across all views.
- Report header layout aligned and detail page routes updated to match the new URL structure.

## [v0.2.1-beta] - 2025-12-14

Navigation overhaul with a new centralized `mod-nav` fragment, plus minor admin UI refinements.

### Added
- New `mod-nav` navigation fragment for both student and admin sides, replacing the previous inline navigation markup with a single maintainable component.
- Centralized navigation configuration moved to properties files, simplifying nav rendering across all views.
- Admin session context card to inform administrators of their current session status.

### Changed
- Navigation config extracted to separate properties and adapted for student-side nav support.
- Internal admin dashboard styles moved to external stylesheets for maintainability.
- Removed unused navigation templates and configurations.
- Admin hero text alignment refined for mobile viewports.
- Admin styling adjusted to maintain design cohesion across views.

### Fixed
- Incorrect fragment path in admin dashboard resolved.

## [v0.2.0-beta] - 2025-12-12

Complete student account system with Spring Security: email verification, password reset, rate limiting, and a redesigned dashboard.

### Added
- Complete student account system with Spring Security, including signup, login, email verification, and role-based access control.
- Email verification process with token expiry and rate limiting to prevent abuse.
- Password reset functionality with rate limiting and automatic token cleanup.
- User dashboard that auto-fills authenticated user info and displays reports in a tabbed card layout.
- Email verification status page with dedicated styling.

### Changed
- Admin authentication fully refactored to use Spring Security's filter chain, replacing the previous custom implementation.
- Separate `UserDetailsService` implementations consolidated into a single unified service.
- Status colors externalized to CSS custom variables for consistent theming.
- Page-specific stylesheets refactored and externalized.
- Auth pages restyled with a consistent visual treatment.
- Dashboard UI redesigned with responsive card layout and improved status display.
- Authentication tokens refactored with improved rate limiting system.
- Bumped `org.apache.tika:tika-core` from 2.9.2 to 3.2.2.

### Fixed
- Admin logout now uses POST request instead of GET, preventing CSRF-based logout attacks.
- Error 405 when logging in as admin resolved.
- Deprecation warning in `studentFilterChain` fixed.
- Password confirmation loop in `AdminInit` corrected.
- Administrator username now tracked when updating report status for audit purposes.

## [v0.1.1-beta] - 2025-10-30

Security and validation hotfix released same day as v0.1.0-beta, addressing path traversal CWEs, input sanitization, and the project rename from Voiz to SafePoint.

### Added
- Property templates for externalized configuration, with updated README documentation.
- Additional server-side validation annotations for improved input handling.

### Changed
- Project renamed from "Voiz" to "SafePoint": package name, artifact ID, and all references updated across code and documentation.
- Complex controller logic refactored into dedicated service classes.
- Classes reorganized into subpackages for better project structure.
- MIME type configuration moved to a dedicated properties file.
- Database settings separated into profile-specific property files.
- Profile card replaced with a survey card on the about page.
- CI/CD workflow refined with minimal permissions and corrected deployment targets.

### Fixed
- Email validation corrected on both client and server sides.
- Path traversal vulnerabilities addressed with additional file name validation in `FileStorageService`.
- User inputs sanitized and email content escaped to prevent XSS and injection attacks.
- YAML configuration properly bound using `@ConfigurationPropertiesScan` annotation.

## [v0.1.0-beta] - 2025-10-30

Initial beta release. Established the core report submission and tracking system, appointment scheduling, admin management, and the foundations of the SafePoint platform.

### Core Features
- Multi-step report submission form with issue description, evidence file upload (10 MB cap), and external link option for large files.
- UUID-based report tracking for anonymous and authenticated students.
- Appointment scheduling system with Flatpickr date selection, weekly grouping, and admin-side rescheduling.
- Report status tracking with full status history and a dedicated status enum.
- Email notification system: submission confirmations, appointment scheduling and rescheduling emails with user-friendly date/time formatting.

### Admin Features
- Admin authentication with role-based access control and Spring Security filter chain.
- Admin dashboard with recent reports overview and status switcher.
- Separate admin views for reports and appointments with detail pages.
- Appointment rescheduling with confirmation modals and past-due validation.
- Admin-side report management with status updates and audit trail.

### UI/UX
- Card-based user-facing interface with multi-step forms for reports and appointments.
- Floating action button (FAB) menu with blur/dark overlay effect and animation.
- Stepped progress indicator replacing the linear progress bar for better cognitive load.
- PWA basics: web app manifest, service worker, favicon, and splash screen.
- Connectivity status bar for offline awareness.
- About page with project information.

### Infrastructure
- Spring Boot project setup with H2 file-based database.
- MPL 2.0 licensing applied across all source files.
- Brand assets including school logo and hero section styling.
- Basic CI/CD workflow with automated deployment scripts.

---

### Why CalVer instead of SemVer

SemVer's version numbers exist to answer "is it safe to upgrade," which matters when other developers depend on your code programmatically. SafePoint doesn't have that kind of consumer — it's deployed directly to schools, one instance per institution, updated on a rolling basis by a single maintainer. What matters here is "how current is this, and what's actually running at a given school," which a date answers more directly than a major/minor/patch classification does. A genuine breaking rewrite (e.g. the in-progress Svelte/Firebase version) is tracked by product name (**Vecchia** vs. **Nuova**) rather than a version-number jump.
