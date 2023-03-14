//
//  CameraStatusView.swift
//  demo-ios
//
//  Created on 2023/04/18.
//

import SwiftUI
import THETABleClient

struct CameraStatusView: View {

    @ObservedObject var thetaBleApi: ThetaBleApi

    var body: some View {
        VStack {
            Text("Camera Status")
                .fontWeight(.bold)

            HStack {
                Text("Battery level: \(thetaBleApi.batteryLevel ?? 0)")
                Button("Update") {
                    Task {
                        try? await thetaBleApi.updateBatteryLevel()
                    }
                }
                .buttonStyle(.borderedProminent)
            }
            HStack {
                Text("Battery status: \(thetaBleApi.batteryStatus?.name ?? "nil")")
                Button("Update") {
                    Task {
                        try? await thetaBleApi.updateBatteryStatus()
                    }
                }
                .buttonStyle(.borderedProminent)
            }
            VStack {
                HStack {
                    Text("Camera power: \(thetaBleApi.cameraPower?.name ?? "nil")")
                    Button("Update") {
                        Task {
                            try? await thetaBleApi.updateCameraPower()
                        }
                    }
                    .buttonStyle(.borderedProminent)
                }
                HStack {
                    Button("Off") {
                        Task {
                            try? await thetaBleApi.setCameraPower(value: .off)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    Button("On") {
                        Task {
                            try? await thetaBleApi.setCameraPower(value: .on)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    Button("Sleep") {
                        Task {
                            try? await thetaBleApi.setCameraPower(value: .sleep)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                }
            }

            VStack {
                HStack {
                    Text("Plugin control: \(thetaBleApi.pluginControl?.pluginControl.name ?? "nil")")
                    Button("Update") {
                        Task {
                            try? await thetaBleApi.updatePluginControl()
                        }
                    }
                    .buttonStyle(.borderedProminent)
                }
                HStack {
                    Button("Stop") {
                        Task {
                            try? await thetaBleApi.setPluginControl(value: .stop)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    Button("Running") {
                        Task {
                            try? await thetaBleApi.setPluginControl(value: .running)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                }
            }

            Text("Information\n\(thetaBleApi.infoText)")
        }
        .padding()
        .onAppear {
            thetaBleApi.setInfoText("")
            if thetaBleApi.checkCameraStatusCommand() {
                thetaBleApi.setInfoText("OK.")
            } else {
                thetaBleApi.setInfoText("Unsupported CameraStatusCommand.")
            }
        }
    }
}

struct CameraStatusView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView( thetaBleApi: ThetaBleApi())
    }
}
