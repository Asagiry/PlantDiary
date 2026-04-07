# Дневник растений: как работает проект

Этот файл сделан как простое объяснение проекта человеческим языком.
Его цель не в красивой документации, а в том, чтобы можно было быстро понять логику приложения и потом спокойно объяснить ее своими словами.

## 1. Что это за приложение

Приложение хранит растения и записи по уходу за ними.
У пользователя есть:

- список растений
- список записей об уходе
- экран расписания на выбранную дату
- экран помощи

По заданию нужно было уметь:

- добавлять, редактировать и удалять растения
- добавлять, редактировать и удалять записи ухода
- искать растения по названию
- фильтровать растения по типу
- показывать полив на сегодня, завтра и любую дату
- показывать высадку на сегодня, завтра и любую дату

## 2. Какой стек используется

Проект написан на:

- `Kotlin`
- `XML` для интерфейса
- `MVVM`
- `Room`
- `Navigation Component`
- `Data Binding`

Почему так:

- `Room` нужен, чтобы хранить данные локально на устройстве
- `ViewModel` нужен, чтобы экран не терял состояние при повороте и пересоздании
- `Navigation Component` упрощает переходы между экранами
- `Data Binding` уменьшает количество ручного кода в формах

## 3. Главные сущности

В проекте по сути две основные сущности.

### Растение

Файл:
`app/src/main/java/com/asagiry/plantdiary/data/local/entity/Plant.kt`

У растения есть:

- `name` — название
- `description` — описание
- `type` — комнатное или садовое
- `wateringIntervalDays` — интервал полива
- `plantingDate` — дата высадки для садового растения
- `plantingTime` — время высадки для садового растения

### Запись об уходе

Файл:
`app/src/main/java/com/asagiry/plantdiary/data/local/entity/CareRecord.kt`

У записи ухода есть:

- `plantId` — к какому растению относится
- `nextWateringDate` — дата следующего полива
- `plannedPlantingDate` — дата высадки
- `plannedPlantingTime` — время высадки

То есть растения и уход разделены.
Это важно: растение хранит общую информацию, а запись ухода хранит конкретное действие и дату.

## 4. Как хранится база

Файл:
`app/src/main/java/com/asagiry/plantdiary/data/local/PlantDiaryDatabase.kt`

В базе две таблицы:

- `plants`
- `care_records`

Связь такая:

- одна запись ухода принадлежит одному растению
- если растение удалить, связанные записи ухода удаляются каскадно

Для этого в `CareRecord` стоит `ForeignKey`.

## 5. Как идет путь данных

Логика в проекте идет по простой цепочке:

`Fragment -> ViewModel -> Repository -> DAO -> Room`

То есть:

1. Пользователь нажимает кнопку на экране.
2. `Fragment` передает действие во `ViewModel`.
3. `ViewModel` валидирует данные.
4. Потом вызывает `Repository`.
5. `Repository` уже работает с `DAO`.
6. `DAO` читает или пишет в `Room`.

Обратно данные приходят примерно так же, только через `Flow` или `LiveData`.

## 6. Что делает каждый основной экран

## Экран растений

Файлы:

- `ui/plants/PlantsFragment.kt`
- `ui/plants/PlantsViewModel.kt`

Что делает:

- показывает список растений
- позволяет искать по названию
- позволяет фильтровать по типу
- позволяет открыть форму добавления или редактирования
- позволяет удалить растение

Если растений нет, показывается пустое состояние.
Если растения есть, но поиск ничего не нашел, показывается отдельное пустое состояние для поиска.

## Форма растения

Файлы:

- `ui/plants/PlantFormFragment.kt`
- `ui/plants/PlantFormViewModel.kt`

Что делает:

- создает новое растение
- редактирует существующее
- проверяет обязательные поля
- для садового растения открывает поля даты и времени высадки
- для комнатного растения скрывает эти поля

Главная проверка находится во `ViewModel`, в методе `savePlant()`.

## Экран ухода

Файлы:

- `ui/care/CareRecordsFragment.kt`
- `ui/care/CareRecordsViewModel.kt`

Что делает:

- показывает список записей ухода
- открывает форму добавления или редактирования
- удаляет запись ухода

## Форма ухода

Файлы:

- `ui/care/CareFormFragment.kt`
- `ui/care/CareFormViewModel.kt`

Что делает:

- пользователь сначала выбирает растение
- потом задает дату следующего полива
- если растение садовое, можно задать дату и время высадки

Если растение еще не выбрано, форма не должна сохраняться.

## Экран расписания

