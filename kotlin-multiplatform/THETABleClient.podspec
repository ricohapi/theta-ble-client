Pod::Spec.new do |spec|
    spec.name                     = 'THETABleClient'
    spec.version                  = '1.3.0'
    spec.homepage                 = 'https://github.com/ricohapi/theta-ble-client'
    spec.source                   = { :http=> ''}
    spec.authors                  = 'Ricoh Co, Ltd.'
    spec.license                  = 'MIT'
    spec.summary                  = 'THETA BLE Client'
    spec.vendored_frameworks      = 'build/cocoapods/framework/THETABleClient.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.0'
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':kotlin-multiplatform',
        'PRODUCT_MODULE_NAME' => 'THETABleClient',
    }
                
    spec.script_phases = [
        {
            :name => 'Build THETABleClient',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end