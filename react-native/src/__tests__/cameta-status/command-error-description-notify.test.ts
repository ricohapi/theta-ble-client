import { NativeModules } from 'react-native';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';
import type { BaseNotify } from '../../theta-device/notify';
import { ThetaDevice } from '../../theta-device';
import { CameraStatusCommand, CommandErrorDescriptionEnum } from '../../service';

describe('setCommandErrorDescriptionNotify', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeSetCommandErrorDescriptionNotify = jest.fn();
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

    const testValue = CommandErrorDescriptionEnum.DEVICE_BUSY;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setCommandErrorDescriptionNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'COMMAND_ERROR_DESCRIPTION',
      params: {
        commandErrorDescription: testValue,
      },
    });

    expect(device.notifyList.get('COMMAND_ERROR_DESCRIPTION')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetCommandErrorDescriptionNotify).toBeCalledWith(devId, true);
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

    const testValue = CommandErrorDescriptionEnum.DISABLED_COMMAND;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setCommandErrorDescriptionNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'bad characteristic',
      params: {
        commandErrorDescription: testValue,
      },
    });

    expect(device.notifyList.get('COMMAND_ERROR_DESCRIPTION')).toBeDefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetCommandErrorDescriptionNotify).toBeCalledWith(devId, true);
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

    const testValue = CommandErrorDescriptionEnum.INVALID_FILE_FORMAT;
    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    await service.setCommandErrorDescriptionNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });
    await service.setCommandErrorDescriptionNotify();

    notifyCallback({
      deviceId: devId,
      characteristic: 'COMMAND_ERROR_DESCRIPTION',
      params: {
        commandErrorDescription: testValue,
      },
    });

    expect(device.notifyList.get('COMMAND_ERROR_DESCRIPTION')).toBeUndefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetCommandErrorDescriptionNotify).toBeCalledTimes(2);
    expect(thetaBle.nativeSetCommandErrorDescriptionNotify).toHaveBeenLastCalledWith(devId, false);
  });

  test('exception', async () => {
    jest.mocked(thetaBle.nativeSetCommandErrorDescriptionNotify).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const onNotify = jest.fn();
    const device = new ThetaDevice(devId, devName);
    await device.connect();
    const service = new CameraStatusCommand(device);
    try {
      await service.setCommandErrorDescriptionNotify(() => {
        onNotify();
      });
      expect(true).toBeFalsy();
    } catch(error) {
      expect(error).toBe('error');
    }
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeSetCommandErrorDescriptionNotify).toBeCalledTimes(1);
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
    await service.setCommandErrorDescriptionNotify((value, error) => {
      expect(value).toBeUndefined();
      expect(error?.message).toBe(errorMessage);
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'COMMAND_ERROR_DESCRIPTION',
      error: {
        message: errorMessage,
      },
    });

    expect(device.notifyList.get('COMMAND_ERROR_DESCRIPTION')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeSetCommandErrorDescriptionNotify).toBeCalledWith(devId, true);
  });
});