Файлы:

- `ui/schedule/ScheduleFragment.kt`
- `ui/schedule/ScheduleViewModel.kt`

Что делает:

- выбирает день: сегодня, завтра или вручную
- отдельно показывает список на полив
- отдельно показывает список на высадку

То есть экран не хранит свои данные сам, а просто берет нужные записи ухода на конкретную дату.

## Экран помощи

Файл:
`ui/help/HelpFragment.kt`

Что делает:

- кратко объясняет, как пользоваться приложением
- позволяет переключать язык интерфейса

## 7. Как работает язык

Сейчас есть:

- русский
- английский

При первом запуске открывается экран выбора языка.
Выбранный язык сохраняется в `SharedPreferences`.

Связанные файлы:

- `PlantDiaryApp.kt`
- `data/preferences/AppPreferences.kt`
- `ui/onboarding/LanguageOnboardingFragment.kt`

Смысл такой:

- если язык еще не был выбран, стартует онбординг
- если язык уже сохранен, приложение сразу идет на главный экран

## 8. Как работает навигация

Файлы:

- `ui/MainActivity.kt`
- `res/navigation/nav_graph.xml`

`MainActivity` содержит:

- `Toolbar`
- `FragmentContainerView`
- нижнюю навигацию

Переходы описаны в `nav_graph.xml`.

Главные экраны:

- растения
- уход
- расписание
- помощь

Отдельно есть:

- форма растения
- форма ухода
- экран выбора языка

## 9. Почему используется MVVM

Простое объяснение:

- `Fragment` отвечает за экран и клики
- `ViewModel` отвечает за состояние и логику
- база отвечает за хранение

Это удобно, потому что код не сваливается в один огромный `Activity`.

## 10. Что можно сказать на защите проекта

Короткая версия:

> Я сделал Android-приложение для учета растений. В нем можно хранить карточки растений и отдельные записи по уходу. Данные сохраняются локально через Room. Интерфейс сделан на XML, логика разделена по MVVM: экран, ViewModel и Repository. Есть поиск, фильтр по типу, расписание полива и высадки на выбранную дату, а также переключение языка между русским и английским.

Еще короче:

> Это локальный Android-дневник растений на Kotlin. Я разделил данные на растения и записи ухода, чтобы можно было отдельно хранить карточку растения и отдельно дату следующего действия.

## 11. Если преподаватель спросит “почему так сделал?”

Можно отвечать так:

### Почему две сущности, а не одна?

Потому что растение — это постоянная карточка, а уход — это отдельная запись с датой.
Если все хранить в одной таблице, будет сложнее делать отдельный CRUD ухода и отдельное расписание.

### Почему Room?

Потому что по заданию приложение локальное, и Room для этого подходит лучше всего.
Он дает SQLite, но с нормальными Kotlin-классами и DAO.

### Почему ViewModel?

Чтобы не хранить состояние формы прямо во Fragment и не терять его при пересоздании экрана.

### Почему Navigation Component?

Чтобы не делать переходы вручную через кучу `FragmentTransaction`.

## 12. Что в проекте самое важное

Если совсем мало времени на подготовку, запомни вот это:

1. Есть две основные таблицы: растения и уход.
2. Все данные лежат локально в Room.
3. Экран общается с ViewModel.
4. ViewModel обращается к Repository.
5. Repository работает с DAO.
6. Расписание строится по выбранной дате.
7. Язык выбирается на первом запуске и сохраняется.

## 13. Где что смотреть в коде

Если нужно быстро найти логику:

- запуск приложения: `PlantDiaryApp.kt`
- общая навигация: `ui/MainActivity.kt`
- граф экранов: `res/navigation/nav_graph.xml`
- сущность растения: `data/local/entity/Plant.kt`
- сущность ухода: `data/local/entity/CareRecord.kt`
- база: `data/local/PlantDiaryDatabase.kt`
- работа с данными: `data/repository/PlantDiaryRepository.kt`
- список растений: `ui/plants/PlantsFragment.kt`
- форма растения: `ui/plants/PlantFormFragment.kt`
- форма ухода: `ui/care/CareFormFragment.kt`
- расписание: `ui/schedule/ScheduleFragment.kt`

## 14. Что можно улучшить потом

Если спросят про развитие проекта, можно сказать:

- добавить уведомления о поливе
- добавить фото растения
- добавить сортировку
- добавить экспорт данных
- сделать статистику по уходу

## 15. Главное

Не нужно пытаться пересказывать весь проект по строчкам.
Достаточно понимать:

