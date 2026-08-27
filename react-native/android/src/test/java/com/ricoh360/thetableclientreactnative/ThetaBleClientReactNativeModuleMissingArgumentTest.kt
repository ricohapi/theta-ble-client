package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.argThat
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

/**
 * Every native* method rejects with MESSAGE_NO_ARGUMENT before doing any real
 * work when a required field is missing or JSON-null in [params]. These calls
 * all resolve synchronously on this check (launch{} only runs after it), so
 * no coroutine test dispatcher is needed.
 */
class ThetaBleClientReactNativeModuleMissingArgumentTest {

  private lateinit var module: ThetaBleClientReactNativeModule

  @Before
  fun setUp() {
    module = ThetaBleClientReactNativeModule(mock<ReactApplicationContext>())
  }

  private data class Target(
    val name: String,
    val invoke: (ThetaBleClientReactNativeModule, ReadableMap, Promise) -> Unit,
    val validParams: Map<String, Any?>
  )

  private fun toJavaOnlyMap(entries: Map<String, Any?>): JavaOnlyMap {
    val map = JavaOnlyMap()
    entries.forEach { (key, value) ->
      when (value) {
        null -> map.putNull(key)
        is Int -> map.putInt(key, value)
        is Boolean -> map.putBoolean(key, value)
        is String -> map.putString(key, value)
        is JavaOnlyMap -> map.putMap(key, value)
        is JavaOnlyArray -> map.putArray(key, value)
        else -> error("Unsupported type for \"$key\": $value")
      }
    }
    return map
  }

  // To cover a new native method, just add one entry here with its valid
  // (all required fields present) params.
  private val targets: List<Target> = listOf(
    Target(
      "nativeIsConnected",
      ThetaBleClientReactNativeModule::nativeIsConnected,
      mapOf("id" to 1)
    ),
    Target(
      "nativeSetBatteryLevelNotify",
      ThetaBleClientReactNativeModule::nativeSetBatteryLevelNotify,
      mapOf("id" to 1, "enable" to true)
    ),
    Target(
      "nativeContainService",
      ThetaBleClientReactNativeModule::nativeContainService,
      mapOf("id" to 1, "service" to "abc")
    ),
    Target(
      "nativeSetPluginControl",
      ThetaBleClientReactNativeModule::nativeSetPluginControl,
      mapOf("id" to 1, "value" to JavaOnlyMap())
    ),
    Target(
      "nativeCameraControlCommandV2GetOptions",
      ThetaBleClientReactNativeModule::nativeCameraControlCommandV2GetOptions,
      mapOf("id" to 1, "optionNames" to JavaOnlyArray().apply { pushString("a") })
    ),
    Target(
      "nativeWlanControlCommandV2SetAccessPointDynamically",
      ThetaBleClientReactNativeModule::nativeWlanControlCommandV2SetAccessPointDynamically,
      mapOf("id" to 1, "params" to JavaOnlyMap())
    )
  )

  @Test
  fun `all targets reject when any required field is missing or JSON-null`() {
    var executedCaseCount = 0

    for (target in targets) {
      for (key in target.validParams.keys) {
        // "missing" (key absent) and "JSON-null" (key present with null) are
        // distinct wire shapes and are checked by separate branches in
        // requireArgument, so both must be exercised.
        val variants = mapOf(
          "missing" to target.validParams - key,
          "null" to target.validParams + (key to null)
        )
        for ((variantName, entries) in variants) {
          val params = toJavaOnlyMap(entries)
          val promise = mock<Promise>()

          target.invoke(module, params, promise)

          verify(promise).reject(
            check<Throwable> {
              assertEquals("[${target.name}: $key $variantName]", MESSAGE_NO_ARGUMENT, it.message)
            }
          )
          executedCaseCount++
        }
      }
    }

    val testedMethodNames = targets.joinToString("\n") { "  - ${it.name}" }
    println(
      "Verified $executedCaseCount missing/null-argument cases across " +
        "${targets.size} native methods:\n$testedMethodNames"
    )
  }

  /** Catches a typo'd key in [Target.validParams] that would otherwise go unnoticed. */
  @Test
  fun `all targets do not reject with no argument when all fields are present`() {
    for (target in targets) {
      val params = toJavaOnlyMap(target.validParams)
      val promise = mock<Promise>()

      target.invoke(module, params, promise)

      verify(promise, never()).reject(
        argThat<Throwable> { message == MESSAGE_NO_ARGUMENT }
      )
    }

    val testedMethodNames = targets.joinToString("\n") { "  - ${it.name}" }
    println(
      "Verified ${targets.size} all-fields-present cases across " +
        "${targets.size} native methods:\n$testedMethodNames"
    )
  }
}
