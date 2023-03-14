import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { BleServiceEnum,
  CameraControlCommandV2,
  CameraErrorEnum,
  CaptureStatusEnum,
  ChargingStateEnum,
  ShootingFunctionEnum, 
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
    thetaBle.nativeCameraControlCommandV2GetState = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2);
    return service as CameraControlCommandV2;
  };

  test('getState', async () => {
    const testData = {
      boardTemp: 38,
      cameraError: [
        CameraErrorEnum.NO_MEMORY,
        CameraErrorEnum.FILE_NUMBER_OVER,
      ],
      batteryState: ChargingStateEnum.CHARGING,
      latestFileUrl: 'http://192.168.1.1/files/100RICOH/R0010267.MP4',
      batteryInsert: true,
      recordableTime: 3000,
      batteryTemp: 43,
      batteryLevel: 0.99,
      recordedTime: 1,
      captureStatus: CaptureStatusEnum.IDLE,
      capturedPictures: 10,
      shootingFunction: ShootingFunctionEnum.NORMAL,
    };
    thetaBle.nativeCameraControlCommandV2GetState = jest.fn().mockImplementation( async (id) => {
      expect(id).toBe(devId);
      return testData;
    });

    const service = await setupService();
    const thetaState = await service.getState();

    expect(thetaState).toBeDefined();
    expect(thetaState.boardTemp).toBe(testData.boardTemp);
    expect(thetaState.cameraError).toBe(testData.cameraError);
    expect(thetaState.batteryState).toBe(testData.batteryState);
    expect(thetaState.latestFileUrl).toBe(testData.latestFileUrl);
    expect(thetaState.batteryInsert).toBe(testData.batteryInsert);
    expect(thetaState.recordableTime).toBe(testData.recordableTime);
    expect(thetaState.batteryTemp).toBe(testData.batteryTemp);
    expect(thetaState.batteryLevel).toBe(testData.batteryLevel);
    expect(thetaState.recordedTime).toBe(testData.recordedTime);
    expect(thetaState.captureStatus).toBe(testData.captureStatus);
    expect(thetaState.capturedPictures).toBe(testData.capturedPictures);
    expect(thetaState.shootingFunction).toBe(testData.shootingFunction);

    expect(thetaBle.nativeCameraControlCommandV2GetState).toHaveBeenCalledWith(devId);
  });
  
  test('Exception getState', async () => {

    thetaBle.nativeCameraControlCommandV2GetState = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.getState();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
