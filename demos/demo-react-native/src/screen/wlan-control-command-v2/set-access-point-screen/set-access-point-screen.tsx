import * as React from 'react';
import { useDeviceContext } from '../../../device-context';
import {
  BleServiceEnum,
  Proxy,
  WifiSecurityModeEnum,
  WlanControlCommandV2,
} from '../../../modules/theta-ble-client';
import { SafeAreaView } from 'react-native-safe-area-context';
import styles from './styles';
import { Alert, ScrollView, Text, View } from 'react-native';
import Button from '../../../components/ui/button';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../../App';
import RadioButton from '../../../components/ui/radio-button';
import { InputString } from '../../../components/ui/input-string';
import { TitledSwitch } from '../../../components/ui/titled-switch';
import { EnumEdit } from '../../../components/options';
import { InputNumber } from '../../../components/ui/input-number';

const ERROR_MESSAGE_NO_DEVICE = 'No device.';
const ERROR_MESSAGE_NOT_CONNECTED = 'Not connected.';
const ERROR_MESSAGE_UNSUPPORTED = 'Unsupported.';
const TITLE = 'Set AccessPoint';

type WlanSetAccessPointProps = NativeStackScreenProps<
  RootStackParamList,
  'WlanSetAccessPoint'
>;

interface AccessPointParams {
  ssid: string;
  ssidStealth?: boolean;
  security?: WifiSecurityModeEnum;
  password?: string;
  connectionPriority?: number;
  ipAddress?: string;
  subnetMask?: string;
  defaultGateway?: string;
  proxy?: Proxy;
}

