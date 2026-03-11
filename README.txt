Plant Diary

Android application for tracking plants and care schedules.

Stack:
- Kotlin
- XML layouts
- MVVM
- Navigation Component
- Room
- Data Binding

Key features:
- CRUD for plants
- CRUD for care records
- Search by plant name
- Filter by plant type
- Watering list by date
- Planting list by date
- Built-in help screen
- English and Russian localization

Build notes:
- Requires Android SDK in local environment.
- Java 21 and Gradle are already configured on this machine.
- Local debug build: `gradlew.bat :app:assembleDebug`
- Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`

CI/CD:
- GitHub Actions workflow: `.github/workflows/android-apk.yml`
- On `pull_request`, `workflow_dispatch`, and `push` the project builds a debug APK and uploads it as an artifact.
- On tag push `v*`, the workflow also publishes `app-debug.apk` into the GitHub Release for that tag.

Course references:
- https://github.com/ivanshchitov/android-kotlin-course
- https://github.com/ivanshchitov/android-kotlin-course/blob/master/requirements.md
