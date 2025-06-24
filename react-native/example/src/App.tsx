import * as React from 'react';

import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { NavigationContainer } from '@react-navigation/native';
import { DeviceProvider } from './device-context';
import MenuScreen from './screen/menu-screen';
import CameraStatusScreen from './screen/camera-status-screen';
import CameraControlCommandV2Screen from './screen/camera-control-command-v2';
import ScanSsidScreen from './screen/scan-ssid-screen';
import OptionsScreen from './screen/camera-control-command-v2/options-screen';
import OptionsByStringScreen from './screen/camera-control-command-v2/options-by-string-screen';
import BluetoothControlCommandScreen from './screen/bluetooth-control-command';
import WlanControlCommandScreen from './screen/wlan-control-command';
import WlanControlCommandV2Screen from './screen/wlan-control-command-v2';
import WlanSsidScreen from './screen/wlan-control-command-v2/wlan-ssid-screen';
import SetAccessPointScreen from './screen/wlan-control-command-v2/set-access-point-screen';

export type RootStackParamList = {
  Menu: undefined,
  BluetoothControlCommand: undefined,
  CameraStatus: undefined,
  CameraControlCommandV2: undefined,
  Options: undefined,
  OptionsByString: undefined,
  ScanSsid: undefined,
  WlanControlCommand: undefined,
  WlanControlCommandV2: undefined,
  WlanSsid: undefined,
  WlanSetAccessPoint: undefined | {
    ssid?: string,
  },
};
const Stack = createNativeStackNavigator<RootStackParamList>();

const RootStack = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerStyle: {
          backgroundColor: '#265366',
        },
        headerTintColor: '#fff',
        headerTitleStyle: {
          fontWeight: 'bold',
        },
      }}>
      <Stack.Screen
        options={{ title: 'THETA BLE Client' }}
        name="Menu"
        component={MenuScreen}
      />
      <Stack.Screen
        options={{ title: 'Camera Status' }}
        name="CameraStatus"
        component={CameraStatusScreen}
      />
      <Stack.Screen
        options={{ title: 'CameraControlCommandV2' }}
        name="CameraControlCommandV2"
        component={CameraControlCommandV2Screen}
      />
      <Stack.Screen
        options={{ title: 'BluetoothControlCommand' }}
        name="BluetoothControlCommand"
        component={BluetoothControlCommandScreen}
      />
      <Stack.Screen
        options={{ title: 'WlanControlCommand' }}
        name="WlanControlCommand"
        component={WlanControlCommandScreen}
      />
      <Stack.Screen
        options={{ title: 'WlanControlCommandV2' }}
        name="WlanControlCommandV2"
        component={WlanControlCommandV2Screen}
      />
      <Stack.Screen
        options={{ title: 'WLAN Scan SSID' }}
        name="WlanSsid"
        component={WlanSsidScreen}
      />
      <Stack.Screen
        options={{ title: 'Set AccessPoint' }}
        name="WlanSetAccessPoint"
        component={SetAccessPointScreen}
      />
      <Stack.Screen
        options={{ title: 'Options' }}
        name="Options"
        component={OptionsScreen}
      />
      <Stack.Screen
        options={{ title: 'OptionsByString' }}
        name="OptionsByString"
        component={OptionsByStringScreen}
      />
      <Stack.Screen
        options={{ title: 'Scan SSID' }}
        name="ScanSsid"
        component={ScanSsidScreen}
      />
    </Stack.Navigator>
  );
};
export default function App() {
  return (
    <NavigationContainer>
      <DeviceProvider>
        <RootStack />
      </DeviceProvider>
    </NavigationContainer>
  );
}
