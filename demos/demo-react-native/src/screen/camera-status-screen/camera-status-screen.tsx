import * as React from 'react';
import { Button, Stack } from '@react-native-material/core';
import { Alert, Text, View } from 'react-native';
import styles from './styles';
import { useDeviceContext } from '../../device-context';
import {
  ChargingStateEnum,
  CameraPowerEnum,
  PluginControl,
  CameraStatusCommand,
  BleServiceEnum,
  PluginPowerStatusEnum,
  ThetaDevice,
  CameraControlCommands,
} from 'theta-ble-client-react-native';

const TITLE = 'Camera Status Command';
const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';

const CameraStatusScreen = ({ navigation }) => {
  const { thetaDevice } = useDeviceContext();
  const [cameraStatusCommand, setCameraStatusCommand] =
    React.useState<CameraStatusCommand>();

  const [infoText, setInfoText] = React.useState('Init');
  const [batteryLevel, setBatteryLevel] = React.useState(0);
  const [batteryStatus, setBatteryStatus] = React.useState<ChargingStateEnum>();
  const [cameraPower, setCameraPower] = React.useState<CameraPowerEnum>();
  const [pluginControl, setPluginControl] = React.useState<PluginControl>();
  const [pluginControlEnum, setPluginControlEnum] =
    React.useState<PluginPowerStatusEnum>();
  const [firstPlugin, setFirstPlugin] = React.useState<number>();

  const initPlugin = async (device: ThetaDevice) => {
    try {
      const thetaService = (await device.getService(
        BleServiceEnum.CAMERA_CONTROL_COMMANDS,
      )) as CameraControlCommands;
      if (thetaService == null) {
        setFirstPlugin(undefined);
        return;
      }
      const orders = await thetaService.getPluginOrders();
      setFirstPlugin(orders.first);
    } catch (error) {
      console.log(JSON.stringify(error));
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

  const init = async () => {
    if (thetaDevice) {
      const isConnected = await thetaDevice.isConnected();
      if (!isConnected) {
        alertToGoBack(ERROR_MESSAGE_NOT_CONNECTED);
        return;
      }
      initPlugin(thetaDevice);
      thetaDevice
        .getService(BleServiceEnum.CAMERA_STATUS_COMMAND)
        .then(thetaService => {
          setCameraStatusCommand(thetaService as CameraStatusCommand);
          setNotifications(thetaService as CameraStatusCommand);
        })
        .catch(() => {
          alertToGoBack(ERROR_MESSAGE_UNSUPPORTED);
        });
    } else {
      alertToGoBack(ERROR_MESSAGE_NO_DEVICE);
    }
  };

  const resetNotify = async (service: CameraStatusCommand) => {
    try {
      await service.setBatteryLevelNotify();
      await service.setBatteryStatusNotify();
      await service.setCameraPowerNotify();
      await service.setCommandErrorDescriptionNotify();
      await service.setPluginControlNotify();
    } catch (_) {}
  };

  const updateBatteryLevel = async () => {
    if (cameraStatusCommand == null) {
      setInfoText('CAMERA_STATUS_COMMAND is unsupported.');
      return;
    }
    try {
      const value = await cameraStatusCommand.getBatteryLevel();
      setBatteryLevel(value);
    } catch (_) {
      setInfoText('Error.');
    }
  };

  const updateBatteryStatus = async () => {
    if (cameraStatusCommand == null) {
      setInfoText('CAMERA_STATUS_COMMAND is unsupported.');
      return;
    }
    try {
      const value = await cameraStatusCommand.getBatteryStatus();
      setBatteryStatus(value);
    } catch (_) {
      setInfoText('Error.');
    }
  };

  const updateCameraPower = async () => {
    if (cameraStatusCommand == null) {
      setInfoText('CAMERA_STATUS_COMMAND is unsupported.');
      return;
    }
    try {
      const value = await cameraStatusCommand.getCameraPower();
      setCameraPower(value);
    } catch (_) {
      setInfoText('Error.');
    }
  };

  const setDeviceCameraPower = async (value: CameraPowerEnum) => {
    if (cameraStatusCommand == null) {
      setInfoText('CAMERA_STATUS_COMMAND is unsupported.');
      return;
    }
    try {
      await cameraStatusCommand.setCameraPower(value);
      setCameraPower(value);
    } catch (_) {
      setInfoText('Error.');
    }
  };

  const setDevicePluginControl = async (value: PluginPowerStatusEnum) => {
    if (thetaDevice == null) {
      setInfoText('Error. No device.');
      return;
    }
    if (cameraStatusCommand == null) {
      setInfoText('CAMERA_STATUS_COMMAND is unsupported.');
      return;
    }
    if (!pluginControl) {
      setInfoText('Error. Not yet acquired.');
      return;
    }
    try {
      if (value === PluginPowerStatusEnum.STOP) {
        await cameraStatusCommand.setPluginControl({
          pluginControl: PluginPowerStatusEnum.STOP,
        });
      } else {
        if (pluginControl.plugin !== undefined && firstPlugin != null) {
          await cameraStatusCommand.setPluginControl({
            pluginControl: PluginPowerStatusEnum.RUNNING,
            plugin: firstPlugin,
          });
        } else {
          await cameraStatusCommand.setPluginControl({
            pluginControl: PluginPowerStatusEnum.RUNNING,
          });
        }
      }
    } catch (_) {
      setInfoText('Error.');
    }
  };

  const updatePluginControl = async () => {
    if (thetaDevice == null) {
      setInfoText('Error. No device.');
      return;
    }
    if (cameraStatusCommand == null) {
      setInfoText('CAMERA_STATUS_COMMAND is unsupported.');
      return;
    }
    try {
      const value = await cameraStatusCommand.getPluginControl();
      setPluginControl(value);
    } catch (_) {
      setInfoText('Error.');
    }
  };

  const setNotifications = async (service: CameraStatusCommand) => {
    console.log('setNotifications()');
    await service.setBatteryLevelNotify(value => {
      setInfoText('Battery level: ' + value);
      setBatteryLevel(value ?? 0);
    });
    await service.setBatteryStatusNotify(value => {
      setInfoText('Battery status: ' + value);
      setBatteryStatus(value);
    });
    await service.setCameraPowerNotify(value => {
      setInfoText('Camera power: ' + value);
      setCameraPower(value);
    });
    await service.setCommandErrorDescriptionNotify(value => {
      setInfoText('Command error: ' + value);
    });
    await service.setPluginControlNotify(value => {
      setInfoText('Plugin control: ' + value?.pluginControl);
      setPluginControl(value);
    });
  };

  React.useEffect(() => {
    init();
    return () => {
      if (cameraStatusCommand) {
        resetNotify(cameraStatusCommand);
      }
    };
    /* eslint-disable-next-line react-hooks/exhaustive-deps */
  }, []);

  React.useEffect(() => {
    setPluginControlEnum(pluginControl?.pluginControl);
  }, [pluginControl]);

  return (
    <Stack fill center spacing={7}>
      <Text style={styles.boldText}>{thetaDevice?.name}</Text>
      <View style={styles.rowContainer}>
        <Text style={styles.text}>Battery level: {batteryLevel}</Text>
        <Button title="Update" onPress={updateBatteryLevel} />
      </View>

      <View style={styles.rowContainer}>
        <Text style={styles.text}>Battery status: {batteryStatus}</Text>
        <Button title="Update" onPress={updateBatteryStatus} />
      </View>

      <Stack spacing={2}>
        <View style={styles.rowContainer}>
          <Text style={styles.text}>Camera power: {cameraPower}</Text>
          <Button title="Update" onPress={updateCameraPower} />
        </View>
        <View style={styles.rowContainer}>
          <Button
            title="Off"
            onPress={() => {
              setDeviceCameraPower(CameraPowerEnum.OFF);
            }}
          />
          <Button
            title="On"
            onPress={() => {
              setDeviceCameraPower(CameraPowerEnum.ON);
            }}
          />
          <Button
            title="Sleep"
            onPress={() => {
              setDeviceCameraPower(CameraPowerEnum.SLEEP);
            }}
          />
        </View>
      </Stack>

      <Stack spacing={2}>
        <View style={styles.rowContainer}>
          <Text style={styles.text}>Plugin control: {pluginControlEnum}</Text>
          <Button title="Update" onPress={updatePluginControl} />
        </View>
        <View style={styles.rowContainer}>
          <Button
            title="Running"
            onPress={() => {
              setDevicePluginControl(PluginPowerStatusEnum.RUNNING);
            }}
          />
          <Button
            title="Stop"
            onPress={() => {
              setDevicePluginControl(PluginPowerStatusEnum.STOP);
            }}
          />
        </View>
      </Stack>

      <Text style={styles.text}>
        Info:{'\n'}
        {infoText}
      </Text>
    </Stack>
  );
};

export default CameraStatusScreen;
