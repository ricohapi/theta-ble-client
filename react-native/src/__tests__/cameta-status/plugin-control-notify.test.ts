import { NativeModules } from 'react-native';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';
import type { BaseNotify } from '../../theta-device/notify';
import { ThetaDevice } from '../../theta-device';
import { CameraStatusCommand, PluginControl, PluginPowerStatusEnum } from '../../service';

describe('setPluginControlNotify', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeSetPluginControlNotify = jest.fn();
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

    const testValue = {
      pluginControl: PluginPowerStatusEnum.RUNNING,
      plugin: 1,
    } as PluginControl;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setPluginControlNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'PLUGIN_CONTROL',
      params: testValue,
    });

    expect(device.notifyList.get('PLUGIN_CONTROL')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetPluginControlNotify).toBeCalledWith(devId, true);
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

    const testValue = {
      pluginControl: PluginPowerStatusEnum.STOP,
      plugin: 2,
    } as PluginControl;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setPluginControlNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'bad characteristic',
      params: testValue,
    });

    expect(device.notifyList.get('PLUGIN_CONTROL')).toBeDefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetPluginControlNotify).toBeCalledWith(devId, true);
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

    const testValue = {
      pluginControl: PluginPowerStatusEnum.RUNNING,
      plugin: 3,
    } as PluginControl;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setPluginControlNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });
    await service.setPluginControlNotify();

    notifyCallback({
      deviceId: devId,
      characteristic: 'PLUGIN_CONTROL',
      params: testValue,
    });

    expect(device.notifyList.get('PLUGIN_CONTROL')).toBeUndefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetPluginControlNotify).toBeCalledTimes(2);
    expect(thetaBle.nativeSetPluginControlNotify).toHaveBeenLastCalledWith(devId, false);
  });

  test('exception', async () => {
    jest.mocked(thetaBle.nativeSetPluginControlNotify).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    try {
      await service.setPluginControlNotify(() => {
        onNotify();
      });
      expect(true).toBeFalsy();
    } catch(error) {
      expect(error).toBe('error');
    }
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetPluginControlNotify).toBeCalledTimes(1);
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
    await service.setPluginControlNotify((value, error) => {
      expect(value).toBeUndefined();
      expect(error?.message).toBe(errorMessage);
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'PLUGIN_CONTROL',
      error: {
        message: errorMessage,
      },
    });

    expect(device.notifyList.get('PLUGIN_CONTROL')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetPluginControlNotify).toBeCalledWith(devId, true);
  });
});
