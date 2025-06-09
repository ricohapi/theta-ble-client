import React from 'react';
import { Alert, ScrollView, Text, TextInput, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import styles from './styles';
import Button from '../../../components/ui/button';
import {
  BleServiceEnum,
  CameraControlCommandV2,
} from '../../../modules/theta-ble-client';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../../App';
import { useDeviceContext } from '../../../device-context';

const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';
const TITLE = 'OptionsByString';


const OptionsByStringScreen: React.FC<
  NativeStackScreenProps<RootStackParamList, 'OptionsByString'>
> = ({ navigation }) => {
  const { thetaDevice } = useDeviceContext();
  const [service, setService] = React.useState<CameraControlCommandV2>();
  const [message, setMessage] = React.useState('');
  const [optionKey, setOptionKey] = React.useState('');

  React.useEffect(() => {
    navigation.setOptions({ title: 'OptionsByString' });
  }, [navigation]);


  const onPressGet = async () => {
    if (optionKey.length === 0 || service == null) {
      return;
    }
    try {
      const options = await service.getOptionsByString([optionKey]);
      setMessage(JSON.stringify(options, null, '\t'));
    } catch (error) {
      if (error instanceof Error) {
        setMessage(error.name + ': ' + error.message);
      }
      console.log('failed getOptionsByString()');
    }
  };

  const alertToGoBack = (message: string) => {
    Alert.alert(TITLE, message, [
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
    const cameraControlCommandV2 = await thetaDevice.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2) as CameraControlCommandV2 | undefined;
    if (cameraControlCommandV2 == null) {
      alertToGoBack(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    setService(cameraControlCommandV2);
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
      <View style={styles.topViewContainer}>
        <View style={styles.rowContainerLayout}>
          <TextInput
            style={styles.input}
            value={optionKey}
            onChangeText={setOptionKey}
            placeholder="Enter option key"
            autoCapitalize='none'
          />
          <Button
            style={styles.button}
            title="Get"
            onPress={onPressGet}
            disabled={optionKey.length === 0}
          />
        </View>
      </View>
      <View style={styles.bottomViewContainer}>
        <ScrollView style={styles.messageArea}>
          <Text style={styles.messageText}>{message}</Text>
        </ScrollView>
      </View>
    </SafeAreaView>
  );
};

export default OptionsByStringScreen;
