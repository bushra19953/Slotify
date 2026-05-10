# Slotify

[📸 **View Project Screenshots (PDF)**](./docs/Project_Screenshots.pdf)

> An Educational Institution Management App built to streamline class schedules, coordinate makeup classes, and keep everyone updated with academic events.

---

##  About The Project

Slotify is a comprehensive Android application designed specifically for educational institutions. It bridges the communication gap between Students, Teachers, and Class Representatives by providing a centralized platform for schedule management and real-time academic updates.

### Key Highlights
- **Role-Based Access Control:** Secure authentication with email domain validation for Students, Teachers, and Class Reps.
- **Real-Time Sync:** Instant updates across all devices using Firebase.
- **Modern UI/UX:** Clean, intuitive interface following Material Design guidelines with a seamless bottom navigation experience.

---

##  Features by Role

### For Students
- **Daily Schedules:** View daily class schedules with real-time status tracking.
- **Instant Notifications:** Receive push alerts for makeup classes and upcoming academic events.
- **Weekly Timetable:** Access a color-coded weekly timetable at a glance.
- **Academic Tracking:** Track attendance and personal class statistics.

### For Teachers
- **Timetable Management:** Manage weekly timetables including subject, time, and venue details.
- **Event Broadcasting:** Create and broadcast makeup classes and academic events.
- **Instant Alerts:** Notify students instantly when schedules or venues change.
- **Dashboard:** View teaching statistics and manage professional profiles.

###  For Class Representatives (CRs)
- **Schedule Requests:** Submit classroom change and time slot modification requests.
- **Class Management:** Manage class swaps and cancellations with proper justification.
- **Alert System:** Create and categorize class alerts (Urgent / Info / Reminder).
- **Task Tracking:** Track pending tasks and efficiently coordinate with faculty members.

---

##  Technologies Used

- **Language:** [Kotlin](https://kotlinlang.org/) & XML
- **Architecture:** MVVM (Model-View-ViewModel)
- **Backend/Database:** Firebase Authentication & Cloud Firestore
- **Asynchronous Programming:** Coroutines & LiveData
- **UI Components:** Material Design Components, RecyclerView with Custom Adapters

---

##  Getting Started

### Prerequisites
- Android Studio (Latest version recommended)
- JDK 17 or higher
- Android SDK 
- A Firebase project setup

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/Slotify.git
   ```

2. **Open in Android Studio:**
   - Open Android Studio and select `Open an existing project`.
   - Navigate to the cloned directory and select it.

3. **Firebase Setup:**
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project or use an existing one.
   - Register your Android app with the project's package name.
   - Download the `google-services.json` file.
   - Place the `google-services.json` file inside the `app/` directory of the project.
   - Enable **Authentication** (Email/Password) and **Firestore Database** in your Firebase project.

4. **Build and Run:**
   - Sync the project with Gradle files.
   - Select an emulator or a physical device.
   - Click the **Run** button.

---

##  Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