const SetAccessPointScreen: React.FC<WlanSetAccessPointProps> = ({
  navigation,
  route,
}) => {
  const { thetaDevice } = useDeviceContext();
  const [service, setService] = React.useState<WlanControlCommandV2>();
  const [message, setMessage] = React.useState('');
  const [isDynamic, setIsDynamic] = React.useState<boolean>(true);

  const [accessPointParams, setAccessPointParams] =
    React.useState<AccessPointParams>({
      ssid: route.params?.ssid ?? '',
      security: WifiSecurityModeEnum.NONE,
      connectionPriority: 1,
      subnetMask: '255.255.255.0',
    });
  const [proxy, setProxy] = React.useState<Proxy>({
    use: false,
  });

  const connectTypes = [
    { label: 'dynamic', value: 0 },
    { label: 'static', value: 1 },
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
    const wlanControlCommandV2 = (await thetaDevice.getService(
      BleServiceEnum.WLAN_CONTROL_COMMAND_V2,
    )) as WlanControlCommandV2 | undefined;
    if (wlanControlCommandV2 == null) {
      alertToGoBack(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    setService(wlanControlCommandV2);
  };

  const addMessage = (newMessage: string) => {
    setMessage(prevItem => {
      return prevItem + '\n' + newMessage;
    });
  };

  const makeProxyParams = () => {
    const { use, port } = proxy;
    if (!use) {
      return undefined;
    }

    let { url, userid, password } = proxy;
    if (url != null && url.length === 0) {
      url = undefined;
    }
    if (userid != null && userid.length === 0) {
      userid = undefined;
    }
    if (password != null && password.length === 0) {
      password = undefined;
    }

    return {
      use,
      url,
      port,
      userid,
      password,
    };
  };

  const makeDynamicallyParams = () => {
    const { ssid, ssidStealth, security, connectionPriority } =
      accessPointParams;

    let { password } = accessPointParams;

    if (password != null && password.length === 0) {
      password = undefined;
    }
    const proxyParams = makeProxyParams();

    return {
      ssid,
      ssidStealth,
      security,
      password,
      connectionPriority,
      proxy: proxyParams,
    };
  };

  const makeStaticallyParams = () => {
    let { ipAddress, subnetMask, defaultGateway } = accessPointParams;

    if (ipAddress == null) {
      ipAddress = '';
    }
    if (subnetMask == null) {
      subnetMask = '';
    }
    if (defaultGateway == null) {
      defaultGateway = '';
    }
    const proxyParams = makeProxyParams();

    return {
      ...makeDynamicallyParams(),
      ipAddress,
      subnetMask,
      defaultGateway,
      proxy: proxyParams,
    };
  };

  const setAccessPointDynamically = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      const params = makeDynamicallyParams();
      const newMessage = `setAccessPointDynamically: ${JSON.stringify(
        params,
        null,
        2,
      )}`;
      console.log(newMessage);
      setMessage(newMessage);
      const {
        ssid,
        ssidStealth,
        security,
        password,
        connectionPriority,
        // eslint-disable-next-line @typescript-eslint/no-shadow
        proxy,
      } = params;
      await service.setAccessPointDynamically(
        ssid,
        ssidStealth,
        security,
        password,
        connectionPriority,
        proxy,
      );
      addMessage('OK');
    } catch (error) {
      addMessage('error:\n' + JSON.stringify(error, null, 2));
    }
  };

  const setAccessPointStatically = async () => {
    if (service == null) {
      setMessage(ERROR_MESSAGE_UNSUPPORTED);
      return;
    }
    try {
      const params = makeStaticallyParams();
      const newMessage = `setAccessPointStatically: ${JSON.stringify(
        params,
        null,
        2,
      )}`;
      console.log(newMessage);
      setMessage(newMessage);
      const {
        ssid,
        ssidStealth,
        security,
        password,
        connectionPriority,
        ipAddress,
        subnetMask,
        defaultGateway,
        // eslint-disable-next-line @typescript-eslint/no-shadow
        proxy,
      } = params;
      await service.setAccessPointStatically(
        ssid,
        ssidStealth,
        security,
        password,
        connectionPriority,
        ipAddress,
        subnetMask,
        defaultGateway,
        proxy,
      );
      addMessage('OK');
    } catch (error) {
      addMessage('error:\n' + JSON.stringify(error, null, 2));
    }
  };

  const onSetAccessPoint = () => {
    isDynamic ? setAccessPointDynamically() : setAccessPointStatically();
  };

  React.useEffect(() => {
    initService();
    /* eslint-disable-next-line react-hooks/exhaustive-deps */
  }, []);

  return (
    <SafeAreaView
      style={styles.safeAreaContainer}
      edges={['left', 'right', 'bottom']}>
      <View style={styles.messageContainerLayout}>
        <ScrollView style={styles.messageArea}>
          <Text style={styles.messageText}>{message}</Text>
        </ScrollView>
      </View>
      <View style={styles.editViewContainerLayout}>
        <Button
          style={styles.button}
          title="Set AccessPoint"
          onPress={onSetAccessPoint}
        />
        <RadioButton
          options={connectTypes}
          selected={isDynamic ? 0 : 1}
          onSelected={value => {
            console.log(`onSelect: ${value}`);
            setIsDynamic(value === 0 ? true : false);
          }}
        />
      </View>
      <ScrollView>
        <View style={styles.editViewContainerLayout}>
          <InputString
            title={'ssid'}
            style={styles.inputText}
            onChange={newValue => {
              setAccessPointParams(prevItem => {
                return { ...prevItem, ssid: newValue };
              });
            }}
            value={accessPointParams.ssid}
          />
          <TitledSwitch
            title="ssidStealth"
            value={accessPointParams.ssidStealth}
            onChange={newValue => {
              setAccessPointParams(prevItem => {
                return { ...prevItem, ssidStealth: newValue };
              });
            }}
          />
          <EnumEdit
            title={'security'}
            option={accessPointParams.security}
            onChange={newValue => {
              setAccessPointParams(prevItem => {
                return { ...prevItem, security: newValue };
              });
            }}
            optionEnum={WifiSecurityModeEnum}
          />
          <InputString
            title={'password'}
            style={styles.inputText}
            onChange={newValue => {
              setAccessPointParams(prevItem => {
                return { ...prevItem, password: newValue };
              });
            }}
            value={accessPointParams.password}
          />
          <InputNumber
            title={'connectionPriority'}
            style={styles.inputText}
            onChange={newValue => {
              setAccessPointParams(prevItem => {
                return { ...prevItem, connectionPriority: newValue };
              });
            }}
            value={accessPointParams.connectionPriority}
          />
        </View>
        {!isDynamic && (
          <View style={styles.editViewContainerLayout}>
            <InputString
              title={'ipAddress'}
              style={styles.inputText}
              onChange={newValue => {
                setAccessPointParams(prevItem => {
                  return { ...prevItem, ipAddress: newValue };
                });
              }}
              value={accessPointParams.ipAddress}
            />
            <InputString
              title={'subnetMask'}
              style={styles.inputText}
              onChange={newValue => {
                setAccessPointParams(prevItem => {
                  return { ...prevItem, subnetMask: newValue };
                });
              }}
              value={accessPointParams.subnetMask}
            />
            <InputString
              title={'defaultGateway'}
              style={styles.inputText}
              onChange={newValue => {
                setAccessPointParams(prevItem => {
                  return { ...prevItem, defaultGateway: newValue };
                });
              }}
              value={accessPointParams.defaultGateway}
            />
          </View>
        )}
        <View style={styles.editViewContainerLayout}>
          <TitledSwitch
            title="Proxy"
            value={proxy.use}
            onChange={newValue => {
              setProxy(prevItem => {
                return { ...prevItem, use: newValue };
              });
            }}
          />
          {proxy.use && (
            <View style={styles.editViewContainerLayout}>
              <InputString
                title={'url'}
                style={styles.inputText}
                onChange={newValue => {
                  setProxy(prevItem => {
                    return { ...prevItem, url: newValue };
                  });
                }}
                value={proxy.url}
              />
              <InputNumber
                title={'port'}
                style={styles.inputText}
                onChange={newValue => {
                  setProxy(prevItem => {
                    return { ...prevItem, port: newValue };
                  });
                }}
                value={proxy.port}
              />
              <InputString
                title={'userid'}
                style={styles.inputText}
                onChange={newValue => {
                  setProxy(prevItem => {
                    return { ...prevItem, userid: newValue };
                  });
                }}
                value={proxy.userid}
              />
              <InputString
                title={'password'}
                style={styles.inputText}
                onChange={newValue => {
                  setProxy(prevItem => {
                    return { ...prevItem, password: newValue };
                  });
                }}
                value={proxy.password}
              />
            </View>
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default SetAccessPointScreen;
