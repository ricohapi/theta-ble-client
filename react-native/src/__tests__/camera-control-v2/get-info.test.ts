import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { BleServiceEnum, CameraControlCommandV2, ThetaModel } from '../../service';

describe('CameraControlCommandV2 getInfo', () => {
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
    thetaBle.nativeCameraControlCommandV2GetInfo = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2);
    return service as CameraControlCommandV2;
  };

  test('getInfo', async () => {
    const testData = {
      serialNumber: '12345678',
      bluetoothMacAddress: '99:99:99:99:99:99',
      manufacturer: 'Ricoh Company, Ltd.',
      model: ThetaModel.THETA_X,
      firmwareVersion: '2.21.0',
      wlanMacAddress: 'AA:AA:AA:AA:AA:AA',
      uptime: 1213,
    };
    thetaBle.nativeCameraControlCommandV2GetInfo = jest.fn().mockImplementation( async (id) => {
      expect(id).toBe(devId);
      return testData;
    });

    const service = await setupService();
    const thetaInfo = await service.getInfo();

    expect(thetaInfo).toBeDefined();
    expect(thetaInfo.serialNumber).toBe(testData.serialNumber);
    expect(thetaInfo.bluetoothMacAddress).toBe(testData.bluetoothMacAddress);
    expect(thetaInfo.manufacturer).toBe(testData.manufacturer);
    expect(thetaInfo.model).toBe(testData.model);
    expect(thetaInfo.firmwareVersion).toBe(testData.firmwareVersion);
    expect(thetaInfo.wlanMacAddress).toBe(testData.wlanMacAddress);
    expect(thetaInfo.uptime).toBe(testData.uptime);

    expect(thetaBle.nativeCameraControlCommandV2GetInfo).toHaveBeenCalledWith(devId);
  });
  
  test('Exception getInfo', async () => {

    thetaBle.nativeCameraControlCommandV2GetInfo = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.getInfo();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
