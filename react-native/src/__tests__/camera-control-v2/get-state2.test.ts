import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import {
  BleServiceEnum,
  CameraControlCommandV2, 
} from '../../service';

describe('CameraControlCommandV2 getState', () => {
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
    thetaBle.nativeCameraControlCommandV2GetState2 = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2);
    return service as CameraControlCommandV2;
  };

  test('getState2', async () => {
    const testData = {
      externalGpsInfo: {
        gpsInfo: {
          dateTimeZone: '2023:08:09 16:23:16+09:00',
          altitude: 4.99,
          lat: 35.48,
          datum: 'WGS84',
          lng: 134.24,
        },
      },
      internalGpsInfo: {
        gpsInfo: {
          dateTimeZone: '2023:08:09 16:23:16+09:00',
          altitude: 5.99,
          lat: 36.48,
          datum: 'WGS84',
          lng: 135.24,
        },
      },
    };
    thetaBle.nativeCameraControlCommandV2GetState2 = jest.fn().mockImplementation( async (id) => {
      expect(id).toBe(devId);
      return testData;
    });

    const service = await setupService();
    const thetaState2 = await service.getState2();

    expect(thetaState2).toBeDefined();
    const externalGpsInfo = thetaState2.externalGpsInfo;
    expect(externalGpsInfo).toBeDefined();
    let gpsInfo = externalGpsInfo.gpsInfo;
    expect(gpsInfo).toBeDefined();
    expect(gpsInfo.dateTimeZone).toBe(testData.externalGpsInfo.gpsInfo.dateTimeZone);
    expect(gpsInfo.altitude).toBe(testData.externalGpsInfo.gpsInfo.altitude);
    expect(gpsInfo.lat).toBe(testData.externalGpsInfo.gpsInfo.lat);
    expect(gpsInfo.lng).toBe(testData.externalGpsInfo.gpsInfo.lng);
    expect(gpsInfo.datum).toBe(testData.externalGpsInfo.gpsInfo.datum);
    const internalGpsInfo = thetaState2.internalGpsInfo;
    expect(internalGpsInfo).toBeDefined();
    gpsInfo = internalGpsInfo.gpsInfo;
    expect(gpsInfo).toBeDefined();
    expect(gpsInfo.dateTimeZone).toBe(testData.internalGpsInfo.gpsInfo.dateTimeZone);
    expect(gpsInfo.altitude).toBe(testData.internalGpsInfo.gpsInfo.altitude);
    expect(gpsInfo.lat).toBe(testData.internalGpsInfo.gpsInfo.lat);
    expect(gpsInfo.lng).toBe(testData.internalGpsInfo.gpsInfo.lng);
    expect(gpsInfo.datum).toBe(testData.internalGpsInfo.gpsInfo.datum);
    expect(thetaBle.nativeCameraControlCommandV2GetState2).toHaveBeenCalledWith(devId);
  });
  
  test('Exception getState2', async () => {

    thetaBle.nativeCameraControlCommandV2GetState2 = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.getState2();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
