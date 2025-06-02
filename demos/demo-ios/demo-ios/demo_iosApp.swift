//
//  demo_iosApp.swift
//  demo-ios
//
//  Created on 2023/03/15.
//

import SwiftUI

@main
struct demo_iosApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    var body: some Scene {
        WindowGroup {
            ContentView(thetaBleApi: ThetaBleApi())
        }
    }
}

class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_: UIApplication, didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        true
    }
}
