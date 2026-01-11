# Gamifizierter Tagesplaner

A **gamified daily planner for Android** that helps users structure
their day and stay motivated by turning task completion into visible
progress and rewards.

This app lets the user create a ToDo list for one day at a time. Users
create a daily task list, complete tasks, earn points, and gradually
fill a **virtual bookshelf** with various items by spending those
points.

## Core Idea

This app makes progress **visible and tangible**:

-   Tasks completion gives the user points
-   Points can be spent on **books, decorations and plants**
-   The bookshelf visually grows over time

## Features

### Daily Task Planning

-   Create tasks for **today**
-   Assign title and **priority**
-   Optionally include a description, duration and reminders
-   Each task has a specific amount of points that is calculated
    depending on priority and duration to fit the in‑game economics
-   The task is then represented with a clear visual UI

### Gamification System

-   Points are rewarded based on task priority and duration
-   A progress bar shows the current progress in task completion
-   Total points are tracked persistently per user
-   Users are only rewarded for finished tasks, not punished for
    unfinished tasks
-   Unfinished tasks simply stay in the list for the next day
-   At the end of a day the user sees a screen showing statistics of the
    finished day

### Virtual Bookshelf and Shop

-   Users can spend points in an in‑app shop
-   Points can be used to buy books, decorations and plants
-   The bookshelf fills visually over time
-   Items stay unlocked permanently

### Smart Notifications

-   Daily reminder to create the task list
-   Task‑specific reminder notifications
-   Notifications when a task start time is reached
-   Notification permissions are handled cleanly and only once

### Thoughtful UI/UX

-   Book‑inspired task UI fitting the overall theme
-   Bookmarks indicate a task's priority
-   Clear visualization of task information and state
-   Subtle animations and transitions
-   Tasks show only essential information by default and can be expanded
    manually

## Tech Stack

### Android

-   **Kotlin**
-   **Jetpack Compose** (Material 3)
-   **StateFlow / Coroutines**
-   **MVVM architecture**

### Backend & Persistence

-   **Firebase Authentication**
-   **Firebase Firestore**
-   User‑specific task and progress storage
-   Cloud‑synced across devices

### System Integration

-   Android **AlarmManager**
-   **Notification Channels**
-   Boot‑safe reminders via `BroadcastReceiver`

## Architecture Overview

    UI (Jetpack Compose)
    │
    ├── ViewModels (StateFlow)
    │   ├── Task logic
    │   ├── Points and progress
    │   └── Shop / bookshelf state
    │
    ├── Repository layer
    │   ├── Firestore access
    │   └── User persistence
    │
    └── System services
        ├── Notifications
        └── Alarms / reminders

## Getting Started

### Requirements

-   Android Studio (Giraffe or newer)
-   Android SDK 34+
-   Firebase project with Authentication and Firestore enabled

### Setup

1.  Clone the repository
2.  Open the project in Android Studio
3.  Add your `google-services.json`
4.  Sync Gradle
5.  Run on emulator or device (Android 10+ recommended)

## Future Ideas

-   Pomodoro timer
-   Achievements
-   Level system based on total gathered points
-   Multiple bookshelf themes
-   Interactable books
-   Weekly summaries

## Author

**Marcel Weyerer**\
Master's student
