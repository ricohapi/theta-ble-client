//
//  WebApiUtil.swift
//  demo-ios
//

import Foundation

class ThetaInfo {
    let model: String
    let serialNumber: String

    init(model: String, serialNumber: String) {
        self.model = model
        self.serialNumber = serialNumber
    }
}

func getThetaInfo() async throws -> ThetaInfo? {
    let url = URL(string: "http://192.168.1.1/osc/info")!
    var request = URLRequest(url: url)
    request.httpMethod = "GET"
    do {
        let (data, urlResponse) = try await URLSession.shared.data(for: request)
        guard let _ = urlResponse as? HTTPURLResponse else {
            return nil
        }
        do {
            let object = try JSONSerialization.jsonObject(with: data, options: []) as! [String: Any]
            let model = object["model"] as? String
            let serialNumber = object["serialNumber"] as? String
            if let model, let serialNumber {
                return ThetaInfo(model: model, serialNumber: serialNumber)
            }
        } catch {
            print(error)
        }
    } catch {
        print(error)
    }

    return nil
}
