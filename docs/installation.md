# Установка и настройка

## Системные требования

| Параметр | Значение |
|---|---|
| Платформа | Android |
| Минимальная версия | Android API 29 |
| Версия сборки | compileSdk 36 |
| Язык | Java 11 |
| Хранилище данных | Room / SQLite |

## Сборка проекта

Клонируйте репозиторий и откройте его в Android Studio:

```text
https://github.com/SmokedBanan/FinanceTracker.git
```

Для сборки отладочной версии выполните в терминале проекта:

```powershell
.\gradlew.bat assembleDebug
```

APK появится в каталоге `app/build/outputs/apk/debug/`.

> Перед сборкой Android Studio должна иметь доступ к интернету для загрузки зависимостей Gradle.
