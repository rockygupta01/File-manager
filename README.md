# 🔒 PrivaFiles — Privacy-First File Manager

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-orange" />
  <img src="https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen" />
  <img src="https://img.shields.io/badge/License-MIT-blue" />
</p>

> **100% offline. Zero ads. Zero analytics. Zero cloud. Your files, your device, your privacy.**

PrivaFiles is a production-grade Android file manager built with **Jetpack Compose** and **Clean Architecture**. It is designed from the ground up to be fully offline — no internet permission is granted (`INTERNET` permission is explicitly removed from the manifest), no data ever leaves your device.

---

## 📱 Screenshots

> _Coming soon — install from source and run on your device._

---

## ✨ Features

### 📁 Core File Management
| Feature | Details |
|---|---|
| **Browse & Navigate** | Full external storage browsing with breadcrumb bar and smooth animations |
| **Create** | Files and folders with a single tap |
| **Copy / Cut / Paste** | In-memory clipboard with live progress bar |
| **Rename** | Single-tap rename dialog with pre-filled current name |
| **Delete** | Confirmation dialog before permanent deletion |
| **Multi-select** | Long-press or tap in selection mode; Select All supported |
| **Sort** | By name, size, date, or type — persisted across sessions |
| **Grid / List View** | Toggle with animated crossfade |
| **Hidden Files** | Show/hide dotfiles with a toggle |
| **Bookmarks** | Mark favourite directories |
| **Recent Files** | Auto-logged on file open (local Room DB) |
| **Quick Preview** | Long-press images/video to peek before opening |
| **Shimmer Skeleton** | Smooth loading placeholder instead of a spinner |

### 🗜️ Archive & Compression
- Create **ZIP** archives from selected files
- **Extract** ZIP archives with optional password
- Password-protected archives via **zip4j**

### 👁️ Built-in File Viewer
| Type | Viewer |
|---|---|
| **Images** | Full-screen Coil 3 viewer |
| **Video & Audio** | Media3 / ExoPlayer with playback controls |
| **PDF** | On-device `PdfRenderer` — all pages, no external service |
| **Text & Code** | Monospace viewer with scroll support |

### 💾 Storage Analyzer
- **Overview** — total / used / free space with visual progress bar
- **Large Files** scanner
- **Duplicate File** finder
- **Junk File** cleaner

### 🔐 Security & Privacy Vault
| Feature | Implementation |
|---|---|
| **PIN Lock** | SHA-256 hashed + salt; stored in `EncryptedSharedPreferences` |
| **Biometric Unlock** | Fingerprint / Face via `BiometricPrompt` |
| **Lockout Protection** | Exponential backoff (5 attempts → 30s → 60s → 5min) |
| **File Encryption** | AES-256-GCM via Android Keystore |
| **Secure Delete** | Overwrite with random bytes before deletion |
| **App Lock Screen** | PIN enforced on every app cold start |

### 💽 Local Backup & Restore
- One-tap **Create Backup** — ZIPs Room DB to app-private storage
- **Backup list** — view all saved backups
- **Auto-retention** — keeps only the last 5 backups
- 100% local, never uploaded anywhere

### 🔎 Search
- **Instant search** with type filters (Images / Videos / Audio / Documents)
- **Recent search history** — stored locally
- **Content & Metadata** filter toggles
- **Background FTS indexer** — `SearchIndexWorker` scans your storage on startup and builds a full-text search index in Room

### 🤖 Automation
- **Auto-Backup** — `PeriodicWorkRequest` writes backup to local storage on schedule
- **Auto-Cleanup** — Configurable cleanup rules run via WorkManager
- **File Organizer** — Rule-based file sorting (source dir → dest dir by type/schedule)

### 📡 LAN File Transfer
- **HTTP file server** (NanoHTTPD) accessible on your local Wi-Fi network
- No internet — LAN only, no data sent to external servers

### 📦 App Manager
- Installed app list with metadata
- **APK extraction** to storage
- **Batch uninstall** support

### 🖥️ Root & Dev Tools
- **Root terminal** with Shell command execution (rooted devices)
- **SQLite viewer**, JSON/XML viewer
- **Dev tools screen** for power users

---

## 🏛️ Architecture

PrivaFiles uses **MVVM + Clean Architecture** with a strict multi-module Gradle setup:

