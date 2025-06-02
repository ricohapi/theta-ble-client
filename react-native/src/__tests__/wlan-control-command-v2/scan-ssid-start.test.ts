import { NativeModules } from 'react-native';
import { BaseNotify, ThetaDevice } from '../../theta-device';
import { BleServiceEnum, WlanControlCommandV2 } from '../../service';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';

describe('WlanControlCommandV2 scanSsidStart', () => {
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

  test('scanSsidStart', async () => {
    let notifyCallback: (notify: BaseNotify) => void = () => {
      expect(true).toBeFalsy();
    };
    jest.mocked(NativeEventEmitter_addListener).mockImplementation(
      jest.fn((_eventType, callback) => {
        notifyCallback = callback;
        return {
          remove: jest.fn(),
        };
      }),
    );
    thetaBle.nativeWlanControlCommandV2ScanSsidStart = jest.fn().mockImplementation( async (id, timeout) => {
      expect(id).toBe(devId);
      expect(timeout).toBe(100);
      return;
    });

    const service = await setupService();
    const onNotify = jest.fn();
    const onCompleted = jest.fn();

    await service.scanSsidStart(100, onNotify, onCompleted);
    expect(service.device.notifyList.get('NOTIFICATION_SCANNED_SSID')).toBeDefined();

    notifyCallback({
      deviceId: devId,
      characteristic: 'NOTIFICATION_SCANNED_SSID',
      params: {
        ssid: 'ssid_1',
      },
    });
    notifyCallback({
      deviceId: devId,
      characteristic: 'NOTIFICATION_SCANNED_SSID',
      params: {
        ssid: 'ssid_2',
      },
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'NOTIFICATION_SCANNED_SSID',
      params: {
        ssidList: ['ssid_1', 'ssid_2'],
      },
    });

    expect(onNotify).toBeCalledTimes(2);
    expect(onNotify).nthCalledWith(1, 'ssid_1');
    expect(onNotify).nthCalledWith(2, 'ssid_2');
    expect(onCompleted).toBeCalledWith(['ssid_1', 'ssid_2']);
    expect(service.device.notifyList.get('NOTIFICATION_SCANNED_SSID')).toBeUndefined();
    expect(thetaBle.nativeWlanControlCommandV2ScanSsidStart).toHaveBeenCalledWith(devId, 100);
  });

  test('Exception scanSsidStart', async () => {
    thetaBle.nativeWlanControlCommandV2ScanSsidStart = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      const onNotify = jest.fn();
      await service.scanSsidStart(100, onNotify);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeWlanControlCommandV2ScanSsidStart).toHaveBeenCalledWith(devId, 100);
  });
});
