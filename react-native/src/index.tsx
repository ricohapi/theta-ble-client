import { NativeModules, Platform } from 'react-native';


const LINKING_ERROR =
  'The package \'theta-ble-client-react-native\' doesn\'t seem to be linked. Make sure: \n\n' +
  Platform.select({ ios: '- You have run \'pod install\'\n', default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ThetaBleClientReactNative = NativeModules.ThetaBleClientReactNative
  ? NativeModules.ThetaBleClientReactNative
  : new Proxy(
    {},
    {
      get() {
        throw new Error(LINKING_ERROR);
      },
    },
  );

export function multiply(a: number, b: number): Promise<number> {
  return ThetaBleClientReactNative.multiply(a, b);
}

export * from './service';
export * from './theta-ble';
export * from './theta-device';
export * from './values';
