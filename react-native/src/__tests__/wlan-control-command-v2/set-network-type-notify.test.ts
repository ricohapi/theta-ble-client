import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';
import type { BaseNotify } from '../../theta-device/notify';
import { BleServiceEnum, NetworkTypeEnum, WlanControlCommandV2 } from '../../service';

describe('WlanControlCommandV2 setNetworkTypeNotify', () => {
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
    thetaBle.nativeWlanControlCommandV2SetNetworkTypeNotify = jest.fn();
    thetaBle.nativeContainService = jest.fn();
  });

  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.WLAN_CONTROL_COMMAND_V2);
    return service as WlanControlCommandV2;
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

    const testValue = NetworkTypeEnum.ETHERNET;
    const onNotify = jest.fn();
    await service.setNetworkTypeNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'WRITE_SET_NETWORK_TYPE',
      params: {
        networkType: testValue,
      },
    });

    expect(service.device.notifyList.get('WRITE_SET_NETWORK_TYPE')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeWlanControlCommandV2SetNetworkTypeNotify).toBeCalledWith(devId, true);
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

    const testValue = NetworkTypeEnum.ETHERNET;
    const onNotify = jest.fn();
    const service = await setupService();
    expect(service).toBeDefined();
    await service.setNetworkTypeNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'bad characteristic',
      params: {
        networkType: testValue,
      },
    });

    expect(service.device.notifyList.get('WRITE_SET_NETWORK_TYPE')).toBeDefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeWlanControlCommandV2SetNetworkTypeNotify).toBeCalledWith(devId, true);
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

    const testValue = NetworkTypeEnum.ETHERNET;
    const onNotify = jest.fn();
    const service = await setupService();
    expect(service).toBeDefined();
    await service.setNetworkTypeNotify((value, error) => {
      expect(value).toBe(testValue);
      expect(error).toBeUndefined();
      onNotify();
    });

    await service.setNetworkTypeNotify();

    notifyCallback({
      deviceId: devId,
      characteristic: 'WRITE_SET_NETWORK_TYPE',
      params: {
        networkType: testValue,
      },
    });

    expect(service.device.notifyList.get('WRITE_SET_NETWORK_TYPE')).toBeUndefined();
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeWlanControlCommandV2SetNetworkTypeNotify).toBeCalledTimes(2);
    expect(thetaBle.nativeWlanControlCommandV2SetNetworkTypeNotify).toHaveBeenLastCalledWith(devId, false);
  });

  test('exception', async () => {
    jest.mocked(thetaBle.nativeWlanControlCommandV2SetNetworkTypeNotify).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const onNotify = jest.fn();
    const service = await setupService();
    expect(service).toBeDefined();
    try {
      await service.setNetworkTypeNotify(() => {
        onNotify();
      });
      expect(true).toBeFalsy();
    } catch(error) {
      expect(error).toBe('error');
    }
    expect(onNotify).toBeCalledTimes(0);
    expect(thetaBle.nativeWlanControlCommandV2SetNetworkTypeNotify).toBeCalledTimes(1);
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
    await service.setNetworkTypeNotify((value, error) => {
      expect(value).toBeUndefined();
      expect(error?.message).toBe(errorMessage);
      onNotify();
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'WRITE_SET_NETWORK_TYPE',
      error: {
        message: errorMessage,
      },
    });

    expect(service.device.notifyList.get('WRITE_SET_NETWORK_TYPE')).toBeDefined();
    expect(onNotify).toBeCalled();
    expect(thetaBle.nativeWlanControlCommandV2SetNetworkTypeNotify).toBeCalledWith(devId, true);
  });
});
