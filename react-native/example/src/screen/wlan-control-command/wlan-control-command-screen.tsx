import * as React from 'react';
import { useDeviceContext } from '../../device-context';
import { BleServiceEnum, WlanControlCommand } from '../../modules/theta-ble-client';
import { SafeAreaView } from 'react-native-safe-area-context';
import styles from './styles';
import { Alert, ScrollView, Text, View } from 'react-native';
import Button from '../../components/ui/button';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../App';

const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';
const TITLE = 'WLAN Control Command ';

const WlanControlCommandScreen: React.FC<
  NativeStackScreenProps<RootStackParamList, 'WlanControlCommand'>
> = ({ navigation }) => {
  const { thetaDevice } = useDeviceContext();
  const [service, setService] = React.useState<WlanControlCommand>();
  const [message, setMessage] = React.useState('');
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
    if (!await thetaDevice.isConnected()) {
      alertToGoBack(ERROR_MESSAGE_NOT_CONNECTED);
      return;
    }
    const wlanControlCommand = await thetaDevice.getService(BleServiceEnum.WLAN_CONTROL_COMMAND) as WlanControlCommand | undefined;
    if (wlanControlCommand == null) {
      alertToGoBack(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    setService(wlanControlCommand);
  };

  const onGetWlanPasswordState = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      const value = await service.getWlanPasswordState();
      const newMessage = `getWlanPasswordState OK.\n${value}`;
      console.log(newMessage);
      setMessage(newMessage);
    } catch (error) {
      setMessage(JSON.stringify(error, null, 2));
    }
  };

  React.useEffect(() => {
    initService();
  }, []);

  return (
    <SafeAreaView
      style={styles.safeAreaContainer}
      edges={['left', 'right', 'bottom']}
    >
      <View style={styles.buttonViewContainerLayout}>
        <Text style={styles.labelText}>WlanPasswordState</Text>
        <Button
          style={styles.button}
          title="Get"
          onPress={onGetWlanPasswordState}
        />
      </View>
      <ScrollView
        style={styles.messageArea}
        ref={scrollViewRef}
      >
        <Text style={styles.messageText}>{message}</Text>
      </ScrollView>
    </SafeAreaView>
  );
};

export default WlanControlCommandScreen;
