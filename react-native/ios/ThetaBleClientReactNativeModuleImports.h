// Public on purpose: CocoaPods packages a Swift-containing pod as a static
// framework, and Swift sees the pod's own Objective-C side only through its
// auto-generated umbrella module (-import-underlying-module) -- a manual
// bridging header isn't supported for framework targets. Keeping this import
// list public puts it in that umbrella so ThetaBleClientReactNativeImpl.swift
// can see RCTEventEmitter's block/callback types.
//
// The codegen spec header (ThetaBleClientReactNativeSpec.h) is deliberately
// NOT imported here: it drags in RCTTurboModule.h -> jsi.h, which needs the
// C++ standard library. Swift's "-import-underlying-module" compiles this
// umbrella as plain Objective-C, so <cassert>/<utility>/<optional> aren't
// visible and the module fails to build. ThetaBleClientReactNative.mm (real
// Objective-C++) imports the spec header directly instead and forwards into
// the Swift implementation.
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
