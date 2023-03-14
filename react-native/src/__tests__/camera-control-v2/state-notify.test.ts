import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';
import type { BaseNotify } from '../../theta-device/notify';
import { BleServiceEnum, CameraControlCommandV2 } from '../../service';

describe('CameraControlCommandV2 setStateNotify', () => {
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
    thetaBle.nativeCameraControlCommandV2SetStateNotify = jest.fn();
    thetaBle.nativeContainService = jest.fn();
  });

  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2);
    return service as CameraControlCommandV2;
  };

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
    const service = await setupService();
    expect(service).toBeDefined();

    const testValue = 10;
    const onNotify = jest.fn();
    await service.setStateNotify((value, error) => {
      expect(value?.batteryLevel).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'NOTIFY_STATE',
      params: {
        batteryLevel: testValue,
      },
    });

    expect(service.device.notifyList.get('NOTIFY_STATE')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeCameraControlCommandV2SetStateNotify).toBeCalledWith(devId, true);
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
    const service = await setupService();
    expect(service).toBeDefined();
    await service.setStateNotify((value, error) => {
      expect(value?.batteryLevel).toBe(testValue);
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

    expect(service.device.notifyList.get('NOTIFY_STATE')).toBeDefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeCameraControlCommandV2SetStateNotify).toBeCalledWith(devId, true);
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
    const service = await setupService();
    expect(service).toBeDefined();
    await service.setStateNotify((value, error) => {
      expect(value?.batteryLevel).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    await service.setStateNotify();

    notifyCallback({
      deviceId: devId,
      characteristic: 'NOTIFY_STATE',
      params: {
        batteryLevel: testValue,
      },
    });

    expect(service.device.notifyList.get('NOTIFY_STATE')).toBeUndefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeCameraControlCommandV2SetStateNotify).toBeCalledTimes(2);
    expect(thetaBle.nativeCameraControlCommandV2SetStateNotify).toHaveBeenLastCalledWith(devId, false);
  });

  test('exception', async () => {
    jest.mocked(thetaBle.nativeCameraControlCommandV2SetStateNotify).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const onNotify = jest.fn();
    const service = await setupService();
    expect(service).toBeDefined();
    try {
      await service.setStateNotify(() => {
        onNotify();
      });
      expect(true).toBeFalsy();
    } catch(error) {
      expect(error).toBe('error');
    }
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeCameraControlCommandV2SetStateNotify).toBeCalledTimes(1);
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
    const service = await setupService();
    expect(service).toBeDefined();
    await service.setStateNotify((value, error) => {
      expect(value).toBeUndefined();
      expect(error?.message).toBe(errorMessage);
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'NOTIFY_STATE',
      error: {
        message: errorMessage,
      },
    });

    expect(service.device.notifyList.get('NOTIFY_STATE')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeCameraControlCommandV2SetStateNotify).toBeCalledWith(devId, true);
  });
});
