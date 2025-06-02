import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { BleServiceEnum, CameraControlCommandV2 } from '../../service';

describe('CameraControlCommandV2 releaseShutter', () => {
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
    thetaBle.nativeContainService = jest.fn();
    thetaBle.nativeCameraControlCommandV2ReleaseShutter = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2);
    return service as CameraControlCommandV2;
  };

  test('releaseShutter', async () => {
    thetaBle.nativeCameraControlCommandV2ReleaseShutter = jest.fn().mockImplementation( async (id) => {
      expect(id).toBe(devId);
    });

    const service = await setupService();
    await service.releaseShutter();

    expect(thetaBle.nativeCameraControlCommandV2ReleaseShutter).toHaveBeenCalledWith(devId);
  });
  
  test('Exception releaseShutter', async () => {

    thetaBle.nativeCameraControlCommandV2ReleaseShutter = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.releaseShutter();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
