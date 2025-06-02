import * as React from 'react';
import { useDeviceContext } from '../../device-context';
import {
  BleServiceEnum,
  ConnectedWifiInfo,
  NetworkTypeEnum,
  NotifyError,
  WlanControlCommandV2,
} from '../../modules/theta-ble-client';
import { SafeAreaView } from 'react-native-safe-area-context';
import styles from './styles';
import { Alert, ScrollView, Text, View } from 'react-native';
import Button from '../../components/ui/button';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../App';
import { EnumEdit } from '../../components/options/enum-edit';

const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';
const TITLE = 'WLAN Control Command V2';

const WlanControlCommandV2Screen: React.FC<
  NativeStackScreenProps<RootStackParamList, 'WlanControlCommandV2'>
> = ({ navigation }) => {
  const { thetaDevice } = useDeviceContext();
  const [service, setService] = React.useState<WlanControlCommandV2>();
  const [message, setMessage] = React.useState('');
  const [currentNetworkType, setCurrentNetworkType] =
    React.useState<NetworkTypeEnum>();
  const scrollViewRef = React.useRef<ScrollView>(null);

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
    if (!(await thetaDevice.isConnected())) {
      alertToGoBack(ERROR_MESSAGE_NOT_CONNECTED);
      return;
    }
    const wlanControlCommandV2 = (await thetaDevice.getService(
      BleServiceEnum.WLAN_CONTROL_COMMAND_V2,
    )) as WlanControlCommandV2 | undefined;
    if (wlanControlCommandV2 == null) {
      alertToGoBack(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    setService(wlanControlCommandV2);
  };

  const resetNotify = async () => {
    if (service == null) {
      return;
    }
    try {
      await service.setNetworkTypeNotify();
      await service.setConnectedWifiInfoNotify();
    } catch (_) {}
  };

  const addMessage = (newMessage: string) => {
    setMessage(prevItem => {
      return prevItem + '\n' + newMessage;
    });
  };

  const messageScrollToBottom = () => {
    scrollViewRef.current?.scrollToEnd({ animated: true });
  };

  const onSetNetworkType = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    if (currentNetworkType == null) {
      return;
    }
    try {
      await service.setNetworkType(currentNetworkType);
      console.log('setNetworkType: ' + currentNetworkType);
      setMessage('setNetworkType OK. ' + currentNetworkType);
    } catch (error) {
      setMessage(JSON.stringify(error, null, 2));
    }
  };

  const setNetworkTypeNotify = async (
    callback?: (value?: NetworkTypeEnum, error?: NotifyError) => void,
  ) => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      await service.setNetworkTypeNotify(callback);
      const newMessage = `setNetworkTypeNotify: ${
        callback != null ? 'enabled' : 'disabled'
      }`;
      console.log(newMessage);
      setMessage(newMessage);
    } catch (error) {
      setMessage(JSON.stringify(error, null, 2));
    }
  };

  const onSetNetworkTypeNotify = async () => {
    setNetworkTypeNotify(networkType => {
      const newMessage = `Notify NetworkType: ${networkType}`;
      addMessage(newMessage);
      messageScrollToBottom();
    });
  };

  const onResetNetworkTypeNotify = async () => {
    setNetworkTypeNotify();
  };

  const onGetConnectedWifiInfo = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      const info = await service.getConnectedWifiInfo();
      const newMessage = `getConnectedWifiInfo OK.\n${JSON.stringify(
        info,
        null,
        2,
      )}`;
      console.log(newMessage);
      setMessage(newMessage);
    } catch (error) {
      setMessage(JSON.stringify(error, null, 2));
    }
  };

  const setConnectedWifiInfoNotify = async (
    callback?: (value?: ConnectedWifiInfo, error?: NotifyError) => void,
  ) => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      await service.setConnectedWifiInfoNotify(callback);
      const newMessage = `setConnectedWifiInfoNotify: ${
        callback != null ? 'enabled' : 'disabled'
      }`;
      console.log(newMessage);
      setMessage(newMessage);
    } catch (error) {
      setMessage(JSON.stringify(error, null, 2));
    }
  };

  const onSetConnectedWifiInfoNotify = async () => {
    setConnectedWifiInfoNotify(info => {
      const newMessage = `Notify ConnectedWifiInfo: ${JSON.stringify(
        info,
        null,
        2,
      )}`;
      addMessage(newMessage);
      messageScrollToBottom();
    });
  };

  const onResetConnectedWifiInfoNotify = async () => {
    setConnectedWifiInfoNotify();
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
      edges={['left', 'right', 'bottom']}>
      <View style={styles.buttonViewContainerLayout}>
        <EnumEdit
          title={'NetworkType'}
          option={currentNetworkType}
          onChange={networkType => {
            setCurrentNetworkType(networkType);
          }}
          optionEnum={NetworkTypeEnum}
        />
        <Button
          style={styles.button}
          title="Set"
          onPress={onSetNetworkType}
          disabled={!currentNetworkType}
        />
      </View>
      <View style={styles.buttonViewContainerLayout}>
        <Button
          style={styles.button}
          title="Set Notify"
          onPress={onSetNetworkTypeNotify}
        />
        <Button
          style={styles.button}
          title="Reset Notify"
          onPress={onResetNetworkTypeNotify}
        />
      </View>
      <View style={styles.buttonViewContainerLayout}>
        <Text style={styles.labelText}>ConnectedWifiInfo</Text>
        <Button
          style={styles.button}
          title="Get"
          onPress={onGetConnectedWifiInfo}
        />
      </View>
      <View style={styles.buttonViewContainerLayout}>
        <Button
          style={styles.button}
          title="Set Notify"
          onPress={onSetConnectedWifiInfoNotify}
        />
        <Button
          style={styles.button}
          title="Reset Notify"
          onPress={onResetConnectedWifiInfoNotify}
        />
      </View>
      <View style={styles.buttonViewContainerLayout}>
        <Button
          style={styles.button}
          title="Scan SSID"
          onPress={() => {
            navigation.navigate('WlanSsid');
          }}
        />
      </View>
      <ScrollView style={styles.messageArea} ref={scrollViewRef}>
        <Text style={styles.messageText}>{message}</Text>
      </ScrollView>
      <View style={styles.buttonViewContainerLayout}>
        <Button
          style={styles.button}
          title="Scan SSID"
          onPress={() => {
            navigation.navigate('WlanSsid');
          }}
        />
      </View>
    </SafeAreaView>
  );
};

export default WlanControlCommandV2Screen;