- какие есть сущности
- как данные попадают в базу
- как экраны получают данные
- как работает расписание
- где используется язык

Если держать в голове эту схему, объяснить проект уже реально.

## 16. Карта файлов проекта

Ниже короткая карта проекта по основным файлам.
Это не список временных файлов, build-артефактов, Gradle Wrapper, бинарников или служебного мусора IDE.
Здесь перечислены именно исходники, ресурсы и конфигурация, которые важны для понимания проекта.

### Корень проекта

- `README.txt` — короткое описание проекта, стека, сборки и CI/CD.
- `PROJECT_GUIDE.md` — подробное объяснение проекта простым языком.
- `settings.gradle.kts` — имя проекта и подключение модуля `app`.
- `build.gradle.kts` — корневой Gradle-файл проекта.
- `gradle.properties` — общие Gradle-настройки.

### Модуль `app`

- `app/build.gradle.kts` — Android-настройки приложения, зависимости, SDK-версии и переименование debug APK.
- `app/proguard-rules.pro` — правила ProGuard/R8 для release-сборки.
- `app/src/main/AndroidManifest.xml` — описание приложения, `Application`, `MainActivity`, back callback и базовые свойства.

### Приложение и общая логика

- `app/src/main/java/com/asagiry/plantdiary/PlantDiaryApp.kt` — точка инициализации приложения, доступ к `Repository` и применение языка.
- `app/src/main/java/com/asagiry/plantdiary/ui/MainActivity.kt` — главный контейнер приложения, toolbar, нижняя навигация, back navigation и стартовый экран.

### Data layer: preferences

- `app/src/main/java/com/asagiry/plantdiary/data/preferences/AppPreferences.kt` — сохранение выбранного языка в `SharedPreferences`.

### Data layer: local database

- `app/src/main/java/com/asagiry/plantdiary/data/local/PlantDiaryDatabase.kt` — Room-база и подключение DAO.
- `app/src/main/java/com/asagiry/plantdiary/data/local/Converters.kt` — конвертеры Room для дат и времени.
- `app/src/main/java/com/asagiry/plantdiary/data/local/entity/Plant.kt` — модель растения.
- `app/src/main/java/com/asagiry/plantdiary/data/local/entity/CareRecord.kt` — модель записи ухода.
- `app/src/main/java/com/asagiry/plantdiary/data/local/entity/PlantType.kt` — enum типа растения: комнатное или садовое.
- `app/src/main/java/com/asagiry/plantdiary/data/local/model/CareRecordWithPlant.kt` — связанная модель: запись ухода плюс растение.
- `app/src/main/java/com/asagiry/plantdiary/data/local/dao/PlantDao.kt` — запросы Room для растений.
- `app/src/main/java/com/asagiry/plantdiary/data/local/dao/CareRecordDao.kt` — запросы Room для ухода и расписания.

### Data layer: repository

- `app/src/main/java/com/asagiry/plantdiary/data/repository/PlantDiaryRepository.kt` — единая точка доступа к данным между `ViewModel` и Room.

### UI: common

- `app/src/main/java/com/asagiry/plantdiary/ui/common/BindingAdapters.kt` — маленький binding adapter для показа и скрытия view.
- `app/src/main/java/com/asagiry/plantdiary/ui/common/DateFormats.kt` — форматирование дат и времени для интерфейса.
- `app/src/main/java/com/asagiry/plantdiary/ui/common/MotionPreferences.kt` — решение, когда уменьшать анимации.
- `app/src/main/java/com/asagiry/plantdiary/ui/common/PickerUtils.kt` — открытие date picker и time picker.
- `app/src/main/java/com/asagiry/plantdiary/ui/common/PlantTypeUi.kt` — вспомогательная UI-логика для отображения типа растения.
- `app/src/main/java/com/asagiry/plantdiary/ui/common/ScreenMotion.kt` — простые анимации появления экранов и списков.

### UI: plants

- `app/src/main/java/com/asagiry/plantdiary/ui/plants/PlantsFragment.kt` — экран списка растений, поиск, фильтр, пустые состояния.
- `app/src/main/java/com/asagiry/plantdiary/ui/plants/PlantsViewModel.kt` — состояние экрана растений, поиск, фильтр и удаление.
- `app/src/main/java/com/asagiry/plantdiary/ui/plants/PlantsAdapter.kt` — адаптер списка растений.
- `app/src/main/java/com/asagiry/plantdiary/ui/plants/PlantFormFragment.kt` — форма добавления и редактирования растения.
- `app/src/main/java/com/asagiry/plantdiary/ui/plants/PlantFormViewModel.kt` — состояние формы растения и ее валидация.

