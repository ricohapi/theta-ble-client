import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { BleServiceEnum, WlanControlCommandV2 } from '../../service';

describe('WlanControlCommandV2 scanSsidStop', () => {
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
    thetaBle.nativeWlanControlCommandV2ScanSsidStart = jest.fn();
    thetaBle.nativeWlanControlCommandV2ScanSsidStop = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.WLAN_CONTROL_COMMAND_V2);
    return service as WlanControlCommandV2;
  };

  test('scanSsidStop', async () => {
    const service = await setupService();
    const onNotify = jest.fn();
    await service.scanSsidStart(100, onNotify);
    expect(service.device.notifyList.get('NOTIFICATION_SCANNED_SSID')).toBeDefined();

    await service.scanSsidStop();
    expect(service.device.notifyList.get('NOTIFICATION_SCANNED_SSID')).toBeUndefined();
    expect(thetaBle.nativeWlanControlCommandV2ScanSsidStop).toHaveBeenCalledWith(devId);

  });

  test('Exception scanSsidStop', async () => {
    thetaBle.nativeWlanControlCommandV2ScanSsidStop = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      const onNotify = jest.fn();
      await service.scanSsidStart(100, onNotify);
      await service.scanSsidStop();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeWlanControlCommandV2ScanSsidStart).toHaveBeenCalledWith(devId, 100);
    expect(thetaBle.nativeWlanControlCommandV2ScanSsidStop).toHaveBeenCalledWith(devId);
  });
});
