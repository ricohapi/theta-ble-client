import { ThetaDevice } from '../theta-device';
import { NativeEventEmitter_addListener } from '../__mocks__/react-native';
import { NativeModules } from 'react-native';

const thetaBle = NativeModules.ThetaBleClientReactNative;

beforeEach(() => {
  jest.clearAllMocks();
});

describe('ThetaDevice', () => {

  const devId = 1;
  const devName = '0123456789';
  const appUuid = 'uuid 0123456789';

  test('Call connect normal', async () => {
    jest.mocked(thetaBle.nativeConnect).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
      }),
    );

    const device = new ThetaDevice(devId, devName);
    await device.connect();

    expect(thetaBle.nativeConnect).toHaveBeenCalledWith(devId, undefined);
    expect(device.id).toBe(devId);
    expect(device.name).toBe(devName);
    expect(device.uuid).toBeUndefined();

    expect(NativeEventEmitter_addListener).toHaveBeenCalledWith('ThetaBleNotify', expect.anything());
  });

  test('Call connect normal with uuid', async () => {
    jest.mocked(thetaBle.nativeConnect).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
      }),
    );

    const device = new ThetaDevice(devId, devName);
    await device.connect(appUuid);

    expect(thetaBle.nativeConnect).toHaveBeenCalledWith(devId, appUuid);
    expect(device.id).toBe(devId);
    expect(device.name).toBe(devName);
    expect(device.uuid).toBe(appUuid);

    expect(NativeEventEmitter_addListener).toHaveBeenCalledWith('ThetaBleNotify', expect.anything());
  });

  test('Exception for Call connect', async () => {
    jest.mocked(thetaBle.nativeConnect).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );
  
    const device = new ThetaDevice(devId, devName);
    try {
      await device.connect();
      throw new Error('failed');
    } catch (error) {
      expect(device.uuid).toBeUndefined();
      expect(error).toBe('error');
    }

    expect(thetaBle.nativeConnect).toHaveBeenCalledWith(devId, undefined);
  });

  test('Exception for Call connect with uuid', async () => {
    jest.mocked(thetaBle.nativeConnect).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );
  
    const device = new ThetaDevice(devId, devName);
    try {
      await device.connect(appUuid);
      throw new Error('failed');
    } catch (error) {
      expect(device.uuid).toBeUndefined();
      expect(error).toBe('error');
    }

    expect(thetaBle.nativeConnect).toHaveBeenCalledWith(devId, appUuid);
  });

  test('Call disconnect normal', async () => {
    jest.mocked(thetaBle.nativeDisconnect).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
      }),
    );

    const device = new ThetaDevice(devId, devName);
    await device.disconnect();

    expect(thetaBle.nativeDisconnect).toHaveBeenCalledWith(devId);
  });

  test('Exception for Call disconnect', async () => {
    jest.mocked(thetaBle.nativeDisconnect).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );
  
    const device = new ThetaDevice(devId, devName);
    try {
      await device.disconnect();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }

    expect(thetaBle.nativeDisconnect).toHaveBeenCalledWith(devId);
  });

  test('Call release normal', async () => {
    jest.mocked(thetaBle.nativeReleaseDevice).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
      }),
    );

    const remove = jest.fn();
    jest.mocked(NativeEventEmitter_addListener).mockImplementation(
      jest.fn(() => {
        return {
          remove,
        };
      }),
    );

    const device = new ThetaDevice(devId, devName);
    await device.release();

    expect(thetaBle.nativeReleaseDevice).toHaveBeenCalledWith(devId);
    expect(remove).toBeCalledTimes(1);
  });

  test('Call isConnected normal', async () => {
    jest.mocked(thetaBle.nativeIsConnected).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
        return true;
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const isConnected = await device.isConnected();

    expect(thetaBle.nativeIsConnected).toHaveBeenCalledWith(devId);
    expect(isConnected).toBeTruthy();
  });

  test('Call isConnected disconnected', async () => {
    jest.mocked(thetaBle.nativeIsConnected).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
        return false;
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const isConnected = await device.isConnected();

    expect(thetaBle.nativeIsConnected).toHaveBeenCalledWith(devId);
    expect(isConnected).toBeFalsy();
  });

});
