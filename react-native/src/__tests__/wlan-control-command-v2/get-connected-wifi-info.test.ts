import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { BleServiceEnum, WlanControlCommandV2 } from '../../service';

describe('WlanControlCommandV2 getConnectedWifiInfo', () => {
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
    thetaBle.nativeWlanControlCommandV2GetConnectedWifiInfo = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.WLAN_CONTROL_COMMAND_V2);
    return service as WlanControlCommandV2;
  };

  test('getConnectedWifiInfo', async () => {
    const testData = {
      wifiInfo: {
        ssid: 'test-ssid',
        isConnected: true,
        isInternetAccessible: false,
      },
    };
    thetaBle.nativeWlanControlCommandV2GetConnectedWifiInfo = jest.fn().mockImplementation( async (id) => {
      expect(id).toBe(devId);
      return testData;
    });

    const service = await setupService();
    const info = await service.getConnectedWifiInfo();

    expect(info).toBe(testData);

    expect(thetaBle.nativeWlanControlCommandV2GetConnectedWifiInfo).toHaveBeenCalledWith(devId);
  });
  
  test('Exception getConnectedWifiInfo', async () => {

    thetaBle.nativeWlanControlCommandV2GetConnectedWifiInfo = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.getConnectedWifiInfo();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
