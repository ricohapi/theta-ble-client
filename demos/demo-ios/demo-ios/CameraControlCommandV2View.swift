//
//  CameraControlCommandV2View.swift
//  demo-ios
//
//  Created on 2023/10/11.
//

import Foundation
import SwiftUI
import THETABleClient

struct CameraControlCommandV2View: View {
    @ObservedObject var thetaBleApi: ThetaBleApi

    var body: some View {
        VStack {
            Button("Get Info") {
                Task {
                    try? await thetaBleApi.cameraControlCommandV2GetInfo()
                }
            }
            .buttonStyle(.borderedProminent)

            Button("Get State") {
                Task {
                    try? await thetaBleApi.cameraControlCommandV2GetState()
                }
            }
            .buttonStyle(.borderedProminent)

            Button("Get State2") {
                Task {
                    try? await thetaBleApi.cameraControlCommandV2GetState2()
                }
            }
            .buttonStyle(.borderedProminent)

            Button("Set State Notify") {
                Task {
                    thetaBleApi.cameraControlCommandV2SetStateNotify()
                }
            }
            .buttonStyle(.borderedProminent)
            ScrollView {
                Text("Information\n\(thetaBleApi.infoText)")
                    .frame(maxHeight: .infinity, alignment: .top)
            }
        }
        .navigationBarTitle("Camera Control Command V2", displayMode: .inline)
        .onAppear {
            thetaBleApi.setInfoText("")
            if thetaBleApi.checkCameraControlCommandV2() {
                thetaBleApi.setInfoText("OK.")
            } else {
                thetaBleApi.setInfoText("Unsupported CameraControlCommandV2.")
            }
        }
        .onDisappear {
            thetaBleApi.cameraControlCommandV2ClearStateNotify()
            thetaBleApi.setInfoText("")
        }
    }
}

struct CameraControlCommandV2View_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(thetaBleApi: ThetaBleApi())
    }
}
