import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { CameraPowerEnum, CameraStatusCommand } from '../../service';

describe('Camera Power', () => {
  const devId = 99;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeGetCameraPower = jest.fn();
    thetaBle.nativeSetCameraPower = jest.fn();
  });
  
  test('Call normal get', async () => {
    const bleValue = CameraPowerEnum.SLEEP;
    jest.mocked(thetaBle.nativeGetCameraPower).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
        return bleValue;
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    const response = await service.getCameraPower();
  
    expect(response).toBe(bleValue);
    expect(thetaBle.nativeGetCameraPower).toHaveBeenCalledWith(devId);
  });
  
  test('Exception get', async () => {
    jest.mocked(thetaBle.nativeGetCameraPower).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    try {
      await service.getCameraPower();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  
    expect(thetaBle.nativeGetCameraPower).toHaveBeenCalledWith(devId);
  });

  test.each([
    CameraPowerEnum.OFF,
    CameraPowerEnum.ON,
    CameraPowerEnum.SLEEP,
  ])('Call normal set', async (testValue) => {
    jest.mocked(thetaBle.nativeSetCameraPower).mockImplementation(
      jest.fn(async (id, value) => {
        expect(id).toBe(devId);
        expect(value).toBe(testValue);
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    await service.setCameraPower(testValue);
  
    expect(thetaBle.nativeSetCameraPower).toHaveBeenCalledWith(devId, testValue as string);
  });

  test('Exception for set', async () => {
    const testValue = CameraPowerEnum.ON;
    jest.mocked(thetaBle.nativeSetCameraPower).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new CameraStatusCommand(device);
    try {
      await service.setCameraPower(testValue);
      throw 'failed';
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeSetCameraPower).toHaveBeenCalledWith(devId, testValue as string);
  });
});
