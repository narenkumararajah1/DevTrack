# Development Rules

## General Principles

- Always write production-quality code.
- Prioritize readability and maintainability.
- Keep the codebase clean and organized.
- Never duplicate code when a reusable solution exists.
- Follow Kotlin coding conventions.

---

## Architecture

- Follow the MVVM architecture.
- Keep business logic out of Composables.
- Use the Repository pattern for all data operations.
- Separate UI, business logic, and data access.
- Every screen should have its own ViewModel.

---

## UI & UX

- Use Material 3.
- Build reusable UI components.
- Support light and dark mode.
- Keep the interface modern, clean, and responsive.
- Ensure accessibility where practical.

---

## Firebase

- Authenticate users with Firebase Authentication.
- Store application data in Cloud Firestore.
- Store media files in Firebase Storage.
- Validate all user input before writing to the database.

---

## Code Quality

- Use meaningful class, function, and variable names.
- Keep functions focused on a single responsibility.
- Keep files reasonably small by splitting large features into multiple files.
- Remove dead or unused code.
- Handle errors gracefully.

---

## Project Structure

- Organize packages by feature where appropriate.
- Reuse existing components before creating new ones.
- Avoid unnecessary dependencies.

---

## Git

- Make small, meaningful commits.
- Write clear commit messages.
- Keep the main branch stable.

---

## Documentation

- Update TASKS.md whenever a task is completed.
- Keep PROJECT.md aligned with major feature changes.
- Keep README.md updated as the project evolves.

---

## Claude Code Instructions

- Modify the minimum number of files required.
- Preserve the existing architecture.
- Do not refactor unrelated code.
- Explain important design decisions when introducing new architecture.
- Ask for clarification only if a requirement is genuinely ambiguous.