### UI: care

- `app/src/main/java/com/asagiry/plantdiary/ui/care/CareRecordsFragment.kt` — экран списка записей ухода.
- `app/src/main/java/com/asagiry/plantdiary/ui/care/CareRecordsViewModel.kt` — загрузка списка ухода и удаление записи.
- `app/src/main/java/com/asagiry/plantdiary/ui/care/CareRecordsAdapter.kt` — адаптер списка записей ухода.
- `app/src/main/java/com/asagiry/plantdiary/ui/care/CareFormFragment.kt` — форма добавления и редактирования записи ухода.
- `app/src/main/java/com/asagiry/plantdiary/ui/care/CareFormViewModel.kt` — состояние формы ухода, выбор растения и проверка обязательных полей.

### UI: schedule

- `app/src/main/java/com/asagiry/plantdiary/ui/schedule/ScheduleFragment.kt` — экран расписания на выбранную дату.
- `app/src/main/java/com/asagiry/plantdiary/ui/schedule/ScheduleViewModel.kt` — выбранная дата и получение записей полива и высадки.
- `app/src/main/java/com/asagiry/plantdiary/ui/schedule/ScheduleAdapter.kt` — адаптер карточек расписания.

### UI: help and onboarding

- `app/src/main/java/com/asagiry/plantdiary/ui/help/HelpFragment.kt` — экран помощи и переключение языка.
- `app/src/main/java/com/asagiry/plantdiary/ui/onboarding/LanguageOnboardingFragment.kt` — первый экран выбора языка.

### Navigation

- `app/src/main/res/navigation/nav_graph.xml` — все экраны и переходы между ними.
- `app/src/main/res/menu/bottom_nav_menu.xml` — пункты нижней навигации.

### Layout XML

- `app/src/main/res/layout/activity_main.xml` — главный layout `MainActivity`.
- `app/src/main/res/layout/fragment_plants.xml` — layout экрана растений.
- `app/src/main/res/layout/fragment_plant_form.xml` — layout формы растения.
- `app/src/main/res/layout/fragment_care_records.xml` — layout списка ухода.
- `app/src/main/res/layout/fragment_care_form.xml` — layout формы ухода.
- `app/src/main/res/layout/fragment_schedule.xml` — layout экрана расписания.
- `app/src/main/res/layout/fragment_help.xml` — layout экрана помощи.
- `app/src/main/res/layout/fragment_language_onboarding.xml` — layout экрана выбора языка.
- `app/src/main/res/layout/item_plant.xml` — одна карточка растения в списке.
- `app/src/main/res/layout/item_care_record.xml` — одна карточка записи ухода.
- `app/src/main/res/layout/item_schedule_record.xml` — одна карточка записи в расписании.

### Values XML

- `app/src/main/res/values/strings.xml` — русские строки интерфейса.
- `app/src/main/res/values-en/strings.xml` — английские строки интерфейса.
- `app/src/main/res/values/plurals.xml` — русские plural-строки.
- `app/src/main/res/values-en/plurals.xml` — английские plural-строки.
- `app/src/main/res/values/colors.xml` — палитра приложения.
- `app/src/main/res/values/styles.xml` — стили полей, кнопок и чипов.
- `app/src/main/res/values/themes.xml` — главная тема приложения.

### Animation XML

- `app/src/main/res/anim/list_item_slide_up.xml` — анимация появления одного элемента списка.
- `app/src/main/res/anim/list_layout_stagger.xml` — каскадная анимация списка.
- `app/src/main/res/anim/nav_enter.xml` — анимация входа при переходе вперед.
- `app/src/main/res/anim/nav_exit.xml` — анимация выхода при переходе вперед.
- `app/src/main/res/anim/nav_pop_enter.xml` — анимация входа при возврате назад.
- `app/src/main/res/anim/nav_pop_exit.xml` — анимация выхода при возврате назад.

### Drawable and color resources

