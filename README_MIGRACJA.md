# Wydatki-iOS — stan po audycie i poprawkach

Ten folder jest niezależnym projektem migracji KMP. Oryginalny Android Wydatki v1.0.13 znajduje się poza nim i nie został zmodyfikowany.

## Wprowadzone poprawki

- uporządkowano synchronizację Firebase w `AppStore`;
- dodano serializację `cloudPush/cloudPull` przez `Mutex`;
- snapshot JSON jest tworzony po uzyskaniu blokady, więc oczekujący push wysyła aktualny stan;
- `cloudPull()` rozróżnia:
  - istniejący dokument z danymi,
  - brak dokumentu,
  - błąd Firebase/sieci;
- błąd odczytu Firebase nie powoduje automatycznego nadpisania chmury lokalnymi danymi;
- pull nie nadpisuje lokalnej zmiany wykonanej podczas trwania odczytu;
- iOS FilePicker korzysta z `UIDocumentPickerViewController`;
- wyszukiwanie aktywnego okna iOS nie opiera się już na `keyWindow` jako fallback;
- projekt Xcode ma jawne `-framework ComposeApp` w linkerze.

## Ważne ograniczenia

Nie dodano `GoogleService-Info.plist`, ponieważ jest to plik zależny od konkretnego projektu Firebase i nie był obecny w przesłanym archiwum.

GitLive Firebase SDK wymaga również podłączenia właściwych frameworków Firebase iOS do aplikacji iOS. To należy skonfigurować na macOS/Xcode przed testem runtime Firebase.

Nie można było wykonać lokalnego buildu Gradle w tym środowisku, ponieważ wrapper wymaga pobrania Gradle 9.5.0 z Internetu, a środowisko wykonawcze nie ma dostępu do `services.gradle.org`.

## Oryginalne API danych

Firestore pozostaje:

`/wydatki/{userId}`

Dokument:

- `data`
- `updatedAt`

Nie wykonano migracji danych ani zmiany formatu Firestore.
