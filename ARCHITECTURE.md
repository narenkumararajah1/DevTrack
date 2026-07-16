# DevTrack Architecture

## Architecture Pattern

DevTrack follows the MVVM (Model-View-ViewModel) architecture combined with the Repository pattern.

---

## High-Level Structure

UI (Jetpack Compose)
↓
ViewModel
↓
Repository
↓
Firebase
↓
Firestore / Authentication / Storage

---

## Responsibilities

### UI

- Displays information.
- Handles user interaction.
- Observes ViewModel state.
- Contains no business logic.

### ViewModel

- Manages UI state.
- Validates user actions.
- Calls repositories.
- Exposes StateFlow to the UI.

### Repository

- Single source of truth.
- Communicates with Firebase.
- Returns clean models to ViewModels.

### Firebase

- Authentication
- Cloud Firestore
- Cloud Storage

---

## Navigation

Navigation Compose will manage all screen navigation.

Each screen owns its own ViewModel.

---

## Folder Structure

app/
├── data/
├── domain/
├── navigation/
├── ui/
│   ├── components/
│   ├── screens/
│   └── theme/
├── viewmodel/
└── util/

---

## Design Principles

- Separation of Concerns
- Single Responsibility Principle
- Reusable Components
- Scalable Architecture
- Testable Code