import * as React from 'react';

import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { NavigationContainer } from '@react-navigation/native';
import { DeviceProvider } from './device-context';
import MenuScreen from './screen/menu-screen';
import CameraStatusScreen from './screen/camera-status-screen';
import CameraControlCommandV2Screen from './screen/camera-control-command-v2';

const Stack = createNativeStackNavigator();

const RootStack = () => {
  return (
    <Stack.Navigator
      initialRouteName="<Menu>"
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
