# Wydatki — GitHub Actions iOS Build

## Start
1. Upload this project to GitHub.
2. Open **Actions → Build iOS**.
3. Click **Run workflow**.
4. Download the **Wydatki-iOS-Simulator** artifact after the build.

## Firebase
For iOS Firebase, add a repository secret named `GOOGLE_SERVICE_INFO_PLIST_BASE64` containing the base64-encoded contents of your real `GoogleService-Info.plist`. Do not commit the real plist to a public repository.

## Physical iPhone
This workflow currently builds an unsigned iOS Simulator app. A physical iPhone `.ipa` requires Apple code signing (certificate + provisioning profile / distribution method). That can be added once Apple signing secrets are configured.


## Aktualna poprawka
Dodano wymagany opt-in `ExperimentalForeignApi` dla iOS `IosFilePicker`, aby kompilacja Kotlin/Native przechodziła w GitHub Actions.
