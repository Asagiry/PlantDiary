# Plant Diary Implementation

## Goal
Собрать Android-приложение `Plant Diary` под требования курса и задания: Kotlin, XML, MVVM, Navigation Component, Room, Data Binding, локализация, CRUD для растений и записей ухода, календарные списки, поиск и фильтрация.

## Tasks
- [x] Зафиксировать требования задания и курса в `plant-diary-task.md` -> Verify: файл есть в корне и содержит ТЗ.
- [x] Привязать текущую папку к Git-репозиторию `Asagiry/PlantDiary` -> Verify: `git status` работает, `origin` указывает на репозиторий.
- [x] Создать Android-проект `Plant Diary` с `applicationId` `com.asagiry.plantdiary` -> Verify: есть `settings.gradle.kts`, `app/build.gradle.kts`, `AndroidManifest.xml`.
- [x] Настроить Navigation, Data Binding, Room и базовую архитектуру MVVM -> Verify: есть nav graph, database, dao, repository, viewmodel.
- [x] Реализовать CRUD растений и поиск/фильтрацию -> Verify: есть список растений, форма растения, dao-методы поиска и фильтрации.
- [x] Реализовать CRUD записей ухода и экран расписания по дате -> Verify: есть список ухода, форма ухода, экран полива/высаживания на выбранный день.
- [x] Добавить помощь, локализацию и валидацию пользовательского ввода -> Verify: есть help screen, `values/strings.xml`, `values-ru/strings.xml`, сообщения об ошибках.
- [x] Попробовать сгенерировать Gradle wrapper и проверить сборку настолько, насколько позволяет окружение -> Verify: `gradlew` создан, результат проверки зафиксирован.

## Done When
- [x] В проекте есть полный Android-каркас под курс.
- [x] Все обязательные сценарии задания реализованы.
- [x] Ограничения и оставшиеся риски явно описаны.

## Verification
- [x] `gradlew.bat help`
- [x] `gradlew.bat :app:assembleDebug`
- [x] GitHub Actions workflow `.github/workflows/android-apk.yml` added for APK build and release-by-tag.

## Notes
- Локальная сборка подтверждена после установки Android SDK command-line tools, `platforms;android-35` и `build-tools;35.0.0`.
- `local.properties` не коммитится и остается локальным файлом окружения.
