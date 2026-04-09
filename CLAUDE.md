# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Architecture

**CarU** is an Android food-vendor marketplace app built with Jetpack Compose.

### Navigation Flow
```
start → login → (future: home)
     ↘ register → create_user_account → (future: home)
```

- Navigation is handled via `NavHost` in `MainActivity.kt`
- Routes are hardcoded strings ("start", "login", "register", "create_user_account")
- Each screen is a standalone `@Composable` function

### Screens
| Screen | File | Purpose |
|--------|------|---------|
| `CarUAppStartScreen` | `MainActivity.kt` | Landing page with login/create account buttons |
| `LoginScreen` | `LoginScreen.kt` | Email/password login |
| `RegisterScreen` | `RegisterScreen.kt` | Role selection (buyer vs vendor) |
| `CreateUserAccountScreen` | `navegacion_snippet.kt` | User registration form |

### Theme System
- `CarUTheme` in `ui/theme/Theme.kt` wraps Material 3 with optional dynamic colors (Android 12+)
- Dark/light state is managed locally in `MainActivity` and passed down via `isDarkTheme`/`onToggleTheme`
- **Custom fonts**: `CaruFontFamily` and `CaruTitleFontFamily` defined in `AppConstants.kt` using `R.font.caru_font` and `R.font.caru_title`

### Shared Constants
- `RedButtonColor = Color(0xFFE53935)` and `TextColorSecondary` are defined in both `AppConstants.kt` and `ui/theme/Color.kt` — these should be consolidated

### Key Dependencies
- Jetpack Compose BOM 2024.09.00
- Material 3
- Navigation Compose 2.7.7
- Material Icons Extended

### Test Structure
- Unit tests: `app/src/test/java/com/empresa/caru/`
- Instrumented tests: `app/src/androidTest/java/com/empresa/caru/`
