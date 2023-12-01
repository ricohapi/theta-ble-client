import * as React from 'react';

import { Button, Stack } from '@react-native-material/core';
import { PermissionsAndroid, Platform, Text } from 'react-native';
import styles from './styles';
import {
  scan,
  CaptureModeEnum,
  BleServiceEnum,
  CameraInformation,
  ShootingControlCommand,
  ThetaDevice,
} from 'theta-ble-client-react-native';
import { setBleUuidWebApi } from '../../theta-webapi/set-ble-uuid-webapi';
import { setBleOnWebApi } from '../../theta-webapi/set-ble-on-webapi';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useDeviceContext } from '../../device-context';
import { getThetaInfoWebApi } from '../../theta-webapi/get-theta-info-webapi';
import DefaultPreference from 'react-native-default-preference';

const KEY_LAST_DEVICE_NAME = 'lastDeviceName';
const KEY_LAST_USE_UUID = 'lastUseUuid';

const MenuScreen = ({ navigation }) => {
  const appUuid = '6BEDD7A3-4E01-4FE4-9DFB-03BFF23ECFD3';

  const [infoText, setInfoText] = React.useState('Init');
  const [devName, setDevName] = React.useState<string>();
  const [useUuid, setUseUuid] = React.useState(true);
  const { thetaDevice, setThetaDevice } = useDeviceContext();

  const requestPermission = async () => {
    if (Platform.OS !== 'android') {
      return;
    }
    try {
      if (Platform.Version > 30) {
        await PermissionsAndroid.requestMultiple([
          PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT,
          PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN,
        ]);
      } else {
        await PermissionsAndroid.requestMultiple([
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        ]);
      }
    } catch (err) {
      console.warn(err);
    }
  };

  async function scanBle(name: string) {
    try {
      setInfoText(`Scanning... ${devName}`);
      const device = await scan({
        name,
        timeout: {
          timeoutScan: 30_000,
          timeoutPeripheral: 10_000,
          timeoutConnect: 30_000,
          timeoutTakePicture: 10_000,
        },
      }) as ThetaDevice | undefined;
      if (device) {
        setInfoText(`Scan. Found device: ${devName}`);
        if (thetaDevice) {
          thetaDevice.release();
        }
        setThetaDevice(device);
      } else {
        setInfoText(`Error. ${devName} not found.`);
      }
    } catch (_) {
      setInfoText(`Error. ${devName} not found.`);
    }
  }

  async function connect() {
    if (thetaDevice == null) {
      setInfoText('Error. No device.');
      return;
    }
    const device = thetaDevice;
    try {
      setInfoText(`Connecting... ${devName}${useUuid ? ' with uuid' : ''}`);
      await device.connect(useUuid ? appUuid : undefined);
      setInfoText(`Connected. ${devName}`);
    } catch (error) {
      console.log(JSON.stringify(error));
      setInfoText(`Error. connect. ${devName}` + error);
    }
  }

  async function disconnect() {
    if (thetaDevice == null) {
      setInfoText('Error. No device.');
      return;
    }
    const device = thetaDevice;
    try {
      await device.disconnect();
      setInfoText(`Disconnected. ${devName}`);
    } catch (_) {
      setInfoText(`Error. disconnect. ${devName}`);
    }
  }

  async function getInfo() {
    if (thetaDevice == null) {
      setInfoText('Error. No device.');
      return;
    }
    const device = thetaDevice;
    const service = await device.getService(BleServiceEnum.CAMERA_INFORMATION) as CameraInformation | undefined;
    if (service == null) {
      setInfoText(
        'CAMERA_INFORMATION is unsupported.',
      );
      return;
    }
    try {
      const firmware = await service.getFirmwareRevision();
      const maker = await service.getManufacturerName();
      const model = await service.getModelNumber();
      const serial = await service.getSerialNumber();
      const wlan = await service.getWlanMacAddress();
      const bluetooth = await service.getBluetoothMacAddress();

      setInfoText(
        ` firmware: ${firmware}\n maker: ${maker}\n model: ${model}\n serial: ${serial}\n wlan: ${wlan}\n ble: ${bluetooth}`,
      );
    } catch (_) {
      setInfoText('Error. Get Information.');
    }
  }

  async function sleep(ms: number) {
    return new Promise<void>(resolve => setTimeout(resolve, ms));
  }

  async function takePicture() {
    if (thetaDevice == null) {
      setInfoText('Error. No device.');
      return;
    }
    const device = thetaDevice;
    const service = await device.getService(BleServiceEnum.SHOOTING_CONTROL_COMMAND) as ShootingControlCommand | undefined;
    if (service == null) {
      setInfoText(
        'SHOOTING_CONTROL_COMMAND is unsupported.',
      );
      return;
    }
    try {
      const captureMode = await service.getCaptureMode();
      if (captureMode !== CaptureModeEnum.IMAGE) {
        setInfoText('Change capture mode...');
        await service.setCaptureMode(CaptureModeEnum.IMAGE);
        await sleep(1000); // Wait a little or you'll fail
      }

      setInfoText('Start take a picture.');
      service.takePicture(error => {
        if (error) {
          setInfoText(`End take a picture. error ${error}`);
        } else {
          setInfoText('End take a picture.');
        }
      });
    } catch (error) {
      setInfoText(`End take a picture. call error ${error}`);
    }
  }

  async function connectWifi() {
    let deviceName: string | undefined;
    try {
      setInfoText('Connect wifi...');
      const thetaInfo = await getThetaInfoWebApi();
      if (thetaInfo == null) {
        setInfoText('Error. wifi connect');
        return;
      }
      deviceName = thetaInfo.serialNumber;
      setDevName(deviceName);
      setUseUuid(false);
      setInfoText(`wifi connected. ${thetaInfo.serialNumber}`);
      saveDevice(false, deviceName);
    } catch (_) {
      setInfoText('Error. wifi connect.');
      return;
    }

    try {
      const name = await setBleUuidWebApi(appUuid);
      if (name) {
        deviceName = name;
        setDevName(name);
        setUseUuid(true);
        await setBleOnWebApi(false);
        await setBleOnWebApi(true);
        saveDevice(true, name);
      } else {
        setUseUuid(false);
        saveDevice(false, deviceName);
      }
    } catch (_) {
      setInfoText('Error. wifi connect.');
    }
  }

  const loadDevice = async () => {
    try {
      const name = await DefaultPreference.get(KEY_LAST_DEVICE_NAME);
      const use = await DefaultPreference.get(KEY_LAST_USE_UUID);
      if (name) {
        setDevName(name);
        setUseUuid(use === 'true');
      }
    } catch (_) {
      console.log('Error. loadDevice');
    }
  };

  const saveDevice = async (use: boolean, name?: string) => {
    if (name == null) {
      return;
    }
    try {
      await DefaultPreference.set(KEY_LAST_DEVICE_NAME, name);
      await DefaultPreference.set(KEY_LAST_USE_UUID, String(use));
    } catch (_) {
      console.log('Error. saveDevice');
    }
  };

  React.useEffect(() => {
    requestPermission();
    loadDevice();
    const unsubscribe = navigation.addListener('focus', () => {
      console.log('onFocus');
    });

    // Return the function to unsubscribe from the event so it gets removed on unmount
    return unsubscribe;
    /* eslint-disable-next-line react-hooks/exhaustive-deps */
  }, [navigation]);

  return (
    <SafeAreaView style={styles.container}>
      <Stack fill center spacing={4}>
        <Text style={styles.boldText}>THETA BLE Client</Text>
        <Button
          title="Connect Wifi"
          onPress={() => {
            connectWifi();
          }}
        />
        <Text style={styles.text}>
          {'device: '}
          {devName != null ? devName : 'null'}
          {devName && useUuid && ' use uuid'}
        </Text>
        <Button
          title="Scan BLE"
          onPress={() => {
            if (devName == null) {
              return;
            }
            scanBle(devName);
          }}
        />
        <Button
          title="Connect"
          onPress={() => {
            connect();
          }}
        />
        <Button
          title="Info"
          onPress={() => {
            getInfo();
          }}
        />
        <Button
          title="Camera Status"
          onPress={() => {
            navigation.navigate('CameraStatus');
          }}
        />
        <Button
          title="Take Picture"
          onPress={() => {
            takePicture();
          }}
        />
        <Button
          title="Camera Control Command V2"
          onPress={() => {
            navigation.navigate('CameraControlCommandV2');
          }}
        />
        <Button
          title="Disconnect"
          onPress={() => {
            disconnect();
          }}
        />
        <Button
          title="Scan SSID"
          onPress={() => {
            navigation.navigate('ScanSsid');
          }}
        />
        <Text style={styles.text}>
          Info:{'\n'}
          {infoText}
        </Text>
      </Stack>
    </SafeAreaView>
  );
};
export default MenuScreen;
