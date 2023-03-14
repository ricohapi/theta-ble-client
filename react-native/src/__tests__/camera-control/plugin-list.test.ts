import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { CameraControlCommands, type PluginList } from '../../service';

describe('Plugin Orders', () => {
  const devId = 99;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeGetPluginList = jest.fn();
  });
  
  test('Call normal get', async () => {
    const bleValue = {
      plugins: [1, 2, 3],
    } as PluginList;
    jest.mocked(thetaBle.nativeGetPluginList).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
        return bleValue;
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraControlCommands(device);
    const response = await service.getPluginList();
  
    expect(response).toBe(bleValue);
    expect(thetaBle.nativeGetPluginList).toHaveBeenCalledWith(devId);
  });
  
  test('Exception get', async () => {
    jest.mocked(thetaBle.nativeGetPluginList).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraControlCommands(device);
    try {
      await service.getPluginList();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  
    expect(thetaBle.nativeGetPluginList).toHaveBeenCalledWith(devId);
  });
});
