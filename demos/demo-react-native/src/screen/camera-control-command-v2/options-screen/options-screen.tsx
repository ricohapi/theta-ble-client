import React from 'react';
import { Alert, ScrollView, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import styles from './styles';
import Button from '../../../components/ui/button';
import {
  BleServiceEnum,
  CameraControlCommandV2,
  ThetaOptions,
} from '../../../modules/theta-ble-client';
import { ItemSelectorView } from '../../../components/ui/item-list';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../../App';
import { useDeviceContext } from '../../../device-context';
import { OptionItem, optionList } from './option-list';

const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';
const TITLE = 'Options';

const OptionsScreen: React.FC<
  NativeStackScreenProps<RootStackParamList, 'Options'>
> = ({ navigation }) => {
  const { thetaDevice } = useDeviceContext();
  const [service, setService] = React.useState<CameraControlCommandV2>();
  const [selectedOption, setSelectedOption] = React.useState<OptionItem>();
  const [message, setMessage] = React.useState('');
  const [editOptions, setEditOptions] = React.useState<ThetaOptions>();

  React.useEffect(() => {
    navigation.setOptions({ title: 'Options' });
  }, [navigation]);

  const onChangeOption = (item: OptionItem) => {
    setMessage('');
    setSelectedOption(item);
    setEditOptions(item.value.defaultValue);
  };

  const onPressGet = async () => {
    if (selectedOption == null || service == null) {
      return;
    }
    try {
      const options = await service.getOptions([
        selectedOption.value.optionName,
      ]);
      setMessage(JSON.stringify(options, null, '\t'));
      setEditOptions(options);
    } catch (error) {
      if (error instanceof Error) {
        setMessage(error.name + ': ' + error.message);
      }
      console.log('failed getOptions()');
    }
  };

  const onPressSet = async () => {
    if (selectedOption == null || editOptions == null || service == null) {
      return;
    }
    selectedOption.value.onWillSet?.(editOptions);
    console.log('call setOptions(): ' + JSON.stringify(editOptions));
    try {
      await service.setOptions(editOptions);
      setMessage('OK.\n' + JSON.stringify(editOptions));
    } catch (error) {
      if (error instanceof Error) {
        setMessage(error.name + ': ' + error.message);
      }
      console.log('failed setOptions()');
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

  React.useEffect(() => {
    initService();
    /* eslint-disable-next-line react-hooks/exhaustive-deps */
  }, []);

  return (
    <SafeAreaView
      style={styles.safeAreaContainer}
      edges={['left', 'right', 'bottom']}>
      <ScrollView style={styles.inputArea}>
        <View style={styles.topViewContainer}>
          <View style={styles.rowContainerLayout}>
            <ItemSelectorView
              style={undefined}
              title="Option"
              itemList={optionList}
              onSelected={item => onChangeOption(item)}
              selectedItem={selectedOption}
              placeHolder="select option"
            />
            <Button
              title="Get"
              onPress={onPressGet}
              disabled={selectedOption == null}
            />
          </View>
        </View>
        {selectedOption?.value.editor && (
          <View style={styles.editorContainerLayout}>
            <View style={styles.contentContainer}>
              {selectedOption?.value.editor(editOptions || {}, setEditOptions)}
            </View>
            <Button
              title="Set"
              onPress={onPressSet}
              disabled={editOptions == null}
            />
          </View>
        )}
      </ScrollView>
      <View style={styles.bottomViewContainer}>
        <ScrollView style={styles.messageArea}>
          <Text style={styles.messageText}>{message}</Text>
        </ScrollView>
      </View>
    </SafeAreaView>
  );
};

export default OptionsScreen;
