/**
 * Registers the ThetaBleClientReactNative module with the React Native bridge.
 * Required so the TurboModule system can obtain the native instance when
 * TurboModuleRegistry.get('ThetaBleClientReactNative') is called.
 * Method dispatch is handled by the New Architecture JSI layer (codegen
 * Spec); RCT_EXTERN_METHOD is not used -- ThetaBleClientReactNativeImpl.swift
 * implements every native* method directly as @objc selectors matching the
 * codegen Spec.
 */
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(ThetaBleClientReactNative, RCTEventEmitter)
@end

#import <ThetaBleClientReactNativeSpec/ThetaBleClientReactNativeSpec.h>
#import <ReactCommon/RCTTurboModule.h>
#import <objc/runtime.h>

// The real implementation is Swift (ThetaBleClientReactNativeImpl.swift,
// exposed to the ObjC runtime under the name "ThetaBleClientReactNative" via
// its @objc(ThetaBleClientReactNative) attribute). That file can't see this
// spec header itself -- it drags in RCTTurboModule.h -> jsi.h, which needs
// the C++ stdlib, and Swift's "-import-underlying-module" build of this
// pod's own headers is Objective-C only (see
// ThetaBleClientReactNativeModuleImports.h). So the two C++-touching pieces
// TurboModule registration requires -- RCTTurboModule protocol conformance
// and getTurboModule: -- are attached to the Swift class here instead, from
// real Objective-C++, at runtime.
__attribute__((constructor)) static void
ThetaBleClientReactNativeRegisterTurboModule(void)
{
  Class cls = NSClassFromString(@"ThetaBleClientReactNative");
  if (cls) {
    class_addProtocol(cls, @protocol(RCTTurboModule));
  }
}

@interface ThetaBleClientReactNative () <NativeThetaBleClientReactNativeSpec>
@end

@implementation ThetaBleClientReactNative (RCTTurboModule)

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wobjc-protocol-method-implementation"
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeThetaBleClientReactNativeSpecJSI>(params);
}
#pragma clang diagnostic pop

@end
