import XCTest
@testable import theta_ble_client_react_native

/// Every native* method rejects with MESSAGE_NO_ARGUMENT before doing any real
/// work when a required field is missing or JSON-null in `params`. These calls
/// all resolve synchronously on this check (Task {} only runs after it), so no
/// expectation/async waiting is needed.
final class ThetaBleClientReactNativeMissingArgumentTests: XCTestCase {

    private var module: ThetaBleClientReactNativeImpl!

    override func setUp() {
        super.setUp()
        module = ThetaBleClientReactNativeImpl()
    }

    typealias NativeMethod = (
        _ params: [AnyHashable: Any],
        _ resolve: @escaping RCTPromiseResolveBlock,
        _ reject: @escaping RCTPromiseRejectBlock
    ) -> Void

    private struct Target {
        let name: String
        let method: NativeMethod
        let validParams: [AnyHashable: Any]
    }

    // To cover a new native method, just add one entry here with its valid
    // (all required fields present) params.
    private lazy var targets: [Target] = [
        Target(name: "nativeIsConnected", method: module.nativeIsConnected,
               validParams: ["id": 1]),
        Target(name: "nativeSetBatteryLevelNotify", method: module.nativeSetBatteryLevelNotify,
               validParams: ["id": 1, "enable": true]),
        Target(name: "nativeContainService", method: module.nativeContainService,
               validParams: ["id": 1, "service": "abc"]),
        Target(name: "nativeSetPluginControl", method: module.nativeSetPluginControl,
               validParams: ["id": 1, "value": [String: Any]()]),
        Target(name: "nativeCameraControlCommandV2GetOptions",
               method: module.nativeCameraControlCommandV2GetOptions,
               validParams: ["id": 1, "optionNames": ["a"]]),
        Target(name: "nativeWlanControlCommandV2SetAccessPointDynamically",
               method: module.nativeWlanControlCommandV2SetAccessPointDynamically,
               validParams: ["id": 1, "params": [String: Any]()]),
    ]

    func test_allTargetsRejectWhenAnyRequiredFieldIsMissingOrJSONNull() {
        var executedCaseCount = 0

        for target in targets {
            for key in target.validParams.keys {
                // "missing" (key absent) and "JSON-null" (key present with
                // NSNull) are distinct wire shapes; both must reject.
                let variants: [(String, [AnyHashable: Any])] = [
                    ("missing", target.validParams.filter { $0.key != key }),
                    ("null", target.validParams.merging([key: NSNull()]) { _, new in new }),
                ]

                for (variantName, params) in variants {
                    XCTContext.runActivity(named: "\(target.name): \(key) \(variantName)") { _ in
                        var resolved = false
                        var rejectedMessage: String?
                        target.method(params,
                                      { _ in resolved = true },
                                      { _, message, _ in rejectedMessage = message })
                        XCTAssertFalse(resolved,
                            "[\(target.name): \(key) \(variantName)] expected reject, got resolve")
                        XCTAssertEqual(rejectedMessage, MESSAGE_NO_ARGUMENT,
                            "[\(target.name): \(key) \(variantName)] wrong message")
                    }
                    executedCaseCount += 1
                }
            }
        }

        let testedMethodNames = targets.map { "  - \($0.name)" }.joined(separator: "\n")
        print("""
            Verified \(executedCaseCount) missing/null-argument cases across \
            \(targets.count) native methods:
            \(testedMethodNames)
            """)
    }

    /// Catches a typo'd key in `validParams` that would otherwise go unnoticed.
    func test_allTargetsDoNotRejectWithNoArgumentWhenAllFieldsPresent() {
        for target in targets {
            XCTContext.runActivity(named: "\(target.name): all fields present") { _ in
                var rejectedMessage: String?
                target.method(target.validParams,
                              { _ in },
                              { _, message, _ in rejectedMessage = message })
                XCTAssertNotEqual(rejectedMessage, MESSAGE_NO_ARGUMENT,
                    "[\(target.name)] rejected with MESSAGE_NO_ARGUMENT even though " +
                    "all fields were present -- check validParams for a typo/wrong key")
            }
        }

        let testedMethodNames = targets.map { "  - \($0.name)" }.joined(separator: "\n")
        print("""
            Verified \(targets.count) all-fields-present cases across \
            \(targets.count) native methods:
            \(testedMethodNames)
            """)
    }
}