```
FileManager/
├── app/                        # Entry point, navigation host, crash handler
├── core/
│   ├── common/                 # Extensions, FileUtils, MIME helpers, Result<T>
│   ├── data/                   # Repositories (LocalFileRepository, LocalStorageRepository)
│   ├── database/               # Room DB, all Entities, DAOs, Hilt module
│   ├── domain/                 # Use cases, domain models (FileItem, SortConfig…)
│   ├── security/               # AppLockManager, EncryptionManager (AES-256-GCM)
│   └── ui/                     # Material 3 theme, shared Composables
└── feature/
    ├── filemanager/            # Browse, CRUD, clipboard, sort, grid/list
    ├── storage/                # Analyzer, large files, duplicates, junk
    ├── security/               # App lock screen, vault UI, backup/restore
    ├── search/                 # Search screen + SearchIndexWorker (FTS)
    ├── archive/                # Compress & extract (zip4j)
    ├── viewer/                 # Image, video, audio, PDF, text viewer
    ├── appmanager/             # Installed apps, APK extract, uninstall
    ├── automation/             # Auto-backup & cleanup WorkManager workers
    ├── lan/                    # LocalFileServer (NanoHTTPD) + UI
    ├── root/                   # RootShell + UI
    └── devtools/               # SQLite viewer, terminal, JSON viewer
```

### Data Flow

```
UI (Compose) ──► ViewModel ──► Use Cases ──► Repository
                                               ├── Room DB
                                               ├── DataStore (Preferences)
                                               └── File System (SAF / java.nio)
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | **Kotlin** | 2.1.0 |
| UI | **Jetpack Compose** + Material 3 | BOM 2024.12.01 |
| Architecture | **MVVM + Clean Architecture** | — |
| DI | **Hilt** | 2.53.1 |
| Navigation | **Compose Navigation** | 2.8.5 |
| Coroutines | **Kotlin Coroutines + Flow** | 1.9.0 |
| Database | **Room** | 2.6.1 |
| Preferences | **DataStore** | 1.1.1 |
| Image Loading | **Coil 3** | 3.0.4 |
| Media Playback | **Media3 / ExoPlayer** | 1.2.0 |
| Archive | **zip4j** | 2.11.5 |
| PDF | Android **PdfRenderer** (zero deps) | — |
| Encryption | **Jetpack Security** + Android Keystore | 1.1.0-alpha06 |
| Biometrics | **BiometricPrompt** | 1.2.0-alpha05 |
| Background | **WorkManager** | 2.9.1 |
| LAN Server | **NanoHTTPD** | 2.3.1 |
| Build | **Gradle KTS** + Version Catalog | AGP 8.7.3 |

### What's NOT included (by design)
- ❌ Firebase (any module)
- ❌ Google Play Services
- ❌ OkHttp / Retrofit / Ktor
- ❌ Any analytics or ad SDK
- ❌ Any crash-reporting SDK that sends data externally

---

## 🔒 Privacy & Security

| Principle | Enforcement |
|---|---|
| **No internet** | `INTERNET` permission explicitly removed via `tools:node="remove"` |
| **No Google Backup** | `android:allowBackup="false"` |
| **No cleartext traffic** | `android:usesCleartextTraffic="false"` |
| **No cloud dependencies** | Zero SDKs with remote endpoints |
| **Encrypted preferences** | `EncryptedSharedPreferences` (AES-256) |
| **On-device crash logs** | Written to `files/logs/` — never uploaded |
| **Local backups only** | Zipped to `files/backups/` — no cloud upload |

---

## 🗄️ Database Schema

```mermaid
erDiagram
    RECENT_FILES   { string path PK; long lastAccessed; string mimeType; long size }
    BOOKMARKS      { int id PK; string path; string label; long createdAt }
    FILE_TAGS      { int id PK; string filePath; string tag }
    VAULT_FILES    { int id PK; string originalPath; string encryptedPath; string iv }
    AUTOMATION_RULES { int id PK; string sourceDir; string destDir; string schedule; boolean enabled }
    SEARCH_INDEX   { int id PK; string filePath; string fileName; string mimeType; long size }
    SEARCH_HISTORY { int id PK; string query; long timestamp }
    APP_LOCK_CONFIG { int id PK; string lockType; string hashedCredential; boolean biometricEnabled }
    CRASH_LOGS     { int id PK; long timestamp; string threadName; string stackTrace }
