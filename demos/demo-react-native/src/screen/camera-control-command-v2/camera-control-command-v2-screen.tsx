import * as React from 'react';
import { useDeviceContext } from '../../device-context';
import {
  BleServiceEnum,
  CameraControlCommandV2,
} from '../../modules/theta-ble-client';
import { SafeAreaView } from 'react-native-safe-area-context';
import styles from './styles';
import { Alert, ScrollView, Text, View } from 'react-native';
import { Item, ItemListView } from '../../components/ui/item-list';
import Button from '../../components/ui/button';
import { RootStackParamList } from '../../App';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';
const TITLE = 'Camera Control CommandV2';

interface CommandItem extends Item {
  value: {
    commandFunction: () => Promise<string>;
  };
}

function getJsonString(object: any) {
  return JSON.stringify(JSON.parse(JSON.stringify(object)), null, 2);
}

const CameraControlCommandV2Screen: React.FC<
  NativeStackScreenProps<RootStackParamList, 'CameraControlCommandV2'>
> = ({ navigation }) => {
  const { thetaDevice } = useDeviceContext();
  const [service, setService] = React.useState<CameraControlCommandV2>();
  const [selectedCommand, setSelectedCommand] = React.useState<CommandItem>();
  const [message, setMessage] = React.useState('');

  const commandList: CommandItem[] = [
    {
      name: 'getInfo',
      value: {
        commandFunction: async () => {
          if (service == null) {
            return ERROR_MESSAGE_UNSUPPORTED;
          }
          try {
            const result = await service.getInfo();
            return `OK getInfo()\n${getJsonString(result)}`;
          } catch (error) {
            return JSON.stringify(error, null, 2);
          }
        },
      },
    },
    {
      name: 'getState',
      value: {
        commandFunction: async () => {
          if (service == null) {
            return ERROR_MESSAGE_UNSUPPORTED;
          }
          try {
            const result = await service.getState();
            return `OK getState()\n${getJsonString(result)}`;
          } catch (error) {
            return JSON.stringify(error, null, 2);
          }
        },
      },
    },
    {
      name: 'getState2',
      value: {
        commandFunction: async () => {
          if (service == null) {
            return ERROR_MESSAGE_UNSUPPORTED;
          }
          try {
            const result = await service.getState2();
            return `OK getState2()\n${getJsonString(result)}`;
          } catch (error) {
            return JSON.stringify(error, null, 2);
          }
        },
      },
    },
    {
      name: 'setStateNotify',
      value: {
        commandFunction: async () => {
          if (service == null) {
            return ERROR_MESSAGE_UNSUPPORTED;
          }
          try {
            await service.setStateNotify((state, error) => {
              if (error) {
                setMessage(JSON.stringify(error, null, 2));
              } else {
                setMessage(`Notify state:\n${getJsonString(state)}`);
              }
            });
            return 'OK setStateNotify()';
          } catch (error) {
            return JSON.stringify(error, null, 2);
          }
        },
      },
    },
    {
      name: 'Options screen',
      value: {
        commandFunction: async () => {
          if (service == null) {
            return ERROR_MESSAGE_UNSUPPORTED;
          }
          navigation.navigate('Options');
          return '';
        },
      },
    },
    {
      name: 'releaseShutter',
      value: {
        commandFunction: async () => {
          if (service == null) {
            return ERROR_MESSAGE_UNSUPPORTED;
          }
          try {
            await service.releaseShutter();
            return 'OK';
          } catch (error) {
            return JSON.stringify(error, null, 2);
          }
        },
      },
    },
  ];

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
    const cameraControlCommandV2 = (await thetaDevice.getService(
      BleServiceEnum.CAMERA_CONTROL_COMMAND_V2,
    )) as CameraControlCommandV2 | undefined;
    if (cameraControlCommandV2 == null) {
      alertToGoBack(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    setService(cameraControlCommandV2);
  };

  const resetNotify = async () => {
    if (service == null) {
      return;
    }
    try {
      await service.setStateNotify();
    } catch (_) {}
  };

  const onSelected = (item: Item) => {
    console.log('selected: ' + item.name);
    setSelectedCommand(item);
    setMessage('');
    resetNotify();
  };

  const onExecute = () => {
    if (selectedCommand == null) {
      return;
    }
    selectedCommand.value.commandFunction().then(result => {
      setMessage(result);
    });
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
      <View style={styles.commandListContainer}>
        <ItemListView
          itemList={commandList}
          onSelected={onSelected}
          selectedItem={selectedCommand}
        />
      </View>
      <View style={styles.buttonViewContainer}>
        <View style={styles.buttonViewContainerLayout}>
          <Button
            style={styles.button}
            title="Execute"
            disabled={selectedCommand == null}
            onPress={onExecute}
          />
        </View>
      </View>
      <ScrollView style={styles.messageArea}>
        <Text style={styles.messageText}>{message}</Text>
      </ScrollView>
    </SafeAreaView>
  );
};

export default CameraControlCommandV2Screen;
