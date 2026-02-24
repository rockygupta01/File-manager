# Privacy Policy — PrivaFiles File Manager

**Effective Date:** February 24, 2026

## Overview

PrivaFiles is a **100% offline, privacy-first** Android file manager. We do not collect, transmit, or share any personal data.

## Data Collection

**We collect NO data.** Specifically:
- ❌ No analytics or crash reporting to external servers
- ❌ No advertising SDKs
- ❌ No cloud sync
- ❌ No user accounts
- ❌ No network access (INTERNET permission is explicitly blocked)

## Permissions Used

| Permission | Purpose |
|-----------|---------|
| `MANAGE_EXTERNAL_STORAGE` | Browse and manage all files on device storage |
| `READ_MEDIA_IMAGES/VIDEO/AUDIO` | Read media files for viewing and management |
| `ACCESS_WIFI_STATE` | Detect local IP for LAN file transfer (no internet) |
| `USE_BIOMETRIC` | Optional PIN/biometric app lock |
| `POST_NOTIFICATIONS` | Notify when automation tasks (backup/cleanup) complete |
| `FOREGROUND_SERVICE` | Run background automation tasks |

All operations are performed **entirely on-device**. LAN Transfer only serves files over the local network (same WiFi), never to the internet.

## Data Storage

- App settings stored in `EncryptedSharedPreferences` (AES-256, on-device only)
- Encrypted vault files stored in app-private directory
- Crash logs (if any) stored locally in `files/logs/` — never uploaded

## Contact

For questions: https://github.com/rockygupta01/File-manager/issues
