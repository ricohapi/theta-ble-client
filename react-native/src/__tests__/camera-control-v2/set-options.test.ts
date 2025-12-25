import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import {
  BleServiceEnum,
  CameraControlCommandV2,
  CameraPowerEnum,
  CaptureModeEnum,
  NetworkTypeEnum,
  WlanAntennaConfigEnum,
  WlanFrequencyEnum,
} from '../../service';

describe('CameraControlCommandV2 setOptions', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

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
    thetaBle.nativeCameraControlCommandV2SetOptions = jest.fn();
  });

  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2);
    return service as CameraControlCommandV2;
  };

  test('setOptions', async () => {
    const testData = {
      cameraPower: CameraPowerEnum.SILENT_MODE,
      captureMode: CaptureModeEnum.IMAGE,
      defaultWifiPassword: '11110000',
      networkType: NetworkTypeEnum.DIRECT,
      password: 'pass123',
      ssid: 'ssid_123',
      username: 'user1',
      wifiPassword: 'wifipass123',
      wlanAntennaConfig: WlanAntennaConfigEnum.MIMO,
      wlanFrequency: WlanFrequencyEnum.GHZ_2_4,
    };
    thetaBle.nativeCameraControlCommandV2SetOptions = jest.fn().mockImplementation(async (id, options) => {
      expect(id).toBe(devId);
      expect(options).toStrictEqual(testData);
      return;
    });

    const service = await setupService();
    await service.setOptions({
      cameraPower: CameraPowerEnum.SILENT_MODE,
      captureMode: CaptureModeEnum.IMAGE,
      defaultWifiPassword: '11110000',
      networkType: NetworkTypeEnum.DIRECT,
      password: 'pass123',
      ssid: 'ssid_123',
      username: 'user1',
      wifiPassword: 'wifipass123',
      wlanAntennaConfig: WlanAntennaConfigEnum.MIMO,
      wlanFrequency: WlanFrequencyEnum.GHZ_2_4,
    });
  });

  test('Exception setOptions', async () => {

    thetaBle.nativeCameraControlCommandV2SetOptions = jest.fn().mockImplementation(() => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.setOptions({
        cameraPower: CameraPowerEnum.SILENT_MODE,
        captureMode: CaptureModeEnum.IMAGE,
        defaultWifiPassword: '11110000',
        networkType: NetworkTypeEnum.DIRECT,
        password: 'pass123',
        ssid: 'ssid_123',
        username: 'user1',
        wifiPassword: 'wifipass123',
        wlanAntennaConfig: WlanAntennaConfigEnum.SISO,
        wlanFrequency: WlanFrequencyEnum.GHZ_2_4,
      });
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
