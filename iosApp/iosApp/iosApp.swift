import SwiftUI
import FirebaseCore
import ComposeApp

@main
struct iosApp: App {
    init() {
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
