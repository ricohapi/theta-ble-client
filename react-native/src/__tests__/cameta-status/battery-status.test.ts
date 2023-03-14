import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { CameraStatusCommand, ChargingStateEnum } from '../../service';

describe('Battery Status', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeGetBatteryStatus = jest.fn();
  });
  
  test('Call normal get', async () => {
    const bleValue = ChargingStateEnum.CHARGED;
    thetaBle.nativeGetBatteryStatus = jest.fn().mockImplementation( async (id) => {
      expect(id).toBe(devId);
      return bleValue;
    });

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    const response = await service.getBatteryStatus();
  
    expect(response).toBe(bleValue);
    expect(thetaBle.nativeGetBatteryStatus).toHaveBeenCalledWith(devId);
  });
  
  test('Exception get', async () => {
    thetaBle.nativeGetBatteryStatus = jest.fn().mockImplementation( (id) => {
      expect(id).toBe(devId);
      throw 'error';
    });

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    try {
      await service.getBatteryStatus();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  
    expect(thetaBle.nativeGetBatteryStatus).toHaveBeenCalledWith(devId);
  });
});
