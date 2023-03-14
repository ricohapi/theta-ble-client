import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { CameraStatusCommand } from '../../service';

describe('Battery Level', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeGetBatteryLevel = jest.fn();
  });
  
  test('Call normal get', async () => {
    const bleValue = 99;
    thetaBle.nativeGetBatteryLevel = jest.fn().mockImplementation( async (id) => {
      expect(id).toBe(devId);
      return bleValue;
    });

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    const response = await service.getBatteryLevel();
  
    expect(response).toBe(bleValue);
    expect(thetaBle.nativeGetBatteryLevel).toHaveBeenCalledWith(devId);
  });
  
  test('Exception get', async () => {
    thetaBle.nativeGetBatteryLevel = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    try {
      await service.getBatteryLevel();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  
    expect(thetaBle.nativeGetBatteryLevel).toHaveBeenCalledWith(devId);
  });
});
