import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import {
  BleServiceEnum,
  NetworkTypeEnum,
  WlanControlCommandV2,
} from '../../service';

describe('WlanControlCommandV2 setNetworkType', () => {
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
    thetaBle.nativeWlanControlCommandV2SetNetworkType = jest.fn();
  });

  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.WLAN_CONTROL_COMMAND_V2);
    return service as WlanControlCommandV2;
  };

  test('setNetworkType', async () => {
    const testData = NetworkTypeEnum.CLIENT;
    thetaBle.nativeWlanControlCommandV2SetNetworkType = jest.fn().mockImplementation(async (id, value) => {
      expect(id).toBe(devId);
      expect(value).toBe(testData);
      return;
    });

    const service = await setupService();
    await service.setNetworkType(NetworkTypeEnum.CLIENT);
  });

  test('Exception setNetworkType', async () => {

    thetaBle.nativeWlanControlCommandV2SetNetworkType = jest.fn().mockImplementation(() => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.setNetworkType(NetworkTypeEnum.CLIENT);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
