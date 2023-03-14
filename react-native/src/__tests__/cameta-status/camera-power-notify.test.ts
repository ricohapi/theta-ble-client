import { NativeModules } from 'react-native';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';
import type { BaseNotify } from '../../theta-device/notify';
import { ThetaDevice } from '../../theta-device';
import { CameraPowerEnum, CameraStatusCommand } from '../../service';

describe('setCameraPowerNotify', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeSetCameraPowerNotify = jest.fn();
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

    const testValue = CameraPowerEnum.OFF;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setCameraPowerNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'CAMERA_POWER',
      params: {
        cameraPower: testValue,
      },
    });

    expect(device.notifyList.get('CAMERA_POWER')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetCameraPowerNotify).toBeCalledWith(devId, true);
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

    const testValue = CameraPowerEnum.SLEEP;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setCameraPowerNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'bad characteristic',
      params: {
        cameraPower: testValue,
      },
    });

    expect(device.notifyList.get('CAMERA_POWER')).toBeDefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetCameraPowerNotify).toBeCalledWith(devId, true);
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

    const testValue = CameraPowerEnum.OFF;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setCameraPowerNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });
    await service.setCameraPowerNotify();

    notifyCallback({
      deviceId: devId,
      characteristic: 'CAMERA_POWER',
      params: {
        cameraPower: testValue,
      },
    });

    expect(device.notifyList.get('CAMERA_POWER')).toBeUndefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetCameraPowerNotify).toBeCalledTimes(2);
    expect(thetaBle.nativeSetCameraPowerNotify).toHaveBeenLastCalledWith(devId, false);
  });

  test('exception', async () => {
    jest.mocked(thetaBle.nativeSetCameraPowerNotify).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    try {
      await service.setCameraPowerNotify(() => {
        onNotify();
      });
      expect(true).toBeFalsy();
    } catch(error) {
      expect(error).toBe('error');
    }
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetCameraPowerNotify).toBeCalledTimes(1);
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
    await service.setCameraPowerNotify((value, error) => {
      expect(value).toBeUndefined();
      expect(error?.message).toBe(errorMessage);
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'CAMERA_POWER',
      error: {
        message: errorMessage,
      },
    });

    expect(device.notifyList.get('CAMERA_POWER')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetCameraPowerNotify).toBeCalledWith(devId, true);
  });
});