- `app/src/main/res/drawable/bg_screen_gradient.xml` — фон всего приложения.
- `app/src/main/res/drawable/bg_top_bar.xml` — фон верхнего toolbar.
- `app/src/main/res/drawable/bg_bottom_bar.xml` — фон нижней навигации.
- `app/src/main/res/drawable/bg_hero_gradient.xml` — фон hero-блоков на экранах.
- `app/src/main/res/drawable/bg_panel_surface.xml` — фон основных карточек и панелей.
- `app/src/main/res/drawable/bg_notice_panel.xml` — фон акцентных информационных блоков.
- `app/src/main/res/drawable/bg_icon_badge.xml` — круглая подложка для крупных иконок.
- `app/src/main/res/drawable/bg_icon_action.xml` — подложка для маленьких action-иконок.
- `app/src/main/res/drawable/bg_pill.xml` — фон для небольших плашек и меток.
- `app/src/main/res/drawable/ic_add_24.xml` — иконка добавления.
- `app/src/main/res/drawable/ic_edit_24.xml` — иконка редактирования.
- `app/src/main/res/drawable/ic_delete_24.xml` — иконка удаления.
- `app/src/main/res/drawable/ic_calendar_24.xml` — иконка календаря.
- `app/src/main/res/drawable/ic_water_24.xml` — иконка полива.
- `app/src/main/res/drawable/ic_help_24.xml` — иконка помощи.
- `app/src/main/res/drawable/ic_plant_leaf.xml` — основная иконка растения.
- `app/src/main/res/color/chip_filter_bg.xml` — цвет фона filter chip.
- `app/src/main/res/color/chip_filter_stroke.xml` — цвет рамки filter chip.
- `app/src/main/res/color/chip_filter_text.xml` — цвет текста filter chip.
- `app/src/main/res/color/nav_item_tint.xml` — цвета иконок и текста нижней навигации.

### Launcher icons

- `app/src/main/res/mipmap-anydpi/ic_launcher.xml` — основная иконка приложения.
- `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml` — круглая иконка приложения.

## 17. Соответствие требованиям курса

Источник требований:
`https://github.com/ivanshchitov/android-kotlin-course/blob/master/requirements.md`

Ниже не просто пересказ, а привязка требований к тому, где это видно в проекте.

### Проверяемые знания по Kotlin

#### Числовые переменные, строки, массивы

- строки и числа используются во `ViewModel`, например в `PlantFormViewModel.kt` и `CareFormViewModel.kt`
- строки и числа используются в моделях `Plant.kt` и `CareRecord.kt`
- массивоподобные коллекции используются в адаптерах и списках экранов, например в `PlantsAdapter.kt`, `CareRecordsAdapter.kt`, `ScheduleAdapter.kt`

#### Управляющие конструкции, условия, циклы

- условия активно используются в `PlantFormViewModel.kt`, `CareFormViewModel.kt`, `PlantsFragment.kt`, `MainActivity.kt`
- циклы и перебор коллекций используются в адаптерах, анимациях и работе со списками, например в `ScreenMotion.kt`

#### Классы и интерфейсы

- классы есть практически во всем проекте: `Fragment`, `ViewModel`, `Repository`, `Application`
- интерфейсы особенно видны в Room-слое через `PlantDao.kt` и `CareRecordDao.kt`

#### Null-безопасность

- nullable-поля есть в моделях и формах: `plantingDate`, `plantingTime`, `plannedPlantingDate`, `plannedPlantingTime`
- проверки на `null` есть в `PlantFormViewModel.kt`, `CareFormViewModel.kt`, `MainActivity.kt`

### Проверяемые знания по Android

#### Сборка, запуск и зависимости проекта

- зависимости и Android-конфигурация находятся в `app/build.gradle.kts`
- общая конфигурация проекта находится в `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`

#### Создание макетов приложения

- все основные макеты лежат в `app/src/main/res/layout/`
- ключевые экраны: `fragment_plants.xml`, `fragment_plant_form.xml`, `fragment_care_records.xml`, `fragment_care_form.xml`, `fragment_schedule.xml`, `fragment_help.xml`, `fragment_language_onboarding.xml`

#### Использование Data Binding

- Data Binding включен в `app/build.gradle.kts`
- в XML используется блок `<layout>` и привязка `viewModel`, например в `fragment_plant_form.xml` и `fragment_care_form.xml`
- пользовательский binding adapter вынесен в `BindingAdapters.kt`

#### Навигация через редактор навигации

- навигация описана в `app/src/main/res/navigation/nav_graph.xml`
- `MainActivity.kt` подключает `NavHostFragment`, toolbar и нижнюю навигацию к `NavController`

#### Жизненный цикл Activity и Fragment

- `MainActivity.kt` управляет общим контейнером приложения
- `Fragment`-экраны используют `onCreateView`, `onViewCreated`, `onDestroyView`
- в `Fragment` используется `viewLifecycleOwner`

#### Архитектура MVVM

- `Model`: файлы в `data/local/` и `data/repository/`
- `View`: `Fragment` и XML-макеты
- `ViewModel`: файлы `*ViewModel.kt`