```

---

## 📋 Permissions

| Permission | Purpose |
|---|---|
| `MANAGE_EXTERNAL_STORAGE` | Full file system access (Android 11+) |
| `READ_MEDIA_IMAGES/VIDEO/AUDIO` | Media access (Android 13+) |
| `READ/WRITE_EXTERNAL_STORAGE` | Legacy storage (Android ≤ 12/9) |
| `ACCESS_WIFI_STATE` | Detect local IP for LAN transfer |
| `FOREGROUND_SERVICE` | Background file operations & automation |
| `POST_NOTIFICATIONS` | Operation progress & automation alerts |
| `RECEIVE_BOOT_COMPLETED` | Re-schedule WorkManager on device restart |
| `USE_BIOMETRIC` | Biometric app unlock |

> **Note:** `INTERNET` is explicitly removed. LAN transfer uses local socket binding only — no internet traffic is possible.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Ladybug | 2024.2.1 or later
- **JDK 17**
- **Android SDK** with API 35 installed

### Clone & Build

```bash
git clone https://github.com/rockygupta01/File-manager.git
cd File-manager

# Build debug APK
./gradlew assembleDebug

# Run on connected device
./gradlew installDebug
```

### Keystore Setup (Release Build)

Create `local.properties` in the project root:

```properties
STORE_FILE=/absolute/path/to/your/keystore.jks
STORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

Then build the release APK:

```bash
./gradlew assembleRelease
```

See [KEYSTORE_SETUP.md](KEYSTORE_SETUP.md) for detailed keystore generation instructions.

---

## 🧪 Testing

```bash
# All unit tests
./gradlew test

# Instrumented tests on device/emulator
./gradlew connectedAndroidTest

# Verify no banned dependencies (analytics, ads, firebase)
./gradlew app:dependencies --configuration releaseRuntimeClasspath \
  | grep -iE "firebase|analytics|ads|crashlytics|sentry" \
  && echo "FAIL: Banned dep found!" || echo "PASS: Clean"
```

---

## 📁 Project Structure — Module Graph

```
app
 ├── core:common
 ├── core:data          ── core:domain, core:database
 ├── core:database
 ├── core:domain
 ├── core:security
 ├── core:ui
 ├── feature:filemanager ── core:common, core:domain, core:database, core:ui
 ├── feature:storage
 ├── feature:security   ── core:security
 ├── feature:search     ── core:database (SearchIndexWorker)
 ├── feature:archive
 ├── feature:viewer
 ├── feature:appmanager
 ├── feature:automation
 ├── feature:lan
 ├── feature:root
 └── feature:devtools
```

---

## 🗺️ Roadmap

| Phase | Status | Description |
|---|---|---|
| Core File Management | ✅ Done | Browse, CRUD, clipboard, sort, views |
| Storage Analyzer | ✅ Done | Overview, large files, duplicates, junk |
| Archive & Compression | ✅ Done | ZIP create/extract, password support |
| Built-in Viewers | ✅ Done | Image, video/audio, PDF, text/code |
| App Manager | ✅ Done | App list, APK extract, uninstall |
| Security & Vault | ✅ Done | PIN/biometric lock, encryption, backup |
| Search & FTS Indexer | ✅ Done | Filter search, history, background indexer |
| Automation Workers | ✅ Done | Auto-backup, cleanup rules |
| LAN Transfer | ✅ Done | Local HTTP file server |
| Root Features | ✅ Done | Shell access, system file editing |
| Dev Tools | ✅ Done | SQLite viewer, terminal, JSON/XML |
| On-device OCR | 🔲 Planned | ML Kit text recognition (bundled model) |
| Performance Polish | 🔲 Planned | Paging 3, R8 optimization, benchmark |

---

## 📄 License

```
MIT License

Copyright (c) 2026 Rocky Kumar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🔗 Links

- [Privacy Policy](PRIVACY_POLICY.md)
- [Keystore Setup Guide](KEYSTORE_SETUP.md)
- [Report an Issue](https://github.com/rockygupta01/File-manager/issues)

---

<p align="center">Made with ❤️ and a zero-phone-home policy.</p>
