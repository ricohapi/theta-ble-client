import { NativeModules } from 'react-native';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';
import type { BaseNotify } from '../../theta-device/notify';
import { ThetaDevice } from '../../theta-device';
import { CameraStatusCommand, ChargingStateEnum } from '../../service';

describe('setBatteryStatusNotify', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeSetBatteryStatusNotify = jest.fn();
  });

  test('Call normal', async () => {
    let notifyCallback: (notify: BaseNotify) => void = () => {
      expect(true).toBeFalsy();
    };
    jest.mocked(NativeEventEmitter_addListener).mockImplementation(
      jest.fn((_, callback) => {
        notifyCallback = callback;
        return {
          remove: jest.fn(),
        };
      }),
    );

    const testValue = ChargingStateEnum.CHARGED;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setBatteryStatusNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'BATTERY_STATUS',
      params: {
        batteryState: testValue,
      },
    });

    expect(device.notifyList.get('BATTERY_STATUS')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetBatteryStatusNotify).toBeCalledWith(devId, true);
  });

  test('Not called different characteristic', async () => {
    let notifyCallback: (notify: BaseNotify) => void = () => {
      expect(true).toBeFalsy();
    };
    jest.mocked(NativeEventEmitter_addListener).mockImplementation(
      jest.fn((_, callback) => {
        notifyCallback = callback;
        return {
          remove: jest.fn(),
        };
      }),
    );

    const testValue = ChargingStateEnum.CHARGING;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setBatteryStatusNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'bad characteristic',
      params: {
        batteryStatus: testValue,
      },
    });

    expect(device.notifyList.get('BATTERY_STATUS')).toBeDefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetBatteryStatusNotify).toBeCalledWith(devId, true);
  });

  test('Set empty', async () => {
    let notifyCallback: (notify: BaseNotify) => void = () => {
      expect(true).toBeFalsy();
    };
    jest.mocked(NativeEventEmitter_addListener).mockImplementation(
      jest.fn((_, callback) => {
        notifyCallback = callback;
        return {
          remove: jest.fn(),
        };
      }),
    );

    const testValue = ChargingStateEnum.DISCONNECT;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setBatteryStatusNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });
    await service.setBatteryStatusNotify();

    notifyCallback({
      deviceId: devId,
      characteristic: 'BATTERY_STATUS',
      params: {
        batteryStatus: testValue,
      },
    });

    expect(device.notifyList.get('BATTERY_STATUS')).toBeUndefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetBatteryStatusNotify).toBeCalledTimes(2);
    expect(thetaBle.nativeSetBatteryStatusNotify).toHaveBeenLastCalledWith(devId, false);
  });

  test('exception', async () => {
    jest.mocked(thetaBle.nativeSetBatteryStatusNotify).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    try {
      await service.setBatteryStatusNotify(() => {
        onNotify();
      });
      expect(true).toBeFalsy();
    } catch(error) {
      expect(error).toBe('error');
    }
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetBatteryStatusNotify).toBeCalledTimes(1);
  });

  test('receive error', async () => {
    let notifyCallback: (notify: BaseNotify) => void = () => {
      expect(true).toBeFalsy();
    };
    jest.mocked(NativeEventEmitter_addListener).mockImplementation(
      jest.fn((_, callback) => {
        notifyCallback = callback;
        return {
          remove: jest.fn(),
        };
      }),
    );

    const errorMessage = 'Error receive';
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setBatteryStatusNotify((value, error) => {
      expect(value).toBeUndefined();
      expect(error?.message).toBe(errorMessage);
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'BATTERY_STATUS',
      error: {
        message: errorMessage,
      },
    });

    expect(device.notifyList.get('BATTERY_STATUS')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetBatteryStatusNotify).toBeCalledWith(devId, true);
  });
});
