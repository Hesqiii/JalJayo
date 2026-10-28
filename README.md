# 🌙 JalJayo

> **A science-backed circadian sleep intervention app that refuses to let overthinking ruin your night.**  
> Built with zero-friction, zero-login, and a 100% offline-first architecture.  
> Developed for **Manajemen Inovasi** — Group 2, Offering D6, Faculty of Engineering, Universitas Negeri Malang.

[![Release](https://img.shields.io/badge/Release-v1.0.0-blue?logo=github)](https://github.com/Hesqiii/JalJayo/releases/tag/v1.0.0)
[![Platform](https://img.shields.io/badge/Platform-Android%20Native-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Audio](https://img.shields.io/badge/Audio-AndroidX%20Media3%20ExoPlayer-FF6F00)](https://developer.android.com/media/media3)
[![Tests](https://img.shields.io/badge/Tests-34%20Passed%20(100%25)-success)](https://github.com/Hesqiii/JalJayo/actions)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline%20(No%20Internet)-22C55E)](#)
[![License](https://img.shields.io/badge/License-MIT-gray)](#)

---

## What is JalJayo?

Most sleep apps fail because they demand high-effort journaling, enforce rigid 5 AM alarms, flood tired eyes with bright late-night screens, and lock basic sounds behind paid cloud subscriptions.

**JalJayo holds a hard floor on cognitive load and then gets out of the way.**

Based on research across 27 engineering students facing chronic sleep deficits (<6 hours) and bed-time *racing thoughts*, JalJayo aligns user behavior with biological circadian rhythms:
* **Day:** Builds healthy sleep pressure with 35-second expert micro-video lessons and actionable bite-sized habit cards.
* **Transition:** Alerts users 45 minutes before sleep with a native lockscreen bedtime anchor and 1-tap relaxation trigger.
* **Night:** Shuts down all visual stimulation into an OLED pitch-black screen-off audio player powered by a cubic exponential fade-out timer.

---

## ✨ New in v1.0.0

* **⚡ Empathetic Bedtime-First Onboarding:** Replaces forceful wake-time demands with natural bedtime inquiries (*"Biasanya Anda tidur jam berapa di malam hari?"*), interactive hour/minute steppers, and evidence-based sleep window recommendations (22:00 – 23:30).
* **🎯 4 Clinical CBT-I Complaint Presets:** Instantly calibrates sleep soundscapes for racing thoughts (Theta 6Hz), environmental noise (Brown Noise), screen fatigue (Rain Sound), and irregular schedules (Ocean Waves).
* **🔥 Duolingo-Style Streak Gamification:** Daily consistency counter with tactical 3D celebrations, backed by a 24-hour grace period buffer to mitigate the psychological *"What-the-hell effect"*.
* **🎬 Offline Video Player with Sleep Educator:** Vertical micro-video lessons featuring **Vishal Dasani** (*Certified Sleep Coach & Educator*), running smoothly on hardware-accelerated local rendering.
* **📚 Open Knowledge Catalog:** Immediate, unrestricted access to all 4 chapters and 14 CBT-I habit modules without arbitrary daily locks.
* **🛡️ Zero-Network Sandbox Security:** Strictly zero `android.permission.INTERNET` requested. Zero tracking, zero telemetry, 100% on-device private DataStore.

---

## 🏛️ Three Circadian Pillars

| Phase | Window | Focus | Mechanism |
|---|---|---|---|
| **☀️ Siang** | 06:00 – 20:00 | Active Daylight Habits | 1 Daily habit card (≤80 words) + 35s vertical video + dynamic caffeine curfew notification (8h before sleep). |
| **🔔 Transisi** | Bedtime - 45m | Bedtime Anchor Prompt | Exact alarm via `AlarmManager.setAlarmClock`, lockscreen wake, and 1-tap `[▶ Mulai Relaksasi]` action. |
| **🌙 Malam** | 20:00 – 05:59 | Zero-Visual Wind-Down | Complete visual lockout. Screen-off audio loop with Media3 ExoPlayer and cubic exponential timer fade. |

---

## 🏗️ Architecture & Engineering

```
JalJayo/
├── core/                        # Pure Kotlin Domain & Business Logic
│   ├── AppClock.kt              # Circadian time provider & testable clock abstractions
│   ├── ComplaintPreset.kt       # Diagnostic mapping of 4 clinical sleep complaints
│   ├── SleepSchedule.kt         # 7.5h 5-cycle sleep math & caffeine curfew engine
│   └── StreakTracker.kt         # Loss-aversion streak evaluator & 24h grace buffer
└── app/                         # Android Application Layer
    ├── audio/PlaybackService.kt # Foreground MediaSessionService (Screen-off playback)
    ├── data/                    # Offline ContentRepository & DataStore SettingsRepository
    ├── notification/            # BedtimeScheduler & BedtimeReceiver (Exact RTC Wake)
    └── ui/                      # Jetpack Compose Design System (Material 3 + Duolingo Tactile)
```

- **Clean Architecture & Separation of Concerns:** Domain layer (`:core`) has zero Android dependencies and is 100% unit-testable on standard JVMs.
- **Resilient Background Execution:** `setAlarmClock` ensures notifications break through vendor OEM battery freezers without requiring elevated Android 14 schedule permissions.
- **Reliable Testing Suite:** 34 comprehensive tests covering unit math, functional services, and complete user journey state machines.

---

## 🚀 Quick Start & Build

### Prerequisites
* Android Studio Ladybug / Meerkat (or IntelliJ IDEA with Android plugin)
* JDK 11 or later
* Android SDK 35 (minSdk 28)

### Clone & Compile
```bash
# Clone the repository
git clone https://github.com/Hesqiii/JalJayo.git
cd JalJayo

# Run comprehensive test suite (34 tests)
./gradlew test

# Build debug APK bundle
./gradlew assembleDebug
```

Compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 Downloads & Releases

Direct binary releases and standalone APKs are published on GitHub:

👉 **[Download JalJayo v1.0.0 APK](https://github.com/Hesqiii/JalJayo/releases/tag/v1.0.0)**

---

## 🔬 Scientific Foundations

1. **Cognitive Behavioral Therapy for Insomnia (CBT-I):** First-line non-pharmacological gold standard for chronic sleep disorders (*Morin et al., 2006*).
2. **Fogg Behavior Model ($B = MAP$):** Maximizing ability by reducing late-night cognitive friction to near-zero (*Fogg, 2009*).
3. **Prospect Theory & Loss Aversion:** Leveraging daily streak protection to sustain long-term habit momentum (*Kahneman & Tversky, 1979*).
4. **Circadian Sleep Architecture:** 90-minute ultradian sleep cycle optimization (*Walker, 2017*).
5. **Adenosine & Caffeine Pharmacokinetics:** 8-hour pre-sleep caffeine curfew to preserve NREM Stage 3 slow-wave sleep (*Drake et al., 2013*).

---

## 👥 Development Team

**Kelompok 2 — Manajemen Inovasi (Offering D6)**  
*Fakultas Teknik, Universitas Negeri Malang*

* **Pandhu Prayogo** — *Lead Developer & Software Architect*
* **Team Members & Contributors** — *Research, UI/UX Design, & Content Strategy*

---

## 📄 License

This project is licensed under the **MIT License** — free and open source for educational and health innovation purposes.
