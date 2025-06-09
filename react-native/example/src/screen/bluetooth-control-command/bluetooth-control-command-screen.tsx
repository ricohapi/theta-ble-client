import * as React from 'react';
import { useDeviceContext } from '../../device-context';
import { BleServiceEnum, BluetoothControlCommand, PeripheralDevice } from '../../modules/theta-ble-client';
import { SafeAreaView } from 'react-native-safe-area-context';
import styles from './styles';
import { Alert, ScrollView, Text, View } from 'react-native';
import Button from '../../components/ui/button';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../App';
import { DeviceListView } from './device-list-view';
import { InputNumber } from '../../components/ui/input-number';

const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';
const TITLE = 'Bluetooth Control Command';
const TIMEOUT = 30_000;

enum ScanStatus {
  IDLE,
  SCAN,
  SCANNING,
  STOPPING,
}

const BluetoothControlCommandScreen: React.FC<
  NativeStackScreenProps<RootStackParamList, 'BluetoothControlCommand'>
> = ({ navigation }) => {
  const { thetaDevice } = useDeviceContext();
  const [service, setService] = React.useState<BluetoothControlCommand>();
  const [message, setMessage] = React.useState('');
  const [deviceList, setDeviceList] = React.useState<PeripheralDevice[]>([]);
  const [scanStatus, SetScanStatus] = React.useState(ScanStatus.IDLE);
  const [selectedDevice, setSelectedDevice] = React.useState<PeripheralDevice>();
  const [scanTimeout, setScanTimeout] = React.useState(TIMEOUT);

  const initList = () => {
    setDeviceList([]);
    setSelectedDevice(undefined);
  };

  const onScan = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      initList();
      SetScanStatus(ScanStatus.SCAN);
      const result = await service.scanPeripheralDevice(scanTimeout);
      SetScanStatus(ScanStatus.IDLE);
      setDeviceList(result);
      setMessage('Scan OK.');
    } catch (error) {
      SetScanStatus(ScanStatus.IDLE);
      setMessage(JSON.stringify(error));
    }
  };

  const onScanStart = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      initList();
      SetScanStatus(ScanStatus.SCANNING);
      await service.scanPeripheralDeviceStart(scanTimeout, (device) => {
        console.log('onNotify:' + JSON.stringify(device));
        setMessage('onNotify:' + JSON.stringify(device));
        setDeviceList((prevState) => {
          const newList = [...prevState];
          const index = newList.findIndex((item) => item.macAddress === device.macAddress);
          if (index >= 0) {
            newList.splice(index, 1, device);
          } else {
            newList.push(device);
          }
          return newList;
        });
      },
      (devList) => {
        setMessage(`Scan onCompleted. ${devList.length} device found.`);
        console.log('Scan onCompleted:' + JSON.stringify(devList));
        SetScanStatus(ScanStatus.IDLE);
      });

      setMessage('Scan Start OK.');
    } catch (error) {
      SetScanStatus(ScanStatus.IDLE);
      setMessage(JSON.stringify(error));
    }
  };

  const onScanStop = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      SetScanStatus(ScanStatus.STOPPING);
      await service.scanPeripheralDeviceStop();
      SetScanStatus(ScanStatus.IDLE);
      setMessage('Scan Stop OK.');
    } catch (error) {
      SetScanStatus(ScanStatus.SCANNING);
      setMessage(JSON.stringify(error));
    }
  };

  const onConnect = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    if (selectedDevice == null) {
      console.log('no device');
      return;
    }
    try {
      await service.connectPeripheralDevice(selectedDevice);
      console.log('Connect: ' + JSON.stringify(selectedDevice));
      setMessage('Connect OK. ' + selectedDevice.device);
    } catch (error) {
      setMessage(JSON.stringify(error));
    }
  };

  const onDelete = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    if (selectedDevice == null) {
      return;
    }
    try {
      await service.deletePeripheralDevice(selectedDevice);
      console.log('Delete: ' + JSON.stringify(selectedDevice));
      setMessage('Delete OK. ' + selectedDevice.device);
    } catch (error) {
      setMessage(JSON.stringify(error));
    }
  };

  const alertToGoBack = (alertMessage: string) => {
    Alert.alert(TITLE, alertMessage, [
      {
        text: 'OK',
        onPress: () => {
          navigation.goBack();
        },
      },
    ]);
  };
  
  const initService = async () => {
    if (thetaDevice == null) {
      alertToGoBack(ERROR_MESSAGE_NO_DEVICE);
      return;
    }
    if (!await thetaDevice.isConnected()) {
      alertToGoBack(ERROR_MESSAGE_NOT_CONNECTED);
      return;
    }
    const bluetoothControlCommand = await thetaDevice.getService(BleServiceEnum.BLUETOOTH_CONTROL_COMMAND) as BluetoothControlCommand | undefined;
    if (bluetoothControlCommand == null) {
      alertToGoBack(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    setService(bluetoothControlCommand);
  };

  React.useEffect(() => {
    initService();
    /* eslint-disable-next-line react-hooks/exhaustive-deps */
  }, []);
  
  return (
    <SafeAreaView
      style={styles.safeAreaContainer}
      edges={['left', 'right', 'bottom']}
    >
      <View>
        <ScrollView style={styles.messageArea}>
          <Text style={styles.messageText}>{message}</Text>
        </ScrollView>
      </View>
      <InputNumber
        title="Timeout"
        placeHolder="millisecond"
        value={scanTimeout}
        onChange={(value) => {
          if (value != null) {
            setScanTimeout(value);
          }
        }}
      />
      <View style={styles.buttonViewContainerLayout}>
        <Button
          style={styles.button}
          title="Scan"
          onPress={onScan}
          disabled={scanStatus !== ScanStatus.IDLE}
        />
        <Button
          style={styles.button}
          title="Scan Start"
          onPress={onScanStart}
          disabled={scanStatus !== ScanStatus.IDLE}
        />
        <Button
          style={styles.button}
          title="Scan Stop"
          onPress={onScanStop}
          disabled={scanStatus !== ScanStatus.SCANNING}
        />
      </View>
      <View style={styles.listContainerLayout}>
        <DeviceListView
          deviceList={deviceList}
          onSelectedDevice={setSelectedDevice}
          selectedDevice={selectedDevice}
        />
      </View>
      <View style={styles.buttonViewContainerLayout}>
        <Button
          style={styles.button}
          title="Connect"
          onPress={onConnect}
          disabled={selectedDevice == null}
        />
        <Button
          style={styles.button}
          title="Delete"
          onPress={onDelete}
          disabled={selectedDevice == null}
        />
      </View>
    </SafeAreaView>
  );
};

export default BluetoothControlCommandScreen;
