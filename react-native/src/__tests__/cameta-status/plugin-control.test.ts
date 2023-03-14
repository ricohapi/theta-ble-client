import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { CameraStatusCommand, PluginControl, PluginPowerStatusEnum } from '../../service';

describe('Plugin Control', () => {
  const devId = 99;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeGetPluginControl = jest.fn();
    thetaBle.nativeSetPluginControl = jest.fn();
  });
  
  test('Call normal get', async () => {
    const bleValue = {
      pluginControl: PluginPowerStatusEnum.RUNNING,
      plugin: 2,
    } as PluginControl;
    jest.mocked(thetaBle.nativeGetPluginControl).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
        return bleValue;
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    const response = await service.getPluginControl();
  
    expect(response).toBe(bleValue);
    expect(thetaBle.nativeGetPluginControl).toHaveBeenCalledWith(devId);
  });
  
  test('Exception get', async () => {
    jest.mocked(thetaBle.nativeGetPluginControl).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    try {
      await service.getPluginControl();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  
    expect(thetaBle.nativeGetPluginControl).toHaveBeenCalledWith(devId);
  });

  test.each([
    PluginPowerStatusEnum.RUNNING,
    PluginPowerStatusEnum.STOP,
  ])('Call normal set', async (targetValue: PluginPowerStatusEnum ) => {
    const testValue = {
      pluginControl: targetValue,
      plugin: 1,
    } as PluginControl;

    jest.mocked(thetaBle.nativeSetPluginControl).mockImplementation(
      jest.fn(async (id, value) => {
        expect(id).toBe(devId);
        expect(value).toBe(testValue);
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    await service.setPluginControl(testValue);
  
    expect(thetaBle.nativeSetPluginControl).toHaveBeenCalledWith(devId, testValue);
  });

  test('Exception for set', async () => {
    const testValue = {
      pluginControl: PluginPowerStatusEnum.RUNNING,
      plugin: 1,
    } as PluginControl;
    jest.mocked(thetaBle.nativeSetPluginControl).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    try {
      await service.setPluginControl(testValue);
      throw 'failed';
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeSetPluginControl).toHaveBeenCalledWith(devId, testValue);
  });
});
