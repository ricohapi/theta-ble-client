import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';
import type { BaseNotify } from '../../theta-device/notify';
import { CameraStatusCommand } from '../../service';

describe('setBatteryLevelNotify', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeSetBatteryLevelNotify = jest.fn();
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

    const testValue = 10;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setBatteryLevelNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'BATTERY_LEVEL',
      params: {
        batteryLevel: testValue,
      },
    });

    expect(device.notifyList.get('BATTERY_LEVEL')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetBatteryLevelNotify).toBeCalledWith(devId, true);
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

    const testValue = 10;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setBatteryLevelNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'bad characteristic',
      params: {
        batteryLevel: testValue,
      },
    });

    expect(device.notifyList.get('BATTERY_LEVEL')).toBeDefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetBatteryLevelNotify).toBeCalledWith(devId, true);
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

    const testValue = 10;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setBatteryLevelNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });
    await service.setBatteryLevelNotify();

    notifyCallback({
      deviceId: devId,
      characteristic: 'BATTERY_LEVEL',
      params: {
        batteryLevel: testValue,
      },
    });

    expect(device.notifyList.get('BATTERY_LEVEL')).toBeUndefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetBatteryLevelNotify).toBeCalledTimes(2);
    expect(thetaBle.nativeSetBatteryLevelNotify).toHaveBeenLastCalledWith(devId, false);
  });

  test('exception', async () => {
    jest.mocked(thetaBle.nativeSetBatteryLevelNotify).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    try {
      await service.setBatteryLevelNotify(() => {
        onNotify();
      });
      expect(true).toBeFalsy();
    } catch(error) {
      expect(error).toBe('error');
    }
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetBatteryLevelNotify).toBeCalledTimes(1);
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
    await service.setBatteryLevelNotify((value, error) => {
      expect(value).toBeUndefined();
      expect(error?.message).toBe(errorMessage);
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'BATTERY_LEVEL',
      error: {
        message: errorMessage,
      },
    });

    expect(device.notifyList.get('BATTERY_LEVEL')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetBatteryLevelNotify).toBeCalledWith(devId, true);
  });
});
