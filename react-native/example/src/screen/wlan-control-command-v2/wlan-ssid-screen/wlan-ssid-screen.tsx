import React from 'react';
import { Alert, ScrollView, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../../App';
import { BleServiceEnum, WlanControlCommandV2 } from '../../../modules/theta-ble-client';
import { useDeviceContext } from '../../../device-context';
import Button from '../../../components/ui/button';
import { InputNumber } from '../../../components/ui/input-number';
import styles from './styles';
import { SsidListView } from './ssid-list-view';

const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';
const TITLE = 'Scan SSID';
const TIMEOUT = 30_000;

enum ScanStatus {
  IDLE,
  SCAN,
  SCANNING,
  STOPPING,
}

const WlanSsidScreen: React.FC<
  NativeStackScreenProps<RootStackParamList, 'WlanSsid'>
> = ({ navigation }) => {
  const { thetaDevice } = useDeviceContext();
  const [service, setService] = React.useState<WlanControlCommandV2>();
  const [message, setMessage] = React.useState('');
  const [ssidList, setSsidList] = React.useState<string[]>([]);
  const [scanStatus, SetScanStatus] = React.useState(ScanStatus.IDLE);
  const [selectedSsid, setSelectedSsid] = React.useState<string>();
  const [scanTimeout, setScanTimeout] = React.useState(TIMEOUT);
  const scrollViewRef = React.useRef<ScrollView>(null);

  const initList = () => {
    setSsidList([]);
    setSelectedSsid(undefined);
  };

  const addMessage = (newMessage: string) => {
    setMessage(prevItem => {
      return prevItem + '\n' + newMessage;
    }); 
  };

  const messageScrollToBottom = () => {
    scrollViewRef.current?.scrollToEnd({ animated: true });
  };

  const onScanStart = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      initList();
      SetScanStatus(ScanStatus.SCANNING);
      await service.scanSsidStart(scanTimeout, (ssid) => {
        const newMessage = 'onNotify SSID: ' + ssid;
        console.log(newMessage);
        addMessage(newMessage);
        messageScrollToBottom();  
        setSsidList((prevState) => {
          const newList = [...prevState];
          const index = newList.findIndex((item) => item === ssid);
          if (index >= 0) {
            newList.splice(index, 1, ssid);
          } else {
            newList.push(ssid);
          }
          return newList;
        });
      },
      (list) => {
        console.log('Scan onCompleted:' + JSON.stringify(list));
        const newMessage = `Scan onCompleted. ${list.length} ssid found.`;
        addMessage(newMessage);
        messageScrollToBottom();  
        SetScanStatus(ScanStatus.IDLE);
      });

      setMessage('Scan Start OK.');
    } catch (error) {
      SetScanStatus(ScanStatus.IDLE);
      setMessage(JSON.stringify(error, null, 2));
    }
  };

  const onScanStop = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      SetScanStatus(ScanStatus.STOPPING);
      await service.scanSsidStop();
      SetScanStatus(ScanStatus.IDLE);
      addMessage('Scan Stop OK.');
      messageScrollToBottom();  
    } catch (error) {
      SetScanStatus(ScanStatus.SCANNING);
      setMessage(JSON.stringify(error, null, 2));
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
    const wlanControlCommandV2 = await thetaDevice.getService(BleServiceEnum.WLAN_CONTROL_COMMAND_V2) as WlanControlCommandV2 | undefined;
    if (wlanControlCommandV2 == null) {
      alertToGoBack(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    setService(wlanControlCommandV2);
    setNetworkTypeNotify(wlanControlCommandV2);
  };

  const setNetworkTypeNotify = async (wlanControlCommandV2: WlanControlCommandV2) => {
    try {
      await wlanControlCommandV2.setNetworkTypeNotify((networkType) => {
        const newMessage = `onChange NetworkType: ${networkType}`;
        addMessage(newMessage);
        messageScrollToBottom();  
      });
    } catch (error) {
      setMessage(JSON.stringify(error, null, 2));
    }
  };
  
  const resetNotify = async () => {
    if (service == null) {
      return;
    }
    try {
      await service.setNetworkTypeNotify();
      /* eslint-disable-next-line no-empty */
    } catch (_) {}
  };

  const gotoSetAccessPoint = async () => {
    if (service == null || selectedSsid == null) {
      return;
    }
    try {
      await service.scanSsidStop();
    } catch (_) {
      /* empty */
    }
    navigation.navigate('WlanSetAccessPoint', { ssid: selectedSsid });
  };

  React.useEffect(() => {
    initService();
    return () => {
      resetNotify();
    };
    /* eslint-disable-next-line react-hooks/exhaustive-deps */
  }, []);
  
  return (
    <SafeAreaView
      style={styles.safeAreaContainer}
      edges={['left', 'right', 'bottom']}
    >
      <View>
        <ScrollView
          style={styles.messageArea}
          ref={scrollViewRef}
        >
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
        <SsidListView
          ssidList={ssidList}
          onSelectedSsid={setSelectedSsid}
          selectedSsid={selectedSsid}
        />
      </View>
      <View style={styles.buttonViewContainerLayout}>
        <Button
          style={styles.button}
          title="Set AccessPoint"
          onPress={gotoSetAccessPoint}
          disabled={!selectedSsid}
        />
      </View>
    </SafeAreaView>
  );
};

export default WlanSsidScreen;
