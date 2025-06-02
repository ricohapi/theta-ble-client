//
//  ContentView.swift
//  demo-ios
//
//  Created on 2023/03/15.
//

import SwiftUI
import THETAClient
let KEY_LAST_DEVICE_NAME = "lastDeviceName"
let KEY_LAST_USE_UUID = "lastUseUuid"
let endPoint: String = "http://192.168.1.1"

struct ContentView: View {

    @State var devName: String?
    @State var useUuid = true
    let uuid = "6BEDD7A3-4E01-4FE4-9DFB-03BFF23ECFD3"

    @ObservedObject var thetaBleApi: ThetaBleApi

    func loadDevice() {
        devName = UserDefaults.standard.string(forKey: KEY_LAST_DEVICE_NAME)
        useUuid = UserDefaults.standard.bool(forKey: KEY_LAST_USE_UUID)
    }

    func saveDevice() {
        if let devName {
            UserDefaults.standard.set(devName, forKey: KEY_LAST_DEVICE_NAME)
            UserDefaults.standard.set(useUuid, forKey: KEY_LAST_USE_UUID)
        }
    }

    func getBleThetaName(info: ThetaInfo) -> String {
        let prefix = info.model == "RICOH360 THETA A1" ? "AA" : ""
        return prefix + info.serialNumber.suffix(8)
    }

    var body: some View {
        NavigationView {
            VStack {
                Group {
                    Button("Connect Wifi") {
                        Task {
                            guard let theta = try? await ThetaRepository.Companion.shared.doNewInstance(endpoint: endPoint, config: nil, timeout: nil)
                            else {
                                thetaBleApi.setInfoText("Error. Connect THETA for wifi.")
                                return
                            }
                            guard let info = try? await getThetaInfo() else {
                                thetaBleApi.setInfoText("Error. Get THETA info.")
                                return
                            }
                            devName = getBleThetaName(info: info)
                            if let name = try? await theta.setBluetoothDevice(uuid: uuid) {
                                useUuid = true
                                devName = name
                            } else {
                                useUuid = false
                            }
                            let options = ThetaRepository.Options()
                            options.bluetoothPower = .on
                            try? await theta.setOptions(options: options)
                            
                            thetaBleApi.setInfoText("wifi connected. \(devName ?? "")")
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    
                    Text("device: \(devName ?? "nil") \(devName != nil && useUuid ? " use uuid" : "")")
                    Button("Scan BLE") {
                        guard let devName else { return }
                        Task {
                            try await thetaBleApi.scan(name: devName)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    
                    Button("Connect") {
                        Task {
                            if await thetaBleApi.connect(uuid: useUuid ? uuid : nil) {
                                saveDevice()
                            }
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    
                    Group {
                        Button("Info") {
                            Task {
                                try await thetaBleApi.getInfo()
                            }
                        }
                        .buttonStyle(.borderedProminent)
                        
                        NavigationLink(destination: CameraStatusView(thetaBleApi: thetaBleApi)) {
                            Text("Camera Status")
                        }
                        .buttonStyle(.borderedProminent)
                        
                        Button("Take Picture") {
                            Task {
                                try await thetaBleApi.takePicture()
                            }
                        }
                        .buttonStyle(.borderedProminent)
                        
                        NavigationLink(destination: CameraControlCommandV2View(thetaBleApi: thetaBleApi)) {
                            Text("Camera Control Command V2")
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    
                    Button("Disconnect") {
                        Task {
                            try await thetaBleApi.disconnect()
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    
                    
                    Button("Scan SSID") {
                        Task {
                            try await thetaBleApi.scanSsid()
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    
                    ScrollView {
                        Text("Information\n\(thetaBleApi.infoText)")
                            .frame(maxHeight: .infinity, alignment: .top)
                    }
                }
            }
            .navigationBarTitle("THETA Client BLE", displayMode: .inline)
        }
        .onAppear {
            loadDevice()
            if devName == nil {
                thetaBleApi.setInfoText("Please connect wifi.")
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView( thetaBleApi: ThetaBleApi())
    }
}
