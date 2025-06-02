import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import {
  BleServiceEnum,
  WifiSecurityModeEnum,
  WlanControlCommandV2,
} from '../../service';

describe('WlanControlCommandV2 setAccessPointStatically', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  const testData = {
    ssid: 'test_ssid',
    ssidStealth: true,
    security: WifiSecurityModeEnum.WEP,
    password: 'password_wifi',
    connectionPriority: 2,
    ipAddress: '192.168.1.1',
    subnetMask: '255.255.255.0',
    defaultGateway: '192.168.1.0',
    proxy: {
      use: true,
      url: 'http://test',
      port: 10,
      userid: 'test_user',
      password: 'password_proxy',
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();
    jest.mocked(thetaBle.nativeContainService).mockImplementation(
      jest.fn(async () => {
        return true;
      }),
    );
  });

  afterEach(() => {
    thetaBle.nativeContainService = jest.fn();
    thetaBle.nativeWlanControlCommandV2SetAccessPointStatically = jest.fn();
  });

  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.WLAN_CONTROL_COMMAND_V2);
    return service as WlanControlCommandV2;
  };

  test('setAccessPointStatically', async () => {
    thetaBle.nativeWlanControlCommandV2SetAccessPointStatically = jest.fn().mockImplementation(async (id, value) => {
      expect(id).toBe(devId);
      expect(value).toStrictEqual(testData);
      return;
    });

    const service = await setupService();
    await service.setAccessPointStatically(
      testData.ssid,
      testData.ssidStealth,
      testData.security,
      testData.password,
      testData.connectionPriority,
      testData.ipAddress,
      testData.subnetMask,
      testData.defaultGateway,
      testData.proxy,
    );
    expect(thetaBle.nativeWlanControlCommandV2SetAccessPointStatically).toBeCalledWith(devId, testData);
  });

  test('Exception setAccessPointStatically', async () => {

    thetaBle.nativeWlanControlCommandV2SetAccessPointStatically = jest.fn().mockImplementation(() => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.setAccessPointStatically(
        testData.ssid,
        testData.ssidStealth,
        testData.security,
        testData.password,
        testData.connectionPriority,
        testData.ipAddress,
        testData.subnetMask,
        testData.defaultGateway,
        testData.proxy,
      );
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeWlanControlCommandV2SetAccessPointStatically).toBeCalledWith(devId, testData);
  });
});
