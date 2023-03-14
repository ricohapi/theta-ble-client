import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { CameraControlCommands, type PluginOrders } from '../../service';

describe('Plugin Orders', () => {
  const devId = 99;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeGetPluginOrders = jest.fn();
    thetaBle.nativeSetPluginOrders = jest.fn();
  });
  
  test('Call normal get', async () => {
    const bleValue = {
      first: 1,
      second: 2,
      third: 3,
    } as PluginOrders;
    jest.mocked(thetaBle.nativeGetPluginOrders).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
        return bleValue;
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraControlCommands(device);
    const response = await service.getPluginOrders();
  
    expect(response).toBe(bleValue);
    expect(thetaBle.nativeGetPluginOrders).toHaveBeenCalledWith(devId);
  });
  
  test('Exception get', async () => {
    jest.mocked(thetaBle.nativeGetPluginOrders).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraControlCommands(device);
    try {
      await service.getPluginOrders();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  
    expect(thetaBle.nativeGetPluginOrders).toHaveBeenCalledWith(devId);
  });

  test('Call normal set', async () => {
    const testValue = {
      first: 1,
      second: 2,
      third: 3,
    } as PluginOrders;

    jest.mocked(thetaBle.nativeSetPluginOrders).mockImplementation(
      jest.fn(async (id, value) => {
        expect(id).toBe(devId);
        expect(value).toBe(testValue);
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraControlCommands(device);
    await service.setPluginOrders(testValue);
  
    expect(thetaBle.nativeSetPluginOrders).toHaveBeenCalledWith(devId, testValue);
  });

  test('Exception for set', async () => {
    const testValue = {
      first: 1,
      second: 2,
      third: 3,
    } as PluginOrders;
    jest.mocked(thetaBle.nativeSetPluginOrders).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraControlCommands(device);
    try {
      await service.setPluginOrders(testValue);
      throw 'failed';
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeSetPluginOrders).toHaveBeenCalledWith(devId, testValue);
  });
});
