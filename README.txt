Дневник растений

Android-приложение для учета растений и расписания ухода.

Стек:
- Kotlin
- XML-разметка
- MVVM
- Navigation Component
- Room
- Data Binding

Основные возможности:
- CRUD для растений
- CRUD для записей об уходе
- Поиск по названию растения
- Фильтрация по типу растения
- Список полива по дате
- Список высадки по дате
- Встроенный экран помощи
- Русская локализация по умолчанию и английская локализация как дополнительная

Сборка:
- Нужен локально установленный Android SDK.
- Java 21 и Gradle уже настроены на этом компьютере.
- Локальная debug-сборка: `gradlew.bat :app:assembleDebug`
- Debug APK: `app/build/renamed-apk/PlantDiary-debug.apk`

CI/CD:
- Сценарий GitHub Actions: `.github/workflows/android-apk.yml`
- На `pull_request`, `workflow_dispatch` и `push` проект собирает debug APK и загружает его как артефакт.
- При пуше тега `v*` workflow дополнительно публикует `PlantDiary-debug.apk` в GitHub Release для этого тега.

Материалы курса:
- https://github.com/ivanshchitov/android-kotlin-course
- https://github.com/ivanshchitov/android-kotlin-course/blob/master/requirements.md

Файл с простым объяснением проекта:
- `PROJECT_GUIDE.md`