#### Использование Room

- база: `PlantDiaryDatabase.kt`
- сущности: `Plant.kt`, `CareRecord.kt`
- DAO: `PlantDao.kt`, `CareRecordDao.kt`
- конвертеры: `Converters.kt`

### Требования к выполнению

#### В корне проекта должен быть `README.txt`

- выполнено
- файл: `README.txt`

#### Приложение должно выполнять все функции задания

- CRUD растений: `PlantsFragment.kt`, `PlantFormFragment.kt`, `PlantFormViewModel.kt`
- CRUD ухода: `CareRecordsFragment.kt`, `CareFormFragment.kt`, `CareFormViewModel.kt`
- поиск растений: `PlantsFragment.kt`, `PlantsViewModel.kt`, `PlantDiaryRepository.kt`
- фильтр по типу: `PlantsFragment.kt`, `PlantsViewModel.kt`
- полив на сегодня, завтра, выбранную дату: `ScheduleFragment.kt`, `ScheduleViewModel.kt`, `CareRecordDao.kt`
- высадка на сегодня, завтра, выбранную дату: `ScheduleFragment.kt`, `ScheduleViewModel.kt`, `CareRecordDao.kt`
- помощь по использованию: `HelpFragment.kt`
- выбор языка: `LanguageOnboardingFragment.kt`, `AppPreferences.kt`, `PlantDiaryApp.kt`

#### Приложение не должно содержать ошибок

- проект должен собираться через `:app:assembleDebug`
- на практике это проверяется именно сборкой и ручной прогонкой сценариев

#### Приложение должно корректно обрабатывать неправильный ввод

- проверки обязательных полей и интервалов есть в `PlantFormViewModel.kt`
- проверки выбранного растения и дат есть в `CareFormViewModel.kt`
- сообщения об ошибках выводятся через строки ресурсов

#### Архитектура должна соответствовать MVVM

- выполнено
- экранная логика вынесена в `ViewModel`
- данные идут через `Repository`
- база вынесена в Room-слой

#### Навигация должна быть построена через редактор навигации

- выполнено
- центральный файл: `nav_graph.xml`

#### Для базы данных должен использоваться Room

- выполнено
- используется Room с `Entity`, `DAO`, `Database`, `TypeConverters`

#### Состояние экранов должно сохраняться при смене ориентации

- это реализовано через `SavedStateHandle` в `PlantsViewModel.kt`, `PlantFormViewModel.kt`, `CareFormViewModel.kt`, `ScheduleViewModel.kt`
- поэтому поиск, фильтр, форма и выбранная дата не теряются так легко при пересоздании экрана

#### Интерфейс должен быть понятным и должен показывать помощь и подсказки для ввода

- экран помощи есть в `HelpFragment.kt`
- подсказки на полях ввода заданы через `TextInputLayout` в `fragment_plant_form.xml` и `fragment_care_form.xml`
- пустые состояния и поясняющие тексты добавлены в экраны растений и ухода

#### Все строки должны быть интернационализированы

- строки вынесены в `app/src/main/res/values/strings.xml` и `app/src/main/res/values-en/strings.xml`
- plural-строки вынесены в `app/src/main/res/values/plurals.xml` и `app/src/main/res/values-en/plurals.xml`

Формальный нюанс:

- в тексте требований указано: `res/values/strings.xml` для английского и `res/values-ru/strings.xml` для русского
- в текущем проекте сделано наоборот по структуре локалей: `values/strings.xml` содержит русский по умолчанию, а английский вынесен в `values-en/strings.xml`
- сама интернационализация в приложении есть и работает, но если нужна буквальная формальная подгонка под это правило из `requirements.md`, структуру ресурсных папок стоит отдельно привести к виду `values = en`, `values-ru = ru`

### 17.1 Подробный разбор требований по Android-части

#### Сборка, запуск и зависимости проекта

Где показать:

- `app/build.gradle.kts`
- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`

Что говорить:

- зависимости подключены в `app/build.gradle.kts`
- там же включен Android application plugin, Kotlin plugin, `kapt`
- там же задан `compileSdk`, `minSdk`, `targetSdk`
- сборка идет через Gradle Wrapper, но для понимания проекта важнее именно `app/build.gradle.kts`

#### Создание макетов приложения

Где показать:

- папка `app/src/main/res/layout/`

Что говорить:

- каждый экран вынесен в отдельный XML
- отдельные XML есть не только для экранов, но и для элементов списков
- это упрощает поддержку и переиспользование интерфейса

#### Использование Data Binding

Где показать:

- `app/build.gradle.kts`
- `fragment_plant_form.xml`
- `fragment_care_form.xml`
- `BindingAdapters.kt`

Что говорить:

- в `build.gradle.kts` включен `dataBinding = true`
- в layout используется корневой тег `<layout>`
- в XML передается `viewModel`
- через binding часть данных автоматически синхронизируется между полями и `ViewModel`

Что преподаватель может попросить показать:

- строку `buildFeatures { dataBinding = true }`
- любое поле с `@={viewModel...}`

#### Навигация через Navigation Component

Где показать:

- `nav_graph.xml`
- `activity_main.xml`
- `MainActivity.kt`

Что говорить:

- все переходы описаны в одном nav graph
- `FragmentContainerView` используется как хост для фрагментов
- `NavController` подключается в `MainActivity`
- верхняя панель и нижняя навигация синхронизированы с navigation component

#### Жизненный цикл Activity и Fragment

Где показать:

- `MainActivity.kt`
- любой `Fragment`, например `PlantsFragment.kt`

Что говорить:

- экран создает binding в `onCreateView`
- подписки и UI-логика запускаются в `onViewCreated`
- binding очищается в `onDestroyView`, чтобы не было утечек памяти

#### MVVM

Где показать:

- `PlantsFragment.kt`
- `PlantsViewModel.kt`
- `PlantDiaryRepository.kt`

Что говорить:

- `Fragment` отвечает за отображение и клики
- `ViewModel` хранит состояние и бизнес-логику экрана
- `Repository` изолирует доступ к данным
- база и DAO вообще не торчат напрямую в экране

#### Room

Где показать:

- `PlantDiaryDatabase.kt`
- `Plant.kt`
- `CareRecord.kt`
- `PlantDao.kt`
- `CareRecordDao.kt`

Что говорить:

- `@Database` описывает базу
- `@Entity` описывает таблицы
- `@Dao` описывает запросы
- `@ForeignKey` связывает запись ухода с растением
- `Converters.kt` нужен, потому что Room не умеет сам хранить `LocalDate` и `LocalTime`

### 17.2 Подробный разбор функциональных требований задания

#### Хранение записей о растениях

Требование:

- наименование
- описание
- тип
- интервал полива
- дата и время высадки для садового растения

Где реализовано:

- `Plant.kt`
- `fragment_plant_form.xml`
- `PlantFormViewModel.kt`

Что показать:

- поля модели `Plant`
- поля формы растения
- проверку обязательных полей в `savePlant()`

#### Хранение записей об уходе

Требование:

- растение
- дата следующего полива
- дата высадки для садового растения
- отдельный CRUD для ухода

Где реализовано:

- `CareRecord.kt`
- `CareRecordWithPlant.kt`
- `fragment_care_form.xml`
- `CareFormViewModel.kt`
- `CareRecordsFragment.kt`

Что говорить:

- в базе запись ухода хранит ссылку на растение через `plantId`
- для отображения вместе с растением используется `CareRecordWithPlant`
- CRUD ухода реализован отдельным экраном и отдельной формой

#### Просмотр, добавление, редактирование и удаление растений

Где реализовано:

- просмотр: `PlantsFragment.kt`, `PlantsAdapter.kt`
- добавление и редактирование: `PlantFormFragment.kt`, `PlantFormViewModel.kt`
- удаление: `PlantsFragment.kt`, `PlantsViewModel.kt`

#### Просмотр, добавление, редактирование и удаление ухода

Где реализовано:

- просмотр: `CareRecordsFragment.kt`, `CareRecordsAdapter.kt`
- добавление и редактирование: `CareFormFragment.kt`, `CareFormViewModel.kt`
- удаление: `CareRecordsFragment.kt`, `CareRecordsViewModel.kt`

#### Список растений для полива на выбранный день

Где реализовано:

- `ScheduleFragment.kt`
- `ScheduleViewModel.kt`
- `CareRecordDao.kt`

Что говорить:

- экран меняет выбранную дату
- `ViewModel` запрашивает у DAO записи для этой даты
- DAO возвращает уже отфильтрованный список

#### Список растений для высаживания на выбранный день

Где реализовано:

- `ScheduleFragment.kt`
- `ScheduleViewModel.kt`
- `CareRecordDao.kt`

Что говорить:

- логика такая же, как для полива, только отдельный запрос на дату высадки

#### Фильтрация растений по типу

Где реализовано:

- `fragment_plants.xml`
- `PlantsFragment.kt`
- `PlantsViewModel.kt`
- `PlantDiaryRepository.kt`

Что показать:

- `ChipGroup` в layout
- обработку выбора chip
- передачу фильтра в repository

#### Поиск растений по названию

Где реализовано:

- `fragment_plants.xml`
- `PlantsFragment.kt`
- `PlantsViewModel.kt`
- `PlantDiaryRepository.kt`

Что говорить:

- пользователь вводит запрос
- `ViewModel` обновляет состояние поиска
- `Repository` фильтрует список по `name.contains(..., ignoreCase = true)`

### 17.3 Чек-лист “что показать, если попросят открыть код”

Если преподаватель просит показать, где что подключено:

- база Room: `PlantDiaryDatabase.kt`
- таблицы: `Plant.kt`, `CareRecord.kt`
- DAO: `PlantDao.kt`, `CareRecordDao.kt`
- MVVM: `PlantsFragment.kt` + `PlantsViewModel.kt` + `PlantDiaryRepository.kt`
- Data Binding: `app/build.gradle.kts` + `fragment_plant_form.xml`
- Navigation: `nav_graph.xml` + `activity_main.xml` + `MainActivity.kt`
- локализация: `values/strings.xml` и `values-en/strings.xml`
- сохранение состояния: `SavedStateHandle` в `PlantsViewModel.kt`, `PlantFormViewModel.kt`, `CareFormViewModel.kt`, `ScheduleViewModel.kt`
- обработка неправильного ввода: `savePlant()` и `saveCareRecord()`

### 17.4 Что может спросить преподаватель по требованиям

Ниже список самых вероятных вопросов.
Их можно использовать как шпаргалку.

#### 1. Почему выбрана архитектура MVVM?

Короткий ответ:

> Чтобы отделить интерфейс от логики. `Fragment` показывает экран, `ViewModel` хранит состояние и обрабатывает действия пользователя, `Repository` дает доступ к данным.

#### 2. Где у тебя реализован Room?

Короткий ответ:

> Room реализован через `PlantDiaryDatabase`, две `Entity`, два `DAO` и `Repository`.

#### 3. Зачем нужен Repository, если есть DAO?

Короткий ответ:

> Чтобы экран и `ViewModel` не работали с базой напрямую. `Repository` собирает все операции с данными в одном месте.

#### 4. Где именно используется Data Binding?

Короткий ответ:

> В формах растения и ухода. `ViewModel` передается в XML, а поля связаны с данными напрямую через binding.

#### 5. Где обрабатывается неправильный ввод?

Короткий ответ:

> Во `ViewModel`, потому что это логика формы. Например, в `savePlant()` и `saveCareRecord()`.

#### 6. Где сохраняется состояние при повороте экрана?

Короткий ответ:

> Через `SavedStateHandle` во `ViewModel`. Так сохраняются поиск, фильтр, данные формы и выбранная дата расписания.

#### 7. Как сделан поиск по названию?

Короткий ответ:

> Текст из поля поиска попадает во `ViewModel`, потом в `Repository`, а там список фильтруется по названию растения.

#### 8. Как сделана фильтрация по типу растения?

Короткий ответ:

> На экране есть `ChipGroup`, выбранный тип уходит во `ViewModel`, а дальше `Repository` и DAO возвращают только нужные растения.

#### 9. Почему растения и уход разделены на две сущности?

Короткий ответ:

> Потому что карточка растения — это постоянные данные, а уход — это отдельная запись с датой действия. Так проще делать CRUD и расписание.

#### 10. Где реализован экран помощи?

Короткий ответ:

> В `HelpFragment.kt`, а layout находится в `fragment_help.xml`.

#### 11. Где реализована интернационализация строк?

Короткий ответ:

> Все строки вынесены в ресурсы. Сейчас используются `values/strings.xml` и `values-en/strings.xml`.

#### 12. Что бы ты улучшил дальше?

Короткий ответ:

> Я бы добавил уведомления, фото растений, сортировку, историю ухода и, возможно, экспорт данных.

### 17.5 На что могут обратить внимание как на формальный риск

Есть один заметный формальный момент:

- в требованиях явно написано про `values/strings.xml` как английский и `values-ru/strings.xml` как русский
- у нас сейчас русская локаль стоит как базовая, а английская лежит в `values-en`

Что отвечать:

> Интернационализация реализована полностью, строки вынесены в ресурсы и язык переключается. Если нужно строго под формат требований, структуру папок можно быстро привести к `values` и `values-ru`.
