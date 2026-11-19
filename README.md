# Kolejka piosenek (Android, Kotlin)

Aplikacja na Androida z jednym przyciskiem, który puszcza wszystkie piosenki z listy z YouTube po kolei.
Lista startowa jest wpisana na stałe w kodzie (`DEFAULT_SONGS`), a piosenki dodane ręcznie zapisują się w telefonie.

Napisana w Kotlinie z Jetpack Compose. Nie wymaga logowania ani klucza API.

## Jak działa
Przycisk „Odtwarzaj wszystkie” otwiera link `https://www.youtube.com/watch_videos?video_ids=...`,
czyli tymczasową kolejkę YouTube (do 50 filmów). Link otwiera się w aplikacji YouTube albo w przeglądarce.

## Zmiana listy na stałe
Edytuj plik `app/src/main/java/pl/kolejkapiosenek/data/DefaultSongs.kt`.
Po zmianie listy domyślnej podbij też `KEY` w `SongStore.kt` (np. `v4`), inaczej telefon z zapisaną
już listą nie pokaże nowych piosenek.

## Budowanie APK
**GitHub Actions (bez instalowania czegokolwiek):** po wgraniu repozytorium wejdź w zakładkę *Actions*,
otwórz ostatni przebieg „Build APK” i pobierz plik `PlaylistYT-apk`. Po utworzeniu tagu (np. `v1.0`)
APK trafia też do *Releases*.

**Android Studio:** otwórz folder projektu, poczekaj na synchronizację i kliknij *Run*.

**Terminal:** `gradle assembleDebug`, gotowy plik: `app/build/outputs/apk/debug/app-debug.apk`.
(Żeby używać `./gradlew`, raz uruchom `gradle wrapper`.)

## Instalacja na telefonie
Skopiuj `PlaylistYT.apk` na telefon, otwórz go i zezwól na instalację z nieznanych źródeł.

## Licencja
MIT
